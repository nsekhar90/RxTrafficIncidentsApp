package com.example.sekharn.trafficincidents.network.api;

import com.example.sekharn.trafficincidents.network.data.PredictionData;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleAutoPlaceCompleteApi {

    @GET("json?")
    Observable<PredictionData> getQueryResults(@Query("input") String input,
                                               @Query("key") String key);
}
