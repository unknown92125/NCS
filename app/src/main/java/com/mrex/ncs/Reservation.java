package com.mrex.ncs;

public class Reservation {

    private String id;
    private String address;
    private String phone;
    private String area;
    private String date;
    private String time;
    private String isDone;

    public Reservation(String id, String address, String phone, String area, String date, String time, String isDone) {
        this.id = id;
        this.address = address;
        this.phone = phone;
        this.area = area;
        this.date = date;
        this.time = time;
        this.isDone = isDone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }
}
