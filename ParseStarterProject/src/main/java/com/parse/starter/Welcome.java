package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

public class Welcome extends Activity {

    // Declare Variable
    Button mlogout;
    Button mBrowse;
    ListView mGoalsList;
    String goal;
    List mUserGoals;
    private ArrayAdapter<String> listAdapter ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.activity_welcome);

        // Retrieve current user from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) findViewById(R.id.welcome);
        mGoalsList = (ListView) findViewById(R.id.goals_list);
        List goals = getUserGoals();


       /* ParseObject Java = new ParseObject("Goals");
        ParseObject pizza = new ParseObject("Goals");
        Java.put("name","How to learn Android Dev!?");
        Java.addAllUnique("steps", Arrays.asList("Install Eclipse from: https://eclipse.org/downloads/", "watch this video: bla bla"));
        pizza.put("name","How to cook pizza!?");
        pizza.addAllUnique("steps", Arrays.asList("Go to Walmart!", "buy frozen pizza","cook it! Easy!"));
        Java.saveInBackground();
        pizza.saveInBackground();
        */
        // Set the currentUser String into TextView

        txtuser.setText("You are logged in as " + struser);
        txtuser.setText("You are logged in as " + struser);

        // Locate Button in welcome.xml
        mlogout = (Button) findViewById(R.id.log_out);
        mBrowse= (Button) findViewById(R.id.browse);
        // Logout Button Click Listener
        mlogout.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                ParseUser.logOut();
                finish();
            }
        });

        mBrowse.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent goals = new Intent(Welcome.this,ListGoals.class);
                Welcome.this.startActivityForResult(goals, 1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("goal");
                Log.d("I got goal: " , result);
                addGoal(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void addGoal( String g){

        goal = g;


        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersGoals");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    Log.d("users:", "Retrieved " + userList.size() + " users");
                    if (userList.size() == 0) {
                        ParseObject obj = new ParseObject("UsersGoals");
                        obj.put("user", ParseUser.getCurrentUser());


                        obj.put("userName", ParseUser.getCurrentUser().getUsername());
                        obj.addAllUnique("GoalsList", Arrays.asList(Welcome.this.goal));
                        obj.saveInBackground();
                        Log.d("Added:", ParseUser.getCurrentUser().getUsername() + "  with  " + Welcome.this.goal);

                    } else {
                        ParseObject obj = userList.get(0);
                        List l = (List) obj.get("GoalsList");
                        Log.d("found user goals", l.size() + "");
                        l.add(Welcome.this.goal);
                        obj.addAllUnique("GoalsList", l);
                        obj.saveInBackground();
                        Log.d("Added:", ParseUser.getCurrentUser().getUsername() + "  with  " + Welcome.this.goal);

                    }
                } else {

                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });


        Log.d("Added:", ParseUser.getCurrentUser().getUsername() + "//  with  //" + Welcome.this.goal);

    }
    public List getUserGoals(){
        List l;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersGoals");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    ParseObject obj = userList.get(0);

                    mUserGoals = (List) obj.get("GoalsList");
                    Log.d("found user goals", mUserGoals.size() + "");
                    mBrowse= (Button) findViewById(R.id.browse);
                    mBrowse.setText(mUserGoals.size() +"");

                } else {
                    Log.d("score", "Error: " + e.getMessage());

                }

            }
        });

    return  mUserGoals;
    }



}