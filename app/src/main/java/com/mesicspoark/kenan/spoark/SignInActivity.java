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

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Pattern;


/**
 * Created by kenmesi on 9/3/15.
 */
public class SignInActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        //Change color of the status bar on top...this only works in android sdk 21+
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }

        email = (EditText) findViewById(R.id.sinemail);
        password = (EditText) findViewById(R.id.sinpass);
        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == getResources().getInteger(R.integer.loginIMEActionId) ||
                   actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    login();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.btnSingIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                login();
            }
        });

        TextView newUser = (TextView) findViewById(R.id.suplink);
        newUser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, NewUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {
        String currentEmailText = email.getText().toString().trim();
        String currentPassText = password.getText().toString().trim();

        boolean error = false;
        StringBuilder errorMessage = new StringBuilder(getString(R.string.errorMessage));
        //Add error check for email password
        if(currentPassText.length() == 0) {
            if(error) {
                errorMessage.append(getString(R.string.errorJoin));
            }
            error = true;
            errorMessage.append(getString(R.string.errorBlankPass));
        }

        if(!currentPassText.equals(password.getText().toString())) {
            if(error) {
                errorMessage.append(getString(R.string.errorJoin));
            }
            error = true;
            errorMessage.append(getString(R.string.errorSpacesPass));
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
        errorMessage.append(getString(R.string.errorEnd));

        if(error) {
            Toast.makeText(SignInActivity.this, errorMessage.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(SignInActivity.this);
        dialog.setMessage(getString(R.string.progress_login));
        dialog.show();
        ParseUser.logInInBackground(currentEmailText, currentPassText, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                //Handle response
                dialog.dismiss();
                if (e != null) {
                    Toast.makeText(SignInActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
