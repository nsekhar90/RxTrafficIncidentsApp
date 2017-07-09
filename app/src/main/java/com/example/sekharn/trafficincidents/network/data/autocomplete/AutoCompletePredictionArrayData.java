package com.example.sekharn.trafficincidents.network.data.autocomplete;

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

    public AutoCompletePredictionDataStructuredFormatting getAutoCompletePredictionDataStructuredFormatting() {
        return autoCompletePredictionDataStructuredFormatting;
    }

    @Override
    public String toString() {
        return autoCompletePredictionDataStructuredFormatting.getMainText() + ", " + autoCompletePredictionDataStructuredFormatting.getSecondaryText();
    }
}
