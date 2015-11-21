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
    ListView steps;
    Button sub, unsub;
    Spinner list;
    EditText text, title;
    List subChannels;
    ArrayAdapter<String> adapter;
    TextView titleText, stepsText, channels;
    final Context context = this;
    String item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_goal);
        steps = (ListView) findViewById(R.id.listView);
        sub = (Button) findViewById(R.id.add_step);
        unsub = (Button) findViewById(R.id.upload_goal);
        unsub.setVisibility(View.GONE);
        text = (EditText) findViewById(R.id.editText);
        title = (EditText) findViewById(R.id.editText2);
        titleText = (TextView) findViewById(R.id.goal_t);
        stepsText = (TextView) findViewById(R.id.taskTest);
        channels = (TextView) findViewById(R.id.categories);
        titleText.setVisibility(View.GONE);
        stepsText.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        text.setVisibility(View.GONE);

        list = (Spinner) findViewById(R.id.spinner);
        channels.setText(getResources().getString(R.string.choose_channel)); // set tilte
        sub.setText(getResources().getString(R.string.subscribe)); // set button text

        subChannels = ParseInstallation.getCurrentInstallation().getList("channels"); // grab the current channels of this user
        if (subChannels == null || subChannels.size() == 0) { // no Channels!
            subChannels = new LinkedList();
            subChannels.add("No Channels");
        }

        // list the user's channels
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, subChannels);
        steps.setAdapter(adapter);

        List l = new LinkedList(); // current channels to subscribe
        l.add("IT");
        l.add("Cook");
        l.add("Travel");

        // set up the channels to subscribe
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, l);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        list.setAdapter(adapter);
        steps.setClickable(true);


        steps.setOnItemClickListener(new AdapterView.OnItemClickListener() // unsubscribe when clicked!

        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                item = (String) subscribeActivity.this.steps.getItemAtPosition(position);
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
                                Toast.makeText(subscribeActivity.this, getResources().getString(R.string.unsubscribe_from) + " "  + item, Toast.LENGTH_SHORT).show();
                                ParsePush.unsubscribeInBackground(item); // unsubscribe in backend
                                subChannels.remove(list.getSelectedItem().toString()); // remove from list view
                                if (subChannels.size() == 0)
                                    subChannels.add("No Channels");

                                // update the list
                                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, subChannels);
                                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                steps.setAdapter(adapter);

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

        sub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ParsePush.subscribeInBackground(list.getSelectedItem().toString()); // subscribe in db

                subChannels.remove("No Channels"); // remove 'no channels/ if any
                subChannels.add(list.getSelectedItem().toString()); // update the list with the new channels
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, subChannels);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                steps.setAdapter(adapter);

            }


        });

        /*unsub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ParsePush.unsubscribeInBackground(item);
                subChannels.remove(item);
                if (subChannels.size() == 0)
                    subChannels.add("No Channels");
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, subChannels);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                steps.setAdapter(adapter);


            }
        });*/


    }
}
