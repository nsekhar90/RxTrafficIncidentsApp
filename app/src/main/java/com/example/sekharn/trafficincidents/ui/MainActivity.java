package com.example.sekharn.trafficincidents.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.TrafficIncidentsApp;
import com.example.sekharn.trafficincidents.adapters.AutoCompleteSuggestionsAdapter;
import com.example.sekharn.trafficincidents.adapters.TrafficDataAdapter;
import com.example.sekharn.trafficincidents.di.annotation.DestLatLong;
import com.example.sekharn.trafficincidents.di.annotation.SourceLatLong;
import com.example.sekharn.trafficincidents.model.LocationAddress;
import com.example.sekharn.trafficincidents.network.api.IBingTrafficDataApi;
import com.example.sekharn.trafficincidents.network.api.IGoogleAutoPlaceCompleteApi;
import com.example.sekharn.trafficincidents.network.api.IGoogleGeoCodingApi;
import com.example.sekharn.trafficincidents.network.data.bingetraffic.Resources;
import com.example.sekharn.trafficincidents.network.data.bingetraffic.TrafficData;
import com.example.sekharn.trafficincidents.network.data.geocode.GeoCodeLatLong;
import com.example.sekharn.trafficincidents.network.data.geocode.GeoCodingData;
import com.example.sekharn.trafficincidents.util.ApiUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAutoCompleteTextView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

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


    private Disposable originLocationSubscription;
    private Disposable destinationLocationSubscription;

    private AutoCompleteSuggestionsAdapter sourceAddressAdapter;
    private AutoCompleteSuggestionsAdapter destinationAddressAdapter;
    private TrafficDataAdapter trafficDataAdapter;
    private RecyclerView trafficDataView;

    private Button getTrafficInfoButton;
    private ArrayList<Resources> trafficDataList;
    private ActionMenuView menuView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TrafficIncidentsApp) getApplication()).getAppComponent().inject(this);

        AutoCompleteTextView source = (AutoCompleteTextView) findViewById(R.id.source_location);
        AutoCompleteTextView destination = (AutoCompleteTextView) findViewById(R.id.destination_location);
        getTrafficInfoButton = (Button) findViewById(R.id.my_button);
        trafficDataView = (RecyclerView) findViewById(R.id.traffic_data_view);

        trafficDataList = new ArrayList<>();

        Observable<CharSequence> sourceLocationObservable = RxTextView.textChanges(source);
        Observable<CharSequence> destinationLocationObservable = RxTextView.textChanges(destination);

        /*code to get lat, long of source address */
        originLocationSubscription = sourceLocationObservable
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(charSequence -> {
                    googleAutoPlaceCompleteApi.getQueryResults(charSequence.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(autoCompletePredictionData -> {
                                sourceAddressAdapter = new AutoCompleteSuggestionsAdapter(this, R.layout.row_auto_complete_place_suggestion_item, autoCompletePredictionData.getPredictionDataList());
                                source.setAdapter(sourceAddressAdapter);
                            }, Throwable::printStackTrace);
                });

        RxAutoCompleteTextView.itemClickEvents(source).subscribe(adapterViewItemClickEvent -> {
            sourceLatLong.setAddress(sourceAddressAdapter.getItem(adapterViewItemClickEvent.position()).getDescription());
        });

        RxAutoCompleteTextView.itemClickEvents(destination).subscribe(adapterViewItemClickEvent -> {
            destinationLatLong.setAddress(destinationAddressAdapter.getItem(adapterViewItemClickEvent.position()).getDescription());
        });

         /*code to get lat, long of destination address */
        destinationLocationSubscription = destinationLocationObservable
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(charSequence1 -> {
                    googleAutoPlaceCompleteApi.getQueryResults(charSequence1.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(autoCompletePredictionData -> {
                                destinationAddressAdapter = new AutoCompleteSuggestionsAdapter(this, R.layout.row_auto_complete_place_suggestion_item, autoCompletePredictionData.getPredictionDataList());
                                destination.setAdapter(destinationAddressAdapter);
                            }, Throwable::printStackTrace);
                });

//        setUpTimer();
        setUpButtonEnableLogic(sourceLocationObservable, destinationLocationObservable);

        RxView.clicks(getTrafficInfoButton)
                .throttleFirst(5, TimeUnit.SECONDS) //throttleFirst just stops further events for next 5 seconds so if user clicks the button multiple times,
                .subscribe(aVoid -> {
                    Single<GeoCodingData> geoCodingSourceObservable = googleGeoCodingApi.getLatLong(sourceLatLong.getAddress());
                    Single<GeoCodingData> geoCodingDestinationObservable = googleGeoCodingApi.getLatLong(destinationLatLong.getAddress());

                    Single.zip(geoCodingSourceObservable.subscribeOn(Schedulers.newThread()), geoCodingDestinationObservable.subscribeOn(Schedulers.newThread()), new BiFunction<GeoCodingData, GeoCodingData, String>() {
                        @Override
                        public String apply(@NonNull GeoCodingData geoCodingData, @NonNull GeoCodingData geoCodingData2) throws Exception {
                            GeoCodeLatLong sourceData = geoCodingData.getGeoCodeResultsDatas().get(0).getGeoCodeGeometryData().getGeoCodeLatLong();
                            GeoCodeLatLong destinationData = geoCodingData2.getGeoCodeResultsDatas().get(0).getGeoCodeGeometryData().getGeoCodeLatLong();
                            sourceLatLong.setLatAndLong(sourceData.getLatitude(), sourceData.getLongitude());
                            destinationLatLong.setLatAndLong(destinationData.getLatitude(), destinationData.getLongitude());
                            return ApiUtils.getFormattedLatLongForTrafficDetailsApi(sourceLatLong, destinationLatLong);
                        }
                    }).flatMap(new Function<String, SingleSource<TrafficData>>() {
                        @Override
                        public SingleSource<TrafficData> apply(@NonNull String s) throws Exception {
                            return bingeTrafficApi.getTrafficData(s);
                        }
                    }).observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe(trafficData -> {

                                Log.e("findMe", "trafficData = " + trafficData.getResourceSets().get(0).getResources().get(0).getDescription());
                                trafficDataList = trafficData.getResourceSets().get(0).getResources();
                                Log.e("findMe: fromTdataList", trafficDataList.get(0).getDescription());
                                trafficDataAdapter = new TrafficDataAdapter(trafficDataList, R.layout.row_traffic_data_item);
                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                trafficDataView.setLayoutManager(layoutManager);
                                trafficDataView.setItemAnimator(new DefaultItemAnimator());
                                trafficDataView.setAdapter(trafficDataAdapter);
                            }, Throwable::printStackTrace);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_callable_observer:
                CallableObservableActivity.start(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
