package com.mrex.ncs;

public class User {

    private String uid;
    private String name;
    private String id;
    private String pw;
    private String phone;

    public User() {
    }

    public User(String uid, String name, String id, String pw) {
        this.uid = uid;
        this.name = name;
        this.id = id;
        this.pw = pw;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
