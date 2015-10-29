package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/**
 * Created by omar on 10/28/2015.
 */

public class MemberActivity extends Activity {
    Button mlogout;
    Button mBrowse, mSetting;
    ListView mGoalsList;
    String memberName;
    List mUserGoals;
    Context pointer;
    private ArrayAdapter listAdapter;
    org.json.JSONObject JSONObject = new JSONObject();
    org.json.JSONArray JSONArray = new JSONArray();
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Calendar calobj = Calendar.getInstance();
    TextView name, bio;
    TextView txtuser, badges;
    ImageView image;
    ParseUser current;
    TextView follower, following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_profile);

        // Retrieve current user from Parse.com

        // Convert currentUser into String
        memberName = getIntent().getStringExtra("name");
        // Locate TextView in welcome.xml
        txtuser = (TextView) findViewById(R.id.welcome);
        mGoalsList = (ListView) findViewById(R.id.goals_list);
        List goals = getUserGoals(memberName);
        Log.d("member=========", memberName);

        ParseQuery<ParseUser> currentUser = ParseUser.getQuery();
        currentUser.whereEqualTo("name", memberName);
        mlogout = (Button) findViewById(R.id.log_out);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Follow");
        query.whereEqualTo("toName", memberName);
        query.whereEqualTo("fromName", ParseUser.getCurrentUser().getUsername().toString());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> catList, ParseException e) {
                if (e == null) {
                    if (catList == null || catList.size() == 0)
                        mlogout.setText("follow");
                    else
                        mlogout.setText("unfollow");

                }


            }
        });


        //  mlogout.setVisibility(View.GONE);
        mBrowse = (Button) findViewById(R.id.browse);
        mBrowse.setVisibility(View.GONE);
        mSetting = (Button) findViewById(R.id.setting);
        mSetting.setVisibility(View.GONE);
        name = (TextView) findViewById(R.id.nameText);
        bio = (TextView) findViewById(R.id.bio);
        image = (ImageView) findViewById(R.id.userIcon);
        badges = (TextView) findViewById(R.id.badgestTextNumber);

        follower = (TextView) findViewById(R.id.followersTextNumber);
        following = (TextView) findViewById(R.id.followeingTextNumber);
        getFollowing();
        getFollowers();


        mlogout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user


                ParseObject follow = new ParseObject("Follow");
                if (mlogout.getText().toString().equals("follow")) {
                    follow.put("from", ParseUser.getCurrentUser());
                    follow.put("to", current);
                    follow.put("toName", memberName);
                    follow.put("fromName", ParseUser.getCurrentUser().getUsername().toString());
                    follow.saveInBackground();
                    Toast.makeText(MemberActivity.this, "You follwed : " + memberName, Toast.LENGTH_SHORT).show();
                    mlogout.setText("unfollow");
                    getFollowers();

                } else {
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Follow");
                    query.whereEqualTo("toName", memberName);
                    query.whereEqualTo("fromName", ParseUser.getCurrentUser().getUsername().toString());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> catList, ParseException e) {
                            if (e == null) {
                                if (catList == null || catList.size() == 0)
                                    ;//mlogout.setText("follow");
                                else {
                                    ParseObject obj = catList.get(0);

                                    obj.deleteInBackground();

                                    Toast.makeText(MemberActivity.this, "You unfollwed : " + memberName, Toast.LENGTH_SHORT).show();
                                    mlogout.setText("follow");

                                }

                            }


                        }
                    });
                    getFollowers();

                }

                //  follow.put("date", Date());


            }
        });


        currentUser.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, ParseException e) {
                if (e == null) {
                    current = userList.get(0);
                    name.setText(current.get("name").toString());
                    bio.setText(current.get("bio").toString());
                    ParseFile imageFile = (ParseFile) current.get("image");
                    MemberActivity.this.txtuser.setText(current.get("name").toString());

                    imageFile.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                // data has the bytes for the image
                                if (data == null || data.length == 0) {

                                } else {
                                    Bitmap imagebit = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    MemberActivity.this.image.setImageBitmap(imagebit);
                                    MemberActivity.this.image.setRotation(90);
                                    MemberActivity.this.image.setVisibility(View.VISIBLE);

                                }


                            } else {
                                // something went wrong
                            }
                        }
                    });

                }
            }

        });
    }


    private List getUserGoals(String name) {
        memberName = name;
        List l;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UsersGoals");
        query.whereEqualTo("userName", memberName);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                // JSONObject JSONObject = new JSONObject();
                // JSONArray JSONArray = new JSONArray();
                mUserGoals = new LinkedList();
                if (e == null) {
                    ParseObject obj = new ParseObject("UsersGoals");
                    if (userList.size() == 0) {

                        obj.add("userName", memberName);
                        obj.add("MyList", JSONArray.put(JSONObject));
                        mUserGoals.add("No_Goals");
                    } else {
                        mUserGoals.remove("No_Goals");
                        obj = userList.get(0);
                        JSONArray = obj.getJSONArray("MyList");
                        for (int i = 1; i < JSONArray.length(); i++) {

                            try {
                                JSONObject = JSONArray.getJSONObject(i);
                                if (JSONObject.opt("Time End:") == null)
                                    mUserGoals.add(JSONObject.get("Name:"));
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    JSONArray = obj.getJSONArray("MyList");
                    if (JSONArray.length() == 0) {

                        mUserGoals.add("No_Goals");
                        Log.e("In if", "ffffffffffffffffff");
                    }

                    Log.d("found user goals", mUserGoals.size() + "------");
                    if (mUserGoals.size() != 0) {
                        mBrowse = (Button) findViewById(R.id.browse);
//                            mBrowse.setText(mUserGoals.size() + "");
                        MemberActivity.this.mGoalsList = (ListView) findViewById(R.id.Goals_list);

                        listAdapter = new ArrayAdapter(MemberActivity.this, R.layout.group_item, R.id.usergoal, mUserGoals);
                        if (listAdapter == null)
                            Log.d("listAdapter == null", "");
                        if (MemberActivity.this.mGoalsList == null)
                            Log.d("mGoalsList == null", "");
                        ;
                        if (mUserGoals.size() == 1 && mUserGoals.get(0).equals("No_Goals"))
                            badges.setText("0");
                        else
                            badges.setText(mUserGoals.size() + "");


                        MemberActivity.this.mGoalsList.setAdapter(listAdapter);
                        MemberActivity.this.mGoalsList.setTextFilterEnabled(true);


                    }


                }
            }
        });

        return null;
    }

    public void getFollowing() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("fromName", memberName);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> followList, ParseException e) {
                following = (TextView) findViewById(R.id.followeingTextNumber);
                following.setText(followList.size() + "");

            }
        });

    }


    public void getFollowers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("toName", memberName);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> followList, ParseException e) {
                follower = (TextView) findViewById(R.id.followersTextNumber);
                follower.setText(followList.size() + "");


            }
        });


    }
}
