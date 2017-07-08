package com.example.sekharn.trafficincidents.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AutoCompletePredictionDataStructuredFormatting implements Serializable {

    @SerializedName("main_text")
    private String mainText;

    @SerializedName("secondary_text")
    private String secondaryText;

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }

    public void setSecondaryText(String secondaryText) {
        this.secondaryText = secondaryText;
    }
}
