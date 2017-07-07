package com.example.sekharn.trafficincidents;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.example.sekharn.trafficincidents.network.api.IGoogleAutoPlaceCompleteApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonObject;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final IGoogleAutoPlaceCompleteApi googleAutoPlaceCompleteApi =
                ((TrafficIndicentsApplication) getApplication()).getGooglePlacesAutoCompleteApi();
        final EditText source = (EditText) findViewById(R.id.place_one);
        RxTextView.textChanges(source)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        Log.e("findMe", "charSequence = " + charSequence);
                        Call<JsonObject> call =
                                googleAutoPlaceCompleteApi.getQueryResults(charSequence.toString(), getString(R.string.google_maps_places_autocomplete_key));
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                Log.e("findMe", "onResponse: " + response.body());
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e("findMe", "onFailure: " + t.getMessage());
                                Log.e("findMe", "onFailure: " + call.request().body());

                            }
                        });
                    }
                });


//        searchResults.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<JsonObject>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.e("findMe", "onSubscribe");
//                    }
//
//                    @Override
//                    public void onNext(JsonObject jsonObject) {
//                        Log.e("findMe", "onNext: " + jsonObject.getAsString());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.e("findMe", "onError: " + e.getMessage());
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.e("findMe", "onComplete: " );
//                    }
//                });


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
