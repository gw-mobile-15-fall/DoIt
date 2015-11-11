package com.parse.starter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    List<String> goals;
    List<Integer> progress;
    List icons;
    Context context;
    onItemClicked l = null;
    private OnClickListener myButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
            Log.d("Postion ::", position+"");
            l.postion(position);

        }
    };
    private static LayoutInflater mInflater = null;
    SharedPreferences mPreferences;
    public MyAdapter(ProfileActivity mainActivity, List goalsList, List progressList,List icons, onItemClicked listenter) {
        goals = goalsList;
        context = mainActivity;
        progress = progressList;
        mInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.icons = icons;
        l = listenter;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return goals.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView goal;
        ImageView icon;
        TextView progress;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;
        rowView = mInflater.inflate(R.layout.profile_goals, null);
        holder.goal = (TextView) rowView.findViewById(R.id.goal_name);
        holder.icon = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.progress = (TextView) rowView.findViewById(R.id.progress);
        //mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            holder.goal.setText(goals.get(position));

        holder.progress.setText("Step: " + progress.get(position));
       holder.goal.setOnClickListener(myButtonClickListener);
        holder.icon.setOnClickListener(myButtonClickListener);
        holder.progress.setOnClickListener(myButtonClickListener);
        byte b[] = (byte[]) icons.get(position);
        if(b == null || b.length==0  ) {
            Drawable myDrawable = context.getResources().getDrawable(R.drawable.doit_icon);
            holder.icon.setImageDrawable(myDrawable);
        }
        else
        holder.icon.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });





        return rowView;
    }

}
