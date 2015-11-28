package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    // Declare Variable
    private  ImageView mlogout;
    private  ImageView mBrowse, mSetting, timeLine, Watch;
    private  ListView mGoalsList;
    private  String goal;
    private  List mUserGoals, mProgress, mIcons;
    private  Context pointer;
    private ArrayAdapter mListAdapter;

    private  DateFormat df = new SimpleDateFormat("dd/MM/yy");
    private  Calendar calobj = Calendar.getInstance();
    private TextView mName, mBio, mBadges;
    private  boolean flag = false;
    private ImageView image;
    private MyAdapter mAdapter;
    TextView follower, following;

    onItemClicked lisinter = new onItemClicked() { // listener to user's goals list
        @Override
        public void postion(int i) {
            Log.d("Profile Activity got:", i + "");

            int item = (int) ProfileActivity.this.mGoalsList.getItemAtPosition(i); // get the postion of item
            Toast.makeText(ProfileActivity.this, "You selected : " + mUserGoals.get(item), Toast.LENGTH_SHORT).show();

            Intent intenet = new Intent(ProfileActivity.this, GoalDeatils.class); // start intent to goal detail
            intenet.putExtra("goal", mUserGoals.get(item).toString()); // add goal name to intent

            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWithGoals"); // get current progress
            query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());// get current progress from this user
            query.whereEqualTo("timeEnd","---");
            query.whereEqualTo("name", mUserGoals.get(item).toString());// get current progress from this goal
            List<ParseObject> userGoalList = null;
            try {
                userGoalList = query.find(); // grab the goal
            } catch (Exception e) {
                e.printStackTrace();
            }

            ParseObject obj = userGoalList.get(0); // get the goal, it is unique! so we use get[0]


            intenet.putExtra("progress", obj.get("progress").toString()); // send progress to new Activity


            Log.d("goal seleceted", mUserGoals.get(item).toString());


            startActivityForResult(intenet, 3); // go to the goal details.
        }
    };




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ParseUser currentUser = ParseUser.getCurrentUser();
        String struser = currentUser.getUsername().toString();
       // TextView txtuser = (TextView) findViewById(R.id.welcome);
        mGoalsList = (ListView) findViewById(R.id.goals_list);
        mlogout = (ImageView) findViewById(R.id.log_out);
        mBrowse = (ImageView) findViewById(R.id.browse);
        mSetting = (ImageView) findViewById(R.id.setting);
        mName = (TextView) findViewById(R.id.nameText);
        mBio = (TextView) findViewById(R.id.bio);
        image = (ImageView) findViewById(R.id.userIcon);
        mBadges = (TextView) findViewById(R.id.badgestTextNumber);
        timeLine = (ImageView) findViewById(R.id.timeLine);

        follower = (TextView) findViewById(R.id.followersTextNumber);
        following = (TextView) findViewById(R.id.followeingTextNumber);
        getFollowing(); // get the following/followers
        getFollowers();
       // txtuser.setText("You are logged in as " + currentUser.get("name"));
        //txtuser.setText("");

        try {
            List goals = getUserGoals(this, false); // grab the user's goals
        } catch (ParseException e) {
            e.printStackTrace();
        }



        following.setOnClickListener(new View.OnClickListener() { // user click on following

            public void onClick(View arg0) {
                Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                intent.putExtra("type", "following");
                startActivityForResult(intent, Constants.FOLLOWING);

            }
        });
        timeLine.setOnClickListener(new View.OnClickListener() {// user click on timeline

            public void onClick(View arg0) {
                 Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                intent.putExtra("type", "timeline");
                 startActivityForResult(intent, Constants.TIMELINE);

            }
        });
        follower.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {// user click on followers
                // Logout current user
                Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                intent.putExtra("type", "followers");

                startActivityForResult(intent, Constants.FOLLOWERS);

            }
        });


        mName.setText(currentUser.get("name").toString()); // show the name
        mBio.setText(currentUser.get("bio").toString()); // show the label
        ParseFile imageFile = (ParseFile) currentUser.get("image"); // get the image "icon"

        imageFile.getDataInBackground(new GetDataCallback() { // get the avatar
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                     if (data == null || data.length == 0) {
                        // no pic, do nothing!
                    } else {
                       // there is avatar, draw it
                        Bitmap imagebit = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ProfileActivity.this.image.setImageBitmap(imagebit);
                        // ProfileActivity.this.image.setPadding(2, 2, 2, 2);
                        // image.setBackgroundColor(Color.BLACK);

                         ProfileActivity.this.image.setVisibility(View.VISIBLE);

                    }


                } else {
                    // something went wrong
                }
            }
        });

        /*
        mSetting.setOnClickListener(new OnClickListener() { // setting pressed

            public void onClick(View arg0) {
                Intent setting = new Intent(ProfileActivity.this, SettingActivity.class);
                ProfileActivity.this.startActivityForResult(setting, Constants.SETTING);

            }
        });*/
        mlogout.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                ParseUser.logOut();
                finish();
            }
        });

        mBrowse.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) { // browse goals
                Intent goals = new Intent(ProfileActivity.this, ListGoals.class);
                ProfileActivity.this.startActivityForResult(goals, Constants.BROWSE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.BROWSE) { // come back from browse goals
            if (resultCode == Activity.RESULT_OK) { // user select goal
                String result = data.getStringExtra("goal"); // get the goal name

                byte[] icon = data.getByteArrayExtra("icon"); // get the icon
                Log.d("I got goal in Profile: ", result);
                addGoal(result, icon); // add goal and icon to the list
                mUserGoals.remove("No_Goals"); // remove the no_goal label if any
                if (mListAdapter == null) {

                } else {

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //user did not select any goal
            }
        } else if (requestCode == Constants.SETTING && resultCode == RESULT_OK) { // user apply changes in setting page
            if (data.getStringExtra("name") != null)  // update name
                mName.setText(data.getStringExtra("name").toString());
            if (data.getStringExtra("bio") != null)// update bio
                mBio.setText(data.getStringExtra("bio").toString());
            if (data.getParcelableExtra("image") != null) { // if there is avatar, draw it
                image.setImageBitmap((Bitmap) data.getParcelableExtra("image"));
            }

        } else if (requestCode == Constants.GOAL_DEATAL) { // back from goal detalis
            getFollowers();
            getFollowing();
            try {
                getUserGoals(this, false); // update the list to get new progress
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (requestCode == Constants.FOLLOWERS) //
            getFollowers(); // update it in case unfollow
        else if (requestCode == Constants.FOLLOWING)
            getFollowing(); // update it in case unfollow



    }

    public void addGoal(String g, byte[] icon) { // add current user to the db as a goal achiever
        goal = g;
        ParseObject obj = new ParseObject("UserWithGoals");
        /* below add user's info */
        obj.put("userName", ParseUser.getCurrentUser().getUsername());
        obj.put("name", goal);
        obj.put("progress", 0); // just started
        obj.put("timeStart", df.format(calobj.getTime())); // start time
        obj.put("timeEnd", "---"); // unkown end date
        if (icon != null) // if avatar exist, add it
            obj.put("icon", icon);


        try {
            obj.save(); // save to db
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            getUserGoals(this, true); // update the (list view) goals list
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public List getUserGoals(Context p, boolean b) throws ParseException {
        pointer = p;
        List l;
        flag = b;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWithGoals");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
        List<ParseObject> userList = query.find(); // get all user's goals
        mUserGoals = new LinkedList();
        mProgress = new LinkedList();
        mIcons = new LinkedList();

        ParseObject obj = new ParseObject("UserWithGoals");

        if (userList.size() == 0) { // user with no goals in db
            mBadges.setText("0"); // set label to 0
            mUserGoals.add("No_Goals"); // add to the list view, no goal
        } else { // user have goals

            mUserGoals.remove("No_Goals"); // remove 'no goal' label if exits


            for (int i = 0; i < userList.size(); i++) { // go throgh the list and extract the goal's name, progress, goal's con

                obj = userList.get(i);

                if (obj.getString("timeEnd").equals("---")) { // if it is ongoing goal (not finished)
                    mUserGoals.add(obj.get("name"));
                    mProgress.add(obj.get("progress"));
                    mIcons.add(obj.get("icon"));
                }
            }
            if (mUserGoals.size() == 0){ //
                mBadges.setText("0"); // set label to 0
                mUserGoals.add("No_Goals"); // add to the list view, no goal
            }


        }


        Log.d("found user goals", mUserGoals.size() + "------");



        if (mUserGoals.size() != 0) { // should be always true ( size must be > 0 )
            mBrowse = (ImageView) findViewById(R.id.browse);
            ProfileActivity.this.mGoalsList = (ListView) findViewById(R.id.Goals_list);

            if (mUserGoals.get(0).toString().equals("No_Goals")) // no goals, simple list view
            {
                mBadges.setText("0");
                mListAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.group_item, R.id.usergoal, mUserGoals);
                ProfileActivity.this.mGoalsList.setAdapter(mListAdapter);

            }
            else // there are goals, custmoize the list view
            {
                mAdapter = new MyAdapter(this, mUserGoals, mProgress, mIcons, lisinter);
                ProfileActivity.this.mGoalsList.setAdapter(mAdapter);

            }


            if (mListAdapter == null)
                Log.d("listAdapter == null", "");
            if (ProfileActivity.this.mGoalsList == null)
                Log.d("mGoalsList == null", "");


            if (mUserGoals.size() == 1 && mUserGoals.get(0).equals("No_Goals"))
                mBadges.setText("0");
            else
                mBadges.setText(mUserGoals.size() + "");



            ProfileActivity.this.mGoalsList.setTextFilterEnabled(true);
            ProfileActivity.this.mGoalsList.setClickable(true);


            /*ProfileActivity.this.mGoalsList.setOnItemClickListener(new AdapterView.OnItemClickListener()

            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String item = (String) ProfileActivity.this.mGoalsList.getItemAtPosition(position);
                    Toast.makeText(ProfileActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();

                    Intent intenet = new Intent(ProfileActivity.this, GoalDeatils.class);
                    intenet.putExtra("goal", item);
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWithGoals");
                    query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
                    query.whereEqualTo("name", item);
                    List<ParseObject> userGoalList = null;
                    try {
                        userGoalList = query.find();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ParseObject obj = userGoalList.get(0);


                    intenet.putExtra("progress", obj.get("progress").toString());


                    Log.d("goal seleceted", item);


                    startActivityForResult(intenet, Constants.GOAL_DEATAL);
                }


            });*/


        }


        return mUserGoals;
    }


    private void getFollowing() { // get the following
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("from", ParseUser.getCurrentUser());  // get the following users

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> followList, ParseException e) {
                following = (TextView) findViewById(R.id.followeingTextNumber);
                following.setText(followList.size() + "");

            }
        });

    }


    private void getFollowers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("to", ParseUser.getCurrentUser());// get people who are following current user

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> followList, ParseException e) {
                follower = (TextView) findViewById(R.id.followersTextNumber);
                follower.setText(followList.size() + "");


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.menu_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent setting = new Intent(ProfileActivity.this, SettingActivity.class);
            ProfileActivity.this.startActivityForResult(setting, Constants.SETTING);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
