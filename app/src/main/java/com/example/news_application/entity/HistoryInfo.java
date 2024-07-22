package com.example.news_application.entity;

public class HistoryInfo {
    private int history_id;
    private String newsID;
    private String username;
    private String news_json;

    public HistoryInfo(int history_id, String newsID, String username, String news_json) {
        this.history_id = history_id;
        this.newsID = newsID;
        this.username = username;
        this.news_json = news_json;
    }

    public int getHistory_id() {
        return history_id;
    }

    public void setHistory_id(int history_id) {
        this.history_id = history_id;
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
