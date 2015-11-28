
package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginOrSignupActivity extends Activity {

    private static final String TYPE = "type";
    private static final String LOGIN = "Log In";
    private static final String SIGNUP = "Sign Up";

    private Button mLoginButton;
    private Button mSignupButton;
    private EditText mEmail;
    private EditText mPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_signup);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mSignupButton = (Button) findViewById(R.id.creatAccountButton);
        mEmail = (EditText) findViewById(R.id.emailLogin);
        mPass = (EditText) findViewById(R.id.passLogin);


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrSignupActivity.this, StarterActivity.class);
                intent.putExtra(TYPE, LOGIN);
                ParseUser.logInInBackground("omar", "1234567",
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    Intent intent = new Intent(
                                            LoginOrSignupActivity.this,
                                            ProfileActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),
                                            getResources().getString(R.string.Logged_in),
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            getResources().getString(R.string.no_user),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the text entered from the EditText
                String usernametxt = mEmail.getText().toString();
                String passwordtxt = mPass.getText().toString();

                // Force user to fill up the form
                if (usernametxt.equals("") && passwordtxt.equals("")) {
                    Toast.makeText(getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    ParseUser user = new ParseUser();
                    user.setUsername(usernametxt);
                    user.setPassword(passwordtxt);
                    user.put("name", usernametxt);
                    user.put("bio", "New Goal Achevier!");

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Show a simple Toast message upon successful registration
                                Toast.makeText(getApplicationContext(),
                                        "Successfully Signed up, please log in.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Sign up Error", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }

            }
        });


        ParseAnalytics.trackAppOpenedInBackground(getIntent());


    }

}
