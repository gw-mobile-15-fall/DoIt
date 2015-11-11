package com.parse.starter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by omar on 10/14/2015.
 */
public class GoalDeatils extends Activity {
    String goal;



    int progress;
    ProgressBar pBar;
    Button nextStepButton,CameraButton,exploreButton,mShare,mWatch;
    TextView goalTitle,nextStep,NextStepText;
    List GoalSteps;
    ImageView   mImageView,timeline;
   org.json.JSONArray JSONArray = new JSONArray();
    JSONObject test = new JSONObject();
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Calendar calobj = Calendar.getInstance();
    Bitmap imageBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent goalIntent = getIntent();

         goal = goalIntent.getStringExtra("goal");
        Log.d("Goa; = ", goal + " ---------------------------");
        progress = Integer.parseInt(goalIntent.getStringExtra("progress"));


        setContentView(R.layout.goal_details);
        mShare = (Button) findViewById(R.id.Share);
        pBar = (ProgressBar) findViewById(R.id.progressBar);
        goalTitle =  (TextView)findViewById(R.id.goal_title);
        nextStep=  (TextView)findViewById(R.id.nextStepTitle);
        NextStepText=  (TextView)findViewById(R.id.NextStepText);
        nextStepButton = (Button) findViewById(R.id.nextStepButton);
        CameraButton = (Button) findViewById(R.id.CameraButton);
           mImageView = (ImageView) findViewById(R.id.imageView);
        exploreButton = (Button) findViewById(R.id.explorebutton);
        timeline  = (ImageView) findViewById(R.id.messageIcon);
        mWatch = (Button) findViewById(R.id.Watch);
        mWatch.setVisibility(View.INVISIBLE);
    getSteps();




        mShare.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
               shareStep();

            }
        });


        exploreButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(GoalDeatils.this,ExploreActivity.class);
                intent.putExtra("type","explore");
                intent.putExtra("goal",goal);
                startActivityForResult(intent, 1);

            }
        });



        CameraButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);

            }
        });


        nextStepButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                if( progress < GoalSteps.size()-1)
                {
                    progress++;
                    NextStepText.setText(GoalSteps.get(progress).toString() + "");
                    saveProgress(false);
                    pBar.setProgress(progress);
                    mImageView.setVisibility(View.INVISIBLE);  Log.d("Watch", "&&&&&&&&&&&&&&&&&&&&&&");
                    if( NextStepText.getText().toString().contains("watch")) {
                        Log.d("Watch", "&&&&&&&&&&&&&&&&&&&&&&");
                        mWatch.setVisibility(View.VISIBLE);
                        mWatch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String temp = NextStepText.getText().toString();
                                int start = temp.indexOf("http");

                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(temp.substring(start, temp.length()))));
                            }
                        });
                    }
                    else
                        mWatch.setVisibility(View.INVISIBLE);

                }
                else
                {
                    NextStepText.setText("Congrats!!");
                    pBar.setProgress(10);
                    nextStep.setVisibility(View.GONE);
                    saveProgress(true);
                    nextStepButton.setText("Wow! No more tasks!");
                    nextStepButton.setEnabled(false);
                    mImageView.setVisibility(View.GONE);
                    removeNameFromGoal();
                }

            }
        });

        goalTitle.setText(goal);
        pBar.setProgress(progress);
        pBar.setVisibility(View.VISIBLE);
        getSteps();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                 imageBitmap = (Bitmap) extras.get("data");
                mImageView.setImageBitmap(imageBitmap);
                mImageView.setVisibility(View.VISIBLE);

            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
        }
    }

    public void saveProgress(final boolean finished) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWithGoals");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
        query.whereEqualTo("name", goal);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    ParseObject obj = userList.get(0);
                    obj.put("progress", progress);
                    obj.put("lastUpdate", System.currentTimeMillis() );
                    if(finished)
                        obj.put("timeEnd", df.format(calobj.getTime()));
                    obj.saveInBackground();
                }




            }
        });

    }


    public void getSteps(){

    ParseQuery<ParseObject> query = ParseQuery.getQuery("Goals");
        query.whereEqualTo("name", goal);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userList, ParseException e) {
                if (e == null) {
                    ParseObject obj = userList.get(0);

                    GoalSteps = (List) obj.get("steps");
                    NextStepText.setText(GoalSteps.get(progress).toString() + "");
                    if( NextStepText.getText().toString().contains("watch")) {
                        Log.d("Watch", "&&&&&&&&&&&&&&&&&&&&&&");
                        mWatch.setVisibility(View.VISIBLE);
                        mWatch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String temp = NextStepText.getText().toString();
                                int start = temp.indexOf("http");

                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(temp.substring(start, temp.length()))));
                            }
                        });
                    }


                    else
                        mWatch.setVisibility(View.INVISIBLE);
                } else {
                    e.printStackTrace();
                }


        }
    });



}
    @Override
    public void onBackPressed() {

        if( progress < GoalSteps.size()-1)
        {
            saveProgress(false);

        }
        else
        {
            saveProgress(true);
        }



        Intent intent = this.getIntent();
        try {
            test.put("Progress:",progress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("object", test.toString());
        this.setResult(RESULT_OK, intent);

        Log.d("onBackPressed", "progress:"+progress);
        finish();
    }
  /*  @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy", "claaed");
        Intent intent = this.getIntent();
        intent.putExtra("object", test.toString());
        this.setResult(RESULT_OK, intent);
        saveProgress(false);

        finish();




    }

    @Override
    public void finish() {
        Log.d("finish", "claaed");

        Intent intent = this.getIntent();
        intent.putExtra("object", test.toString());
         this.setResult(RESULT_OK, intent);
        //saveProgress(false);


        super.finish();
    }*/

    public void removeNameFromGoal(){


        ParseQuery<ParseObject> queryGoal = ParseQuery.getQuery("Goals");
        queryGoal.whereEqualTo("name", goal);
        Log.d("will remove namr from", goal);

        queryGoal.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> catList, ParseException e) {
                if (e == null) {
                    Log.d("will remove user:",ParseUser.getCurrentUser().getUsername());
                    if (catList.size() > 0) {
                        Log.d("catList.size()", catList.size() + "");
                        ParseObject goal = catList.get(0);
                        List al = new LinkedList<String>();
                        al = goal.getList("users");
                        al.remove(ParseUser.getCurrentUser().getUsername());

                        goal.put("users",al);
                        // goal.put("CCC",ParseUser.getCurrentUser());
                        goal.saveInBackground();

                    }
                    else {
                        Log.d("catList.size() zero:",catList.size()+"");
                    }

                }
                else{

                }

            }

        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      if(id == R.id.action_share){
            Log.d("", "share button pressed");
            shareStep();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void shareStep(){


        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_step_text) + ":" + progress + " in :" + goal);


        if(mImageView.getDrawable() != null){
            shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(imageBitmap));
            shareIntent.setType("image/*");
        }
        else{
            shareIntent.setType("text/plain");
        }


        startActivity(shareIntent);
    }
    public Uri getImageUri( Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
