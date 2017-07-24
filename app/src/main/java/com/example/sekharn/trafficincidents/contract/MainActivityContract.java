package com.example.sekharn.trafficincidents.contract;

import com.example.sekharn.trafficincidents.network.data.autocomplete.AutoCompletePredictionData;
import com.example.sekharn.trafficincidents.network.data.bingetraffic.TrafficData;

public interface MainActivityContract {

    void updateSourceLocationText(AutoCompletePredictionData autoCompletePredictionData);

    void updateDestinationLocationText(AutoCompletePredictionData autoCompletePredictionData);

    void updateTrafficData(TrafficData trafficData);

    void setTrafficButtonEnabled(Boolean aBoolean);
}