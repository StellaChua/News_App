package com.example.news_application.entity;

public class SavedInfo {
    private int saved_id;
    private String newsID;
    private String username;
    private String news_json;

    public static SavedInfo sSavedInfo;

    public static SavedInfo getsSavedInfo() {
        return sSavedInfo;
    }

    public static void setsSavedInfo(SavedInfo sSavedInfo) {
        SavedInfo.sSavedInfo = sSavedInfo;
    }

    public SavedInfo(int saved_id, String newsID, String username, String news_json) {
        this.saved_id = saved_id;
        this.newsID = newsID;
        this.username = username;
        this.news_json = news_json;
    }

    public int getSaved_id() {
        return saved_id;
    }

    public void setSaved_id(int saved_id) {
        this.saved_id = saved_id;
    }

    public String getNewsID() {
        return newsID;
    }

    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNews_json() {
        return news_json;
    }

    public void setNews_json(String news_json) {
        this.news_json = news_json;
    }
}
