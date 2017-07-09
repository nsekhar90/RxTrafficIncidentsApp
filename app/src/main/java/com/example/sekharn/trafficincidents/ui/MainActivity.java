package com.example.sekharn.trafficincidents.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.TrafficIncidentsApp;
import com.example.sekharn.trafficincidents.adapters.AutoCompleteSuggestionsAdapter;
import com.example.sekharn.trafficincidents.di.annotation.DestLatLong;
import com.example.sekharn.trafficincidents.di.annotation.SourceLatLong;
import com.example.sekharn.trafficincidents.model.LocationAddress;
import com.example.sekharn.trafficincidents.network.api.IBingTrafficDataApi;
import com.example.sekharn.trafficincidents.network.api.IGoogleAutoPlaceCompleteApi;
import com.example.sekharn.trafficincidents.network.api.IGoogleGeoCodingApi;
import com.example.sekharn.trafficincidents.network.data.geocode.GeoCodeLatLong;
import com.example.sekharn.trafficincidents.util.ApiUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAutoCompleteTextView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Disposable originLocationSubscription;
    private Disposable destinationLocationSubscription;

    @Inject
    IGoogleAutoPlaceCompleteApi googleAutoPlaceCompleteApi;

    @Inject
    IGoogleGeoCodingApi googleGeoCodingApi;

    @Inject
    IBingTrafficDataApi bingeTrafficApi;

    @Inject
    @SourceLatLong
    LocationAddress sourceLatLong;

    @Inject
    @DestLatLong
    LocationAddress destinationLatLong;

    private Button getTrafficInfoButton;
    private AutoCompleteSuggestionsAdapter sourceAddressAdapter;
    private AutoCompleteSuggestionsAdapter destinationAddressAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TrafficIncidentsApp) getApplication()).getAppComponent().inject(this);

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
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterSuccess(autoCompletePredictionData -> {
                                sourceAddressAdapter = new AutoCompleteSuggestionsAdapter(this, R.layout.row_auto_complete_place_suggestion, autoCompletePredictionData.getPredictionDataList());
                                source.setAdapter(sourceAddressAdapter);
                            })
                            .flatMap(autoCompletePredictionData -> {
                                Log.e("findMe", "onNext in flatmap: " + autoCompletePredictionData.getPredictionDataList().get(0).getDescription());
                                return googleGeoCodingApi.getLatLong(autoCompletePredictionData.getPredictionDataList().get(0).getDescription());
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(geoCodingData -> {
                                if (geoCodingData.getGeoCodeResultsDatas().size() >= 1) {
                                    GeoCodeLatLong geometryData = geoCodingData.getGeoCodeResultsDatas().get(0).getGeoCodeGeometryData().getGeoCodeLatLong();
                                    sourceLatLong.setLatAndLong(geometryData.getLatitude(), geometryData.getLongitude());
                                }
                            }, throwable -> Log.e("findMe", "exception while fetching results: " + throwable.getCause()));
                });

        RxAutoCompleteTextView.itemClickEvents(source).subscribe(adapterViewItemClickEvent -> {
           Log.e("findMe", "onItemSelected source: " + sourceAddressAdapter.getItem(adapterViewItemClickEvent.position()).getDescription());
        });

        RxAutoCompleteTextView.itemClickEvents(destination).subscribe(adapterViewItemClickEvent -> {
           Log.e("findMe", "onItemSelected destination: " + destinationAddressAdapter.getItem(adapterViewItemClickEvent.position()).getDescription());
        });

         /*code to get lat, long of destination address */
        destinationLocationSubscription = destinationLocationObservable
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(charSequence -> {
                    googleAutoPlaceCompleteApi.getQueryResults(charSequence.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doAfterSuccess(autoCompletePredictionData -> {
                                destinationAddressAdapter = new AutoCompleteSuggestionsAdapter(this, R.layout.row_auto_complete_place_suggestion, autoCompletePredictionData.getPredictionDataList());
                                destination.setAdapter(destinationAddressAdapter);
                            })
                            .flatMap(autoCompletePredictionData -> {
                                Log.e("findMe", "onNext in flatmap: " + autoCompletePredictionData.getPredictionDataList().get(0).getDescription());
                                return googleGeoCodingApi.getLatLong(autoCompletePredictionData.getPredictionDataList().get(0).getDescription());
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(geoCodingData -> {
                                if (geoCodingData.getGeoCodeResultsDatas().size() >= 1) {
                                    GeoCodeLatLong geometryData = geoCodingData.getGeoCodeResultsDatas().get(0).getGeoCodeGeometryData().getGeoCodeLatLong();
                                    destinationLatLong.setLatAndLong(geometryData.getLatitude(), geometryData.getLongitude());
                                    Log.e("findMe", "destinationAddress: lat: " + destinationLatLong.getLatitude() + " long: " + destinationLatLong.getLongitude());
                                }
                            }, throwable -> Log.e("findMe", "exception while fetching results: " + throwable.getCause()));
                });

        setUpTimer();
        setUpButtonEnableLogic(sourceLocationObservable, destinationLocationObservable);

        RxView.clicks(getTrafficInfoButton)
                .throttleFirst(5, TimeUnit.SECONDS) //throttleFirst just stops further events for next 5 seconds so if user clicks the button multiple times,
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    bingeTrafficApi.getTrafficData(ApiUtils.getFormattedLatLongForTrafficDetailsApi(sourceLatLong, destinationLatLong))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(trafficData -> {
                                Log.e("findMe", "trafficData = " + trafficData.getResourceSetses().get(0).getResources().get(0).getDescription());
                            }, throwable -> {
                                Log.e("findMe", "error = " + throwable.getMessage());
                            });
                });

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

        timerObservable.subscribe(aLong -> Log.e("findMe", "onNext of timer: " + aLong));
    }
}
