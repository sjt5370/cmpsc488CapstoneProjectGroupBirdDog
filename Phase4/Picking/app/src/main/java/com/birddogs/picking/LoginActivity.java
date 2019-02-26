package com.birddogs.picking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    public static final String LOGIN_MESSAGE = "com.birddogs.picking.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    //opens Main Page when Login Button is clicked
    public void login(View view){
        Intent intent = new Intent(this, MainActivity.class);
        EditText id = (EditText) findViewById(R.id.employeeID);
        EditText pass = (EditText) findViewById(R.id.employeePassword);

        //checks users
        HashMap<String, String> users = DatabaseInterface.getUsers();
        String i = id.getText().toString();
        System.out.println(i);

        if(users.containsKey(i)){
            if(pass.getText().toString().equals(users.get(i))){
                String message = i;
                intent.putExtra(LOGIN_MESSAGE, message);
                startActivity(intent);
            }else{

            }
        }else{

        }



    }
}
