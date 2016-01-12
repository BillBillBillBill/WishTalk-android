package com.wishtalk;

public class Comment {
    private String username;
    private String time;
    private String content;

    protected Comment(String usr, String Time, String Content) {
        this.username = usr;
        this.time = Time;
        this.content = Content;

    }

    protected String getUsername() {
        return username;
    }
    protected String getTime() {
        return time;
    }
    protected String getContent() {
        return content;
    }
    protected void setUsername(String usr) {
        username = usr;
    }
    protected void setTime(String Time) {
        time = Time;
    }
    protected void setContent(String Content) {
        content = Content;
    }
}
