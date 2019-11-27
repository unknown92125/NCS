package com.mrex.ncs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reservation implements Comparable<Reservation> {

    private String address;
    private String date;
    private String phone;
    private String area;

    private String expectedTime;
    private String payPrice;
    private String payOption;
    private String payName;
    private String payDate;
    private long milliDate;

    public Reservation() {
    }

    public Reservation(String address, String date, String phone, String area, String expectedTime, String payPrice, String payOption, String payName, String payDate) {
        this.address = address;
        this.date = date;
        this.phone = phone;
        this.area = area;
        this.expectedTime = expectedTime;
        this.payPrice = payPrice;
        this.payOption = payOption;
        this.payName = payName;
        this.payDate = payDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public String getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(String payPrice) {
        this.payPrice = payPrice;
    }

    public String getPayOption() {
        return payOption;
    }

    public void setPayOption(String payOption) {
        this.payOption = payOption;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public long getMilliDate() {
        return milliDate;
    }

    public void setMilliDate(long milliDate) {
        this.milliDate = milliDate;
    }

    @Override
    public int compareTo(Reservation reservation) {

        try {
            Date d = new SimpleDateFormat("yyyy/M/d (E) a h:mm", Locale.getDefault()).parse(this.date);
            long date1 = d.getTime();
            d = new SimpleDateFormat("yyyy/M/d (E) a h:mm", Locale.getDefault()).parse(reservation.getDate());
            long date2 = d.getTime();

            if (date1 == date2) return 0;
            else if (date1 > date2) return 1;
            else return -1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;

    }
}
//    @Override
//    public int compareTo(Reservation reservation) {
//
//        try {
//            Date dDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).parse(this.date.substring(0, this.date.length() - 4));
//            long date0 = dDate.getTime();
//            dDate = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault()).parse(reservation.getDate().substring(0, reservation.getDate().length() - 4));
//            long date1 = dDate.getTime();
//
//            if (date0 == date1) return 0;
//            else if (date0 > date1) return 1;
//            else return -1;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return 0;
//
//    }

