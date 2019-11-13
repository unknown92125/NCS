package com.mrex.ncs;

public class User {

    private String iD;
    private String name;
    private String address;
    private String phone;
    private int point;

    public User() {
    }

    public User(String iD, String name, String address, String phone, int point) {
        this.iD = iD;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.point = point;
    }

    public String getiD() {
        return iD;
    }

    public void setiD(String iD) {
        this.iD = iD;
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
