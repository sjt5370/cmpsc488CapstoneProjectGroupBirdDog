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
                                clicked(v, pallet);
                            }
                        });


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
        if(returnedId != -1){
            DatabaseManager.removeInventory(returnedId, pallet.get(returnedId).getQuantity());
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    //checks that all products are scanned and enables/disables finish/hold buttons
    public void clicked(View view, HashMap<Integer, PickProduct> pallet){
        //get buttons
        Button finish = (Button) findViewById(R.id.finishPalette);
        Button hold = (Button) findViewById(R.id.holdButton);
        Button scan = (Button) findViewById(R.id.scanButton);
        int j = view.getId();
        j = j + 100000;
        TextView v = (TextView) findViewById(j);
        boolean fin = true;

        if(v.getVisibility() == View.VISIBLE){
            v.setVisibility(View.GONE);
        }else {
            v.setVisibility(View.VISIBLE);
        }

        for(Integer prod : prods){
            CheckBox c = findViewById(prod);
            if(!c.isChecked()){
                fin = false;
            }
        }
        CheckBox c = (CheckBox) view;
        int i = c.getId();
        System.out.println(i);
        if(c.isChecked()) {
            //temporary solution to camera emulation
            //c.setChecked(false);
            DatabaseManager.removeInventory(i, pallet.get(i).getQuantity());
            //end temp
        }else{
            c.setChecked(true);
        }

        //check for checked and update buttons
        if(fin){
            finish.setEnabled(true);
            hold.setEnabled(false);
            scan.setEnabled(false);
        }
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
                    DatabaseManager.fulfillPallet();
                    Order.this.finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        else if(view.getId() == R.id.holdButton) {
            alert.setTitle("Place Palette on Hold and Exit?");
            alert.setMessage("").setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseManager.returnInventory(pallet);
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
