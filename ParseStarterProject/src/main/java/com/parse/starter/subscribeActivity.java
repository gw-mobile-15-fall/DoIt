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

public class subscribeActivity  extends Activity {
    ListView steps;
    Button sub,unsub;
    Spinner list;
    EditText text,title;
    List subChannels;
    ArrayAdapter<String> adapter;
    TextView titleText,stepsText,channels;
    final Context context = this;
    String item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_goal);
        steps = (ListView)findViewById(R.id.listView);
        sub = (Button) findViewById(R.id.add_step);
        unsub= (Button) findViewById(R.id.upload_goal);;
        text = (EditText) findViewById(R.id. editText);
        title= (EditText) findViewById(R.id. editText2);
        titleText = (TextView) findViewById(R.id.goal_t);
        stepsText = (TextView) findViewById(R.id.taskTest);
        channels = (TextView) findViewById(R.id.categories);
        titleText.setVisibility(View.GONE);
        stepsText.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        text.setVisibility(View.GONE);
        channels.setText("choose to sub/unsub");
        sub.setText("sub");
        unsub.setText("unsub");

        list = (Spinner)findViewById(R.id.spinner);
        subChannels = ParseInstallation.getCurrentInstallation().getList("channels");
        if(subChannels  == null || subChannels.size() == 0 )
        {subChannels = new LinkedList();
            subChannels.add("No Channels");
        }


        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, subChannels);
        steps.setAdapter(adapter);

        List l = new LinkedList();
        l.add("IT");
        l.add("Cook");
        l.add("Travel");
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, l);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        list.setAdapter(adapter);
        steps.setClickable(true);
        steps.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                 item = (String) subscribeActivity.this.steps.getItemAtPosition(position);
                Toast.makeText(subscribeActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set title
                alertDialogBuilder.setTitle("Unsubscribe From "+item +"?");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Click yes to Unsubscribe!")
                        .setCancelable(false)
                        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, close
                                // current activity
                                Toast.makeText(subscribeActivity.this, "You will Unsubscribe from " + item , Toast.LENGTH_SHORT).show();
                                ParsePush.unsubscribeInBackground(list.getSelectedItem().toString());
                                subChannels.remove(list.getSelectedItem().toString());
                                if(subChannels.size() == 0 )
                                    subChannels.add("No Channels");
                                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, subChannels);
                                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                steps.setAdapter(adapter);

                            }
                        })
                        .setNegativeButton("No",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }


        });

        sub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ParsePush.subscribeInBackground(list.getSelectedItem().toString());

                subChannels.remove("No Channels");
                subChannels.add(list.getSelectedItem().toString());
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, subChannels);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                steps.setAdapter(adapter);

            }



    });

        unsub.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                ParsePush.unsubscribeInBackground(list.getSelectedItem().toString());
                subChannels.remove(list.getSelectedItem().toString());
                if(subChannels.size() == 0 )
                    subChannels.add("No Channels");
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, subChannels);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                steps.setAdapter(adapter);


            }
        });


    }
}
