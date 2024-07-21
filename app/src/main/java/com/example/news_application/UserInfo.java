package com.example.news_application;

public class UserInfo {
    private int user_id;
    private String username;
    private String password;
    private String nickname;

    public static  UserInfo sUserInfo;

    public static UserInfo getUserInfo() {
        return sUserInfo;
    }

    public static void setUserInfo(UserInfo userInfo) {
        sUserInfo = userInfo;
    }

    public UserInfo(int user_id, String username, String password, String nickname) {
        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public int getUser_id_id() {
        return user_id;
    }

    public void setUser_id_id(int _id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
