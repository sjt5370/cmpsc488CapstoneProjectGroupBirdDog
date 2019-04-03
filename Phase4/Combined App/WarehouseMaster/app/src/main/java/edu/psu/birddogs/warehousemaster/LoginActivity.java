package edu.psu.birddogs.warehousemaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {  //FIXME: All back buttons should be custom to avoid restarting activities

    public static final String LOGIN_MESSAGE = "edu.psu.birddogs.warehousemaster.MESSAGE";
    private boolean authPending;
    private static String u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authPending = false;
        setContentView(R.layout.activity_login);
        //((ImageView) findViewById(R.id.titleImage)).setImageDrawable(getResources().getDrawable(R.drawable.we));
        ((Button) findViewById(R.id.forgotPasswordButton)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    u = username.toString();
                    DatabaseManager.authenticate(username.toString(), password.toString(), new DatabaseManager.OnAuthenticationListener() {
                        @Override
                        public void onAuthentication(boolean authGranted, String job) {
                            if (authGranted && job.equals("stocker")) {
                                Intent intent = new Intent(LoginActivity.this, StockingActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else if (authGranted && job.equals("picker")) {
                                DatabaseManager.getUsers(new DatabaseManager.OnGetUsersListener() {
                                    @Override
                                    public void onGetUsers(User user) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        String n = user.getFirst_name() + " " + user.getLast_name();
                                        intent.putExtra(LOGIN_MESSAGE, n);
                                        startActivity(intent);
                                    }
                                }, u);
                            } else if (authGranted) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_unauthorized), Toast.LENGTH_LONG).show();
                            } else {
                                //FIXME: Highlight textboxes as invalid
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                            }
                            authPending = false;
                        }
                    });
                } else {
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
