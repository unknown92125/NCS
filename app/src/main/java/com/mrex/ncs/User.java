package com.mrex.ncs;

public class User {

    private String uid;
    private String id;
    private String pw;
    private String name;
    private String phone;
    private String type;
    private String token;

    public User() {
    }

    public User(String uid, String id, String pw, String name, String phone, String type, String token) {
        this.uid = uid;
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.token = token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
