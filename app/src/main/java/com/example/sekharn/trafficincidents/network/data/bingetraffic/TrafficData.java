package com.example.sekharn.trafficincidents.network.data.bingetraffic;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TrafficData implements Serializable {

    @SerializedName("authenticationResultCode")
    private String authenticationResultCode;

    @SerializedName("copyright")
    private String copyright;

    @SerializedName("resourceSets")
    private ArrayList<ResourceSets> resourceSetses;

    public String getAuthenticationResultCode() {
        return authenticationResultCode;
    }

    public String getCopyright() {
        return copyright;
    }

    public ArrayList<ResourceSets> getResourceSets() {
        return resourceSetses;
    }
}
