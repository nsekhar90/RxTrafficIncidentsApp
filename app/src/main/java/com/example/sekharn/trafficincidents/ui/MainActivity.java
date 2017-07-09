package com.example.sekharn.trafficincidents.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.TrafficIndicentsApplication;
import com.example.sekharn.trafficincidents.model.LocationAddress;
import com.example.sekharn.trafficincidents.network.api.IBingTrafficDataApi;
import com.example.sekharn.trafficincidents.network.api.IGoogleAutoPlaceCompleteApi;
import com.example.sekharn.trafficincidents.network.api.IGoogleGeoCodingApi;
import com.example.sekharn.trafficincidents.network.data.geocode.GeoCodeLatLong;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Disposable originLocationSubscription;
    private Disposable destinationLocationSubscription;

    private IGoogleAutoPlaceCompleteApi googleAutoPlaceCompleteApi;
    private IGoogleGeoCodingApi googleGeoCodingApi;
    private IBingTrafficDataApi bingeTrafficApi;

    private LocationAddress sourceAddress;
    private LocationAddress destinationAddress;

    private Button getTrafficInfoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleAutoPlaceCompleteApi = ((TrafficIndicentsApplication) getApplication()).getGooglePlacesAutoCompleteApi();
        googleGeoCodingApi = ((TrafficIndicentsApplication) getApplication()).getGeoCodingApi();
        bingeTrafficApi = ((TrafficIndicentsApplication) getApplication()).getBingTrafficDataApi();

        AutoCompleteTextView source = (AutoCompleteTextView) findViewById(R.id.source_location);
        AutoCompleteTextView destination = (AutoCompleteTextView) findViewById(R.id.destination_location);
        getTrafficInfoButton = (Button) findViewById(R.id.my_button);

        Observable<CharSequence> sourceLocationObservable = RxTextView.textChanges(source);
        Observable<CharSequence> destinationLocationObservable = RxTextView.textChanges(destination);

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
                                    GeoCodeLatLong geometryData = geoCodingData.getGeoCodeResultsDatas().get(0).getGeoCodeGeometryData().getGeoCodeLatLong();
                                    sourceAddress = new LocationAddress(geometryData.getLatitude(), geometryData.getLongitude());
                                    Log.e("findMe", "sourceAddress: lat: " + sourceAddress.getLatitude() + " long: " + sourceAddress.getLongitude());
                                }
                            }, throwable -> Log.e("findMe", "exception while fetching results: " + throwable.getCause()));
                });

         /*code to get lat, long of destination address */
        destinationLocationSubscription = destinationLocationObservable
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
                                    GeoCodeLatLong geometryData = geoCodingData.getGeoCodeResultsDatas().get(0).getGeoCodeGeometryData().getGeoCodeLatLong();
                                    destinationAddress = new LocationAddress(geometryData.getLatitude(), geometryData.getLongitude());
                                    Log.e("findMe", "destinationAddress: lat: " + destinationAddress.getLatitude() + " long: " + destinationAddress.getLongitude());
                                }
                            }, throwable -> Log.e("findMe", "exception while fetching results: " + throwable.getCause()));
                });

        setUpTimer();
        setUpButtonEnableLogic(sourceLocationObservable, destinationLocationObservable);

//        RxView.clicks(getTrafficInfoButton)
////                .skip(4)
//                .throttleFirst(5, TimeUnit.MILLISECONDS) //throttleFirst just stops further events for next 5 seconds so if user clicks the button multiple times,
//                .flatMap(new Function<Object, ObservableSource<TrafficData>>() {
//                    @Override
//                    public ObservableSource<TrafficData> apply(@NonNull Object o) throws Exception {
//                        return bingeTrafficApi.getTrafficData("37.354107,-121.95524,37.354107,-121.95524");
//                    }
//                }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(trafficData -> {
//                   Log.e("findMe", "trafficData = " + trafficData.getAuthenticationResultCode());
//                }, throwable -> {
//                    Log.e("findMe", "trafficData error= " + throwable.getStackTrace().toString());
//                });

    }

    private void setUpButtonEnableLogic(Observable<CharSequence> sourceLocationObservable, Observable<CharSequence> destinationLocationObservable) {
        Observable.combineLatest(sourceLocationObservable, destinationLocationObservable, (charSequence, charSequence2) -> {
            boolean sourceCheck = charSequence.toString().length() >= 5;
            boolean destinationCheck = charSequence2.toString().length() >= 5;
            return sourceCheck && destinationCheck;
        }).subscribe(getTrafficInfoButton::setEnabled); //same as getTrafficInfoButton.setEnabled(boolean)
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (originLocationSubscription != null && !originLocationSubscription.isDisposed()) {
            originLocationSubscription.dispose();
        }

        if (destinationLocationSubscription != null && !destinationLocationSubscription.isDisposed()) {
            destinationLocationSubscription.dispose();
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
