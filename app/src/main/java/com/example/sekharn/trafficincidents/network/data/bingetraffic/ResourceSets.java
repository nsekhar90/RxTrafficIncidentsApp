package com.example.sekharn.trafficincidents.network.data.bingetraffic;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ResourceSets implements Serializable {

    @SerializedName("estimatedTotal")
    private String estimatedTotal;

    @SerializedName("resources")
    private ArrayList<Resources> resources;

    public String getEstimatedTotal() {
        return estimatedTotal;
    }

    public ArrayList<Resources> getResources() {
        return resources;
    }
}
