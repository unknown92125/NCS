package com.mrex.ncs;

public class User {

    private String uID;
    private String name;
    private String address;
    private String phone;
    private int point;

    public User() {
    }

    public User(String uID, String name, String address, String phone, int point) {
        this.uID = uID;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.point = point;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
