package com.example.sekharn.trafficincidents.network.api;

import com.example.sekharn.trafficincidents.network.data.autocomplete.AutoCompletePredictionData;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleAutoPlaceCompleteApi {

    @GET("json?")
    Single<AutoCompletePredictionData> getQueryResults(@Query("input") String input);
}
