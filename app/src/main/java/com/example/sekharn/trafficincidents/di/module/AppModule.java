package com.example.sekharn.trafficincidents.di.module;

import android.content.Context;

import com.example.sekharn.trafficincidents.TrafficIncidentsApp;
import com.example.sekharn.trafficincidents.di.annotation.DestLatLong;
import com.example.sekharn.trafficincidents.di.annotation.ForApplication;
import com.example.sekharn.trafficincidents.di.annotation.SourceLatLong;
import com.example.sekharn.trafficincidents.model.LocationAddress;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private TrafficIncidentsApp app;

    public AppModule(TrafficIncidentsApp app) {
        this.app = app;
    }

    @Provides
    @ForApplication
    @Singleton
    Context provideApplicationContext() {
        return app.getBaseContext();
    }

    @Provides
    @Singleton
    @SourceLatLong
    LocationAddress provideSourceLocationAddress() {
        return new LocationAddress();
    }

    @Provides
    @Singleton
    @DestLatLong
    LocationAddress provideDestinationLocationAddress() {
        return new LocationAddress();
    }
}
