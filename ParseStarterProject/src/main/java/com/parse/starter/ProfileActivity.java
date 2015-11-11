package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
public class ProfileActivity extends Activity {

    // Declare Variable
    Button mlogout;
    Button mBrowse, mSetting,timeLine,Watch;
    ListView mGoalsList;
    String goal;
    List mUserGoals,mProgress,mIcons;
    Context pointer;
    private ArrayAdapter listAdapter;
    JSONObject JSONObject_ = new JSONObject();
    JSONArray JSONArray = new JSONArray();
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    Calendar calobj = Calendar.getInstance();
    TextView name, bio, badges;
    boolean flag = false;
    ImageView image;
    MyAdapter adapter;
    onItemClicked lisinter = new onItemClicked() {
        @Override
        public void postion(int i) {
            Log.d("Profile Activity got:", i + "");

            int item = (int) ProfileActivity.this.mGoalsList.getItemAtPosition(i);
            Toast.makeText(ProfileActivity.this, "You selected : " + mUserGoals.get(item), Toast.LENGTH_SHORT).show();

            Intent intenet = new Intent(ProfileActivity.this, GoalDeatils.class);
            intenet.putExtra("goal",  mUserGoals.get(item).toString());
            ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWithGoals");
            query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
            query.whereEqualTo("name", mUserGoals.get(item).toString());
            List<ParseObject> userGoalList = null;
            try{
                userGoalList = query.find();
            }
            catch( Exception e ){
                e.printStackTrace();
            }

            ParseObject obj = userGoalList.get(0);



            intenet.putExtra("progress", obj.get("progress").toString());


            Log.d("goal seleceted", mUserGoals.get(item).toString());


            startActivityForResult(intenet, 3);
        }
    };

    TextView follower, following;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from singleitemview.xml
        setContentView(R.layout.activity_profile);

        // Retrieve current user from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) findViewById(R.id.welcome);
        mGoalsList = (ListView) findViewById(R.id.goals_list);
        mlogout = (Button) findViewById(R.id.log_out);
        mBrowse = (Button) findViewById(R.id.browse);
        mSetting = (Button) findViewById(R.id.setting);
        name = (TextView) findViewById(R.id.nameText);
        bio = (TextView) findViewById(R.id.bio);
        image = (ImageView) findViewById(R.id.userIcon);
        badges = (TextView) findViewById(R.id.badgestTextNumber);
        timeLine  = (Button) findViewById(R.id.timeLine);

        follower = (TextView) findViewById(R.id.followersTextNumber);
        following = (TextView) findViewById(R.id.followeingTextNumber);
        getFollowing();
        getFollowers();

        try {
            List goals = getUserGoals(this,false);
        } catch (ParseException e) {
            e.printStackTrace();
        }


       /* ParseObject Java = new ParseObject("Goals");
        ParseObject pizza = new ParseObject("Goals");
        Java.put("name","How to learn Android Dev!?");
        Java.addAllUnique("steps", Arrays.asList("Install Eclipse from: https://eclipse.org/downloads/", "watch this video: bla bla"));
        pizza.put("name","How to cook pizza!?");
        pizza.addAllUnique("steps", Arrays.asList("Go to Walmart!", "buy frozen pizza","cook it! Easy!"));
        Java.saveInBackground();
        pizza.saveInBackground();
        */
        // Set the currentUser String into TextView

        txtuser.setText("You are logged in as " + currentUser.get("name"));


        // Locate Button in welcome.xml


        following.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                intent.putExtra("type", "following");
                //intent.putExtra("goal", goal);
                startActivityForResult(intent, 5);

            }
        });
        timeLine.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                intent.putExtra("type", "timeline");
                //intent.putExtra("goal", goal);
                startActivityForResult(intent, 5);

            }
        });
        follower.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent intent = new Intent(ProfileActivity.this, ExploreActivity.class);
                intent.putExtra("type", "followers");
                //intent.putExtra("goal", goal);
                startActivityForResult(intent, 4);

            }
        });


        name.setText(currentUser.get("name").toString());
        bio.setText(currentUser.get("bio").toString());
        ParseFile imageFile = (ParseFile) currentUser.get("image");

        imageFile.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    // data has the bytes for the image
                    if (data == null || data.length == 0) {

                    } else {
                        Bitmap imagebit = BitmapFactory.decodeByteArray(data, 0, data.length);
                        ProfileActivity.this.image.setImageBitmap(imagebit);
                        ProfileActivity.this.image.setRotation(90);
                        ProfileActivity.this.image.setVisibility(View.VISIBLE);

                    }


                } else {
                    // something went wrong
                }
            }
        });
        mSetting.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent setting = new Intent(ProfileActivity.this, SettingActivity.class);
                ProfileActivity.this.startActivityForResult(setting, 2);

            }
        });
        mlogout.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                ParseUser.logOut();
                finish();
            }
        });

        mBrowse.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                Intent goals = new Intent(ProfileActivity.this, ListGoals.class);
                ProfileActivity.this.startActivityForResult(goals, 1);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("goal");

                byte[] icon = data.getByteArrayExtra("icon");
                Log.d("I got goal in Profile: ", result);
                addGoal(result,icon);
                mUserGoals.remove("No_Goals");
                if (listAdapter == null) {

                } else {
                    //listAdapter.add(result);

                   // listAdapter.notifyDataSetChanged();

                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            if (data.getStringExtra("name")!= null)
                name.setText(data.getStringExtra("name").toString());
            if (data.getStringExtra("bio") != null)
                bio.setText(data.getStringExtra("bio").toString());
            if (data.getParcelableExtra("image") != null) {
                image.setImageBitmap((Bitmap) data.getParcelableExtra("image"));
            }

        } else if (requestCode == 3) {
            getFollowers();
            getFollowing();
            try {
                getUserGoals(this,false);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("Back:requestCode == 3", "");



        }



        else if(requestCode == 4 )
            getFollowers();
        else if (requestCode == 5)
            getFollowing();


    }

    public void addGoal(String g,byte[] icon) {

        goal = g;


        ParseObject obj = new ParseObject("UserWithGoals");

        obj.put("userName",ParseUser.getCurrentUser().getUsername());
        obj.put("name",g);
        obj.put("progress",0);
        obj.put("timeStart",df.format(calobj.getTime()));
        obj.put("timeEnd","---");
        if(icon  != null)
        obj.put("icon",icon);


        // obj.put("progress",0);
        try {
            obj.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //  }
            //}
            //   }
            //   });

            try {
                getUserGoals(this, true);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    /*
    if (mUserGoals != null) {
        Log.d("found user goals", mUserGoals.size() + "");
        obj.saveInBackground();
    } else {
        Log.d("found user goals", "with 0 goals");
        mUserGoals = new LinkedList();


    }


    // obj.addAllUnique("GoalsList", mUserGoals);

    Log.d("Added:size() != 0", ParseUser.getCurrentUser().getUsername() + "  with  " + ProfileActivity.this.goal);
    mBrowse = (Button) findViewById(R.id.browse);
//                        mBrowse.setText(mUserGoals.size() + "");

    listAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.group_item, R.id.usergoal, mUserGoals);
    mGoalsList = (ListView) findViewById(R.id.Goals_list);
    if (listAdapter == null) {
        Log.d("listAdapter == null", "");
    }
    if (mGoalsList == null)
        Log.d("mGoalsList == null ", "");


        ProfileActivity.this.mGoalsList.setAdapter(listAdapter);
        ProfileActivity.this.mGoalsList.setTextFilterEnabled(true);
        ProfileActivity.this.mGoalsList.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) ProfileActivity.this.mGoalsList.getItemAtPosition(position);
                Toast.makeText(ProfileActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();

                Intent intenet = new Intent(ProfileActivity.this, GoalDeatils.class);

                for (int i = 0; i < ProfileActivity.this.JSONArray.length(); i++) {
                    try {
                        JSONObject_ = JSONArray.getJSONObject(i);
                        Log.d("JSONObject.get(\"Name:\")",JSONObject_.get("Name:").toString());

                        if (JSONObject_.get("Name:").toString().equals(item)) {

                            intenet.putExtra("goal", item);
                            Log.d("will get::::::", JSONObject_.get("Progress:").toString());

                            intenet.putExtra("progress", JSONObject_.get("Progress:").toString());

                            break;
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }

                }

                Log.d("goal&&&&&&&&&&", item);
//                                    Log.d("progress", mUserGoals.get(mUserGoals.indexOf(item) + 1).toString());
                intenet.putExtra("object", JSONObject_.toString());
                startActivityForResult(intenet, 3);

            }
        });



}


} else {

Log.d("score", "Error: " + e.getMessage());
}
Log.d("Added:", ParseUser.getCurrentUser().getUsername() + "//  with  //" + ProfileActivity.this.goal);

}


});
}
*/
    public List getUserGoals(Context p, boolean b) throws ParseException {
        pointer = p;
        List l;
        flag = b ;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWithGoals");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());
        List<ParseObject> userList =  query.find();//;InBackground(new FindCallback<ParseObject>() {
           // public void done(List<ParseObject> userList, ParseException e) {
                // JSONObject JSONObject = new JSONObject();
                // JSONArray JSONArray = new JSONArray();
                mUserGoals = new LinkedList();
                mProgress = new LinkedList();
                mIcons = new LinkedList();
                   //     if (e == null) {
                    ParseObject obj = new ParseObject("UserWithGoals");
                    if (userList.size() == 0) {


                        mUserGoals.add("No_Goals");
                    } else {

                        mUserGoals.remove("No_Goals");


                        for (int i = 0; i < userList.size(); i++) {

                                    obj = userList.get(i);

                            if(obj.getString("timeEnd").equals("---") ){
                                mUserGoals.add(obj.get("name"));
                                    mProgress.add(obj.get("progress"));
                                    mIcons.add(obj.get("icon"));
                            }
                                }



                        }


                      Log.d("found user goals", mUserGoals.size() + "------");
                    if (mUserGoals.size() != 0) {
                        mBrowse = (Button) findViewById(R.id.browse);
//                            mBrowse.setText(mUserGoals.size() + "");
                        ProfileActivity.this.mGoalsList = (ListView) findViewById(R.id.Goals_list);
                        if(mUserGoals.get(0).toString().equals("No_Goals"))
                            listAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.group_item, R.id.usergoal, mUserGoals);
                        else
                        adapter = new MyAdapter(this,mUserGoals,mProgress,mIcons,lisinter);
                        //listAdapter = new ArrayAdapter(ProfileActivity.this, R.layout.group_item, R.id.usergoal, mUserGoals);
                        if (listAdapter == null)
                            Log.d("listAdapter == null", "");
                        if (ProfileActivity.this.mGoalsList == null)
                            Log.d("mGoalsList == null", "");
                        ;

                        if (mUserGoals.size() == 1 && mUserGoals.get(0).equals("No_Goals"))
                            badges.setText("0");
                        else
                            badges.setText(mUserGoals.size() + "");
                        if(mUserGoals.get(0).toString().equals("No_Goals"))
                            ProfileActivity.this.mGoalsList.setAdapter(listAdapter);
                        else
                            ProfileActivity.this.mGoalsList.setAdapter(adapter);

                        ProfileActivity.this.mGoalsList.setTextFilterEnabled(true);
                        ProfileActivity.this.mGoalsList.setClickable(true);


                       // ProfileActivity.this.mGoalsList.setOnItemClickListener(onItemClickListener);

                        ProfileActivity.this.mGoalsList.setOnItemClickListener( new AdapterView.OnItemClickListener()

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
                                try{
                                     userGoalList = query.find();
                                }
                                catch( Exception e ){
                                    e.printStackTrace();
                                }

                                ParseObject obj = userGoalList.get(0);



                                intenet.putExtra("progress", obj.get("progress").toString());


                                Log.d("goal seleceted", item);


                                startActivityForResult(intenet, 3);
                                }





                        });


                    }

           //     } else {
           //         Log.d("score", "Error: " + e.getMessage());

           //     }

         //   }
       // });

        return mUserGoals;
    }


    public void getFollowing() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("from", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> followList, ParseException e) {
                following = (TextView) findViewById(R.id.followeingTextNumber);
                following.setText(followList.size()+"");

            }
        });

    }


    public void getFollowers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Follow");
        query.whereEqualTo("to", ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> followList, ParseException e) {
                follower = (TextView) findViewById(R.id.followersTextNumber);
                follower.setText(followList.size()+"");




            }
        });




    }
}