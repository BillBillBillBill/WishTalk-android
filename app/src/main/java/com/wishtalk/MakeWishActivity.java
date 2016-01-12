package com.wishtalk;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
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
import android.widget.EditText;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MakeWishActivity extends AppCompatActivity {
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView mNavigationView;
    CoordinatorLayout rootLayout;
    private Context mContext;

    private EditText title;
    private EditText content;
    private EditText outTime;
    private EditText location;
    private Button yes;
    private Button no;

    private String Title=null;
    private String Content=null;
    private String OutTime=null;
    private String Location=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.makewish);

        mContext = this;

        initToolbar();
        initInstances();
        initNavigationView();

        title=(EditText)findViewById(R.id.title);
        content=(EditText)findViewById(R.id.content);
        outTime=(EditText)findViewById(R.id.outTime);
        location=(EditText)findViewById(R.id.loc);
        yes=(Button)findViewById(R.id.yes);
        no=(Button)findViewById(R.id.no);

        Title=title.getText().toString();
        Content=content.getText().toString();
        OutTime=outTime.getText().toString();
        Location=location.getText().toString();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_wish(Title,Location,OutTime,Content);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Title=null;
                Content=null;
                OutTime=null;
                Location=null;
                Intent intent = new Intent();
                intent.setClass(MakeWishActivity.this, UserCenterActivity.class);
                startActivity(intent);
            }
        });

    }

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
        collapsingToolbarLayout.setTitle("心愿池");
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

