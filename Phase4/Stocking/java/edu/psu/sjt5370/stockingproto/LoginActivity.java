package edu.psu.sjt5370.stockingproto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {  //FIXME: All back buttons should be custom to avoid restarting activities

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

    private void authenticate() {
        if (!authPending) {
            authPending = true;
            try {
                Editable username = ((EditText) findViewById(R.id.emailField)).getText();
                Editable password = ((EditText) findViewById(R.id.passwordField)).getText();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password))
                    DatabaseManager.authenticate(username.toString(), password.toString(), new DatabaseManager.OnAuthenticationListener() {
                        @Override
                        public void onAuthentication(boolean authGranted) {
                            if (authGranted) {
                                Intent intent = new Intent(LoginActivity.this, StockingActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                //FIXME: Highlight textboxes as invalid
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                            }
                            authPending = false;
                        }
                    });
                else {
                    //FIXME: Highlight textboxes as invalid
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                    authPending = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}
