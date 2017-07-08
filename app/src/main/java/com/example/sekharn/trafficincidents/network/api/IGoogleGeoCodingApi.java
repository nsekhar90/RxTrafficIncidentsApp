package com.example.sekharn.trafficincidents.network.api;

import com.example.sekharn.trafficincidents.network.data.geocode.GeoCodingData;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IGoogleGeoCodingApi {

    @GET("json?")
    Single<GeoCodingData> getLatLong(@Query("address") String address);
}
