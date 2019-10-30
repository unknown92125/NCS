package com.mrex.ncs;

public class AddressData {

    private String address;
    private String placeName;
    private Double lat;
    private Double lng;

    public AddressData(String address, String placeName, Double lat, Double lng) {
        this.address = address;
        this.placeName = placeName;
        this.lat = lat;
        this.lng = lng;
    }

    public AddressData(String address, Double lat, Double lng) {
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.placeName="";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
