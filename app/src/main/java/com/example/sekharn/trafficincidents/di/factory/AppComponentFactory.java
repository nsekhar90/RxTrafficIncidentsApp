package com.example.sekharn.trafficincidents.di.factory;


import com.example.sekharn.trafficincidents.TrafficIncidentsApp;
import com.example.sekharn.trafficincidents.di.component.AppComponent;
import com.example.sekharn.trafficincidents.di.component.DaggerAppComponent;
import com.example.sekharn.trafficincidents.di.module.AppModule;

public final class AppComponentFactory {

    private AppComponentFactory() {
    }

    public static AppComponent create(TrafficIncidentsApp app) {
        return DaggerAppComponent
                .builder()
                .appModule(new AppModule(app))
                .build();
    }
}