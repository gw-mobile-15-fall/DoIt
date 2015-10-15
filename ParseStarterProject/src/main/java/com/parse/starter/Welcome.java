package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Welcome extends Activity {

    // Declare Variable
    Button mlogout;
    Button mBrowse;
    ListView mGoalsList;
    String goal;
    List mUserGoals;
    Context pointer;
    private ArrayAdapter listAdapter ;

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
        List goals = getUserGoals(this);


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
                Log.d("I got goal: ", result);
                addGoal(result);
                if(listAdapter == null) {

                }
                else {
                    listAdapter.add(result);

                    listAdapter.notifyDataSetChanged();

                }
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
                        mUserGoals = (List) obj.get("GoalsList");
                        if(mUserGoals != null)
                        Log.d("found user goals", mUserGoals.size() + "");
                        else {
                            Log.d("found user goals", "with 0 goals");
                            mUserGoals = new LinkedList();


                        }
                        mUserGoals.add(Welcome.this.goal);
                        mUserGoals.add("0");
                        obj.addAllUnique("GoalsList", mUserGoals);
                        obj.saveInBackground();
                        Log.d("Added:", ParseUser.getCurrentUser().getUsername() + "  with  " + Welcome.this.goal);
                        mBrowse= (Button) findViewById(R.id.browse);
                        mBrowse.setText(mUserGoals.size() + "");

                        listAdapter = new ArrayAdapter(Welcome.this, R.layout.group_item, R.id.usergoal, mUserGoals);
                        mGoalsList = (ListView) findViewById(R.id.Goals_list);
                        if(listAdapter == null) {
                            Log.d("listAdapter == null","");
                        }
                        if(mGoalsList == null )
                            Log.d("mGoalsList == null ","");

                        else {
                        Welcome.this.mGoalsList.setAdapter(listAdapter);
                        Welcome.this.mGoalsList.setTextFilterEnabled(true);
                            Welcome.this.mGoalsList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String item = (String) Welcome.this.mGoalsList.getItemAtPosition(position);
                                    Toast.makeText(Welcome.this, "You selected : " + item, Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(Welcome.this,GoalDeatils.class);
                                    i.putExtra("goal", item);
                                    i.putExtra("progress",mUserGoals.get (mUserGoals.indexOf(item) +1).toString() );
                                    Log.d("goal", item);
                                    Log.d("progress", mUserGoals.get(mUserGoals.indexOf(item) + 1).toString());

                                    startActivity(i);

                                }
                            });



                    }
                }



            }
                else {

                    Log.d("score", "Error: " + e.getMessage());
                }
                Log.d("Added:", ParseUser.getCurrentUser().getUsername() + "//  with  //" + Welcome.this.goal);

            }



    });
        }
    public List getUserGoals(Context p){
        pointer = p;
        List l;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersGoals");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    ParseObject obj = userList.get(0);

                    mUserGoals = (List) obj.get("GoalsList");
                    if (mUserGoals != null) {
                        Log.d("found user goals", mUserGoals.size() + "------");
                        if (mUserGoals.size() != 0) {
                            mBrowse = (Button) findViewById(R.id.browse);
                            mBrowse.setText(mUserGoals.size() + "");
                            Welcome.this.mGoalsList = (ListView) findViewById(R.id.Goals_list);

                            listAdapter = new ArrayAdapter(Welcome.this, R.layout.group_item, R.id.usergoal, mUserGoals);
                            if (listAdapter == null)
                                Log.d("listAdapter == null", "");
                            if (Welcome.this.mGoalsList == null)
                                Log.d("mGoalsList == null", "");
                            ;


                            Welcome.this.mGoalsList.setAdapter(listAdapter);
                            Welcome.this.mGoalsList.setTextFilterEnabled(true);
                            Welcome.this.mGoalsList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String item = (String) Welcome.this.mGoalsList.getItemAtPosition(position);
                                    Toast.makeText(Welcome.this, "You selected : " + item, Toast.LENGTH_SHORT).show();

                                    Intent i = new Intent(Welcome.this,GoalDeatils.class);
                                    i.putExtra("goal", item);
                                    i.putExtra("progress",mUserGoals.get (mUserGoals.indexOf(item) +1).toString() );
                                    Log.d("goal", item);
                                    Log.d("progress", mUserGoals.get(mUserGoals.indexOf(item) + 1).toString());

                                    startActivity(i);




                                }
                            });


                            //setContentView(R.layout.activity_welcome);
                        }

                    }
                } else {
                    Log.d("score", "Error: " + e.getMessage());

                }

            }
        });

    return  mUserGoals;
    }



}