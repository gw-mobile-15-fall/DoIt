package com.parse.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.ParsePush;

import java.util.LinkedList;
import java.util.List;

public class subscribeActivity extends Activity {
    ListView mSteps;
    Button mSub, mUnsub;
    Spinner mList;
    List mSubChannels;
    ArrayAdapter<String> mAdapter;
    TextView titleText, stepsText, channels;
    final Context context = this;
    String item;
    EditText text, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_goal);
        mSteps = (ListView) findViewById(R.id.listView);
        mSub = (Button) findViewById(R.id.add_step);
        mUnsub = (Button) findViewById(R.id.upload_goal);
        mUnsub.setVisibility(View.GONE);
        text = (EditText) findViewById(R.id.editText);
        title = (EditText) findViewById(R.id.editText2);
        titleText = (TextView) findViewById(R.id.goal_t);
        stepsText = (TextView) findViewById(R.id.taskTest);
        channels = (TextView) findViewById(R.id.categories);
        titleText.setVisibility(View.GONE);
        stepsText.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        text.setVisibility(View.GONE);

        mList = (Spinner) findViewById(R.id.spinner);
        channels.setText(getResources().getString(R.string.choose_channel)); // set tilte
        mSub.setText(getResources().getString(R.string.subscribe)); // set button text

        mSubChannels = ParseInstallation.getCurrentInstallation().getList("channels"); // grab the current channels of this user
        if (mSubChannels == null || mSubChannels.size() == 0) { // no Channels!
            mSubChannels = new LinkedList();
            mSubChannels.add("No Channels");
        }

        // list the user's channels
        mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mSubChannels);
        mSteps.setAdapter(mAdapter);

        List l = new LinkedList(); // current channels to subscribe
        l.add("IT");
        l.add("Cook");
        l.add("Travel");

        // set up the channels to subscribe
        mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, l);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mList.setAdapter(mAdapter);
        mSteps.setClickable(true);


        mSteps.setOnItemClickListener(new AdapterView.OnItemClickListener() // unsubscribe when clicked!

        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                item = (String) subscribeActivity.this.mSteps.getItemAtPosition(position);
                Toast.makeText(subscribeActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set title
                alertDialogBuilder.setTitle(getResources().getString(R.string.unsubscribe_from) + item + "?");

                // set dialog message
                alertDialogBuilder
                        .setMessage(getResources().getString(R.string.unsubscribe_yes))
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                Toast.makeText(subscribeActivity.this, getResources().getString(R.string.unsubscribe_from) + " " + item, Toast.LENGTH_SHORT).show();
                                ParsePush.unsubscribeInBackground(item); // unsubscribe in backend
                                mSubChannels.remove(mList.getSelectedItem().toString()); // remove from list view
                                if (mSubChannels.size() == 0)
                                    mSubChannels.add("No Channels");

                                // update the list
                                mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mSubChannels);
                                mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                mSteps.setAdapter(mAdapter);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // nothing
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
            }


        });

        mSub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ParsePush.subscribeInBackground(mList.getSelectedItem().toString()); // subscribe in db

                mSubChannels.remove("No Channels"); // remove 'no channels/ if any
                mSubChannels.add(mList.getSelectedItem().toString()); // update the list with the new channels
                mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mSubChannels);
                mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                mSteps.setAdapter(mAdapter);

            }


        });

        /*mUnsub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ParsePush.unsubscribeInBackground(item);
                mSubChannels.remove(item);
                if (mSubChannels.size() == 0)
                    mSubChannels.add("No Channels");
                mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mSubChannels);
                mAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                mSteps.setAdapter(mAdapter);


            }
        });*/


    }
}
