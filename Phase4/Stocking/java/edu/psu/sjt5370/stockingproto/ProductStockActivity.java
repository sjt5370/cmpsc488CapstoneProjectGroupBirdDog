package edu.psu.sjt5370.stockingproto;

import android.app.Activity;
import android.content.Intent;
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

    Product product;
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_stock);
        Intent intent = getIntent();
        product = intent.getParcelableExtra("product");
        index = intent.getIntExtra("index", -1);
        if (product == null) product = new Product();
        this.setTitle(product.name);
        ((TextView) findViewById(R.id.bulkStockQuantity)).setText("" + product.bulkStock);
        ((Button) findViewById(R.id.moveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable toMoveS = ((EditText) findViewById(R.id.moveQuantity)).getText();
                if (TextUtils.isEmpty(toMoveS)) {
                    Toast.makeText(ProductStockActivity.this, getResources().getString(R.string.negative_quantity_response), Toast.LENGTH_LONG).show();
                    return;
                }
                int toMove = Integer.parseInt(toMoveS.toString());
                if (toMove <= 0)
                    Toast.makeText(ProductStockActivity.this, getResources().getString(R.string.negative_quantity_response), Toast.LENGTH_LONG).show();
                else if (toMove > product.bulkStock)
                    Toast.makeText(ProductStockActivity.this, getResources().getString(R.string.insufficient_quantity_response), Toast.LENGTH_LONG).show();
                else {
                    product.bulkStock -= toMove;
                    product.shelfStock += toMove;
                    setResult(Activity.RESULT_OK, new Intent().putExtra("index", index));
                    finish();
                }
            }
        });
    }
}
