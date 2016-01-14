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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    CoordinatorLayout rootLayout;
    NavigationView mNavigationView;

    public UserManager userManager;
    Context mContext;

    EditText usernameEditText;
    EditText passwordEditText;

    Button loginBtn;
    TextView forgetTextview;
    TextView registerTextview;

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

    // 登录
    private void login(String username, String password) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);

            WishtalkRestClient.post(mContext, "session", jsonObject, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // called when response HTTP status is "200 OK"
                    Log.i("登录", String.valueOf(statusCode));
                    Log.i("登录", jsonObject.toString());
                    showSnackbar("登录成功！！！");
                    try {
                        String token = jsonObject.getJSONObject("data").get("token").toString();
                        Log.i("登录", token);
                        Log.i("登录", jsonObject.get("stat").toString());
                        userManager.save_token(token);
                        Intent intent = new Intent(LoginActivity.this, WishPoolActivity.class);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        Log.i("登录", jsonObject.get("err").toString());
                        Log.i("登录", jsonObject.get("msg").toString());
                        Log.i("登录", jsonObject.get("stat").toString());
                        showSnackbar("登录失败！！！");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            Log.i("Fail", "fuck");
            e.printStackTrace();
        }
    }


    // 获取用户信息
    private void get_user_info() {
        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.get(mContext, "user", jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("获取用户信息", jsonObject.toString());
                showSnackbar("获取用户信息成功！！！");
                try {
                    JSONObject userData = jsonObject.getJSONObject("data");
                    Log.i("获取用户信息", userData.get("gender").toString());
                    Log.i("获取用户信息", userData.get("grade").toString());
                    Log.i("获取用户信息", userData.get("nickname").toString());
                    Log.i("获取用户信息", userData.get("school").toString());
                    Log.i("获取用户信息", userData.get("username").toString());
                    Log.i("获取用户信息", userData.get("user_id").toString());
                    Log.i("获取用户信息", jsonObject.get("stat").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    Log.i("获取用户信息", jsonObject.get("err").toString());
                    Log.i("获取用户信息", jsonObject.get("msg").toString());
                    Log.i("获取用户信息", jsonObject.get("stat").toString());
                    showSnackbar("获取用户信息失败！！！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 登出， 删除token
    private void logout() {
        UserManager userManager = new UserManager(mContext);
        if (userManager.isLogin) {
            userManager.delete_token();
            showSnackbar("注销成功！！！");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initToolbar();
        initInstances();
        initNavigationView();

        mContext = this;
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        forgetTextview = (TextView) findViewById(R.id.forgetTextview);
        registerTextview = (TextView) findViewById(R.id.registerTextview);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        userManager = new UserManager(mContext);

        if (userManager.isLogin) {
            showSnackbar("用户已登录");
            Intent intent = new Intent(LoginActivity.this, WishPoolActivity.class);
            startActivity(intent);
        } else {
            showSnackbar("用户尚未登录");
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        forgetTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
        });

        registerTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });




    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInstances() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(LoginActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("心愿说");
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
                    startActivity(next_intent);
                } else if (title.equals("许愿")) {
                    showSnackbar("请先登录");
                } else if (title.equals("个人中心")) {
                    showSnackbar("请先登录");
                }
                return true;
            }
        });
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

