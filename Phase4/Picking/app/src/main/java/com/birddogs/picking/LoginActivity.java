package com.birddogs.picking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    public static final String LOGIN_MESSAGE = "com.birddogs.picking.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    //opens Main Page when Login Button is clicked
    public void login(View view){
        Intent intent = new Intent(this, MainActivity.class);
        EditText editText = (EditText) findViewById(R.id.employeeID);
        String message = editText.getText().toString();
        intent.putExtra(LOGIN_MESSAGE, message);
        startActivity(intent);
    }
}
