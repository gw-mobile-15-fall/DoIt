package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class ExploreActivity extends Activity {
    private ListView mList;
    private List<String> mUsersList = new LinkedList<>();
    private List<String> mProgress = new LinkedList<>();

     private  MyAdapterUsers mUsersAdapter;
    private MyTimeLineAdapter mTimeLine;
    private TextView mTitle;
    onItemClicked lisinter = new onItemClicked() { // lisinter to members' mList to access thier profiles
        @Override
        public void postion(int i) {
            Log.d("Profile Activity got:", i + "");

            int item = (int) ExploreActivity.this.mList.getItemAtPosition(i);
            Toast.makeText(ExploreActivity.this, getResources().getString(R.string.you_selected) + mUsersList.get(item), Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(ExploreActivity.this, MemberActivity.class);
            intent.putExtra("name", mUsersList.get(item));
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_friends);
        mList = (ListView) findViewById(R.id.memebers_list);
        mTitle = (TextView) findViewById(R.id.title_explore);
        Intent intent = getIntent();


        if (intent.getStringExtra("type").equals("explore")) { // members who have same goal!
            mTitle.setText(getResources().getString(R.string.friends_who_have)); // set title
            String goal = intent.getStringExtra("goal");
            Log.d("Goal is: ", goal);
            mTitle.append(" " + goal + " goal!");


            ParseQuery<ParseObject> queryGoal = ParseQuery.getQuery("UserWithGoals");
            queryGoal.whereEqualTo("name", goal); //get a list of users with same goal
            queryGoal.whereNotEqualTo("userName", ParseUser.getCurrentUser().getUsername());
            queryGoal.whereEqualTo("timeEnd", "---");

            Log.d("will request", goal);
            mUsersList = new LinkedList<>();
            mProgress = new LinkedList<>();
            queryGoal.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> catList, ParseException e) {
                    if (e == null) { // No Error, means get the list of members
                        if(catList == null || catList.size() == 0)
                        {
                            mUsersList.add("No Users"); // no users have same goal
                            mUsersAdapter = new MyAdapterUsers(ExploreActivity.this, mUsersList, lisinter); // set the list view with memebers' list
                            ExploreActivity.this.mList.setAdapter(mUsersAdapter);
                            ExploreActivity.this.mList.setTextFilterEnabled(true);
                        }
                        else
                        {
                            for(int i = 0 ; i < catList.size() ; i++ )
                                {
                                    ParseObject obj = catList.get(i); // since we have one list => index = 0
                                    mUsersList.add(obj.getString("userName")); // added the members' list to LinkedList
                                    mProgress.add( getResources().getString(R.string.in_step) +": "+ obj.getInt("progress"));
                                }
                            mTimeLine = new MyTimeLineAdapter(ExploreActivity.this, mUsersList,mProgress, lisinter); // set the list view with memebers' list
                            ExploreActivity.this.mList.setAdapter(mTimeLine);
                            ExploreActivity.this.mList.setTextFilterEnabled(true);
                        }


                    }
                }
            });


        } else if (intent.getStringExtra("type").equals("followers")) { // get followers
            mUsersList = new LinkedList<>();
            mTitle.setText(getResources().getString(R.string.followers)); // set title of screen
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
            query.whereEqualTo("to", ParseUser.getCurrentUser()); // find people eho follow current user

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> followList, ParseException e) {
                    if (e == null) { // get list, no error
                        ParseObject obj = new ParseObject("Follow");
                        for (int i = 0; i < followList.size(); i++) {
                            obj = followList.get(i);
                            mUsersList.add(obj.get(Constants.FROM_NAME).toString());// get the names of each one, added to list
                        }
                        mUsersAdapter = new MyAdapterUsers(ExploreActivity.this, mUsersList, lisinter); // set adapter list
                        ExploreActivity.this.mList.setAdapter(mUsersAdapter);
                        ExploreActivity.this.mList.setTextFilterEnabled(true);

                    }


                }
            });


        } else if (intent.getStringExtra("type").equals("following")) { // same followers :)
            mTitle.setText(getResources().getString(R.string.following));

            mUsersList = new LinkedList<>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
            query.whereEqualTo("from", ParseUser.getCurrentUser());

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> followList, ParseException e) {
                    if (e == null) {
                        ParseUser user = new ParseUser();
                        ParseObject obj = new ParseObject("Follow");
                        for (int i = 0; i < followList.size(); i++) {
                            obj = followList.get(i);
                            mUsersList.add(obj.get(Constants.TO_NAME).toString());
                            user = obj.getParseUser("to");


                        }
                        mUsersAdapter = new MyAdapterUsers(ExploreActivity.this, mUsersList, lisinter);
                        ExploreActivity.this.mList.setAdapter(mUsersAdapter);
                        ExploreActivity.this.mList.setTextFilterEnabled(true);


                    }

                }


            });
        } else if (intent.getStringExtra("type").equals("timeline")) { // user click on timeline
            mTitle.setText(getResources().getString(R.string.timeLine)); // set tilte

            Calendar calendar = Calendar.getInstance();

            Log.d("start timeline", "");
            mUsersList = new LinkedList<>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
            query.whereEqualTo("from", ParseUser.getCurrentUser()); // get all the members were followed by current user

            List<ParseObject> followList = null;
            try {
                followList = query.find(); // get users list!
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (followList != null || followList.size() > 0) { //  get the list of memebers if has following
                ParseObject obj = new ParseObject("Follow");
                for (int i = 0; i < followList.size(); i++) {
                    obj = followList.get(i);
                    mUsersList.add(obj.get(Constants.TO_NAME).toString()); // add the names to list
                    Log.d("users::", obj.get(Constants.TO_NAME).toString());
                }


                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

                for (int i = 0; i < mUsersList.size(); i++) { // here go to all users in the list, create a query for goals and
                    // add them to SUPER query
                    ParseQuery<ParseObject> getGoals = ParseQuery.getQuery("UserWithGoals");
                    getGoals.whereEqualTo("userName", mUsersList.get(i).toString());
                    Log.d("get goals of users::", mUsersList.get(i).toString());
                    queries.add(getGoals);

                }
                ParseQuery<ParseObject> superQuery =  ParseQuery.or(queries);  // super query to get all the goals from all flowwing users
               // ParseQuery.or(queries); // add all the queries to the SUPER query
                superQuery.addDescendingOrder("updatedAt"); // sort them by updated date
                superQuery.setLimit(50); // get the most recent 50 events
                List<ParseObject> usersGoalsLists = null;
                try {
                    usersGoalsLists = superQuery.find(); // excute
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mUsersList.clear();
                for (int i = 0; i < usersGoalsLists.size(); i++) {

                    Date d = (Date) usersGoalsLists.get(i).getUpdatedAt(); // get the last update date

                    mUsersList.add(usersGoalsLists.get(i).get("userName").toString()); // get the user f this event
                    mProgress.add("" + getPhrase(usersGoalsLists.get(i).get("progress").toString()) + " " + usersGoalsLists.get(i).get("name").toString() +
                            " on: " + getTime(d)); // get the pharse based on the current step!

                    mTimeLine = new MyTimeLineAdapter(ExploreActivity.this, mUsersList, mProgress, lisinter); // set the adapter
                    ExploreActivity.this.mList.setAdapter(mTimeLine);
                    ExploreActivity.this.mList.setTextFilterEnabled(true);


                }


            }

        } else if (intent.getStringExtra("type").equals("history")) {
            /*title.setText(getResources().getString(R.string.history));

            Log.d("start history", "");
            List<String> goalsDone = new LinkedList<>();
            List<String> Duration = new LinkedList<>();

            List<byte[]> goalsIcons = new LinkedList<>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWithGoals");
            query.whereNotEqualTo("timeEnd", "---");
            query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());

            List<ParseObject> goalsList = null;
            try {
                goalsList = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (goalsList != null || goalsList.size() > 0) {
                ParseObject obj = new ParseObject("UsersWithGoals");
                for (int i = 0; i < goalsList.size(); i++) {
                    obj = goalsList.get(i);
                    goalsDone.add(obj.get("name").toString());
                    goalsIcons.add(obj.getBytes("icon"));
                    Duration.add(getDifferenceDays(obj.getCreatedAt(), obj.getUpdatedAt()) + " Days");

                }


                MyAdapter GoalsDoneAdapter = new MyAdapter(ExploreActivity.this, goalsDone, Duration, goalsIcons, null);
                ExploreActivity.this.list.setAdapter(GoalsDoneAdapter);
                ExploreActivity.this.list.setTextFilterEnabled(true);


            }*/


        }

    }


    @Override
    public void onBackPressed() {


        Intent intent = this.getIntent();

        this.setResult(RESULT_OK, intent);
        ;
        finish();
    }

    private String getPhrase(String stepInString) { // called from time line , get the phares based on step
        int step = Integer.parseInt(stepInString);
        switch (step) {
            case 0:
                return getResources().getString(R.string.just_added);
            case 1:
                return getResources().getString(R.string.first_step);
            case 5:
                return getResources().getString(R.string.half_way);
            case 9:
                return getResources().getString(R.string.almost_finish);
            case 10:
                return getResources().getString(R.string.finished);
            default:
                return getResources().getString(R.string.in_step) + step + getResources().getString(R.string.of);
        }
    }

    private String getTime(Date date) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE hh:mm a");
        return DATE_FORMAT.format(date);

    }


}