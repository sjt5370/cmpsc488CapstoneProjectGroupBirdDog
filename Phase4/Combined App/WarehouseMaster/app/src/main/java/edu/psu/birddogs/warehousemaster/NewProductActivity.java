package edu.psu.birddogs.warehousemaster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewProductActivity extends AppCompatActivity {
    private StockProduct stockProduct;
    //private int toMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);
        this.setTitle(getResources().getString(R.string.new_product_bar));
        stockProduct = new StockProduct();
        stockProduct.setID(getIntent().getIntExtra("id", -1));
        stockProduct.setShelfStock(0);

        ((Button) findViewById(R.id.pickUpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable name = ((EditText) findViewById(R.id.nameField)).getText();
                Editable desc = ((EditText) findViewById(R.id.descriptionField)).getText();
                Editable manu = ((EditText) findViewById(R.id.manufacturerField)).getText();
                Editable price = ((EditText) findViewById(R.id.priceField)).getText();
                Editable bulk = ((EditText) findViewById(R.id.receiveQuantity)).getText();
                Editable priority = ((EditText) findViewById(R.id.priorityField)).getText();
                Editable volume = ((EditText) findViewById(R.id.volumeField)).getText();
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(desc) || TextUtils.isEmpty(manu) || TextUtils.isEmpty(price)
                        || TextUtils.isEmpty(bulk) || TextUtils.isEmpty(priority) || TextUtils.isEmpty(volume)) {
                    Toast.makeText(NewProductActivity.this, getResources().getString(R.string.empty_fields_response), Toast.LENGTH_SHORT).show();
                    return;
                }
                stockProduct.setProductName(name.toString());
                stockProduct.setDescription(desc.toString());
                stockProduct.setManufacturer(manu.toString());
                stockProduct.setPrice(Double.parseDouble(price.toString()));
                stockProduct.setBulkStock(Integer.parseInt(bulk.toString()));
                stockProduct.setPriority(Integer.parseInt(priority.toString()));
                stockProduct.setVolume(Double.parseDouble(volume.toString()));

                DatabaseManager.addProduct(stockProduct);                    //FIXME: Does not currently check that unique is enforced
                finish();
            }
        });
    }
}
