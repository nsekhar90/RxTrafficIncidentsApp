package com.example.sekharn.trafficincidents.network.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleAutoPlaceCompleteApi {

    @GET("json?")
    Call<JsonObject> getQueryResults(@Query("input") String input,
                                     @Query("key") String key);
}
