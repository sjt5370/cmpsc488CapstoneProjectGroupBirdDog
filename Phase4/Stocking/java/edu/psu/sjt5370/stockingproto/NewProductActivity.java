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

import java.sql.Connection;

public class NewProductActivity extends AppCompatActivity {
    private Product product;
    //private int toMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
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
                new DatabaseManager().getDBConnection(new DatabaseManager.OnDatabaseReadyListener() {
                    @Override
                    public void onDatabaseReady(Connection db) {        //FIXME: Does not currently check that unique is enforced
                        DatabaseManager.addProduct(product, db);
                        finish();
                    }
                });
            }
        });
    }
}
