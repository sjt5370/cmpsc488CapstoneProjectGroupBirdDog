package com.birddogs.picking;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    public static final String LOGIN_MESSAGE = "com.birddogs.picking.MESSAGE";
    private HashMap<String, User> users;
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
        EditText id = (EditText) findViewById(R.id.employeeID);
        EditText pass = (EditText) findViewById(R.id.employeePassword);

        //checks users
        final String u = id.getText().toString();
        final String p = pass.getText().toString();

        users = new HashMap<>();
        DatabaseInterface.getUsers(new DatabaseInterface.OnGetUsersListener(){
            @Override
            public void onGetUsers(HashMap<String, User> users2){
                users = users2;
                if(users.containsKey(u)){
                    if(p.equals(users.get(u).getPassword())){
                        if(users.get(u).getJob().equals("picker")) {
                            String n = users.get(u).getFirst_name() + " " + users.get(u).getLast_name();
                            intent.putExtra(LOGIN_MESSAGE, n);
                            startActivity(intent);
                        }
                    }
                }
            }
        });
    }
}
