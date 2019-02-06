package com.birddogs.picking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //opens order page when Next Order Button is clicked
    public void openOrder(View view){
        Intent intent = new Intent(this, Order.class);
        startActivity(intent);
    }


}
