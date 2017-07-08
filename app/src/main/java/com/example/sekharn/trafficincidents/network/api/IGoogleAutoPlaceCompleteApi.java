package com.example.sekharn.trafficincidents.network.api;

import com.example.sekharn.trafficincidents.network.data.PredictionData;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleAutoPlaceCompleteApi {

    @GET("json?")
    Single<PredictionData> getQueryResults(@Query("input") String input);
}
