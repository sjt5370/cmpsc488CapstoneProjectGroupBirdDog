package com.birddogs.picking;

import android.content.Intent;
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

        //check for checked and update buttons
        if(p1.isChecked() && p2.isChecked() && p3.isChecked() && p4.isChecked() && p5.isChecked()){
            finish.setEnabled(true);
            hold.setEnabled(false);
        }
    }

    //returns user to Main Page
    public void previous(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
