package com.example.sekharn.trafficincidents.network.data.geocode;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GeoCodeResultsData implements Serializable {


    @SerializedName("geometry")
    private GeoCodeGeometryData geoCodeGeometryData;

    public GeoCodeGeometryData getGeoCodeGeometryData() {
        return geoCodeGeometryData;
    }
}
