package com.example.sekharn.trafficincidents.model;

public class LocationAddress {

    private float latitude;
    private float longitude;

    public LocationAddress() {
    }

    public LocationAddress(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

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

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
