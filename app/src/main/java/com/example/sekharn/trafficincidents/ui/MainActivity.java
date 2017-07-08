package com.example.sekharn.trafficincidents.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.TrafficIndicentsApplication;
import com.example.sekharn.trafficincidents.network.api.IGoogleAutoPlaceCompleteApi;
import com.example.sekharn.trafficincidents.network.data.PredictionData;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private Subscription originLocationSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final IGoogleAutoPlaceCompleteApi googleAutoPlaceCompleteApi =
                ((TrafficIndicentsApplication) getApplication()).getGooglePlacesAutoCompleteApi();
        final EditText source = (EditText) findViewById(R.id.place_one);
        originLocationSubscription = RxTextView.textChanges(source)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        Log.e("findMe", "charSequence = " + charSequence);
                        googleAutoPlaceCompleteApi.getQueryResults(charSequence.toString(), getString(R.string.google_maps_places_autocomplete_key))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<PredictionData>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                        Log.e("findMe", "onSubscribe ");
                                    }

                                    @Override
                                    public void onNext(PredictionData predictionData) {
                                        if (predictionData.getPredictionDataList().size() > 1) {
                                            Log.e("findMe", "onNext: " + predictionData.getPredictionDataList().get(0).getDescription());
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e("findMe", "onError: " + e.getMessage());
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                    }
                });

        setUpTimer();

        Button myButton = (Button) findViewById(R.id.my_button);
        RxView.clicks(myButton)
//                .skip(4)
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    Toast.makeText(MainActivity.this, "Toast displayed after 500 ms", Toast.LENGTH_SHORT).show();
                    AppsListActivity.start(MainActivity.this);
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!originLocationSubscription.isUnsubscribed()) {
            originLocationSubscription.unsubscribe();
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
