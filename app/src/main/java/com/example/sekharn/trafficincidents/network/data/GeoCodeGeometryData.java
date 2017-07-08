package com.example.sekharn.trafficincidents.network.data;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeoCodeGeometryData implements Serializable {

    @SerializedName("location")
    private GeoCodeLatLong geoCodeLatLong;

    public GeoCodeLatLong getGeoCodeLatLong() {
        return geoCodeLatLong;
    }
}
