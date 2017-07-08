package com.example.sekharn.trafficincidents.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AutoCompletePredictionArrayData implements Serializable {

    @SerializedName("description")
    private String description;

    @SerializedName("structured_formatting")
    private AutoCompletePredictionDataStructuredFormatting autoCompletePredictionDataStructuredFormatting;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
