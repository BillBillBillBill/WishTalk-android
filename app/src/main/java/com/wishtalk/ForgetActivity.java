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

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ForgetActivity extends AppCompatActivity {

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
    EditText checkcodeEditText;

    Button getCheckcodeBtn;
    Button loginBtn;

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

    // 获取忘记密码验证码
    private void get_forget_check_code(String username) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", username);

            WishtalkRestClient.post(mContext, "user/forget_checkcode", jsonObject, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // called when response HTTP status is "200 OK"
                    Log.i("获取忘记密码验证码", jsonObject.toString());
                    showSnackbar("获取忘记密码验证码成功！！！");
                    try {
                        Log.i("获取忘记密码验证码", jsonObject.get("data").toString());
                        Log.i("获取忘记密码验证码", jsonObject.get("stat").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        Log.i("获取忘记密码验证码", jsonObject.get("err").toString());
                        Log.i("获取忘记密码验证码", jsonObject.get("msg").toString());
                        Log.i("获取忘记密码验证码", jsonObject.get("stat").toString());
                        showSnackbar("获取忘记密码验证码失败！！！");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 重置密码
    private void password_reset(String username, String new_password, String checkcode) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("new_password", new_password);
            jsonObject.put("checkcode", checkcode);

            WishtalkRestClient.post(mContext, "user/password_reset", jsonObject, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // called when response HTTP status is "200 OK"
                    Log.i("重置密码", jsonObject.toString());
                    showSnackbar("重置密码成功！！！");
                    try {
                        Log.i("重置密码", jsonObject.get("data").toString());
                        Log.i("重置密码", jsonObject.get("stat").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        Log.i("重置密码", jsonObject.get("err").toString());
                        Log.i("重置密码", jsonObject.get("msg").toString());
                        Log.i("重置密码", jsonObject.get("stat").toString());
                        showSnackbar("重置密码失败！！！");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);

        initToolbar();
        initInstances();
        initNavigationView();

        mContext = this;
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        checkcodeEditText = (EditText) findViewById(R.id.checkcodeEditText);
        getCheckcodeBtn = (Button) findViewById(R.id.getCheckcodeBtn);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        userManager = new UserManager(mContext);

//        logout();
//        login("1", "1");
        if (userManager.isLogin) {
            showSnackbar("用户已登录");
        } else {
            showSnackbar("用户尚未登录");

        }

        getCheckcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_forget_check_code(usernameEditText.getText().toString());
                String hint = "重新获取验证码";
                getCheckcodeBtn.setText(hint);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password_reset(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), checkcodeEditText.getText().toString());
            }
        });


//        update_user_info("昵称", "男", "年级", "中山大学");
//        get_forget_check_code("18819473330");
//        password_reset("18819473330", "fuck", "311864");


    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInstances() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(ForgetActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
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


