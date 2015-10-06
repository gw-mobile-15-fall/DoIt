/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseAnalytics;


public class LoginOrSignupActivity extends Activity {

  public static final String TYPE = "type";
  public static final String LOGIN = "Log In";
  public static final String SIGNUP = "Sign Up";

  protected Button mLoginButton;
  protected Button mSignupButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_or_signup);

    mLoginButton = (Button) findViewById(R.id.loginButton);
    mSignupButton = (Button) findViewById(R.id.creatAccountButton);

    mLoginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(LoginOrSignupActivity.this, Starter.class);
        intent.putExtra(TYPE, LOGIN);
        startActivity(intent);
      }
    });

    mSignupButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(LoginOrSignupActivity.this, Starter.class);
        intent.putExtra(TYPE, SIGNUP);
        startActivity(intent);
      }
    });

    ParseAnalytics.trackAppOpenedInBackground(getIntent());


  }


}
