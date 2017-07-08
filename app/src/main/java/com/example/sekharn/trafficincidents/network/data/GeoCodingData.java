package com.example.sekharn.trafficincidents.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GeoCodingData implements Serializable {

    @SerializedName("results")
    private ArrayList<GeoCodeResultsData> geoCodeResultsDatas;

    @SerializedName("status")
    private String status;

    public ArrayList<GeoCodeResultsData> getGeoCodeResultsDatas() {
        return geoCodeResultsDatas;
    }

    public String getStatus() {
        return status;
    }
}
