package com.parse.starter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GraphActivity extends Activity {
    private Context context = this;
    private static BarChart chart;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> goalsDone = new LinkedList<>();
        List<String> Duration = new LinkedList<>();
        iv = (ImageView) findViewById(R.id.imageView3);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserWithGoals");
        query.whereNotEqualTo("timeEnd", "---");
        query.whereEqualTo("userName", ParseUser.getCurrentUser().getUsername());

        List<ParseObject> goalsList = null;
        try {
            goalsList = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (goalsList != null || goalsList.size() > 0) {
            ParseObject obj = new ParseObject("UsersWithGoals");
            for (int i = 0; i < goalsList.size(); i++) {
                obj = goalsList.get(i);
                goalsDone.add(obj.get("name").toString());
                Duration.add(getDifferenceDays(obj.getCreatedAt(), obj.getUpdatedAt()) + "");

            }
            ArrayList<String> labels = new ArrayList<String>();

            ArrayList<BarEntry> entries = new ArrayList<>();
            for (int i = 0; i < goalsDone.size(); i++) {
                entries.add(new BarEntry(Integer.parseInt(Duration.get(i)), i));
                labels.add(goalsDone.get(i));
            }

            BarDataSet dataset = new BarDataSet(entries, "");


            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


            setContentView(R.layout.graph_layout);
            chart = (BarChart) findViewById(R.id.profile_chart);
            BarData data = new BarData(labels, dataset);
            chart.setData(data);
            chart.setBackgroundColor(Color.parseColor("#FBB03B"));
            chart.setGridBackgroundColor(Color.parseColor("#FBB03B"));
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            chart.setDoubleTapToZoomEnabled(false);
            chart.setDescription("");
            chart.animateY(3000);


            chart.setOnClickListener(new View.OnClickListener() {
                public void onClick(View arg0) {
                    Log.d("setOnClickListener:", "");
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_my_finished_Goals));

                    shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(chart.getChartBitmap()));
                    shareIntent.setType("image/*");

                    startActivity(shareIntent);


                }
            });


        }
    }

    private static long getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}
