package com.example.sekharn.trafficincidents.network.api;

import com.example.sekharn.trafficincidents.network.data.bingetraffic.TrafficData;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IBingTrafficDataApi {

    @GET("{latlonglatlong}")
    Single<TrafficData> getTrafficData(@Path("latlonglatlong") String latLonglatLong);
}
