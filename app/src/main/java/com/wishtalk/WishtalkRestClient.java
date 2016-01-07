package com.wishtalk;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.protocol.HTTP;

// 封装了网络访问
public class WishtalkRestClient {
    private static final String BASE_URL = "http://marserv.cn/api/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void post(Context mContext, String url, JSONObject jsonObject, JsonHttpResponseHandler jsonHttpResponseHandler) {
        UserManager userManager = new UserManager(mContext);
        try {
            jsonObject.put("token", userManager.get_token());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity stringEntity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
        client.post(mContext, getAbsoluteTokenUrl(url, mContext), stringEntity, "application/json", jsonHttpResponseHandler);
    }

    public static void put(Context mContext, String url, JSONObject jsonObject, JsonHttpResponseHandler jsonHttpResponseHandler) {
        UserManager userManager = new UserManager(mContext);
        try {
            jsonObject.put("token", userManager.get_token());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity stringEntity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
        client.put(mContext, getAbsoluteTokenUrl(url, mContext), stringEntity, "application/json; charset=utf-8", jsonHttpResponseHandler);
    }

    public static void get(Context mContext, String url, JSONObject jsonObject, JsonHttpResponseHandler jsonHttpResponseHandler) {
        UserManager userManager = new UserManager(mContext);
        try {
            jsonObject.put("token", userManager.get_token());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        StringEntity stringEntity = new StringEntity(jsonObject.toString(), HTTP.UTF_8);
        client.get(mContext, getAbsoluteTokenUrl(url, mContext), stringEntity, "application/json", jsonHttpResponseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteTokenUrl(String relativeUrl, Context mContext) {
        UserManager userManager = new UserManager(mContext);
        return BASE_URL + relativeUrl + "?token=" + userManager.get_token();
    }
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
