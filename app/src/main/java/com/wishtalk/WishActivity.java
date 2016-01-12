package com.wishtalk;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class WishActivity extends AppCompatActivity {
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView mNavigationView;
    CoordinatorLayout rootLayout;

    private TextView user_id;
    private TextView sponsor;
    private TextView title;
    private TextView content;
    private TextView upTime;
    private TextView outTime; // 过期时间
    private TextView location;
    private TextView status;

    private TextView text;
    private TextView finished_time; // 完成时间

    private Button back;
    private ImageButton heart;
    private Button get;
    private ListView comments;
    private EditText make_comment;
    private Button submit;

    private int num = 1;
    private String temp=null;
    private String sta =null;
    private String oTime=null;
    private String nickName=null;
    private String User_id=null;
    private String uptime=null;
    private String Contents=null;
    private Comment comment;
    //private String COMMENT=null;

    private Handler handler = new Handler();
    private ArrayList<Comment> list = new ArrayList<Comment>();
    private CommentAdapter commentAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        initToolbar();
        initInstances();
        initNavigationView();

        user_id=(TextView)findViewById(R.id.user_id);
        sponsor=(TextView)findViewById(R.id.originator);
        title=(TextView)findViewById(R.id.title);
        content=(TextView)findViewById(R.id.content);
        upTime=(TextView)findViewById(R.id.onTime);
        outTime=(TextView)findViewById(R.id.outTime);
        location=(TextView)findViewById(R.id.loc);
        status=(TextView)findViewById(R.id.status);

        text=(TextView)findViewById(R.id.text);
        finished_time=(TextView)findViewById(R.id.finished_time);

        back=(Button)findViewById(R.id.back);
        heart=(ImageButton)findViewById(R.id.heart);
        get=(Button)findViewById(R.id.get);
        comments=(ListView)findViewById(R.id.list_comment);
        make_comment=(EditText)findViewById(R.id.comment);
        //COMMENT=make_comment.getText().toString();
        submit=(Button)findViewById(R.id.submit);

        // 先获取一些关于该心愿的基本信息
        get_wish_by_id("1");
        // 设置wish的相关状态
        status.setText(sta);
        if (sta.equals("finished")) {
            text.setVisibility(text.VISIBLE);
            outTime.setVisibility(outTime.VISIBLE);
        } else {
            text.setVisibility(text.GONE);
            outTime.setVisibility(outTime.GONE);
        }

        // 返回按钮事件监听
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(WishActivity.this,WishPoolActivity.class);
                startActivity(intent);
            }
        });
        // 点赞或取消按钮事件监听
        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num == 1) {
                    // 提供 wish的id
                    like_wish("1");
                    heart.setImageResource(R.drawable.heart1);
                    //heart.setImageDrawable(getResources().getDrawable(R.drawable.heart1, null));
                    num++;
                } else {
                    // 提供wish的id
                    unlike_wish("1");
                    heart.setImageResource(R.drawable.heart2);
                    //heart.setImageDrawable(getResources().getDrawable(R.drawable.heart2, null));
                    num--;
                }
            }
        });
        // 接受或放弃心愿
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_user_info();
                // 当前用户是心愿的发起者
                if (temp.equals(user_id)) {
                    // 该心愿的状态是未完成/正在完成时
                    // 发起者可以选择完成该心愿
                    if (get.getText().equals("接受/完成") && (sta.equals("finished") || sta.equals("finishing"))) {
                        // 发起者完成该心愿，传入id
                        finish_wish("1");
                        // 更新UI
                        // 获取当前心愿的id和相关信息
                        get_wish_by_id("1");
                        new Thread() {
                            @Override
                            public void run() {
                                //更新UI操作；
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        status.setText(sta);
                                        outTime.setText(oTime);
                                        text.setVisibility(text.VISIBLE);
                                        outTime.setVisibility(outTime.VISIBLE);
                                    }
                                });
                            }
                        }.start();
                        // 改变按键信息
                        get.setText("放弃/关闭");
                        // 发起者可以选择在任意状态下（除了关闭）关闭该心愿
                    } else if (get.getText().equals("放弃/关闭") && !(sta.equals("closed"))) {
                        // 关闭当前心愿
                        close_wish("1");
                        //跳转回心愿池
                        Intent intent = new Intent();
                        intent.setClass(WishActivity.this,WishPoolActivity.class);
                        startActivity(intent);
                    }
                } else if (!temp.equals(user_id)) {
                    // 当前用户不是心愿的发起者
                    // 当心愿没有用户来完成，可以领取心愿
                    if (get.getText().equals("接受/完成") && sta.equals("unfinished")) {
                        // 领取当前心愿
                        take_wish("1");
                        // 更新UI
                        // 获取更新后信息
                        get_wish_by_id("1");
                        new Thread() {
                            @Override
                            public void run() {
                                //更新UI操作；
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        status.setText(sta);
                                    }
                                });
                            }
                        }.start();
                        // 改变按键信息
                        get.setText("放弃/关闭");
                    } else if (get.getText().equals("放弃/关闭") && sta.equals("finishing")) {
                        // 当心愿正在完成时，用户可以选择放弃
                        // 放弃当前心愿
                        giveup_wish("1");
                        // 更新UI
                        // 获得更新后信息
                        get_wish_by_id("1");
                        new Thread() {
                            @Override
                            public void run() {
                                //更新UI操作；
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        status.setText(sta);
                                    }
                                });
                            }
                        }.start();
                        // 改变按键信息
                        get.setText("接受/完成");
                    }
                }
            }
        });
        // 通过当前心愿的id获取评论列表
        get_wish_comment_list("1");

        /* set the adapter */
        commentAdapter = new CommentAdapter(this, R.layout.comment, list);
        comments.setAdapter(commentAdapter);

        /* remove the item from the fruit list */
        /* notify that data has changed through the adapter */
        comments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (temp.equals(User_id)) {
                    list.remove(position);
                    commentAdapter.notifyDataSetChanged();
                    delete_wish_comment_by_id("1");
                }
                return true;
            }
        });


        // 发表评论
        comment_wish("1", "????????");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 更新UI
                get_wish_by_id("1");
                new Thread() {
                    @Override
                    public void run() {
                        //更新UI操作；
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                status.setText(sta);
                            }
                        });
                    }
                }.start();
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
                    JSONObject wish = jsonObject.getJSONObject("data");
                    // 获取 owner 的相关信息
                    JSONObject owner = jsonObject.getJSONObject("wish");
                    user_id.setText(owner.get("user_id").toString());
                    sponsor.setText(owner.get("nickname").toString());
                    // 获取wish 的相关信息
                    title.setText(wish.get("title").toString());
                    content.setText(wish.get("content").toString());
                    upTime.setText(wish.get("create_time").toString());
                    outTime.setText(wish.get("out_time").toString());
                    location.setText(wish.get("location").toString());
                    sta = wish.get("status").toString();
                    oTime = wish.get("finished_time").toString();
                    Log.i("获取心愿详情", wish.toString());
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

    // 对心愿进行点赞
    private void like_wish(String wish_id) {

        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.post(mContext, "wish/like" + "/" + wish_id, jsonObject, new JsonHttpResponseHandler() {

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

        WishtalkRestClient.delete(mContext, "wish/like" + "/" + wish_id, jsonObject, new JsonHttpResponseHandler() {

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
                    temp = userData.get("user_id").toString();
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

        WishtalkRestClient.get(mContext, "wish/comment" + "/" + wish_id, jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("获取心愿评论", jsonObject.toString());
                showSnackbar("获取心愿评论成功！！！");
                try {
                    JSONArray Comments = jsonObject.getJSONArray("data");
                    for (int i = 0; i < Comments.length(); i++) {
                        JSONObject Comment = Comments.getJSONObject(i);
                        JSONObject USER = Comment.getJSONObject("Comment");
                        nickName = USER.get("nickname").toString();
                        User_id = USER.get("user_id").toString();
                        uptime = Comment.get("create_time").toString();
                        Contents = Comment.get("content").toString();
                        comment = new Comment(nickName,uptime,Contents);
                        list.add(comment);
                        Log.i("获取心愿评论", Comment.toString());
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

        WishtalkRestClient.delete(mContext, "wish/comment" + "/" + comment_id, jsonObject, new JsonHttpResponseHandler() {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
        drawerToggle = new ActionBarDrawerToggle(WishActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = (CoordinatorLayout) findViewById(R.id.rootLayout);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        collapsingToolbarLayout.setTitle("心愿池");
    }
}

