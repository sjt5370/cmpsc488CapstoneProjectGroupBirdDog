package edu.psu.birddogs.warehousemaster;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DriverActivity extends AppCompatActivity {
    private HashMap<Integer, ArrayList<StopOrder>> routes;
    private ArrayList<Integer> keys;
    RouteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        setTitle(R.string.driver_bar);
        keys = new ArrayList<>();
        adapter = new RouteListAdapter(this, R.layout.route, keys);
        ((ListView) findViewById(R.id.listRoutes)).setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseManager.getRoutes(new DatabaseManager.OnGetRoutesListener() {
            @Override
            public void onGetRoutes(HashMap<Integer, ArrayList<StopOrder>> routes1) {
                if (keys.size() > 0) keys.clear();
                routes = routes1;
                keys.addAll(routes.keySet());
                adapter.notifyDataSetChanged();
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
                    Intent intent = new Intent(DriverActivity.this, LoginActivity.class);
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

    private class RouteListAdapter extends ArrayAdapter<Integer> {
        public RouteListAdapter(Context context, int resource, ArrayList<Integer> routes) { super(context, resource, routes); }

        @Override
        public Integer getItem(int position) { return keys.get(position); }

        @Override
        @NonNull
        public View getView(int position, View view, @NonNull ViewGroup listView) {
            if (view == null)  {
                view = getLayoutInflater().inflate(R.layout.route, listView, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DriverActivity.this, RouteActivity.class);
                        int index = (int) view.getTag();
                        intent.putExtra("index", index);
                        intent.putExtra("route", getItem(index));
                        intent.putExtra("orders", routes.get(getItem(index)));
                        startActivityForResult(intent, 0);
                    }
                });
            }
            view.setTag(position);
            ((TextView) view.findViewById(R.id.listRouteNum)).setText(getResources().getString(R.string.route_label) + " " + getItem(position));
            ((TextView) view.findViewById(R.id.listRouteQuantity)).setText(getResources().getString(R.string.route_quantity_label) + ": " + routes.get(getItem(position)).size());
            return view;
        }
    }
}
