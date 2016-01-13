package com.wishtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private int resource;

    public CommentAdapter(Context context, int resourceId, List<Comment> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout imageListView;

        Comment comment= getItem(position);
        String userName = comment.getUsername();
        String time=comment.getTime();
        String content=comment.getContent();

        if (convertView == null) {
            imageListView = new RelativeLayout(getContext());
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(resource, imageListView, true);
        } else {
            imageListView = (RelativeLayout)convertView;
        }

        TextView textView1 = (TextView)imageListView.findViewById(R.id.User);
        TextView textView2 = (TextView)imageListView.findViewById(R.id.time);
        TextView textView3 = (TextView)imageListView.findViewById(R.id.content);
        textView1.setText(userName);
        textView2.setText(time);
        textView3.setText(content);

        return imageListView;
    }

}

