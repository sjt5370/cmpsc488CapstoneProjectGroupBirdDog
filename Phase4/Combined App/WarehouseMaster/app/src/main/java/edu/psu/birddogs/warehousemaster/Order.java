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

import java.util.HashMap;
import java.util.Set;

public class Order extends AppCompatActivity {
    private HashMap<Integer, PickProduct> pallet;
    private boolean firstTime = true;
    public static int returnedId = -1;
    private Set<Integer> prods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        DatabaseManager.getNewPallet(new DatabaseManager.OnGetPalletListener(){
            @Override
            public void onGetPallet(HashMap<Integer, PickProduct> pallet2){
                pallet = pallet2;
                //set list of checkboxes
                final LinearLayout lm = (LinearLayout) findViewById(R.id.layout);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

                if(firstTime) {
                    prods = pallet.keySet();
                    int i = 0;
                    for(Integer prod : prods){
                        CheckBox c = new CheckBox(Order.this);
                        TextView v = new TextView(Order.this);

                        String txt = pallet.get(prod).getDescription() + "\nManufacturer: " + pallet.get(prod).getManu();
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
                                check(v, true);     //FIXME: Change to false for final version
                            }
                        });
                        //c.setClickable(false);        FIXME: Uncomment for final version


                        //c.setLayoutParams(params);
                        lm.addView(c);
                        lm.addView(v);
                        i++;
                    }
                    firstTime = false;
                }
            }

        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (returnedId != -1) check(findViewById(returnedId), true);
    }

    private void check(View v, boolean b) {
        int j = v.getId();
        j = j + 100000;
        ((CheckBox) v).setChecked(b);
        ((CheckBox) v).setClickable(b);
        if (b) {
            returnedId = -1;
            ((TextView) findViewById(j)).setVisibility(View.VISIBLE);
            for (Integer prod : prods)
                if (!((CheckBox) findViewById(prod)).isChecked()) b = false;
        } else ((TextView) findViewById(j)).setVisibility(View.GONE);

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
            alert.setTitle("Finish Palette and Exit?");
            alert.setMessage("").setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseManager.fulfillPallet(pallet);
                    Order.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        } else if(view.getId() == R.id.holdButton) {
            alert.setTitle("Place Palette on Hold and Exit?");
            alert.setMessage("").setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseManager.hold(pallet);
                    pallet = new HashMap<>();
                    Order.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
