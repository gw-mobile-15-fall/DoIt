package com.parse.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;
import java.util.Map;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity mContext;
    private Map<String, List<String>> mGoalCollections;
    private Map<String, ParseFile> mIconCollections;

    private List<String> goals;

    public ExpandableListAdapter(Activity context, List<String> goalList,
                                 Map<String, List<String>> goalCollections, Map<String, ParseFile> iconCollections) {
        this.mContext = context;
        this.mGoalCollections = goalCollections;
        this.goals = goalList;
        this.mIconCollections = iconCollections;

    }

    public Object getChild(int groupPosition, int childPosition) {
        return mGoalCollections.get(goals.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String goal = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = mContext.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.goal);

        ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
        ParseFile icon = mIconCollections.get(goal);
        byte[] bitmapdata;
        try {
            if (icon == null)
                ;

            else {
                bitmapdata = icon.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                delete.setImageBitmap(bitmap);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        delete.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Do you want to add this goal?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                List<String> child =
                                        mGoalCollections.get(goals.get(groupPosition));
                                child.get(childPosition);
                                notifyDataSetChanged();
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        item.setText(goal);
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {

        if (goals == null)
            Log.d("Null is", "goal");
        if (mGoalCollections == null)
            Log.d("Null is", "GoalCollections");
        Log.d("#######", (goals.get(groupPosition)).toString());

        Log.d("#######", mGoalCollections.get(goals.get(groupPosition)).toString());
        return mGoalCollections.get(goals.get(groupPosition)).size();

    }

    public Object getGroup(int groupPosition) {
        return goals.get(groupPosition);
    }

    public int getGroupCount() {
        return goals.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String laptopName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.usergoal);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(laptopName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}