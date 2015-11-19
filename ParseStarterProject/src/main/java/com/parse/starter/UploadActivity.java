package com.parse.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by omar on 10/31/2015.
 */
public class UploadActivity extends Activity {
        ListView steps;
        Button add,upload;
    Spinner list;
    EditText text,title;
    List stepsList;
    ArrayAdapter<String> adapter;
    final Context context = this;
String item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_goal);
        steps = (ListView)findViewById(R.id.listView);
        add = (Button) findViewById(R.id.add_step);
        upload= (Button) findViewById(R.id.upload_goal);;
        text = (EditText) findViewById(R.id. editText);
        title= (EditText) findViewById(R.id. editText2);
        list = (Spinner)findViewById(R.id.spinner);
        stepsList = new LinkedList();
        List l = ParseUser.getCurrentUser().getList("areas");
        Log.d("area:", l.get(0).toString());

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, l);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        list.setAdapter(adapter);

        steps.setClickable(true);
        steps.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                item = (String) UploadActivity.this.steps.getItemAtPosition(position);
                Toast.makeText(UploadActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set title
                alertDialogBuilder.setTitle("Remove this step " + item + "?");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Click yes to remove!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                Toast.makeText(UploadActivity.this, "You will remove " + item, Toast.LENGTH_SHORT).show();
                                stepsList.remove(item);
                                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stepsList);
                                steps.setAdapter(adapter);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!text.getText().toString().equals("")) {
                    stepsList.add(text.getText().toString());
                    adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stepsList);
                    steps.setAdapter(adapter);
                    text.clearFocus();
                    text.setText("");

                }


            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Log.d("setOnClickListener:", "");
                if (stepsList.size() > 5 ){
                    Log.d("stepsList.size() > 5 ", "");

                    ParseObject obj = new ParseObject("Goals");
                    obj.put("name",title.getText().toString());
                    obj.put("Category",list.getSelectedItem().toString());
                    obj.put("steps", stepsList);
                    obj.saveInBackground();
                    Log.d("ssave", "");
                  //  ParsePush push = new ParsePush();
                  //  push.setChannel(list.getSelectedItem().toString());
                  //  push.setMessage("New Goal in " + list.getSelectedItem().toString());
                  //  Log.d("New Goal in " , list.getSelectedItem().toString());
                   // push.sendInBackground();
                 //   Log.d("push.sendInBackground()", list.getSelectedItem().toString());
                }


            }
        });
        

    }
}
