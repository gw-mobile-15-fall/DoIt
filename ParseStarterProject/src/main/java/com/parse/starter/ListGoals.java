package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.*;

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
        Map<String, List<String>> goalsCollection;
        ExpandableListView expListView;
    List<String> travel = new LinkedList<String>();
    List<String> it = new LinkedList<String>();
    List<String> cook = new LinkedList<String>();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.list_goals);

            createGroupList();

            createCollection();

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
                    Toast.makeText(getBaseContext(), selected, Toast.LENGTH_LONG)
                            .show();

                    return true;
                }
            });
        }

    private void createGroupList() {
        groupList = new ArrayList<String>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Goals");
        query.whereNotEqualTo("Category", "");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> catList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < catList.size(); i++) {
                        if (!groupList.contains(catList.get(i).getString("Category")))
                            groupList.add(catList.get(i).getString("Category"));
                        if(catList.get(i).getString("Category").equals("IT"))
                            it.add(catList.get(i).getString("name"));
                        else if(catList.get(i).getString("Category").equals("Travel"))
                            travel.add(catList.get(i).getString("name"));
                        else  if(catList.get(i).getString("Category").equals("Cook"))
                            cook.add(catList.get(i).getString("name"));
                    }
                    Log.d("********", "");
                    Log.d("score", "Retrieved " + catList.size() + " scores");
                } else {
                    Log.d("score", "Error: ");
                }
            }
        });

    }

    private void createCollection() {
        // preparing laptops collection(child)


        goalsCollection = new LinkedHashMap<String, List<String>>();

        for (String Goal : groupList) {
            if (Goal.equals("Cook")) {
                loadChild(cook);
            } else if (Goal.equals("IT"))
                loadChild(it);
            else if (Goal.equals("Travel"))
                loadChild(travel);

            goalsCollection.put(Goal, childList);
        }
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



