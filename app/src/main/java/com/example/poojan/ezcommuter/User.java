package com.example.poojan.ezcommuter;

public class User {
    private String userName;
    private String userEmail;
    private String userImgUrl;
    private String token;

    public User(String userName, String userEmail, String userImgUrl, String token) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userImgUrl = userImgUrl;
        this.token = token;
    }

    public User() {}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getToken(){ return token; }

    public void setToken(String favGenre2) {
        this.token = token;
    }

}
