package com.example.sekharn.trafficincidents.model;

public class LocationAddress {

    private float latitude;
    private float longitude;

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

}
