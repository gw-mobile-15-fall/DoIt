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
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * Created by omar on 10/13/2015.
 */
public class ListGoals extends Activity{


        List<String> groupList;
        List<String> childList;
        Map<String, List<String>> goalsCollection ;
        ExpandableListView expListView;
    List<String> travel = new LinkedList<String>();
    List<String> it = new LinkedList<String>();
    List<String> cook = new LinkedList<String>();
//    goalsCollection = new LinkedHashMap<String, List<String>>();
boolean flag = true;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.list_goals);

            createGroupList();

           /* createCollection();

            expListView = (ExpandableListView) findViewById(R.id.goals_list);
            final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                    this, groupList, goalsCollection);
            expListView.setAdapter(expListAdapter);

            //setGroupIndicatorToRight();

            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    final String selected = (String) expListAdapter.getChild(
                            groupPosition, childPosition);
                    Toast.makeText(getBaseContext(), selected+"**", Toast.LENGTH_LONG)
                            .show();



                    Intent toWelcome = new Intent();
                    toWelcome.putExtra("goal",selected);
                    setResult(RESULT_OK, toWelcome);
                    finish();
                    return true;
                }
            });*/
        }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Goals");
        query.whereNotEqualTo("Category", "");
        Log.d("will request", "Category ");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> catList, ParseException e) {
                if (e == null) {
                    Log.d("ParseException", "null ");

                    for (int i = 0; i < catList.size(); i++) {
                        if (!groupList.contains(catList.get(i).getString("Category")))
                            groupList.add(catList.get(i).getString("Category"));

                        
                        if(catList.get(i).getString("Category").equals("IT")) {
                            it.add(catList.get(i).getString("name"));
                            Log.d("Added to IT", catList.get(i).getString("name"));
                        }
                        else if(catList.get(i).getString("Category").equals("Travel")) {
                            travel.add(catList.get(i).getString("name"));
                            Log.d("Added to travel", catList.get(i).getString("name"));

                        }
                        else  if(catList.get(i).getString("Category").equals("Cook")) {


                            cook.add(catList.get(i).getString("name"));
                            Log.d("Added to Cook", catList.get(i).getString("name"));
                        }
                    }
                   // createCollection();
                    Log.d("********", "");
                    Log.d("score", "Retrieved " + groupList.size() + " scores");


                      createCollection();

            expListView = (ExpandableListView) findViewById(R.id.goals_list);
            final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(
                    ListGoals.this, groupList, goalsCollection);
            expListView.setAdapter(expListAdapter);

            //setGroupIndicatorToRight();

            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {
                    final String selected = (String) expListAdapter.getChild(
                            groupPosition, childPosition);
                    Toast.makeText(getBaseContext(), selected + "**", Toast.LENGTH_LONG)
                            .show();



                    Intent toWelcome = new Intent();
                    toWelcome.putExtra("goal",selected);
                    setResult(RESULT_OK, toWelcome);
                    finish();
                    return true;
                }
            });












                    flag = false;
                } else {
                    Log.d("############", "Error: ");
                }
            }
        });

    }

    private void createCollection() {
        // preparing laptops collection(child)

        Log.d("********", " start grou :" + groupList.size());
       /* groupList.add("Cook");
        groupList.add("IT");
        groupList.add("Travel");
        cook.add("pizaa");
        travel.add("Paris");
        travel.add("NY");
        travel.add("LA");
cook.add("burger");
        it.add("iOS");
        it.add("Android");
        */

        goalsCollection = new LinkedHashMap<String, List<String>>();

        for (String Goal : groupList) {
            if (Goal.equals("Cook")) {
                Log.d("********", " start Cook::::");

                loadChild(cook);
                goalsCollection.put(Goal, childList);
            } else if (Goal.equals("IT")) {

                Log.d("********", " start IT::::");

                loadChild(it);
                goalsCollection.put(Goal, childList);
            }
            else if (Goal.equals("Travel")) {
                loadChild(travel);
                goalsCollection.put(Goal, childList);
            }

        }

        Log.d("goalsCollection size ", Integer.toString(goalsCollection.get("Cook").size()));
    }

    private void loadChild(List<String> laptopModels) {
        childList = new ArrayList<String>();
        for (String model : laptopModels)
            childList.add(model);

    }

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}



