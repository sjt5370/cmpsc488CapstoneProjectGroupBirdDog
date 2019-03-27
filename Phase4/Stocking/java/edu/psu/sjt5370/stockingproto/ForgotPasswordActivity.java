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

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Editable username = ((EditText) findViewById(R.id.emailField)).getText();
        Editable fname = ((EditText) findViewById(R.id.nameField)).getText();
        Editable lname = ((EditText) findViewById(R.id.surnameField)).getText();
        Editable id = ((EditText) findViewById(R.id.employeeIDField)).getText();
        Editable newpass = ((EditText) findViewById(R.id.newPasswordField)).getText();
        Editable checkpass = ((EditText) findViewById(R.id.repeatPasswordField)).getText();
        if (!TextUtils.isEmpty(username)) savedInstanceState.putString("username", username.toString());
        if (!TextUtils.isEmpty(fname)) savedInstanceState.putString("fname", fname.toString());
        if (!TextUtils.isEmpty(lname)) savedInstanceState.putString("lname", lname.toString());
        if (!TextUtils.isEmpty(id)) savedInstanceState.putString("id", id.toString());
        if (!TextUtils.isEmpty(newpass)) savedInstanceState.putString("newpass", newpass.toString());
        if (!TextUtils.isEmpty(checkpass)) savedInstanceState.putString("checkpass", checkpass.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String s;
        if ((s = savedInstanceState.getString("username")) != null) ((EditText) findViewById(R.id.emailField)).setText(s);
        if ((s = savedInstanceState.getString("fname")) != null) ((EditText) findViewById(R.id.nameField)).setText(s);
        if ((s = savedInstanceState.getString("lname")) != null) ((EditText) findViewById(R.id.surnameField)).setText(s);
        if ((s = savedInstanceState.getString("id")) != null) ((EditText) findViewById(R.id.employeeIDField)).setText(s);
        if ((s = savedInstanceState.getString("newpass")) != null) ((EditText) findViewById(R.id.newPasswordField)).setText(s);
        if ((s = savedInstanceState.getString("checkpass")) != null) ((EditText) findViewById(R.id.repeatPasswordField)).setText(s);
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
                        DatabaseManager.changePassword(username.toString(), fname.toString(), lname.toString(), Integer.parseInt(id.toString()), newpassS, new DatabaseManager.OnPasswordChangedListener() {
                            @Override
                            public void OnPasswordChanged(boolean passChanged) {
                                if (passChanged) {
                                    finish();
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.login_failed), Toast.LENGTH_LONG).show();
                                    authPending = false;
                                }
                            }
                        });
                    }
                } else {
                    //FIXME: Highlight textboxes as invalid
                    Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.empty_fields_response), Toast.LENGTH_LONG).show();
                    authPending = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }
    }
}
