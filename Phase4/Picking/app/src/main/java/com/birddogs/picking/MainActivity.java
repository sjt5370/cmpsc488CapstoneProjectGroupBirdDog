package com.birddogs.picking;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String message = intent.getStringExtra(LoginActivity.LOGIN_MESSAGE);
        TextView textView = findViewById(R.id.textView);

        if(!message.isEmpty()) {
            textView.setText(message);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    //opens order page when Next Order Button is clicked
    public void openOrder(View view){
        Intent intent = new Intent(this, Order.class);
        startActivity(intent);
    }

    //shows confirmation dialog for logout
    public void logout(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Are you sure you want to Logout?");

        alert.setMessage("").setCancelable(false).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
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
