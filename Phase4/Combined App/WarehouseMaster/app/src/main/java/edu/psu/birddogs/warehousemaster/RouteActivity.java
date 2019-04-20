package edu.psu.birddogs.warehousemaster;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

public class RouteActivity extends AppCompatActivity {
    private int index;
    private int route;
    private ArrayList<StopOrder> allOrders;
    private ArrayList<StopOrder> displayOrders;
    private int stop;
    private OrderListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ((Button) findViewById(R.id.nextStopButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(RouteActivity.this);
                alert.setTitle(getResources().getString(R.string.next_stop_button));
                alert.setMessage(getResources().getString(R.string.next_stop_confirmation)).setCancelable(false).setPositiveButton(getResources().getString(R.string.confirm_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nextStop();
                    }
                }).setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.create().show();
            }
        });
        Intent intent = getIntent();
        stop = -1;
        index = intent.getIntExtra("index", -1);
        route = intent.getIntExtra("route", -1);
        allOrders = intent.getParcelableArrayListExtra("orders");
        if (allOrders == null) allOrders = new ArrayList<>();
        allOrders.sort(new Comparator<StopOrder>() {
            @Override
            public int compare(StopOrder o1, StopOrder o2) {
                return o1.stopNum - o2.stopNum;
            }
        });
        displayOrders = new ArrayList<>();
        adapter = new OrderListAdapter(this, R.layout.stop_order, displayOrders);
        ((ListView) findViewById(R.id.listOrders)).setAdapter(adapter);
        setTitle(getResources().getString(R.string.route_label) + " " + route);
        nextStop();
    }

    private void nextStop() {   // Complete marked orders and display next stop's orders
        if (stop != -1) {
            for (StopOrder order : displayOrders) {
                if (order.complete) DatabaseManager.completeOrder(order.orderNum);
                else DatabaseManager.delayOrder(order.orderNum);
            }
            displayOrders.clear();
        }
        int prev = stop;
        for (int i = 0; i < allOrders.size() && prev == stop; i++)
            if (allOrders.get(i).stopNum > prev) stop = allOrders.get(i).stopNum;
        if (prev == stop) {
            setResult(Activity.RESULT_OK, new Intent().putExtra("index", index));
            finish();
        } else {
            ((TextView) findViewById(R.id.stopLabel)).setText(getResources().getString(R.string.stop_label) + " " + stop);
            for (StopOrder order : allOrders) {
                if (order.stopNum == stop) displayOrders.add(order);
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList("allOrders", allOrders);
        savedInstanceState.putParcelableArrayList("displayOrders", displayOrders);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        allOrders = savedInstanceState.getParcelableArrayList("allOrders");
        displayOrders = savedInstanceState.getParcelableArrayList("displayOrders");
        if (displayOrders == null)
            displayOrders = new ArrayList<>();
        if (allOrders == null)
            allOrders = new ArrayList<>();
        adapter = new OrderListAdapter(this, R.layout.stop_order, displayOrders);
        ((ListView) findViewById(R.id.listOrders)).setAdapter(adapter);
    }

    @Override
    public void onBackPressed() { moveTaskToBack(false); }

    private class OrderListAdapter extends ArrayAdapter<StopOrder> {
        public OrderListAdapter(Context context, int resource, ArrayList<StopOrder> orders) { super(context, resource, orders); }

        @Override
        public StopOrder getItem(int position) { return displayOrders.get(position); }

        @Override
        @NonNull
        public View getView(int position, View view, @NonNull ViewGroup listView) {
            if (view == null)  {
                view = getLayoutInflater().inflate(R.layout.stop_order, listView, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CheckBox c = (CheckBox) view.findViewById(R.id.listOrderCheck);
                        boolean b = c.isChecked();
                        int index = (int) view.getTag();
                        getItem(index).complete = !b;
                        c.setChecked(!b);
                    }
                });
            }
            view.setTag(position);
            ((CheckBox) view.findViewById(R.id.listOrderCheck)).setChecked(getItem(position).complete);
            ((TextView) view.findViewById(R.id.listOrderNum)).setText(getResources().getString(R.string.order_label) + " " + getItem(position).orderNum);
            return view;
        }
    }
}
