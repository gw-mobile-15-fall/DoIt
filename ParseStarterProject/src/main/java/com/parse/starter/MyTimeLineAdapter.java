package com.parse.starter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MyTimeLineAdapter extends BaseAdapter {
    private List<String> userList, events;


    private Context context;
    private  Holder holder = new Holder();
    private static LayoutInflater mInflater = null;
    private  onItemClicked l = null;
    private View.OnClickListener myButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View parentRow = (View) v.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);
            Log.d("Postion ::", position + "");
            l.postion(position);

        }
    };

    public MyTimeLineAdapter(ExploreActivity mainActivity, List userList, List Progress, onItemClicked lisitener) {
        this.userList = userList;
        context = mainActivity;
        mInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        l = lisitener;
        events = Progress;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return userList.size();
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
        TextView user;
        ImageView icon;
        TextView progressText;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        View rowView;
        rowView = mInflater.inflate(R.layout.profile_goals, null);
        holder.user = (TextView) rowView.findViewById(R.id.goal_name);
        holder.icon = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.progressText = (TextView) rowView.findViewById(R.id.progress);
        holder.user.setOnClickListener(myButtonClickListener);
        holder.icon.setOnClickListener(myButtonClickListener);
        holder.progressText.setOnClickListener(myButtonClickListener);

        holder.user.setText(userList.get(position));
        holder.progressText.setText(": " + events.get(position));
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", userList.get(position));

        try {
            List<ParseUser> objects = query.find();

            ParseFile p = (ParseFile) objects.get(0).getParseFile("image");
            try {
                byte[] b = p.getData();
                if (b == null || b.length == 0) {
                    Drawable myDrawable = context.getResources().getDrawable(R.drawable.doit_icon);
                    holder.icon.setImageDrawable(myDrawable);
                } else
                    holder.icon.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));

            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {

        }


        // The query was successful.


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });


        return rowView;
    }

}
