package com.example.sekharn.trafficincidents.network.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PredictionData implements Serializable {

    @SerializedName("predictions")
    private ArrayList<PredictionArrayData> predictionDataList;

    @SerializedName("status")
    private String status;

    public ArrayList<PredictionArrayData> getPredictionDataList() {
        return predictionDataList;
    }

    public void setPredictionDataList(ArrayList<PredictionArrayData> predictionDataList) {
        this.predictionDataList = predictionDataList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
