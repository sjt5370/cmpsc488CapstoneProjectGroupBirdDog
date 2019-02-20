package edu.psu.sjt5370.stockingproto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ReceivingActivityOld extends AppCompatActivity {

    ArrayList<Product> exampleProducts;
    ProductListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving);
        this.setTitle(getResources().getString(R.string.receiving_bar));
        exampleProducts = new ArrayList<>();
        for (int i = 0; i < 20; i++) exampleProducts.add(new Product());
        adapter = new ProductListAdapter(this, R.layout.product, exampleProducts);
        ((ListView) findViewById(R.id.productList)).setAdapter(adapter);
        ((Button) findViewById(R.id.pickUpButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                while(exampleProducts.size() > 0) {
                    exampleProducts.remove(0);
                    adapter.notifyDataSetChanged();
                }
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
            Intent intent = new Intent(ReceivingActivityOld.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("productList", exampleProducts);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        exampleProducts = savedInstanceState.getParcelableArrayList("productList");
        if (exampleProducts == null)
            exampleProducts = new ArrayList<>();
        adapter = new ProductListAdapter(this, R.layout.product, exampleProducts);
        ((ListView) findViewById(R.id.productList)).setAdapter(adapter);
    }

    private class ProductListAdapter extends ArrayAdapter<Product> {
        public ProductListAdapter(Context context, int resource, ArrayList<Product> products) { super(context, resource, products); }

        @Override
        public Product getItem(int position) { return exampleProducts.get(position); }

        @Override
        @NonNull
        public View getView(int position, View view, @NonNull ViewGroup listView) {
            if (view == null)  {
                view = getLayoutInflater().inflate(R.layout.product, listView, false);
            }
            view.setTag(position);
            ((TextView) view.findViewById(R.id.listName)).setText(getItem(position).getProductName());
            return view;
        }
    }
}

