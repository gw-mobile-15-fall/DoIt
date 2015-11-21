package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ListGoals extends Activity{


    private   List<String> mGroupList;
    private List<String> mChildList;
    private   Map<String, List<String>> mGoalsCollection;
    private    ExpandableListView expListView;
    private  List<String> mTravel = new LinkedList<String>();
    private  List<String> mIT = new LinkedList<String>();
    private  List<String> mCook = new LinkedList<String>();
    private Map<String, ParseFile> iconCollections  = new LinkedHashMap<String, ParseFile>(); ;
    private boolean flag = true;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.list_goals);

            createGroupList();


        }

    private void createGroupList() {
        mGroupList = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Goals");
        query.whereNotEqualTo("Category", "");
        Log.d("will request", "Category ");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> catList, ParseException e) {
                if (e == null) {
                    Log.d("ParseException", "null ");

                    for (int i = 0; i < catList.size(); i++) {
                        if (!mGroupList.contains(catList.get(i).getString("Category")))
                            mGroupList.add(catList.get(i).getString("Category"));


                        if(catList.get(i).getString("Category").equals("IT")) {
                            mIT.add(catList.get(i).getString("name"));
                            Log.d("Added to IT", catList.get(i).getString("name"));
                            iconCollections.put(catList.get(i).getString("name"), catList.get(i).getParseFile("icon"));

                        }
                        else if(catList.get(i).getString("Category").equals("Travel")) {
                            mTravel.add(catList.get(i).getString("name"));
                            Log.d("Added to travel", catList.get(i).getString("name"));
                            iconCollections.put(catList.get(i).getString("name"),catList.get(i).getParseFile("icon"));

                        }
                        else  if(catList.get(i).getString("Category").equals("Cook")) {


                            mCook.add(catList.get(i).getString("name"));
                            Log.d("Added to Cook", catList.get(i).getString("name"));
                            iconCollections.put(catList.get(i).getString("name"),catList.get(i).getParseFile("icon"));
                        }
                    }
                    Log.d("score", "Retrieved " + mGroupList.size() + " scores");


                      createCollection();

            expListView = (ExpandableListView) findViewById(R.id.goals_list);
            final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                    ListGoals.this, mGroupList, mGoalsCollection,iconCollections);
            expListView.setAdapter(expListAdapter);

            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    final String selected = (String) expListAdapter.getChild(
                            groupPosition, childPosition);
                    Toast.makeText(getBaseContext(), selected + "**", Toast.LENGTH_LONG)
                            .show();


                    ParseQuery<ParseObject> queryGoal = ParseQuery.getQuery("Goals");
                    queryGoal.whereEqualTo("name", selected);
                    Log.d("will request", selected);

                    queryGoal.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> catList, ParseException e) {
                            if (e == null) {
                                Log.d("will add user:", ParseUser.getCurrentUser().getUsername());
                                if (catList.size() > 0) {
                                    Log.d("catList.size()", catList.size() + "");
                                    ParseObject goal = catList.get(0);
                                    goal.addUnique("users", ParseUser.getCurrentUser().getUsername());
                                    try {
                                        goal.save();
                                        Intent toWelcome = new Intent();
                                        toWelcome.putExtra("goal", selected);
                                        if(goal.getParseFile("icon") == null)
                                            ;
                                        else
                                        toWelcome.putExtra("icon", goal.getParseFile("icon").getData());

                                        setResult(RESULT_OK, toWelcome);
                                        finish();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }

                                } else {
                                    Log.d("catList.size() zero:", catList.size() + "");
                                }

                            } else {

                            }

                        }

                    });


                    return true;

                }
            });

                    flag = false;
                } else {
                }
            }
        });

    }

    private void createCollection() {
    mGoalsCollection = new LinkedHashMap<String, List<String>>();

        for (String Goal : mGroupList) {
            if (Goal.equals("Cook")) {
                loadChild(mCook);
                mGoalsCollection.put(Goal, mChildList);
            } else if (Goal.equals("IT")) {
                loadChild(mIT);
                mGoalsCollection.put(Goal, mChildList);
            }
            else if (Goal.equals("Travel")) {
                loadChild(mTravel);
                mGoalsCollection.put(Goal, mChildList);
            }

        }

        Log.d("goalsCollection size ", Integer.toString(mGoalsCollection.get("Cook").size()));
    }

    private void loadChild(List<String> laptopModels) {
        mChildList = new ArrayList<String>();
        for (String model : laptopModels)
            mChildList.add(model);

    }

    private void setGroupIndicatorToRight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    public int getDipsFromPixel(float pixels) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}



