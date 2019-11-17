package com.mrex.ncs;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reservation implements Comparable<Reservation> {

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
    private long milliDate;

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

    public long getMilliDate() {
        return milliDate;
    }

    public void setMilliDate(long milliDate) {
        this.milliDate = milliDate;
    }

    //TODO
    @Override
    public int compareTo(Reservation reservation) {

        try {
            Date dDate = new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.getDefault()).parse(this.date);
            long date0 = dDate.getTime();
            Log.e("tag", date0 + "");
            dDate = new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.getDefault()).parse(reservation.getDate());
            long date1 = dDate.getTime();
            Log.e("tag", date1 + "");
            if (date0 == date1) return 0;
            else if (date0 > date1) return 1;
            else return -1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

//        long date0 = this.getMilliDate();
//        long date1 = reservation.getMilliDate();
//        if (date0 == date1) return 0;
//        else if (date0 > date1) return 1;
//        else return -1;

    }
}
