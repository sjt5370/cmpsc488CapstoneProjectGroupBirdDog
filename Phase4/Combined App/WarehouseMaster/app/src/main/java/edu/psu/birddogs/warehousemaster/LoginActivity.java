package edu.psu.birddogs.warehousemaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.DriverManager;

public class LoginActivity extends AppCompatActivity {

    public static final String LOGIN_MESSAGE = "edu.psu.birddogs.warehousemaster.MESSAGE";
    private boolean authPending;
    private static String u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authPending = false;
        setContentView(R.layout.activity_login);
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
                    }
                }
        );
        ((Button) findViewById(R.id.settingsButton)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView warning = (TextView) findViewById(R.id.databaseWarning);
        if (PreferenceManager.getDefaultSharedPreferences(this).getString("server", null) == null)
            warning.setVisibility(View.VISIBLE);
        else warning.setVisibility(View.INVISIBLE);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        StringBuilder url = new StringBuilder();
        url.append("jdbc:jtds:sqlserver://");
        url.append(prefs.getString("server", getResources().getString(R.string.pref_default_server)));
        url.append(":");
        url.append(prefs.getString("port", getResources().getString(R.string.pref_default_port)));
        url.append(";databaseName=");
        url.append(prefs.getString("database", getResources().getString(R.string.pref_default_database)));
        url.append(";user=");
        url.append(prefs.getString("username", getResources().getString(R.string.pref_default_username)));
        url.append(";password=");
        url.append(prefs.getString("password", getResources().getString(R.string.pref_default_password)));
        url.append(";sslProtocol=");
        url.append(prefs.getString("protocol", getResources().getString(R.string.pref_default_protocol)));
        DatabaseManager.URL = url.toString();
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
                            if (authGranted && job.toLowerCase().equals("stocker")) {
                                Intent intent = new Intent(LoginActivity.this, StockingActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else if (authGranted && job.toLowerCase().equals("picker")) {
                                DatabaseManager.getUsers(new DatabaseManager.OnGetUsersListener() {
                                    @Override
                                    public void onGetUsers(User user) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        String n = user.getFirst_name() + " " + user.getLast_name();
                                        intent.putExtra(LOGIN_MESSAGE, n);
                                        startActivity(intent);
                                    }
                                }, u);
                            } else if (authGranted && job.toLowerCase().equals("driver")) {
                                Intent intent = new Intent(LoginActivity.this, DriverActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else if (authGranted) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_unauthorized), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                            }
                            authPending = false;
                        }
                    });
                } else {
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
