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
import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class WishActivity extends AppCompatActivity {
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView mNavigationView;
    CoordinatorLayout rootLayout;

    public UserManager userManager;

    TextView wishTitle, ownerName, wishStatus, wishContent, outTime, flag, locationText;
    Button takeBtn, giveUpBtn, finishBtn, closeBtn, submitBtn;
    ImageButton heart;

    ListView lv;
    EditText commentEditText;

    ArrayList<Comment> commentList = new ArrayList<Comment>();
    private CommentAdapter commentAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wish);

        mContext = this;

        userManager = new UserManager(mContext);

        initToolbar();
        initInstances();
        initNavigationView();

        lv = (ListView) findViewById(R.id.lv);

        wishTitle = (TextView) findViewById(R.id.wishTitle);
        ownerName = (TextView) findViewById(R.id.ownerName);
        wishStatus = (TextView) findViewById(R.id.wishStatus);
        wishContent = (TextView) findViewById(R.id.wishContent);
        flag = (TextView) findViewById(R.id.flag);
        outTime = (TextView) findViewById(R.id.outTime);
        locationText = (TextView) findViewById(R.id.locationText);


        takeBtn = (Button) findViewById(R.id.takeBtn);
        giveUpBtn = (Button) findViewById(R.id.giveUpBtn);
        finishBtn = (Button) findViewById(R.id.finishBtn);
        closeBtn = (Button) findViewById(R.id.closeBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        heart=(ImageButton)findViewById(R.id.heart);

        commentEditText = (EditText) findViewById(R.id.commentEditText);

        // get the wish id
        Bundle myBundle = this.getIntent().getExtras();
        final String wishId = myBundle.getString("id");

        get_wish_by_id(wishId);
        get_wish_comment_list(wishId);

        // 领取心愿
        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                take_wish(wishId);
            }
        });

        // 放弃心愿
        giveUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveup_wish(wishId);
            }
        });

        // 完成心愿
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish_wish(wishId);
            }
        });

        // 关闭心愿
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close_wish(wishId);
            }
        });

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.getText().toString().equals("求赞")) {
                    like_wish(wishId);
                    flag.setText("取消赞");
                    heart.setImageDrawable(getResources().getDrawable(R.drawable.heart1));
                } else {
                    unlike_wish(wishId);
                    flag.setText("求赞");
                    heart.setImageDrawable(getResources().getDrawable(R.drawable.heart2));
                }


            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_wish(wishId, commentEditText.getText().toString());
            }
        });

    }

    // 获取单个心愿详情
    private void get_wish_by_id(String id) {
        JSONObject jsonObject = new JSONObject();

        WishtalkRestClient.get(mContext, "wish" + "/" + id, jsonObject, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                // called when response HTTP status is "200 OK"
                Log.i("获取心愿详情", jsonObject.toString());
                showSnackbar("获取心愿详情成功！！！");
                try {
                    JSONObject wish = jsonObject.getJSONObject("data");
                    wishTitle.setText(wish.get("title").toString());
                    wishContent.setText(wish.get("content").toString());
                    locationText.setText(wish.get("location").toString());
                    String status = wish.get("status").toString();
                    if (status.equals("unfinished")) {

                        status = "求实现";
                    } else if (status.equals("finishing")) {
                        status = "完成中";
                    } else if (status.equals("finished")) {

                        status = "已完成";
                    } else if (status.equals("closed")) {

                        status = "已关闭";
                    }
                    wishStatus.setText(status);
                    outTime.setText(wish.get("out_time").toString());

                    // 获取 owner 的相关信息
                    JSONObject owner = wish.getJSONObject("owner");
                    ownerName.setText(owner.get("nickname").toString());

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
                        Bundle myBundle = getIntent().getExtras();
                        Intent intent_second = new Intent(mContext, WishActivity.class);
                        intent_second.putExtras(myBundle);
                        startActivity(intent_second);
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
                        showSnackbar("操作心愿失败！！！");
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
                        Bundle myBundle = getIntent().getExtras();
                        Intent intent_second = new Intent(mContext, WishActivity.class);
                        intent_second.putExtras(myBundle);
                        startActivity(intent_second);
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
                        String nickName = USER.get("nickname").toString();
                        String User_id = USER.get("user_id").toString();
                        String uptime = Comment.get("create_time").toString();
                        String content = Comment.get("content").toString();
                        Comment comment = new Comment(nickName, uptime, content);
                        commentList.add(comment);
                        Log.i("获取心愿评论", Comment.toString());
                    }
                    commentAdapter = new CommentAdapter(mContext, R.layout.comment, commentList);
                    lv.setAdapter(commentAdapter);
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

