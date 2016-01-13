package com.wishtalk;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MakeWishActivity extends AppCompatActivity {
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView mNavigationView;
    CoordinatorLayout rootLayout;
    private Context mContext;

    EditText titleEditText, contentEditText;
    Button makeWishBtn;

    // get the location
    LocationManager locationManager;
    String provider;

    // get time
    DatePicker datePicker;

    // 发布心愿
    private void make_wish(String title, String location, String out_time, String content) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("title", title);
            jsonObject.put("location", location);
            jsonObject.put("out_time", out_time);
            jsonObject.put("content", content);

            WishtalkRestClient.post(mContext, "wish", jsonObject, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // called when response HTTP status is "200 OK"
                    Log.i("发布心愿", jsonObject.toString());
                    showSnackbar("发布心愿成功！！！");
                    try {
                        Log.i("发布心愿", jsonObject.getJSONObject("data").get("insert_id").toString());
                        Log.i("发布心愿", jsonObject.get("stat").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        Log.i("发布心愿", jsonObject.get("err").toString());
                        Log.i("发布心愿", jsonObject.get("msg").toString());
                        Log.i("发布心愿", jsonObject.get("stat").toString());
                        showSnackbar("发布心愿失败！！！");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 得到经纬度
    private String getLocation() {

        String location = "";

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);

        Log.d("Test", "" + providerList.get(0).toString());

        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
        }

        try {
            Location loc = locationManager.getLastKnownLocation(provider);

            if (loc != null) {
                String longtitude = "" + loc.getLongitude();
                String latitude = "" + loc.getLatitude();
                location = "(" + longtitude + "," + latitude + ")";
                Log.i("location", location);
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return location;
    }

    private String getTime() {
        String mYear = "" + datePicker.getYear();
        String mMonth, mDay;
        String time = "2099-01-01 00:00:00"; // default

        if (datePicker.getMonth() < 10) {
            mMonth = "0" + datePicker.getMonth();
        } else {
            mMonth = "" + datePicker.getMonth();
        }

        if (datePicker.getDayOfMonth() < 10) {
            mDay = "0" + datePicker.getDayOfMonth();
        } else {
            mDay = "" + datePicker.getDayOfMonth();
        }

        time = mYear + "-" + mMonth + "-" + mDay + " " +  "00:00::00";
        Log.i("test", time);
        return time;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makewish);

        mContext = this;

        initToolbar();
        initInstances();
        initNavigationView();

        titleEditText = (EditText) findViewById(R.id.titleEditText);
        contentEditText = (EditText) findViewById(R.id.contentEditText);
        makeWishBtn = (Button) findViewById(R.id.makeWishBtn);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        final String location = getLocation();
        final String time = getTime();

        makeWishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_wish(titleEditText.getText().toString(), location, time,
                        contentEditText.getText().toString());
            }
        });

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNavigationView() {
        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                String title = String.valueOf(item.getTitle());
                Intent next_intent;
                // 通过点击项进行跳转
                if (title.equals("心愿池")) {
                    next_intent = new Intent(mContext, WishPoolActivity.class);
                } else if (title.equals("许愿")) {
                    next_intent = new Intent(mContext, MakeWishActivity.class);
                } else if (title.equals("个人中心")) {
                    next_intent = new Intent(mContext, UserCenterActivity.class);
                } else if (title.equals("摇一摇")) {
                    next_intent = new Intent(mContext, ShakeItActivity.class);
                } else {
                    return true;
                }
                startActivity(next_intent);
                return true;
            }
        });
    }

    private void initInstances() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MakeWishActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("许愿");
    }

    // 弹出snackbar 用于提示
    public void showSnackbar(String text) {
        Snackbar.make(rootLayout, text, Snackbar.LENGTH_SHORT)
                .setAction("好的", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                })
                .show();
    }
}

