package com.example.sekharn.trafficincidents.di.component;


import com.example.sekharn.trafficincidents.TrafficIncidentsApp;
import com.example.sekharn.trafficincidents.di.module.AppModule;
import com.example.sekharn.trafficincidents.di.module.BackEndModule;
import com.example.sekharn.trafficincidents.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class,
                BackEndModule.class
        }
)
public interface AppComponent {

    void inject(TrafficIncidentsApp trafficIncidentsApp);

    void inject(MainActivity mainActivity);
}
