package edu.psu.sjt5370.stockingproto;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewProductActivity extends AppCompatActivity {
    private DatabaseManager instance;
    private Product product;
    private int toMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        instance = DatabaseManager.getInstance(this);
        this.setTitle(getResources().getString(R.string.new_product_bar));
        product = new Product();
        product.setID(getIntent().getIntExtra("id", -1));
        product.setShelfStock(0);

        ((Button) findViewById(R.id.pickUpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable name = ((EditText) findViewById(R.id.nameField)).getText();
                Editable desc = ((EditText) findViewById(R.id.descriptionField)).getText();
                Editable manu = ((EditText) findViewById(R.id.manufacturerField)).getText();
                Editable price = ((EditText) findViewById(R.id.priceField)).getText();
                Editable bulk = ((EditText) findViewById(R.id.receiveQuantity)).getText();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(manu) || TextUtils.isEmpty(price) || TextUtils.isEmpty(bulk)) {
                    Toast.makeText(NewProductActivity.this, getResources().getString(R.string.empty_fields_response), Toast.LENGTH_SHORT).show();
                    return;
                }
                product.setProductName(name.toString());
                product.setDescription(desc.toString());
                product.setManufacturer(manu.toString());
                product.setPrice(Double.parseDouble(price.toString()));
                product.setBulkStock(Integer.parseInt(bulk.toString()));
                instance.getWritableDatabase(new DatabaseManager.OnDatabaseReadyListener() {
                    @Override
                    public void onDatabaseReady(SQLiteDatabase db) {        //FIXME: Does not currently check that unique is enforced
                        instance.addProduct(product, db);
                        finish();
                    }
                });

            }
        });
        //((TextView) findViewById(R.id.byManufacturer)).setText("by " + product.getManufacturer());
        //((TextView) findViewById(R.id.shelfStockQuantity)).setText(String.valueOf(product.getShelfStock()));
        //((TextView) findViewById(R.id.bulkStockQuantity)).setText(String.valueOf(product.getBulkStock()));
        //((TextView) findViewById(R.id.description)).setText(String.valueOf(product.getDescription()));
        /*
        ((Button) findViewById(R.id.pickUpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable toMoveS = ((EditText) findViewById(R.id.receiveQuantity)).getText();
                if (TextUtils.isEmpty(toMoveS) || (toMove = Integer.parseInt(toMoveS.toString())) <= 0)
                    Toast.makeText(NewProductActivity.this, getResources().getString(R.string.negative_quantity_response), Toast.LENGTH_LONG).show();
                else {
                    instance = DatabaseManager.getInstance(NewProductActivity.this);
                    instance.getWritableDatabase(new DatabaseManager.OnDatabaseReadyListener() {
                        @Override
                        public void onDatabaseReady(SQLiteDatabase db) {
                            instance.receiveProduct(product.getID(), toMove, db);
                            //setResult(Activity.RESULT_OK, new Intent().putExtra("index", index));
                            finish();
                        }
                    });
                }
            }
        });
        */
    }
}
