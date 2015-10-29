package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by omar on 10/28/2015.
 */
public class ExploreActivity extends Activity {
    ListView list;
    List<String> usersList = new LinkedList<>();
    ArrayAdapter listAdapter;
    TextView title;
    ;

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
                            listAdapter = new ArrayAdapter(ExploreActivity.this, R.layout.group_item, R.id.usergoal, usersList);
                            ExploreActivity.this.list.setAdapter(listAdapter);
                            ExploreActivity.this.list.setTextFilterEnabled(true);
                            ExploreActivity.this.list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()

                            {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    String item = (String) ExploreActivity.this.list.getItemAtPosition(position);
                                    Toast.makeText(ExploreActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ExploreActivity.this, MemberActivity.class);
                                    intent.putExtra("name", item);
                                    startActivity(intent);

                                }
                            });
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
                        listAdapter = new ArrayAdapter(ExploreActivity.this, R.layout.group_item, R.id.usergoal, usersList);
                        ExploreActivity.this.list.setAdapter(listAdapter);
                        ExploreActivity.this.list.setTextFilterEnabled(true);
                        ExploreActivity.this.list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()

                        {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                String item = (String) ExploreActivity.this.list.getItemAtPosition(position);
                                Toast.makeText(ExploreActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ExploreActivity.this, MemberActivity.class);
                                intent.putExtra("name", item);
                                startActivity(intent);

                            }
                        });

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
                        ParseObject obj = new ParseObject("Follow");
                        for (int i = 0; i < followList.size(); i++) {
                            obj = followList.get(i);
                            usersList.add(obj.get("toName").toString());
                        }
                        listAdapter = new ArrayAdapter(ExploreActivity.this, R.layout.group_item, R.id.usergoal, usersList);
                        ExploreActivity.this.list.setAdapter(listAdapter);
                        ExploreActivity.this.list.setTextFilterEnabled(true);
                        ExploreActivity.this.list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener()

                        {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                String item = (String) ExploreActivity.this.list.getItemAtPosition(position);
                                Toast.makeText(ExploreActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ExploreActivity.this, MemberActivity.class);
                                intent.putExtra("name", item);
                                startActivity(intent);

                            }
                        });

                    }

                }


            });
        }


    }

    @Override
    public void onBackPressed() {


        Intent intent = this.getIntent();

        this.setResult(RESULT_OK, intent);
;
        finish();
    }
}