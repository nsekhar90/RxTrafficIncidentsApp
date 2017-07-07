package com.example.sekharn.trafficincidents;

import android.app.Application;

import com.example.sekharn.trafficincidents.network.api.IGoogleAutoPlaceCompleteApi;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrafficIndicentsApplication extends Application {

    private IGoogleAutoPlaceCompleteApi googlePlacesAutoComplete;

    @Override
    public void onCreate() {
        super.onCreate();
        buildNecessaryComponents();
    }

    private void buildNecessaryComponents() {
       buildRetrofitForGooglePlacesAutoComplete();
    }

    private void buildRetrofitForGooglePlacesAutoComplete() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/place/autocomplete/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        googlePlacesAutoComplete = retrofit.create(IGoogleAutoPlaceCompleteApi.class);
    }

    public IGoogleAutoPlaceCompleteApi getGooglePlacesAutoCompleteApi() {
        return googlePlacesAutoComplete;
    }
}
