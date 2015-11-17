package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
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

/**
 * Created by omar on 10/28/2015.
 */
public class ExploreActivity extends Activity {
    ListView list;
    List<String> usersList = new LinkedList<>();
    List<String> progress = new LinkedList<>();

    ArrayAdapter listAdapter;
    MyAdapterUsers usersAdapter;
    MyTimeLineAdapter timeLine;
    TextView title;
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    onItemClicked lisinter = new onItemClicked() {
        @Override
        public void postion(int i) {
            Log.d("Profile Activity got:", i + "");

            int item = (int) ExploreActivity.this.list.getItemAtPosition(i);
            Toast.makeText(ExploreActivity.this, "You selected : " + usersList.get(item), Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(ExploreActivity.this, MemberActivity.class);
            intent.putExtra("name",  usersList.get(item));
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.explore_friends);
        list = (ListView) findViewById(R.id.memebers_list);
        title = (TextView) findViewById(R.id.title_explore);
        Intent intent = getIntent();


        if (intent.getStringExtra("type").equals("explore")) {
            String goal = intent.getStringExtra("goal");
            Log.d("Goal is: ", goal);
            title.append(" " + goal + " goal!");
            ParseQuery<ParseObject> queryGoal = ParseQuery.getQuery("Goals");
            queryGoal.whereEqualTo("name", goal);
            Log.d("will request", goal);
            usersList = new LinkedList<>();
            queryGoal.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> catList, ParseException e) {
                    if (e == null) {
                        ParseObject obj = catList.get(0);
                        if (obj.getList("users") != null) {
                            usersList = obj.getList("users");





                            usersAdapter = new MyAdapterUsers(ExploreActivity.this, usersList,lisinter);
                            ExploreActivity.this.list.setAdapter(usersAdapter);
                            ExploreActivity.this.list.setTextFilterEnabled(true);


                           /* ExploreActivity.this.list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()

                            {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    String item = (String) ExploreActivity.this.list.getItemAtPosition(position);


                                }
                            });*/



                        } else

                        {
                            usersList.add("No Users");
                        }


                    }
                }
            });


        } else if (intent.getStringExtra("type").equals("followers")) {
            usersList = new LinkedList<>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
            query.whereEqualTo("to", ParseUser.getCurrentUser());

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> followList, ParseException e) {
                    if (e == null) {
                        ParseObject obj = new ParseObject("Follow");
                        for (int i = 0; i < followList.size(); i++) {
                            obj = followList.get(i);
                            usersList.add(obj.get("fromName").toString());
                        }
                        usersAdapter = new MyAdapterUsers(ExploreActivity.this, usersList,lisinter);
                        ExploreActivity.this.list.setAdapter(usersAdapter);
                        ExploreActivity.this.list.setTextFilterEnabled(true);

                    }


                }
            });


        } else if (intent.getStringExtra("type").equals("following")) {
            usersList = new LinkedList<>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
            query.whereEqualTo("from", ParseUser.getCurrentUser());

            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> followList, ParseException e) {
                    if (e == null) {
                        ParseUser user = new ParseUser();
                        ParseObject obj = new ParseObject("Follow");
                        for (int i = 0; i < followList.size(); i++) {
                            obj = followList.get(i);
                            usersList.add(obj.get("toName").toString());
                            user = obj.getParseUser("to");
                          // obj.getParseObject("to").fetch();



                        }
                        usersAdapter = new MyAdapterUsers(ExploreActivity.this, usersList,lisinter);
                        ExploreActivity.this.list.setAdapter(usersAdapter);
                        ExploreActivity.this.list.setTextFilterEnabled(true);



                    }

                }


            });
        }

        else if (intent.getStringExtra("type").equals("timeline") ){
            Calendar calendar = Calendar.getInstance();

            Log.d( "start timeline","");
            usersList = new LinkedList<>();
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
            query.whereEqualTo("from", ParseUser.getCurrentUser());

            List<ParseObject>   followList = null;
            try {
                followList = query.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(followList != null || followList.size() > 0 ) {
                ParseObject obj = new ParseObject("Follow");
                for (int i = 0; i < followList.size(); i++) {
                    obj = followList.get(i);
                    usersList.add(obj.get("toName").toString());
                    Log.d("users::", obj.get("toName").toString());
                }



                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

                for(int i = 0 ; i < usersList.size() ; i++) {
                    ParseQuery<ParseObject> getGoals = ParseQuery.getQuery("UserWithGoals");
                    getGoals.whereEqualTo("userName", usersList.get(i).toString());
                    Log.d("get goals of users::", usersList.get(i).toString());
                    queries.add(getGoals);

                }
               // getGoals.orderByAscending("lastUpdate");
                ParseQuery<ParseObject> superQuery = ParseQuery.getQuery("UserWithGoals");
                ParseQuery.or(queries);
                superQuery.addDescendingOrder("updatedAt");
                List<ParseObject>   usersGoalsLists = null;
                try {
                    usersGoalsLists = superQuery.find();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                usersList.clear();
                for(int i = 0 ; i < usersGoalsLists.size() ; i++) {
                    Log.d("gwt Event", usersGoalsLists.get(i).get("lastUpdate").toString());
                    //calendar.setTimeInMillis(Long.parseLong(usersGoalsLists.get(i).get("lastUpdate").toString()));
                    Date d = (Date)usersGoalsLists.get(i).getUpdatedAt();
                    getTime(d);
                    usersList.add(usersGoalsLists.get(i).get("userName").toString() );
                    progress.add("" + getPhrase( usersGoalsLists.get(i).get("progress").toString()) + " " + usersGoalsLists.get(i).get("name").toString() +
                             " on: "+   getTime(d) );

                    timeLine = new MyTimeLineAdapter(ExploreActivity.this, usersList, progress, lisinter);
                    ExploreActivity.this.list.setAdapter(timeLine);
                    ExploreActivity.this.list.setTextFilterEnabled(true);



                }



            }

        }


    }

    @Override
    public void onBackPressed() {


        Intent intent = this.getIntent();

        this.setResult(RESULT_OK, intent);
;
        finish();
    }

    public String getPhrase(String stepInString) {
        int step = Integer.parseInt(stepInString);
        switch (step) {
            case 0:
                return " Just added ";
            case 1:
                return "in the first step of";
            case 5:
                return " half way throgh in ";
            case 9:
                return "almost finish ";
            case 10:
                return "finished ";
            default:
                return "in step " + step + "of";
        }
    }
    public String getTime(Date date){
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE hh:mm a");
    return DATE_FORMAT.format(date);

    }
}