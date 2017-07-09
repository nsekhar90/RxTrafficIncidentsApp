package com.example.sekharn.trafficincidents;

import android.app.Application;

import com.example.sekharn.trafficincidents.di.component.AppComponent;
import com.example.sekharn.trafficincidents.di.factory.AppComponentFactory;

import timber.log.Timber;

public class TrafficIncidentsApp extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        buildComponentGraph();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    private void buildComponentGraph() {
        appComponent = AppComponentFactory.create(this);
        appComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
