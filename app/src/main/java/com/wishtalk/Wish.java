package com.wishtalk;

/**
 * Created by Administrator on 2016/1/12.
 */
public class Wish {
    private String title;
    private String time;
    private String status;
    private String wishId;

    public Wish(String title, String time, String status, String wishId) {
        this.title = title;
        this.time = time;
        this.status = status;
        this.wishId = wishId;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getWishId() {
        return wishId;
    }
}
