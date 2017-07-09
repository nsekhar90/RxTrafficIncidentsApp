package com.example.sekharn.trafficincidents.model;

public class LocationAddress {

    private float latitude;
    private float longitude;
    private String address;

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLatAndLong(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
