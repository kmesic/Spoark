package com.mesicspoark.kenan.spoark;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Pattern;

/**
 * Created by kenme_000 on 11/16/2015.
 */
public class NewUserActivity extends AppCompatActivity {
    private EditText confirmPassword;
    private EditText password;
    private EditText email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_user);

        //Change color of the status bar on top...this only works in android sdk 21+
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }

        confirmPassword = (EditText) findViewById(R.id.supConfirmpass);
        password = (EditText) findViewById(R.id.suppass);
        email = (EditText) findViewById(R.id.supemail);

        confirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == getResources().getInteger(R.integer.loginIMEActionId) ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    createAccount();
                    return true;
                }
                return false;
            }
        });

        Button createAccountButton = (Button) findViewById(R.id.btnCreateAccount);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String currentEmailText = email.getText().toString().trim();
        String currentPassText = password.getText().toString().trim();
        String currentConfirmPassText = confirmPassword.getText().toString().trim();

        boolean error = false;
        StringBuilder errorMessage = new StringBuilder(getString(R.string.errorMessage));
        //Add error check for email password
        if(!currentPassText.equals(password.getText().toString())) {
            if(error) {
                errorMessage.append(getString(R.string.errorJoin));
            }
            error = true;
            errorMessage.append(getString(R.string.errorSpacesPass));
        }

        if(!currentConfirmPassText.equals(confirmPassword.getText().toString())) {
            if(error) {
                errorMessage.append(getString(R.string.errorJoin));
            }
            error = true;
            errorMessage.append(getString(R.string.errorSpacesConfirm));
        }

        if(currentPassText.length() == 0) {
            if(error) {
                errorMessage.append(getString(R.string.errorJoin));
            }
            error = true;
            errorMessage.append(getString(R.string.errorBlankPass));
        }

        if(currentEmailText.length() == 0) {
            if(error) {
                errorMessage.append(getString(R.string.errorJoin));
            }
            error = true;
            errorMessage.append(getString(R.string.errorBlankEmail));
        }
        else{
            String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
            Pattern pattern = Pattern.compile(regex);
            if(!pattern.matcher(currentEmailText).matches()) {
                if(error) {
                    errorMessage.append(getString(R.string.errorJoin));
                }
                error = true;
                errorMessage.append(getString(R.string.errorInvalidEmail));
            }
        }

        if(!currentPassText.equals(currentConfirmPassText)) {
            if(error) {
                errorMessage.append(getString(R.string.errorJoin));
            }
            error = true;
            errorMessage.append(getString(R.string.errorMisMatchedPass));
        }
        errorMessage.append(getString(R.string.errorEnd));

        if(error) {
            Toast.makeText(NewUserActivity.this, errorMessage.toString(), Toast.LENGTH_LONG).show();
            return;
        }
        //Set up the Parse user
        ParseUser user = new ParseUser();
        user.setUsername(currentEmailText);
        user.setPassword(currentPassText);
        user.setEmail(currentEmailText);

        final ProgressDialog dialog = new ProgressDialog(NewUserActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                //Handle response
                dialog.dismiss();
                if (e != null) {
                    Toast.makeText(NewUserActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(NewUserActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
