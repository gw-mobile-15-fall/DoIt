/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class DoItParse extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

    // Add your initialization code here
    Parse.initialize(this, "E6nml5ahUWOBUMy56TFFOQvqjtcCAhsSGRr0L4gD", "7GMmQNtklSapF6cJO1sxkDPjTCdGj5X62Z2nAmR9");




      ParseInstallation.getCurrentInstallation().saveInBackground();


            ParseUser.enableAutomaticUser();
            ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);
      ParseInstallation installation = ParseInstallation.getCurrentInstallation();
      installation.put("user",ParseUser.getCurrentUser());
      installation.saveInBackground();
      ParseInstallation.getCurrentInstallation().saveInBackground();
      /*ParsePush.subscribeInBackground("", new SaveCallback() {
          @Override
          public void done(ParseException e) {
              if (e == null) {
                  Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");

              } else {
                  Log.e("com.parse.push", "failed to subscribe for push", e);
              }
          }
      });*/


      ParseObject testObject = new ParseObject("TestObject");
    testObject.put("foo", "bar");
    testObject.saveInBackground();

  }
}
