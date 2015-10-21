package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by omar on 10/14/2015.
 */
public class GoalDeatils extends Activity {
    String goal;
    int progress;
    ProgressBar pBar;
    TextView goalTitle,nextStep,NextStepText;
    List GoalSteps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent goalIntent = getIntent();

        goal = goalIntent.getStringExtra("goal");
        progress = Integer.parseInt(goalIntent.getStringExtra("progress"));
        setContentView(R.layout.goal_details);

        pBar = (ProgressBar) findViewById(R.id.progressBar);
        goalTitle =  (TextView)findViewById(R.id.goal_title);
        nextStep=  (TextView)findViewById(R.id.nextStepTitle);
        NextStepText=  (TextView)findViewById(R.id.NextStepText);
        goalTitle.setText(goal);
        pBar.setProgress(progress);
        pBar.setVisibility(View.VISIBLE);
        getSteps();


    }


public void getSteps(){

    ParseQuery<ParseObject> query = ParseQuery.getQuery("Goals");
    query.whereEqualTo("name", goal);
    query.findInBackground(new FindCallback<ParseObject>() {
        public void done(List<ParseObject> userList, ParseException e) {
            if (e == null) {
                ParseObject obj = userList.get(0);

                GoalSteps = (List) obj.get("steps");
                NextStepText.setText(GoalSteps.get(progress).toString()+"");

            } else {
                e.printStackTrace();
            }



        }
    });


}}
