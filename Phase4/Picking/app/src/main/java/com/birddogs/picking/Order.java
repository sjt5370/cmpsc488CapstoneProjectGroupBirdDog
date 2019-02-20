package com.birddogs.picking;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class Order extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    //checks that all products are scanned and enables/disables finish/hold buttons
    public void clicked(View view){
        //get checkboxes
        CheckBox p1 = (CheckBox) findViewById(R.id.product1);
        CheckBox p2 = (CheckBox) findViewById(R.id.product2);
        CheckBox p3 = (CheckBox) findViewById(R.id.product3);
        CheckBox p4 = (CheckBox) findViewById(R.id.product4);
        CheckBox p5 = (CheckBox) findViewById(R.id.product5);

        //get buttons
        Button finish = (Button) findViewById(R.id.finishOrder);
        Button hold = (Button) findViewById(R.id.holdButton);
        Button scan = (Button) findViewById(R.id.scanButton);

        //check for checked and update buttons
        if(p1.isChecked() && p2.isChecked() && p3.isChecked() && p4.isChecked() && p5.isChecked()){
            finish.setEnabled(true);
            hold.setEnabled(false);
            scan.setEnabled(false);
        }
        else{
            finish.setEnabled(false);
            hold.setEnabled(true);
            scan.setEnabled(true);
        }
    }

    public void openScan(View view){
        Intent intent = new Intent(this, Scan.class);
        startActivity(intent);
    }

    //shows confirmation dialog for Place on Hold and Finish Order button
    public void alert(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        if(view.getId() == R.id.finishOrder){
            alert.setTitle("Finish Order and Exit?");
        }
        else if(view.getId() == R.id.holdButton) {
            alert.setTitle("Place Order on Hold and Exit?");
        }

        alert.setMessage("").setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Order.this.finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alert.create();
        alertD.show();
    }


}
