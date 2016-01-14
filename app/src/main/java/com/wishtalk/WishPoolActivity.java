package com.wishtalk;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.preference.PreferenceActivity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class WishPoolActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    CoordinatorLayout rootLayout;

    public UserManager userManager;
    Context mContext;

    ListView lv;
    List<Wish> wishList = new ArrayList<Wish>();
    WishAdapter wishAdapter;

    NavigationView mNavigationView;

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

    // 获取心愿列表
    private void get_wish_list() {
        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.get(mContext, "wish", jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("获取心愿列表", jsonObject.toString());
                showSnackbar("获取心愿列表成功！！！");
                try {
                    JSONArray userData = jsonObject.getJSONArray("data");
                    for (int i = 0; i < userData.length(); i++) {
                        JSONObject wish = userData.getJSONObject(i);
                        String title = wish.get("title").toString();
                        String time = wish.get("create_time").toString();
                        String status = wish.get("status").toString();
                        String id = wish.get("id").toString();

                        if (status.equals("unfinished")) {
                            status = "求实现";
                        } else if (status.equals("finishing")) {
                            status = "完成中";
                        } else if (status.equals("finished")) {
                            status = "已完成";
                        } else if (status.equals("closed")) {
                            status = "已关闭";
                        }
                        wishList.add(new Wish(title, time, status, id));
                        Log.i("获取心愿列表", wish.toString());
                    }
                    Log.i("心愿列表长度:", String.valueOf(wishList.size()));
                    wishAdapter = new WishAdapter(mContext, R.layout.wish_item, wishList);
                    lv.setAdapter(wishAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    Log.i("获取心愿列表", jsonObject.get("err").toString());
                    Log.i("获取心愿列表", jsonObject.get("msg").toString());
                    Log.i("获取心愿列表", jsonObject.get("stat").toString());
                    showSnackbar("获取心愿列表失败！！！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*{
        "comment_count": 0,
            "content": "\u8fd9\u662f\u5fc3\u613f\u8be6\u60c5\u554a\u554a\u554a",
            "create_time": "2016-01-07 17:04:18",
            "ctr": 0,
            "finished_time": "",
            "has_like": false,
            "helper": "",
            "id": 14,
            "likers_count": 0,
            "location": "(111,111)",
            "out_time": "2099-01-01 00:00:00",
            "owner": {
                 "avatar": "default.jpg",
                "id": 1,
                "is_blocked": false,
                "nickname": "\u8349",
                "username": "1"
             },
        "status": "unfinished",
            "title": "\u5fc3\u613f\u6807\u9898"
    }*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wishpool);

        initToolbar();
        initInstances();
        initNavigationView();

        lv = (ListView) findViewById(R.id.lv);
        mContext = this;
        userManager = new UserManager(mContext);
        get_wish_list();
        //Log.i("test", wishList.toString());

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Wish wish = (Wish)lv.getItemAtPosition(position);
                String wishId = wish.getWishId();
                Log.i("点击愿望：", wishId);
                Intent intent_second = new Intent(mContext, WishActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", wishId);
                intent_second.putExtras(bundle);
                startActivity(intent_second);
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
                    Log.i("test1", "success");
                    next_intent = new Intent(mContext, WishPoolActivity.class);
                } else if (title.equals("许愿")) {
                    next_intent = new Intent(mContext, MakeWishActivity.class);
                } else if (title.equals("个人中心")) {
                    Log.i("test2", "success");
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
        drawerToggle = new ActionBarDrawerToggle(WishPoolActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("心愿池");
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
