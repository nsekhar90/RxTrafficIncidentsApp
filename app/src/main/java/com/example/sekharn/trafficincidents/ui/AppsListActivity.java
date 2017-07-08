package com.example.sekharn.trafficincidents.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.sekharn.trafficincidents.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class AppsListActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent intent = new Intent(context, AppsListActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);
        setUpArrayObservables();
        setUpAppsListObservables();
    }

    private void setUpAppsListObservables() {

        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<String> applicationNames = new ArrayList<>();
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.name != null) {
                Log.d("findMe", "Installed package :" + packageInfo.name.substring(packageInfo.name.lastIndexOf('.') + 1).trim());
                applicationNames.add(packageInfo.name.substring(packageInfo.name.lastIndexOf('.') + 1).trim());
            }
        }
        Observable<List<String>> arrayListObservable = Observable.fromArray(applicationNames);

    }

    private void setUpArrayObservables() {
        ArrayList<Double> arrayList = new ArrayList<>();
        Double maxN = Math.random() + 5;
        for (double i = 0; i < maxN; i++) {
            arrayList.add(i);
        }

        Observable<ArrayList<Double>> arrayObservable = Observable.fromArray(arrayList);
        arrayObservable.subscribe(new Observer<ArrayList<Double>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(ArrayList<Double> doubles) {
                Log.e("findMe", "onNext of Arrays: " + doubles.toString());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }


}
