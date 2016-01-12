package com.wishtalk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/1/12.
 */
public class WishAdapter extends ArrayAdapter<Wish> {

    private int resource;

    public WishAdapter(Context context, int resourceId, List<Wish> objects) {
        super(context, resourceId, objects);
        resource = resourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout wishListView;

        Wish wish = getItem(position);
        String wishTitle = wish.getTitle();
        String wishTime = wish.getTime();
        String wishStatus = wish.getStatus();

        if (convertView == null) {
            wishListView = new RelativeLayout(getContext());
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(resource, wishListView, true);
        } else {
            wishListView = (RelativeLayout)convertView;
        }

        TextView titleTextView = (TextView) wishListView.findViewById(R.id.titleTextView);
        TextView timeTextView = (TextView) wishListView.findViewById(R.id.timeTextview);
        TextView statusTextView = (TextView) wishListView.findViewById(R.id.statusTextView);

        titleTextView.setText(wishTitle);
        timeTextView.setText(wishTime);
        statusTextView.setText(wishStatus);

        return wishListView;
    }

}
