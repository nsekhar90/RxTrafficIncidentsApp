package com.example.sekharn.trafficincidents.network.data.bingetraffic;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Resources implements Serializable {

    @SerializedName("description")
    private String description;

    @SerializedName("verified")
    private boolean verified;

    public Resources(String description, boolean verified) {
        this.description = description;
        this.verified = verified;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return verified;
    }
}
