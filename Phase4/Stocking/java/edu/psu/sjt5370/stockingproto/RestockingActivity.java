package edu.psu.sjt5370.stockingproto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RestockingActivity extends AppCompatActivity {

    private ArrayList<Product> productList;
    private ArrayList<Product> displayList;
    private ProductListAdapter adapter;
    private Spinner spinner;
    //private ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restocking);
        this.setTitle(getResources().getString(R.string.restocking_bar));
        productList = new ArrayList<>();
        displayList = new ArrayList<>();

        adapter = new ProductListAdapter(this, R.layout.product, displayList);
        ((ListView) findViewById(R.id.productList)).setAdapter(adapter);

        spinner = findViewById(R.id.searchBySpinner);
        spinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.search_array, android.R.layout.simple_spinner_dropdown_item));

        ((Button) findViewById(R.id.searchButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseManager.getProductList(new DatabaseManager.OnGetProductListListener() {
            @Override
            public void onGetProductList(ArrayList<Product> productList2) {
                if (productList.size() > 0) productList.clear();
                productList.addAll(productList2);
                search();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutButton) {
            Intent intent = new Intent(RestockingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("displayList", displayList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        productList = savedInstanceState.getParcelableArrayList("displayList");
        if (displayList == null)
            displayList = new ArrayList<>();
        if (productList == null)
            productList = new ArrayList<>();
        adapter = new ProductListAdapter(this, R.layout.product, displayList);
        ((ListView) findViewById(R.id.productList)).setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent result) {
        if (reqCode == 0 && resCode == Activity.RESULT_OK) {
            int index = result.getIntExtra("index", -1);
            if (index >= 0 && index < displayList.size()) {
                Product product = displayList.get(index);
                displayList.remove(product);
                productList.remove(product);
                //productList.remove(index);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void search() {
        Editable queryField = ((EditText) findViewById(R.id.searchField)).getText();
        String query;
        if (TextUtils.isEmpty(queryField)) {
            displayList.clear();
            displayList.addAll(productList);
        } else {
            displayList.clear();
            query = queryField.toString();
            switch(spinner.getSelectedItem().toString()) {
                case "Product Name":
                    for (Product product : productList)
                        if (product.getProductName().contains(query))
                            displayList.add(product);
                    break;
                case "Manufacturer":
                    for (Product product : productList)
                        if (product.getManufacturer().contains(query))
                            displayList.add(product);
                    break;
                case "Description":
                    for (Product product : productList)
                        if (product.getDescription().contains(query))
                            displayList.add(product);
                    break;
                default:
                    System.exit(1);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class ProductListAdapter extends ArrayAdapter<Product> {
        public ProductListAdapter(Context context, int resource, ArrayList<Product> products) { super(context, resource, products); }

        @Override
        public Product getItem(int position) { return displayList.get(position); }

        @Override
        @NonNull
        public View getView(int position, View view, @NonNull ViewGroup listView) {
            if (view == null)  {
                view = getLayoutInflater().inflate(R.layout.product, listView, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RestockingActivity.this, ProductStockActivity.class);
                        int index = (int) view.getTag();
                        intent.putExtra("product", getItem(index));
                        intent.putExtra("index", index);
                        startActivityForResult(intent, 0);
                    }
                });
            }
            view.setTag(position);
            ((TextView) view.findViewById(R.id.listName)).setText(getItem(position).getProductName());
            ((TextView) view.findViewById(R.id.listManufacturer)).setText("by " + getItem(position).getManufacturer());
            return view;
        }
    }
}
