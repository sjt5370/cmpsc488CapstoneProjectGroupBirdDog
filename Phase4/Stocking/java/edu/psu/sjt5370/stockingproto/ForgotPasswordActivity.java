package edu.psu.sjt5370.stockingproto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {

    private boolean authPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        authPending = false;

        ((Button) findViewById(R.id.backButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        ((Button) findViewById(R.id.changePasswordButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        if (!authPending) {
            authPending = true;
            try {
                Editable username = ((EditText) findViewById(R.id.emailField)).getText();
                Editable fname = ((EditText) findViewById(R.id.nameField)).getText();
                Editable lname = ((EditText) findViewById(R.id.surnameField)).getText();
                Editable id = ((EditText) findViewById(R.id.employeeIDField)).getText();
                Editable newpass = ((EditText) findViewById(R.id.newPasswordField)).getText();
                Editable checkpass = ((EditText) findViewById(R.id.repeatPasswordField)).getText();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname)
                        && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(newpass) && !TextUtils.isEmpty(checkpass)) {
                    String newpassS = newpass.toString();
                    String checkpassS = checkpass.toString();

                    if (!newpassS.equals(checkpassS)) {
                        Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.password_match_failed), Toast.LENGTH_LONG).show();
                        authPending = false;
                    } else {
                        /*
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
                        */
                    }
                }

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
