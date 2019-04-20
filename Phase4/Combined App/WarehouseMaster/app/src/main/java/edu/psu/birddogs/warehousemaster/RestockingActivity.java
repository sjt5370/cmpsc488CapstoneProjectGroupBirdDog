package edu.psu.birddogs.warehousemaster;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;

public class RestockingActivity extends AppCompatActivity {

    private ArrayList<StockProduct> stockProductList;
    private ArrayList<StockProduct> displayList;
    private ProductListAdapter adapter;
    private Spinner spinner;
    //private ArrayAdapter<CharSequence> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restocking);
        this.setTitle(getResources().getString(R.string.restocking_bar));
        stockProductList = new ArrayList<>();
        displayList = new ArrayList<>();

        adapter = new ProductListAdapter(this, R.layout.product, displayList);
        ((ListView) findViewById(R.id.stockProductList)).setAdapter(adapter);

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
            public void onGetProductList(ArrayList<StockProduct> stockProductList2) {
                if (stockProductList.size() > 0) stockProductList.clear();
                stockProductList.addAll(stockProductList2);
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
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(getResources().getString(R.string.logout_button));
            alert.setMessage(getResources().getString(R.string.logout_confirmation)).setCancelable(false).setPositiveButton(getResources().getString(R.string.confirm_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(RestockingActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }).setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertD = alert.create();
            alertD.show();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("stockProductList", stockProductList);
        savedInstanceState.putParcelableArrayList("displayList", displayList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        stockProductList = savedInstanceState.getParcelableArrayList("stockProductList");
        displayList = savedInstanceState.getParcelableArrayList("displayList");
        if (displayList == null)
            displayList = new ArrayList<>();
        if (stockProductList == null)
            stockProductList = new ArrayList<>();
        adapter = new ProductListAdapter(this, R.layout.product, displayList);
        ((ListView) findViewById(R.id.stockProductList)).setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int reqCode, int resCode, Intent result) {
        if (reqCode == 0 && resCode == Activity.RESULT_OK) {
            int index = result.getIntExtra("index", -1);
            if (index >= 0 && index < displayList.size()) {
                StockProduct stockProduct = displayList.get(index);
                displayList.remove(stockProduct);
                stockProductList.remove(stockProduct);
                //stockProductList.remove(index);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void search() {
        Editable queryField = ((EditText) findViewById(R.id.searchField)).getText();
        displayList.clear();
        if (TextUtils.isEmpty(queryField)) {
            displayList.addAll(stockProductList);
        } else {
            String query = queryField.toString();
            switch(spinner.getSelectedItem().toString()) {
                case "Product Name":
                    for (StockProduct stockProduct : stockProductList)
                        if (stockProduct.getProductName().toLowerCase().contains(query.toLowerCase()))
                            displayList.add(stockProduct);
                    break;
                case "Manufacturer":
                    for (StockProduct stockProduct : stockProductList)
                        if (stockProduct.getManufacturer().toLowerCase().contains(query.toLowerCase()))
                            displayList.add(stockProduct);
                    break;
                case "Description":
                    for (StockProduct stockProduct : stockProductList)
                        if (stockProduct.getDescription().toLowerCase().contains(query.toLowerCase()))
                            displayList.add(stockProduct);
                    break;
                default:
                    System.exit(1);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class ProductListAdapter extends ArrayAdapter<StockProduct> {
        public ProductListAdapter(Context context, int resource, ArrayList<StockProduct> stockProducts) { super(context, resource, stockProducts); }

        @Override
        public StockProduct getItem(int position) { return displayList.get(position); }

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
                        intent.putExtra("stockProduct", getItem(index));
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
