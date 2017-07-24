package com.example.sekharn.trafficincidents.presenter;

import com.example.sekharn.trafficincidents.contract.MainActivityContract;
import com.example.sekharn.trafficincidents.model.LocationAddress;
import com.example.sekharn.trafficincidents.network.api.IBingTrafficDataApi;
import com.example.sekharn.trafficincidents.network.api.IGoogleAutoPlaceCompleteApi;
import com.example.sekharn.trafficincidents.network.api.IGoogleGeoCodingApi;
import com.example.sekharn.trafficincidents.network.data.bingetraffic.TrafficData;
import com.example.sekharn.trafficincidents.network.data.geocode.GeoCodeLatLong;
import com.example.sekharn.trafficincidents.network.data.geocode.GeoCodingData;
import com.example.sekharn.trafficincidents.util.ApiUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MainPresenter {

    private IGoogleAutoPlaceCompleteApi googleAutoPlaceCompleteApi;
    private IGoogleGeoCodingApi googleGeoCodingApi;
    private IBingTrafficDataApi bingeTrafficApi;
    private LocationAddress sourceLatLong;
    private LocationAddress destinationLatLong;

    @Inject
    public MainPresenter(IGoogleAutoPlaceCompleteApi googleAutoPlaceCompleteApi, LocationAddress sourceLatLong,
                         LocationAddress destinationLatLong, IGoogleGeoCodingApi googleGeoCodingApi,
                         IBingTrafficDataApi bingeTrafficDataApi) {
        this.destinationLatLong = destinationLatLong;
        this.sourceLatLong = sourceLatLong;
        this.googleAutoPlaceCompleteApi = googleAutoPlaceCompleteApi;
        this.googleGeoCodingApi = googleGeoCodingApi;
        this.bingeTrafficApi = bingeTrafficDataApi;
    }

    public void onSourceLocationEditTextInputChanged(MainActivityContract contract, String text) {
        googleAutoPlaceCompleteApi.getQueryResults(text)
                .subscribeOn(Schedulers.io()) // get data on IO thread
                .observeOn(AndroidSchedulers.mainThread()) //get notified on main thread
                .subscribe(autoCompletePredictionData1 -> {
                    contract.updateSourceLocationText(autoCompletePredictionData1);
                }, throwable -> {
                    Timber.e("Exception while fetching autocomplete data: " + throwable.getStackTrace());
                });
    }

    public void onDestinationLocationEditTextInputChanged(MainActivityContract contract, String text) {
        googleAutoPlaceCompleteApi.getQueryResults(text)
                .subscribeOn(Schedulers.io()) // get data on IO thread
                .observeOn(AndroidSchedulers.mainThread()) //get notified on main thread
                .subscribe(autoCompletePredictionData1 -> {
                    contract.updateDestinationLocationText(autoCompletePredictionData1);
                }, throwable -> {
                    Timber.e("Exception while fetching autocomplete data: " + throwable.getStackTrace());
                });
    }

    public void onTrafficDataObtained(MainActivityContract mainActivityContract) {
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
                            mainActivityContract.updateTrafficData(trafficData);
                        },
                        throwable -> {
                            throwable.printStackTrace();
                        });

    }

    public void setSourceAddress(String sourceAddress) {
        sourceLatLong.setAddress(sourceAddress);
    }

    public void setDestinationAddress(String destinationAddress) {
        destinationLatLong.setAddress(destinationAddress);
    }

    public void setTrafficDataButttonEnableLogic(MainActivityContract mainActivityContract, Observable<CharSequence> sourceLocationObservable,
                                                 Observable<CharSequence> destinationLocationObservable) {
        Observable.combineLatest(sourceLocationObservable, destinationLocationObservable, (charSequence, charSequence2) -> {
            boolean sourceCheck = charSequence.toString().length() >= 5;
            boolean destinationCheck = charSequence2.toString().length() >= 5;
            return sourceCheck && destinationCheck;
        }).subscribe(aBoolean -> mainActivityContract.setTrafficButtonEnabled(aBoolean));

    }
}