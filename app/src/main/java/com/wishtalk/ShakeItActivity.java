package com.wishtalk;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
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
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class ShakeItActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView mNavigationView;
    CoordinatorLayout rootLayout;

    private SensorManager sensorManager;
    private Vibrator vibrator;
    private int sum=0;
    private int num =0;
    Context mContext;

    private static final int SENSOR_SHAKE = 10;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shake_it);
        mContext = this;
        initToolbar();
        initInstances();
        initNavigationView();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {// 注册监听器
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            // 第一个参数是Listener，第二个参数是所得传感器类型，第三个参数值获取传感器信息的频率
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {// 取消监听器
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    /**
     * 重力感应监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            // 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
            int medumValue = 19;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
            if (Math.abs(x) > medumValue || Math.abs(y) > medumValue || Math.abs(z) > medumValue) {
                vibrator.vibrate(200);
                Message msg = new Message();
                msg.what = SENSOR_SHAKE;
                handler.sendMessage(msg);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 动作执行
     */
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENSOR_SHAKE:
                    num = 1 + (int)(Math.random() * sum);
                    Intent intent = new Intent();
                    intent.setClass(ShakeItActivity.this, WishActivity.class);
                    String temp = String.valueOf(num);
                    intent.putExtra("id", temp);
                    startActivity(intent);
                    break;
            }
        }

    };


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
                        sum = userData.length();
                        Log.i("获取心愿列表", wish.toString());
                    }
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
        drawerToggle = new ActionBarDrawerToggle(ShakeItActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("心愿池");
    }
}

