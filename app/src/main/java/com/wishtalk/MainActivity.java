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

public class MainActivity extends AppCompatActivity {

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

    // 操作心愿
    private void action_on_wish(String id, String action) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("action", action);

            WishtalkRestClient.put(mContext, "wish"+"/"+id, jsonObject, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // called when response HTTP status is "200 OK"
                    Log.i("操作心愿", jsonObject.toString());
                    showSnackbar("操作心愿成功！！！");
                    try {
                        Log.i("操作心愿", jsonObject.getJSONObject("data").get("msg").toString());
                        Log.i("操作心愿", jsonObject.get("stat").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        Log.i("操作心愿", jsonObject.get("err").toString());
                        Log.i("操作心愿", jsonObject.get("msg").toString());
                        Log.i("操作心愿", jsonObject.get("stat").toString());
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

    // 领取（帮助）心愿 权限：非发起者
    private void take_wish(String id) {
        action_on_wish(id, "take");
    }

    // 放弃心愿 权限：非发起者
    private void giveup_wish(String id) {
        action_on_wish(id, "giveup");
    }

    // 完成心愿 权限：发起者
    private void finish_wish(String id) {
        action_on_wish(id, "finish");
    }

    // 取消(关闭)心愿 权限：发起者
    private void close_wish(String id) {
        action_on_wish(id, "close");
    }

    // 评论心愿
    private void comment_wish(String wish_id, String content) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content);

            WishtalkRestClient.post(mContext, "wish/comment"+"/"+wish_id, jsonObject, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                    // called when response HTTP status is "200 OK"
                    Log.i("发布心愿评论", jsonObject.toString());
                    showSnackbar("发布心愿评论成功！！！");
                    try {
                        Log.i("发布心愿评论", jsonObject.getJSONObject("data").get("insert_id").toString());
                        Log.i("发布心愿评论", jsonObject.get("stat").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        Log.i("发布心愿评论", jsonObject.get("err").toString());
                        Log.i("发布心愿评论", jsonObject.get("msg").toString());
                        Log.i("发布心愿评论", jsonObject.get("stat").toString());
                        showSnackbar("发布心愿评论失败！！！");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 获取心愿评论
    private void get_wish_comment_list(String wish_id) {
        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.get(mContext, "wish/comment"+"/"+wish_id, jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("获取心愿评论", jsonObject.toString());
                showSnackbar("获取心愿评论成功！！！");
                try {
                    JSONArray userData = jsonObject.getJSONArray("data");
                    for (int i = 0; i < userData.length(); i++) {
                        JSONObject wish = userData.getJSONObject(i);
                        Log.i("获取心愿评论", wish.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    Log.i("获取心愿评论", jsonObject.get("err").toString());
                    Log.i("获取心愿评论", jsonObject.get("msg").toString());
                    Log.i("获取心愿评论", jsonObject.get("stat").toString());
                    showSnackbar("获取心愿评论失败！！！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 删除指定id评论
    private void delete_wish_comment_by_id(String comment_id) {
        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.delete(mContext, "wish/comment"+"/"+comment_id, jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("删除心愿评论", jsonObject.toString());
                showSnackbar("删除心愿评论成功！！！");
                try {
                    Log.i("删除心愿评论", jsonObject.get("stat").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    Log.i("删除心愿评论", jsonObject.get("err").toString());
                    Log.i("删除心愿评论", jsonObject.get("msg").toString());
                    Log.i("删除心愿评论", jsonObject.get("stat").toString());
                    showSnackbar("删除心愿评论失败！！！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 对心愿进行点赞
    private void like_wish(String wish_id) {

        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.post(mContext, "wish/like"+"/"+wish_id, jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("对心愿点赞", jsonObject.toString());
                showSnackbar("对心愿点赞成功！！！");
                try {
                    Log.i("对心愿点赞", jsonObject.get("stat").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    Log.i("对心愿点赞", jsonObject.get("err").toString());
                    Log.i("对心愿点赞", jsonObject.get("msg").toString());
                    Log.i("对心愿点赞", jsonObject.get("stat").toString());
                    showSnackbar("对心愿点赞失败！！！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    // 对心愿取消点赞
    private void unlike_wish(String wish_id) {
        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.delete(mContext, "wish/like"+"/"+wish_id, jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("对心愿取消点赞", jsonObject.toString());
                showSnackbar("对心愿取消点赞成功！！！");
                try {
                    Log.i("对心愿取消点赞", jsonObject.get("stat").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    Log.i("对心愿取消点赞", jsonObject.get("err").toString());
                    Log.i("对心愿取消点赞", jsonObject.get("msg").toString());
                    Log.i("对心愿取消点赞", jsonObject.get("stat").toString());
                    showSnackbar("对心愿取消点赞失败！！！");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        unlike_wish("2");
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInstances() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
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
