package com.parse.starter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

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
                    obj.put("steps",stepsList);
                    obj.saveInBackground();
                    Log.d("ssave", "");

                }


            }
        });
        

    }
}
