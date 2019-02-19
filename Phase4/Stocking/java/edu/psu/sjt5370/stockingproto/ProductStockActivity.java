package edu.psu.sjt5370.stockingproto;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProductStockActivity extends AppCompatActivity {
    private DatabaseManager instance;
    private Product product;
    private int index;
    private int toMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_stock);
        Intent intent = getIntent();
        product = intent.getParcelableExtra("product");
        index = intent.getIntExtra("index", -1);
        if (product == null) product = new Product();
        this.setTitle(product.getProductName());
        ((TextView) findViewById(R.id.byManufacturer)).setText("by " + product.getManufacturer());
        ((TextView) findViewById(R.id.shelfStockQuantity)).setText(String.valueOf(product.getShelfStock()));
        ((TextView) findViewById(R.id.bulkStockQuantity)).setText(String.valueOf(product.getBulkStock()));
        ((TextView) findViewById(R.id.description)).setText(String.valueOf(product.getDescription()));
        ((Button) findViewById(R.id.moveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable toMoveS = ((EditText) findViewById(R.id.moveQuantity)).getText();
                if (TextUtils.isEmpty(toMoveS) || (toMove = Integer.parseInt(toMoveS.toString())) <= 0)
                    Toast.makeText(ProductStockActivity.this, getResources().getString(R.string.negative_quantity_response), Toast.LENGTH_LONG).show();
                else if (toMove > product.getBulkStock())
                    Toast.makeText(ProductStockActivity.this, getResources().getString(R.string.insufficient_quantity_response), Toast.LENGTH_LONG).show();
                else {
                    instance = DatabaseManager.getInstance(ProductStockActivity.this);
                    instance.getWritableDatabase(new DatabaseManager.OnDatabaseReadyListener() {
                        @Override
                        public void onDatabaseReady(SQLiteDatabase db) {
                            instance.stockProduct(product.getID(), toMove, db);
                            setResult(Activity.RESULT_OK, new Intent().putExtra("index", index));
                            finish();
                        }
                    });
                }
            }
        });
    }
}
