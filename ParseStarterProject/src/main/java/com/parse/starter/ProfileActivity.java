package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ProfileActivity extends Activity {

    // Declare Variable
    Button mlogout;
    Button mBrowse, mSetting;
    ListView mGoalsList;
    String goal;
    List mUserGoals;
    Context pointer;
    private ArrayAdapter listAdapter;
    JSONObject JSONObject_ = new JSONObject();
    JSONArray JSONArray = new JSONArray();
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Calendar calobj = Calendar.getInstance();
    TextView name, bio, badges;
    boolean flag = false;
    ImageView image;


    TextView follower, following;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.activity_profile);

        // Retrieve current user from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) findViewById(R.id.welcome);
        mGoalsList = (ListView) findViewById(R.id.goals_list);
        List goals = getUserGoals(this,false);


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

        txtuser.setText("You are logged in as " + currentUser.get("name"));


        // Locate Button in welcome.xml
        mlogout = (Button) findViewById(R.id.log_out);
        mBrowse = (Button) findViewById(R.id.browse);
        mSetting = (Button) findViewById(R.id.setting);
        name = (TextView) findViewById(R.id.nameText);
        bio = (TextView) findViewById(R.id.bio);
        image = (ImageView) findViewById(R.id.userIcon);
        badges = (TextView) findViewById(R.id.badgestTextNumber);

        follower = (TextView) findViewById(R.id.followersTextNumber);
        following = (TextView) findViewById(R.id.followeingTextNumber);
        getFollowing();
        getFollowers();


        following.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                intent.putExtra("type", "following");
                //intent.putExtra("goal", goal);
                startActivityForResult(intent, 5);

            }
        });

        follower.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                intent.putExtra("type", "followers");
                //intent.putExtra("goal", goal);
                startActivityForResult(intent, 4);

            }
        });


        name.setText(currentUser.get("name").toString());
        bio.setText(currentUser.get("bio").toString());
        ParseFile imageFile = (ParseFile) currentUser.get("image");

        imageFile.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    // data has the bytes for the image
                    if (data == null || data.length == 0) {

                    } else {
                        Bitmap imagebit = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ProfileActivity.this.image.setImageBitmap(imagebit);
                        ProfileActivity.this.image.setRotation(90);
                        ProfileActivity.this.image.setVisibility(View.VISIBLE);

                    }


                } else {
                    // something went wrong
                }
            }
        });
        mSetting.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent setting = new Intent(ProfileActivity.this, SettingActivity.class);
                ProfileActivity.this.startActivityForResult(setting, 2);

            }
        });
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
                Intent goals = new Intent(ProfileActivity.this, ListGoals.class);
                ProfileActivity.this.startActivityForResult(goals, 1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("goal");
                Log.d("I got goal in Profile: ", result);
                addGoal(result);
                mUserGoals.remove("No_Goals");
                if (listAdapter == null) {

                } else {
                    //listAdapter.add(result);

                    //listAdapter.notifyDataSetChanged();

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == 2) {
            if (data.getStringExtra("name").toString() != null)
                name.setText(data.getStringExtra("name").toString());
            if (data.getStringExtra("bio").toString() != null)
                bio.setText(data.getStringExtra("bio").toString());
            if (data.getParcelableExtra("image") != null) {
                image.setImageBitmap((Bitmap) data.getParcelableExtra("image"));
            }

        } else if (requestCode == 3) {
            getFollowers();
            getFollowing();
            Log.d("Back:requestCode == 3", "");
            JSONObject obj = new JSONObject();
            if ((data.getStringExtra("object") != null)) {
                try {
                    Log.d("Back:requestCode -null", "");
                    obj = new JSONObject(data.getStringExtra("object"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 1; i < ProfileActivity.this.JSONArray.length(); i++) {
                    try {
                        JSONObject_ = JSONArray.getJSONObject(i);
                        if (JSONObject_.get("Name:").equals(obj.get("Name:"))) {
                            JSONObject_.put("Progress:", obj.get("Progress:"));
                            JSONArray.put(i, JSONObject_);


                            Log.d("JSONArray updated to:", obj.get("Progress:").toString());
                            getUserGoals(this,false);
                        }
                    } catch (Exception e) {

                    }


                }

            } else {
                Log.d("I got null from:", data.getComponent().toString());
            }

        }
        else if(requestCode == 4 )
            getFollowers();
        else if (requestCode == 5)
            getFollowing();


    }

    public void addGoal(String g) {

        goal = g;


        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersGoals");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                //final JSONObject JSONObject = new JSONObject();
                //JSONArray JSONArray = new JSONArray();
                if (e == null) {
                    Log.d("users:", "Retrieved " + userList.size() + " users");
                    if (userList.size() == 0) {
                        ParseObject obj = new ParseObject("UsersGoals");
                        obj.put("user", ParseUser.getCurrentUser());
                        obj.put("userName", ParseUser.getCurrentUser().getUsername());
                        try {
                            JSONObject_.put("Name:", ProfileActivity.this.goal);
                            JSONObject_.put("Progress:", 0);
                            JSONObject_.put("Time Started:", df.format(calobj.getTime()));

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        JSONArray.put(JSONObject_);
                        obj.put("MyList", JSONArray);

                        obj.saveInBackground();
                        Log.d("Added: size() == 0", ParseUser.getCurrentUser().getUsername() + "  with  " + ProfileActivity.this.goal);

                    } else {
                        ParseObject obj = userList.get(0);
                        //  mUserGoals.add(ProfileActivity.this.goal);
                        JSONArray = obj.getJSONArray("MyList");
                        try {
                            JSONObject_.put("Name:", ProfileActivity.this.goal);
                            JSONObject_.put("Progress:", 0);
                            JSONObject_.put("Time Started:", df.format(calobj.getTime()));

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        JSONArray.put(JSONObject_);
                        obj.put("MyList", JSONArray);
                        obj.saveInBackground();


                    }
                }
            }
        });

        getUserGoals(this,true);
    }

    /*
    if (mUserGoals != null) {
        Log.d("found user goals", mUserGoals.size() + "");
        obj.saveInBackground();
    } else {
        Log.d("found user goals", "with 0 goals");
        mUserGoals = new LinkedList();


    }


    // obj.addAllUnique("GoalsList", mUserGoals);

    Log.d("Added:size() != 0", ParseUser.getCurrentUser().getUsername() + "  with  " + ProfileActivity.this.goal);
    mBrowse = (Button) findViewById(R.id.browse);
//                        mBrowse.setText(mUserGoals.size() + "");

    listAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.group_item, R.id.usergoal, mUserGoals);
    mGoalsList = (ListView) findViewById(R.id.Goals_list);
    if (listAdapter == null) {
        Log.d("listAdapter == null", "");
    }
    if (mGoalsList == null)
        Log.d("mGoalsList == null ", "");


        ProfileActivity.this.mGoalsList.setAdapter(listAdapter);
        ProfileActivity.this.mGoalsList.setTextFilterEnabled(true);
        ProfileActivity.this.mGoalsList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) ProfileActivity.this.mGoalsList.getItemAtPosition(position);
                Toast.makeText(ProfileActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();

                Intent intenet = new Intent(ProfileActivity.this, GoalDeatils.class);

                for (int i = 0; i < ProfileActivity.this.JSONArray.length(); i++) {
                    try {
                        JSONObject_ = JSONArray.getJSONObject(i);
                        Log.d("JSONObject.get(\"Name:\")",JSONObject_.get("Name:").toString());

                        if (JSONObject_.get("Name:").toString().equals(item)) {

                            intenet.putExtra("goal", item);
                            Log.d("will get::::::", JSONObject_.get("Progress:").toString());

                            intenet.putExtra("progress", JSONObject_.get("Progress:").toString());

                            break;
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }

                Log.d("goal&&&&&&&&&&", item);
//                                    Log.d("progress", mUserGoals.get(mUserGoals.indexOf(item) + 1).toString());
                intenet.putExtra("object", JSONObject_.toString());
                startActivityForResult(intenet, 3);

            }
        });



}


} else {

Log.d("score", "Error: " + e.getMessage());
}
Log.d("Added:", ParseUser.getCurrentUser().getUsername() + "//  with  //" + ProfileActivity.this.goal);

}


});
}
*/
    public List getUserGoals(Context p, boolean b) {
        pointer = p;
        List l;
        flag = b ;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersGoals");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                // JSONObject JSONObject = new JSONObject();
                // JSONArray JSONArray = new JSONArray();
                mUserGoals = new LinkedList();
                if (e == null) {
                    ParseObject obj = new ParseObject("UsersGoals");
                    if (userList.size() == 0) {

                        obj.add("userName", ParseUser.getCurrentUser().getUsername().toString());
                        obj.add("MyList", JSONArray.put(JSONObject_));
                        mUserGoals.add("No_Goals");
                    } else {
                        mUserGoals.remove("No_Goals");
                        obj = userList.get(0);
                        JSONArray = obj.getJSONArray("MyList");
                        for (int i = 1; i < JSONArray.length(); i++) {

                            try {
                                JSONObject_ = JSONArray.getJSONObject(i);
                                if (JSONObject_.opt("Time End:") == null)
                                {
                                    Log.e("will add to List:", JSONObject_.get("Name:").toString());
                                    mUserGoals.add(JSONObject_.get("Name:"));
                                }


                            } catch (JSONException e1) {
                                Log.e("JSONException", "ffffffffffffffffff");
                                e1.printStackTrace();
                            }
                        }
                    }

                    //JSONArray = obj.getJSONArray("MyList");
                    if (JSONArray.length() == 0) {

                        mUserGoals.add("No_Goals");
                        Log.e("In if", "ffffffffffffffffff");
                    }
                    //if( flag )
                     //   mUserGoals.add(goal);

                    Log.d("found user goals", mUserGoals.size() + "------");
                    if (mUserGoals.size() != 0) {
                        mBrowse = (Button) findViewById(R.id.browse);
//                            mBrowse.setText(mUserGoals.size() + "");
                        ProfileActivity.this.mGoalsList = (ListView) findViewById(R.id.Goals_list);

                        listAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.group_item, R.id.usergoal, mUserGoals);
                        if (listAdapter == null)
                            Log.d("listAdapter == null", "");
                        if (ProfileActivity.this.mGoalsList == null)
                            Log.d("mGoalsList == null", "");
                        ;

                        if (mUserGoals.size() == 1 && mUserGoals.get(0).equals("No_Goals"))
                            badges.setText("0");
                        else
                            badges.setText(mUserGoals.size() + "");


                        ProfileActivity.this.mGoalsList.setAdapter(listAdapter);
                        ProfileActivity.this.mGoalsList.setTextFilterEnabled(true);


                        ProfileActivity.this.mGoalsList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()

                        {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                String item = (String) ProfileActivity.this.mGoalsList.getItemAtPosition(position);
                                Toast.makeText(ProfileActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();

                                Intent intenet = new Intent(ProfileActivity.this, GoalDeatils.class);
                                Log.d("will search on ::::::", ProfileActivity.this.JSONArray.length() + "");
                                for (int i = 1; i < ProfileActivity.this.JSONArray.length(); i++) {
                                    try {
                                        ProfileActivity.this.JSONObject_ = ProfileActivity.this.JSONArray.getJSONObject(i);
                                        Log.d("JSONObject.get(\"Name:\")", JSONObject_.get("Name:").toString());

                                        if (ProfileActivity.this.JSONObject_.get("Name:").equals(item)) {
                                            intenet.putExtra("goal", item);
                                            Log.d("will get::::::", ProfileActivity.this.JSONObject_.get("Progress:").toString());

                                            intenet.putExtra("progress", ProfileActivity.this.JSONObject_.get("Progress:").toString());
                                            // intenet.putExtra("object", JSONObject.toString());
                                            break;
                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                }

                                Log.d("goal seleceted", item);
                                intenet.putExtra("object", JSONObject_.toString());

                                startActivityForResult(intenet, 3);


                            }
                        });


                    }

                } else {
                    Log.d("score", "Error: " + e.getMessage());

                }

            }
        });

        return mUserGoals;
    }


    public void getFollowing() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("from", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> followList, ParseException e) {
                following = (TextView) findViewById(R.id.followeingTextNumber);
                following.setText(followList.size()+"");

            }
        });

    }


    public void getFollowers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("to", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> followList, ParseException e) {
                follower = (TextView) findViewById(R.id.followersTextNumber);
                follower.setText(followList.size()+"");




            }
        });




    }
}