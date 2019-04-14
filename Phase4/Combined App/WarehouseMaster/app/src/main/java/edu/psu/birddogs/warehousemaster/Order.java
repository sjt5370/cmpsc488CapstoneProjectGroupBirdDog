package edu.psu.birddogs.warehousemaster;

import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Set;

public class Order extends AppCompatActivity {
    private HashMap<Integer, PickProduct> pallet;
    private boolean firstTime = true;
    private boolean finishing = false;
    public static int returnedId = -1;
    private Set<Integer> prods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        DatabaseManager.getNewPallet(new DatabaseManager.OnGetPalletListener(){
            @Override
            public void onGetPallet(HashMap<Integer, PickProduct> pallet2){
                if (pallet2.size() == 0) {
                    Toast.makeText(Order.this, getResources().getString(R.string.no_palettes), Toast.LENGTH_LONG).show();
                    Order.this.finish();
                }
                pallet = pallet2;
                //set list of checkboxes
                final LinearLayout lm = (LinearLayout) findViewById(R.id.layout);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                if(firstTime) {
                    prods = pallet.keySet();
                    for(Integer prod : prods){
                        CheckBox c = new CheckBox(Order.this);
                        TextView v = new TextView(Order.this);

                        String txt = pallet.get(prod).getDescription() + '\n' + getResources().getString(R.string.manufacturer_label) + ": " + pallet.get(prod).getManu();
                        v.setText(txt);
                        v.setId(100000 + prod);
                        v.setPadding(100, 10, 5, 10);
                        v.setVisibility(View.GONE);

                        c.setText(pallet.get(prod).getID() + ": " + pallet.get(prod).getName() + "     x" + pallet.get(prod).getQuantity());
                        c.setId(prod);
                        c.setGravity(Gravity.CENTER);
                        c.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                check(v, false);
                            }
                        });
                        c.setClickable(false);


                        //c.setLayoutParams(params);
                        lm.addView(c);
                        lm.addView(v);
                    }
                    firstTime = false;
                }
            }

        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (returnedId != -1) {
            View v = findViewById(returnedId);
            if (v != null) check(v, true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finishing) for (int key : prods) {
            PickProduct product = pallet.get(key);
            if (product.getScanned())
                DatabaseManager.returnInventory(product.getID(), product.getQuantity());
        }
        finish();
    }

    private void check(View v, boolean b) {
        int j = v.getId();
        PickProduct product = pallet.get(j);
        ((CheckBox) v).setChecked(b);
        ((CheckBox) v).setClickable(b);

        j = j + 100000;
        if (b) {
            if (!product.getScanned())
                DatabaseManager.removeInventory(product.getID(), product.getQuantity());
            product.setScanned(true);
            returnedId = -1;
            ((TextView) findViewById(j)).setVisibility(View.VISIBLE);
            for (Integer prod : prods)
                if (!((CheckBox) findViewById(prod)).isChecked()) b = false;
        } else {
            if (product.getScanned())
                DatabaseManager.returnInventory(product.getID(), product.getQuantity());
            product.setScanned(false);
            ((TextView) findViewById(j)).setVisibility(View.GONE);
        }

        ((Button) findViewById(R.id.finishPalette)).setEnabled(b);
        ((Button) findViewById(R.id.holdButton)).setEnabled(!b);
        ((Button) findViewById(R.id.scanButton)).setEnabled(!b);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    public void openScan(View view){
        Intent intent = new Intent(this, Scan.class);
        startActivity(intent);
    }

    //shows confirmation dialog for Place on Hold and Finish Order button
    public void alert(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        if(view.getId() == R.id.finishPalette){
            alert.setTitle(getResources().getString(R.string.finish_palette));
            alert.setMessage(getResources().getString(R.string.finish_confirmation)).setCancelable(false).setPositiveButton(getResources().getString(R.string.confirm_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishing = true;
                    DatabaseManager.fulfillPallet(pallet);
                    Order.this.finish();
                }
            }).setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        } else if(view.getId() == R.id.holdButton) {
            alert.setTitle(getResources().getString(R.string.hold_palette));
            alert.setMessage(getResources().getString(R.string.hold_confirmation)).setCancelable(false).setPositiveButton(getResources().getString(R.string.confirm_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseManager.hold(pallet);
                    Order.this.finish();
                }
            }).setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }

        AlertDialog alertD = alert.create();
        alertD.show();
    }


}
