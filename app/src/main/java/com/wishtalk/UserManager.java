package com.wishtalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

// 提供token的储存与获取
public class UserManager {
    public SharedPreferences sharedPreferences;
    public Context mContext;
    public Boolean isLogin;

    public UserManager(Context mContext) {
        this.mContext = mContext;
        this.sharedPreferences = mContext.getSharedPreferences("data", mContext.MODE_PRIVATE);
        if (get_token().equals("")) {
            this.isLogin = false;
        } else {
            this.isLogin = true;
        }
    }

    public void save_token(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString("token", token);
            editor.commit();
            isLogin = true;
            Log.i("save_token", "储存token成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete_token() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            editor.putString("token", "");
            editor.commit();
            isLogin = false;
            Log.i("delete_token", "删除token成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get_token() {
        String token = sharedPreferences.getString("token", "");
        Log.i("get_token", "token:" + token);
        return token;
    }
}
