package com.example.sekharn.trafficincidents;

import android.app.Application;

import com.example.sekharn.trafficincidents.network.api.IGoogleAutoPlaceCompleteApi;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            HttpUrl originalHttpUrl = original.url();

            HttpUrl url = originalHttpUrl.newBuilder()
                    .addQueryParameter("key", getString(R.string.google_maps_places_autocomplete_key))
                    .build();

            Request.Builder requestBuilder = original.newBuilder()
                    .url(url);

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
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
