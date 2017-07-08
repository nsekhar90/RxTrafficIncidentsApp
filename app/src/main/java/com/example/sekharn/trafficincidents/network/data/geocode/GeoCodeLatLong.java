package com.example.sekharn.trafficincidents.network.data.geocode;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeoCodeLatLong implements Serializable {

    @SerializedName("lat")
    private float latitude;

    @SerializedName("lng")
    private float longitude;

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
