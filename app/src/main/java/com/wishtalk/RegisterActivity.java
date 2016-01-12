package com.wishtalk;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.preference.PreferenceActivity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
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

public class RegisterActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    CoordinatorLayout rootLayout;

    public UserManager userManager;
    Context mContext;

    EditText usernameEditText;
    EditText passwordEditText;
    EditText nicknameEditText;
    EditText checkcodeEditText;

    Button getCheckcodeBtn;
    Button registerBtn;

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


    // 获取验证码
    private void get_check_code(String username) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("phone", username);

            WishtalkRestClient.post(mContext, "user/checkcode", jsonObject, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // called when response HTTP status is "200 OK"
                    Log.i("获取验证码", jsonObject.toString());
                    showSnackbar("获取验证码成功！！！");
                    try {
                        Log.i("获取验证码", jsonObject.get("data").toString());
                        Log.i("获取验证码", jsonObject.get("stat").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        Log.i("获取验证码", jsonObject.get("err").toString());
                        Log.i("获取验证码", jsonObject.get("msg").toString());
                        Log.i("获取验证码", jsonObject.get("stat").toString());
                        showSnackbar("获取验证码失败！！！");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    // 注册
    private void register(String username, String password, String nickname, String checkcode) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("nickname", nickname);
            jsonObject.put("checkcode", checkcode);

            WishtalkRestClient.post(mContext, "user", jsonObject, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // called when response HTTP status is "200 OK"
                    Log.i("注册", jsonObject.toString());
                    showSnackbar("注册成功！！！");
                    try {
                        String token = jsonObject.getJSONObject("data").get("token").toString();
                        Log.i("注册", token);
                        Log.i("注册", jsonObject.get("stat").toString());
                        // 注册后自动登录
                        userManager.save_token(token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        Log.i("注册", jsonObject.get("err").toString());
                        Log.i("注册", jsonObject.get("msg").toString());
                        Log.i("注册", jsonObject.get("stat").toString());
                        showSnackbar("注册失败！！！");
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
        setContentView(R.layout.register);

        initToolbar();
        initInstances();

        mContext = this;
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        nicknameEditText = (EditText) findViewById(R.id.nicknameEditText);
        checkcodeEditText = (EditText) findViewById(R.id.checkcodeEditText);

        getCheckcodeBtn = (Button) findViewById(R.id.getCheckcodeBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);

        userManager = new UserManager(mContext);

        getCheckcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_check_code(usernameEditText.getText().toString());
                String hint = "重新获取验证码";
                getCheckcodeBtn.setText(hint);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(usernameEditText.getText().toString(), passwordEditText.getText().toString(),
                        nicknameEditText.getText().toString(), checkcodeEditText.getText().toString());

                if (userManager.isLogin) {
                    Intent intent = new Intent(RegisterActivity.this, WishPoolActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInstances() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(RegisterActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("心愿说");
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

