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
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;


public class UploadActivity extends Activity {
    ListView mSteps;
    Button mAdd, mUpload;
    Spinner mList;
    List mStepsList;
    ArrayAdapter<String> mAdapter;
    final Context context = this;
    String item;
    EditText text,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_goal);
        mSteps = (ListView)findViewById(R.id.listView);
        mAdd = (Button) findViewById(R.id.add_step);
        mUpload = (Button) findViewById(R.id.upload_goal);;
        text = (EditText) findViewById(R.id. editText);
        title= (EditText) findViewById(R.id. editText2);
        mList = (Spinner)findViewById(R.id.spinner);
        mStepsList = new LinkedList();
        List l = ParseUser.getCurrentUser().getList("areas"); // get the channels that the user can add goals to db
        // set the adapter from the channels
        mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, l);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mList.setAdapter(mAdapter);



        mSteps.setClickable(true);
        // user can click on step to remove it!
        mSteps.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                item = (String) UploadActivity.this.mSteps.getItemAtPosition(position);
                Toast.makeText(UploadActivity.this, "You selected : " + item, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);


                alertDialogBuilder.setTitle("Remove this step " + item + "?");


                alertDialogBuilder
                        .setMessage("Click yes to remove!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Toast.makeText(UploadActivity.this, "You will remove " + item, Toast.LENGTH_SHORT).show();
                                mStepsList.remove(item); // remove from list
                                mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mStepsList);
                                mSteps.setAdapter(mAdapter); // update the list view

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // nothig to do
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }


        });

        mAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!text.getText().toString().equals("")) { // if the task isn't empty
                    mStepsList.add(text.getText().toString()); // add to list
                    mAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mStepsList);
                    //adapter.notifyDataSetChanged();
                    mSteps.setAdapter(mAdapter); // update list view
                    text.clearFocus();
                    text.setText(""); // set the textfiled to empty

                }


            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                if (mStepsList.size() > Constants.MiNIMUM_STEPS) { // check if above the minmim = 5 mSteps
                    // add the goal detalis
                    ParseObject obj = new ParseObject("Goals");
                    obj.put("name", title.getText().toString());
                    obj.put("Category", mList.getSelectedItem().toString());
                    obj.put("steps", mStepsList);
                    obj.saveInBackground(); // save it to db
                    text.setText(""); // set the textfiled to empty
                    text.setText(""); // set the textfiled to empty
                }


            }
        });
        

    }
}
