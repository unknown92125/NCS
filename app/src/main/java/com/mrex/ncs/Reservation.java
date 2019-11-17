package com.mrex.ncs;

public class Reservation {

    private String id;
    private String address;
    private String phone;
    private String area;
    private String date;
    private String time;
    private String expectedTime;
    private String payMethod;
    private String payDate;
    private String isDone;
    private String payPrice;

    public Reservation() {
    }

    public Reservation(String address, String phone, String area, String date, String time, String expectedTime, String payMethod, String payDate, String isDone, String payPrice) {

        this.address = address;
        this.phone = phone;
        this.area = area;
        this.date = date;
        this.time = time;
        this.expectedTime = expectedTime;
        this.payMethod = payMethod;
        this.payDate = payDate;
        this.isDone = isDone;
        this.payPrice = payPrice;
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

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getIsDone() {
        return isDone;
    }

    public void setIsDone(String isDone) {
        this.isDone = isDone;
    }

    public String getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(String payPrice) {
        this.payPrice = payPrice;
    }
}
