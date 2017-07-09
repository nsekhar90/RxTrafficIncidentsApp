package com.example.sekharn.trafficincidents.network.data.bingetraffic;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Resources implements Serializable {

    @SerializedName("__type")
    private String type;

    @SerializedName("congestion")
    private String congestion;

    @SerializedName("description")
    private String description;

    @SerializedName("detour")
    private String detour;

    @SerializedName("verified")
    private boolean verified;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCongestion() {
        return congestion;
    }

    public void setCongestion(String congestion) {
        this.congestion = congestion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetour() {
        return detour;
    }

    public void setDetour(String detour) {
        this.detour = detour;
    }

    public boolean isVerified() {
        return verified;
    }
}
