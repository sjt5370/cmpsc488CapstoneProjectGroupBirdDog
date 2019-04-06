package com.birddogs.picking;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    public static final String LOGIN_MESSAGE = "com.birddogs.picking.MESSAGE";
    private User user;
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
        final Intent intent = new Intent(this, MainActivity.class);
        final EditText id = (EditText) findViewById(R.id.employeeID);
        final EditText pass = (EditText) findViewById(R.id.employeePassword);
        final TextView error = (TextView) findViewById(R.id.LoginError);

        //checks users
        final String u = id.getText().toString();
        final String p = pass.getText().toString();

        user = new User();
        DatabaseInterface.getUsers(new DatabaseInterface.OnGetUsersListener(){
            @Override
            public void onGetUsers(User user2){
                user = user2;
                if(user != null) {
                    if (user.getPassword().equals(p)) {
                        if (user.getJob().equals("picker")) {
                            pass.setText("");
                            String n = user.getFirst_name() + " " + user.getLast_name();
                            intent.putExtra(LOGIN_MESSAGE, n);
                            startActivity(intent);
                        }else{
                            error.setText("You are not authorized to use this app");
                            error.setVisibility(View.VISIBLE);
                        }
                    }else{
                        error.setText("Invalid Password");
                        error.setVisibility(View.VISIBLE);
                    }
                }else{
                    error.setText("Please enter a valid Username");
                    error.setVisibility(View.VISIBLE);
                }
            }
        }, u);
    }
}
