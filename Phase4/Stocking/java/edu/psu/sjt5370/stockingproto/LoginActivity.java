package edu.psu.sjt5370.stockingproto;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {  //FIXME: All back buttons should be custom to avoid restarting activities

    //private Connection database;
    private boolean authPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authPending = false;
        new DatabaseManager().addBasics();
        setContentView(R.layout.activity_login);
        ((Button) findViewById(R.id.forgotPasswordButton)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.forgot_password_response), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        ((Button) findViewById(R.id.loginButton)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        authenticate();
                        //FIXME: Add loading gif
                    }
                }
        );
    }
/*
    @Override
    protected void onResume() {
        super.onResume();

        new DatabaseManager().getDBConnection(new DatabaseManager.OnDatabaseReadyListener() {
            @Override
            public void onDatabaseReady(Connection db) {
                database = db;
                if (authPending) authenticate();
            }
        });
    }
*/
    private void authenticate() {
        if (!authPending) {
            authPending = true;
            new DatabaseManager().getDBConnection(new DatabaseManager.OnDatabaseReadyListener() {
                @Override
                public void onDatabaseReady(Connection db) {
                    try {
                        Editable id = ((EditText) findViewById(R.id.emailField)).getText();
                        Editable password = ((EditText) findViewById(R.id.passwordField)).getText();
                        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(password) && DatabaseManager.authenticate(Integer.parseInt(id.toString()), password.toString(), db)) {
                            Intent intent = new Intent(LoginActivity.this, StockingActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            //FIXME: Highlight textboxes as invalid
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.exit(1);
                    } finally {
                        authPending = false;
                        try {
                            db.close();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            System.exit(1);
                        }
                    }
                }
            });
        }
        /*
        if (database == null) {
            authPending = true;
            return;
        }
        authPending = false;
        Editable id = ((EditText) findViewById(R.id.emailField)).getText();
        Editable password = ((EditText) findViewById(R.id.passwordField)).getText();
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(password) && DatabaseManager.authenticate(Integer.parseInt(id.toString()), password.toString(), database)) {
            Intent intent = new Intent(LoginActivity.this, StockingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            try {
                database.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            finish();
        } else {
            //FIXME: Highlight textboxes as invalid
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        }
        */
    }
}
