package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class GoalDeatils extends AppCompatActivity {
    private String goal;

    private  int mProgress;
    private ProgressBar mBar;
    private Button mNextStepButton, mCameraButton, mExploreButton,mShare,mWatch;
    private TextView mGoalTitle, mNextStep, mNextStepText;
    private List mGoalSteps;
    private  ImageView   mImageView;

    private  JSONObject test = new JSONObject();
    private DateFormat df = new SimpleDateFormat("dd/MM/yy");
    private Calendar calobj = Calendar.getInstance();
    private Bitmap mImageBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent goalIntent = getIntent();

         goal = goalIntent.getStringExtra("goal");
        Log.d("Goa; = ", goal + " ---------------------------");
        mProgress = Integer.parseInt(goalIntent.getStringExtra("progress"));


        setContentView(R.layout.goal_details);
        mShare = (Button) findViewById(R.id.Share);
        mBar = (ProgressBar) findViewById(R.id.progressBar);
        mGoalTitle =  (TextView)findViewById(R.id.goal_title);
        mNextStep =  (TextView)findViewById(R.id.nextStepTitle);
        mNextStepText =  (TextView)findViewById(R.id.NextStepText);
        mNextStepButton = (Button) findViewById(R.id.nextStepButton);
        mCameraButton = (Button) findViewById(R.id.CameraButton);
           mImageView = (ImageView) findViewById(R.id.imageView);
        mExploreButton = (Button) findViewById(R.id.explorebutton);
        mWatch = (Button) findViewById(R.id.Watch);
        mWatch.setVisibility(View.INVISIBLE);
        getSteps();
        mGoalTitle.setText(goal);
        mBar.setProgress(mProgress);
        mBar.setVisibility(View.VISIBLE);



        mShare.setVisibility(View.GONE);
        /*mShare.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) { // share button presses
                // Logout current user
                shareStep();

            }
        });*/


        mExploreButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {  // explore Button
                // Logout current user
                Intent intent = new Intent(GoalDeatils.this, ExploreActivity.class);
                intent.putExtra("type", "explore");
                intent.putExtra("goal", goal); // send the goal name to lookup in parse
                startActivityForResult(intent, 1);

            }
        });



        mCameraButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) { // camera button presses
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, Constants.CAMERA);

            }
        });


        mNextStepButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) { // next Step Button pressed

                if (mProgress < mGoalSteps.size() - 1) // if not last step
                {
                    mProgress++; // increase the mProgress
                    mNextStepText.setText(mGoalSteps.get(mProgress).toString() + ""); // set the next step
                    saveProgress(false); // save the mProgress in DB
                    mBar.setProgress(mProgress); // update the mProgress bar
                    mImageView.setVisibility(View.INVISIBLE); // remove the image taken if any
                    mBar.getProgressDrawable().setColorFilter(getColor(mProgress), PorterDuff.Mode.SRC_IN); // set color of mProgress bar
                    checkIfExternal();
                } else // last step
                {
                    mNextStepText.setText(getResources().getString(R.string.congrats)); // show congrats message
                    mBar.setProgress(10); // set the mProgress to completed
                    mNextStep.setVisibility(View.GONE);
                    saveProgress(true);// save mProgress
                    mBar.getProgressDrawable().setColorFilter(getColor(mProgress), PorterDuff.Mode.SRC_IN); // set color of mProgress bar

                    mNextStepButton.setText(getResources().getString(R.string.no_task_left)); // set next step to no task left
                    mNextStepButton.setEnabled(false);
                    mImageView.setVisibility(View.GONE);
                    removeNameFromGoal(); // remove the user from the members who have this goal
                }

            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.CAMERA) { // user took a pic of a goal
            if(resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                 mImageBitmap = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(mImageBitmap); // show the pic of a goal
                mImageView.setVisibility(View.VISIBLE);

            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
        }




    }

    public void saveProgress(final boolean finished) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWithGoals");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername()); // get the current  user
        query.whereEqualTo("name", goal); // get this goal from this user
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    ParseObject obj = userList.get(0); // one result will be returened
                    obj.put("progress", mProgress);
                    obj.put("lastUpdate", System.currentTimeMillis() ); // set the time of last update
                    if(finished) // if user finished, add the finish time
                        obj.put("timeEnd", df.format(calobj.getTime()));
                    obj.saveInBackground();
                }




            }
        });

    }


    public void getSteps(){ // get the mSteps of this goal

    ParseQuery<ParseObject> query = ParseQuery.getQuery("Goals");
        query.whereEqualTo("name", goal);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    ParseObject obj = userList.get(0);

                    mGoalSteps = (List) obj.get("steps"); // add the mSteps in local list
                    mNextStepText.setText(mGoalSteps.get(mProgress).toString()); // set the current step based on mProgress
                    checkIfExternal();

                }

                else {
                    e.printStackTrace();
                }


        }
    });



}

    private void checkIfExternal() { // check if there is an external link
        if( mNextStepText.getText().toString().contains("watch")) { // if the next step is video

            mWatch.setVisibility(View.VISIBLE);
            mWatch.setText("Watch");
            mWatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String temp = mNextStepText.getText().toString();
                    int start = temp.indexOf("http");

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(temp.substring(start, temp.length()))));
                }
            });
        }
        else if( mNextStepText.getText().toString().contains("read")) { // if the next step is article

            mWatch.setVisibility(View.VISIBLE);
            mWatch.setText("Read");
            mWatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String temp = mNextStepText.getText().toString();
                    int start = temp.indexOf("http");

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(temp.substring(start, temp.length()))));
                }
            });
        }


        else
            mWatch.setVisibility(View.INVISIBLE); // make the watch inviable if it is normal task

    }

    @Override
    public void onBackPressed() { // if back presses, save current mProgress

        if( mProgress < mGoalSteps.size()-1)
        {
            saveProgress(false); // save, not finished goal

        }
        else
        {
            saveProgress(true); // save, finished goal
        }



        Intent intent = this.getIntent();
        try {
            test.put("Progress:", mProgress); // send lateset mProgress to caller to be updated
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("object", test.toString());
        this.setResult(RESULT_OK, intent);

        Log.d("onBackPressed", "progress:" + mProgress);
        finish();
    }


    private void removeNameFromGoal(){ // remove the name from db


        ParseQuery<ParseObject> queryGoal = ParseQuery.getQuery("Goals");
        queryGoal.whereEqualTo("name", goal);
        Log.d("will remove namr from", goal);

        queryGoal.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> catList, ParseException e) {
                if (e == null) {
                    Log.d("will remove user:", ParseUser.getCurrentUser().getUsername());
                    if (catList.size() > 0) {
                        Log.d("catList.size()", catList.size() + "");
                        ParseObject goal = catList.get(0); // get the  goal
                        List al = new LinkedList<String>();
                        al = goal.getList("users");  // get the users list
                        al.remove(ParseUser.getCurrentUser().getUsername()); // remove the user

                        goal.put("users", al); // add the list back
                        goal.saveInBackground(); // save

                    } else {
                        Log.d("catList.size() zero:", catList.size() + "");
                    }

                } else {

                }

            }

        });



    }


    private void shareStep(){ // share content


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_step_text) + ":" + mProgress + " in :" + goal);


        if(mImageView.getDrawable() != null){ // if there is a pic
            shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(mImageBitmap));
            shareIntent.setType("image/*");
        }
        else{
            shareIntent.setType("text/plain");
        }


        startActivity(shareIntent);
    }
    private Uri getImageUri( Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private int getColor(int progress){

        if ( progress > 0 && progress < 4 )
            return Color.WHITE;
        else if  ( progress >= 4  && progress < 7 )
            return Color.GRAY;
        else if ( progress >= 7  && progress < 9 )
            return Color.GREEN;
        else
            return Color.RED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        menu.removeItem(R.id.action_share);
        inflater.inflate(R.menu.menu_goal_details, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_share) {
            shareStep();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
