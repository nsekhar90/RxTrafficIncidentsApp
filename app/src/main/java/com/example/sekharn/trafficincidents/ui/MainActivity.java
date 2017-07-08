package com.example.sekharn.trafficincidents.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.TrafficIndicentsApplication;
import com.example.sekharn.trafficincidents.network.api.IGoogleAutoPlaceCompleteApi;
import com.example.sekharn.trafficincidents.network.api.IGoogleGeoCodingApi;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.Subscription;

public class MainActivity extends AppCompatActivity {

    private Subscription originLocationSubscription;
    private Subscription destinationLocationSubscription;
    private IGoogleAutoPlaceCompleteApi googleAutoPlaceCompleteApi;
    private IGoogleGeoCodingApi googleGeoCodingApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleAutoPlaceCompleteApi = ((TrafficIndicentsApplication) getApplication()).getGooglePlacesAutoCompleteApi();
        googleGeoCodingApi = ((TrafficIndicentsApplication) getApplication()).getGeoCodingApi();

        AutoCompleteTextView source = (AutoCompleteTextView) findViewById(R.id.source_location);
        AutoCompleteTextView destination = (AutoCompleteTextView) findViewById(R.id.destination_location);
        Button getTrafficInfoButton = (Button) findViewById(R.id.my_button);

        rx.Observable<CharSequence> sourceLocationObservable = RxTextView.textChanges(source);
        rx.Observable<CharSequence> destinationLocationObservable = RxTextView.textChanges(destination);

        /*code to get lat, long of source address */
        originLocationSubscription = sourceLocationObservable
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(charSequence -> {
                    googleAutoPlaceCompleteApi.getQueryResults(charSequence.toString())
                            .flatMap(autoCompletePredictionData -> {
                                Log.e("findMe", "onNext in flatmap: " + autoCompletePredictionData.getPredictionDataList().get(0).getDescription());
                                return googleGeoCodingApi.getLatLong(autoCompletePredictionData.getPredictionDataList().get(0).getDescription());
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(geoCodingData -> {
                                if (geoCodingData.getGeoCodeResultsDatas().size() >= 1) {
                                    Log.e("findMe", "Lat:" + geoCodingData.getGeoCodeResultsDatas().get(0).getGeoCodeGeometryData().getGeoCodeLatLong().getLatitude());
                                    Log.e("findMe", "Long:" + geoCodingData.getGeoCodeResultsDatas().get(0).getGeoCodeGeometryData().getGeoCodeLatLong().getLongitude());
                                }
                            }, throwable -> Log.e("findMe", "exception while fetching results: " + throwable.getCause()));
                });

        rx.Observable.combineLatest(sourceLocationObservable, destinationLocationObservable, (charSequence, charSequence2) -> {
            boolean sourceCheck = charSequence.toString().length() >= 5;
            boolean destinationCheck = charSequence2.toString().length() >= 5;
            return sourceCheck && destinationCheck;
        }).subscribe(getTrafficInfoButton::setEnabled); //same as getTrafficInfoButton.setEnabled(boolean)

        RxView.clicks(getTrafficInfoButton)
//                .skip(4)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    Toast.makeText(MainActivity.this, "Toast displayed after 500 ms", Toast.LENGTH_SHORT).show();
                    AppsListActivity.start(MainActivity.this);
                });
//        setUpTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (originLocationSubscription != null && !originLocationSubscription.isUnsubscribed()) {
            originLocationSubscription.unsubscribe();
        }

        if (destinationLocationSubscription != null && !destinationLocationSubscription.isUnsubscribed()) {
            destinationLocationSubscription.unsubscribe();
        }
    }

    private void setUpTimer() {
        Observable<Long> timerObservable = Observable.interval(500, 400, TimeUnit.MILLISECONDS)
                .take(10) //Do 10 times and automatically stop
                .map(aLong -> aLong * aLong);

        timerObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Long aLong) {
                Log.e("findMe", "onNext of timer: " + aLong);
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
