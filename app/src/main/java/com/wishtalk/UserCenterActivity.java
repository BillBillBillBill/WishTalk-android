package com.wishtalk;

import android.content.Context;
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

public class UserCenterActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;

    CoordinatorLayout rootLayout;
    FloatingActionButton fabBtn;

    public UserManager userManager;
    Context mContext;

    EditText usernameEditText;
    EditText passwordEditText;

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

    // 登出， 删除token
    private void logout() {
        UserManager userManager = new UserManager(mContext);
        if (userManager.isLogin) {
            userManager.delete_token();
            showSnackbar("注销成功！！！");
        }
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

    // 更新用户信息
    private void update_user_info(String nickname, String gender, String grade, String school) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("nickname", nickname);
            jsonObject.put("grade", grade);
            jsonObject.put("gender", gender);
            jsonObject.put("school", school);

            WishtalkRestClient.put(mContext, "user", jsonObject, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // called when response HTTP status is "200 OK"
                    Log.i("更新用户信息", jsonObject.toString());
                    showSnackbar("更新用户信息成功！！！");
                    try {
                        Log.i("更新用户信息", jsonObject.getJSONObject("data").get("msg").toString());
                        Log.i("更新用户信息", jsonObject.get("stat").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        Log.i("更新用户信息", jsonObject.get("err").toString());
                        Log.i("更新用户信息", jsonObject.get("msg").toString());
                        Log.i("更新用户信息", jsonObject.get("stat").toString());
                        showSnackbar("更新用户信息失败！！！");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
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

    // 获取单个心愿详情
    private void get_wish_by_id(String id) {
        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.get(mContext, "wish"+"/"+id, jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("获取心愿详情", jsonObject.toString());
                showSnackbar("获取心愿详情成功！！！");
                try {
                    JSONObject userData = jsonObject.getJSONObject("data");
                    Log.i("获取心愿详情", userData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    Log.i("获取心愿详情", jsonObject.get("err").toString());
                    Log.i("获取心愿详情", jsonObject.get("msg").toString());
                    Log.i("获取心愿详情", jsonObject.get("stat").toString());
                    showSnackbar("获取心愿详情失败！！！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 获取个人发布心愿列表
    private void get_my_wish_list() {
        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.get(mContext, "my_wish", jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("获取个人发布心愿列表", jsonObject.toString());
                showSnackbar("获取个人发布心愿列表成功！！！");
                try {
                    JSONArray userData = jsonObject.getJSONArray("data");
                    for (int i = 0; i < userData.length(); i++) {
                        JSONObject wish = userData.getJSONObject(i);
                        Log.i("获取个人发布心愿列表", wish.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    Log.i("获取个人发布心愿列表", jsonObject.get("err").toString());
                    Log.i("获取个人发布心愿列表", jsonObject.get("msg").toString());
                    Log.i("获取个人发布心愿列表", jsonObject.get("stat").toString());
                    showSnackbar("获取个人发布心愿列表失败！！！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 获取个人帮助心愿列表
    private void get_my_help_wish_list() {
        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.get(mContext, "my_help_wish", jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("获取个人帮助心愿列表", jsonObject.toString());
                showSnackbar("获取个人帮助心愿列表成功！！！");
                try {
                    JSONArray userData = jsonObject.getJSONArray("data");
                    for (int i = 0; i < userData.length(); i++) {
                        JSONObject wish = userData.getJSONObject(i);
                        Log.i("获取个人帮助心愿列表", wish.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    Log.i("获取个人帮助心愿列表", jsonObject.get("err").toString());
                    Log.i("获取个人帮助心愿列表", jsonObject.get("msg").toString());
                    Log.i("获取个人帮助心愿列表", jsonObject.get("stat").toString());
                    showSnackbar("获取个人帮助心愿列表失败！！！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_center);

        initToolbar();
        initInstances();

        mContext = this;
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        userManager = new UserManager(mContext);

//        logout();
//        login("1", "1");
        if (userManager.isLogin) {
            showSnackbar("用户已登录");
        } else {
            showSnackbar("用户尚未登录");

        }
//        get_check_code("18819473330");
//        register("18819473330", "password", "nickname", "561370");
//        update_user_info("昵称", "男", "年级", "中山大学");
//        get_forget_check_code("18819473330");
//        password_reset("18819473330", "fuck", "311864");

//        make_wish("心愿标题", "(111,111)", "", "这是心愿详情啊啊啊");
        get_wish_list();
        get_user_info();
        get_wish_by_id("1");
        get_wish_by_id("2");
//        get_wish_comment_list("1");
//        get_wish_comment_list("2");
//        delete_wish_comment_by_id("1");
//        comment_wish("1", "这是评论啊红红火火");
//        comment_wish("2", "ADUGSAiUDGUIWGUO");
//        finish_wish("3");
//        finish_wish("4");
//        finish_wish("5");
//        finish_wish("6");
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInstances() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(UserCenterActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        fabBtn = (FloatingActionButton) findViewById(R.id.fabBtn);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(rootLayout, "Hello. I am Snackbar!", Snackbar.LENGTH_SHORT)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .show();
            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("个人中心");
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

