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

public class LoginActivity extends AppCompatActivity {  //FIXME: All back buttons should be custom to avoid restarting activities

    private SQLiteDatabase database;
    private boolean authPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authPending = false;
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

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseManager.getInstance(this).getWritableDatabase(new DatabaseManager.OnDatabaseReadyListener() {
            @Override
            public void onDatabaseReady(SQLiteDatabase db) {
                database = db;
                if (authPending) authenticate();
            }
        });
    }

    private void authenticate() {
        if (database == null) {
            authPending = true;
            return;
        }
        authPending = false;
        Editable id = ((EditText) findViewById(R.id.emailField)).getText();
        Editable password = ((EditText) findViewById(R.id.passwordField)).getText();
        if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(password) && DatabaseManager.getInstance(this).authenticate(Integer.parseInt(id.toString()), password.toString(), database)) {
            Intent intent = new Intent(LoginActivity.this, StockingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            if (database != null) database.close();
            finish();
        } else {
            //FIXME: Highlight textboxes as invalid
            Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
        }
    }
}
