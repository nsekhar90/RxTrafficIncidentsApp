package com.example.sekharn.trafficincidents.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.sekharn.trafficincidents.presenter.MainPresenter;
import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.TrafficIncidentsApp;
import com.example.sekharn.trafficincidents.adapter.AutoCompleteSuggestionsAdapter;
import com.example.sekharn.trafficincidents.adapter.TrafficDataAdapter;
import com.example.sekharn.trafficincidents.contract.MainActivityContract;
import com.example.sekharn.trafficincidents.network.data.autocomplete.AutoCompletePredictionData;
import com.example.sekharn.trafficincidents.network.data.bingetraffic.Resources;
import com.example.sekharn.trafficincidents.network.data.bingetraffic.TrafficData;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAutoCompleteTextView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements MainActivityContract {

    @Inject
    MainPresenter mainPresenter;

    private CompositeDisposable mainActivityCompositeDisposable;

    private AutoCompleteSuggestionsAdapter sourceAddressAdapter;
    private AutoCompleteSuggestionsAdapter destinationAddressAdapter;
    private RecyclerView trafficDataView;

    private Button trafficInfoButton;
    private ArrayList<Resources> trafficDataList;

    private AutoCompleteTextView source;
    private AutoCompleteTextView destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((TrafficIncidentsApp) getApplication()).getAppComponent().inject(this);

        source = (AutoCompleteTextView) findViewById(R.id.source_location);
        destination = (AutoCompleteTextView) findViewById(R.id.destination_location);
        trafficInfoButton = (Button) findViewById(R.id.my_button);
        trafficDataView = (RecyclerView) findViewById(R.id.traffic_data_view);

        trafficDataList = new ArrayList<>();

        Observable<CharSequence> sourceLocationObservable = RxTextView.textChanges(source);
        Observable<CharSequence> destinationLocationObservable = RxTextView.textChanges(destination);
        mainActivityCompositeDisposable = new CompositeDisposable();

        Disposable sourceLocationDisposable = sourceLocationObservable
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> mainPresenter.onSourceLocationEditTextInputChanged(this, charSequence.toString()));

        Disposable destinationLocationDisposable = destinationLocationObservable
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> mainPresenter.onDestinationLocationEditTextInputChanged(this, charSequence.toString()));

        mainActivityCompositeDisposable.add(sourceLocationDisposable);
        mainActivityCompositeDisposable.add(destinationLocationDisposable);

        RxAutoCompleteTextView.itemClickEvents(source).subscribe(adapterViewItemClickEvent -> {
            mainPresenter.setSourceAddress(sourceAddressAdapter.getItem(adapterViewItemClickEvent.position()).getDescription());
        });

        RxAutoCompleteTextView.itemClickEvents(destination).subscribe(adapterViewItemClickEvent -> {
            mainPresenter.setDestinationAddress(destinationAddressAdapter.getItem(adapterViewItemClickEvent.position()).getDescription());
        });

        mainPresenter.setTrafficDataButttonEnableLogic(this, sourceLocationObservable, destinationLocationObservable);

        RxView.clicks(trafficInfoButton)
                .throttleFirst(5, TimeUnit.SECONDS)
                .subscribe(o -> mainPresenter.onTrafficDataObtained(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mainActivityCompositeDisposable != null && !mainActivityCompositeDisposable.isDisposed()) {
            mainActivityCompositeDisposable.dispose();
        }
    }

    @Override
    public void updateSourceLocationText(AutoCompletePredictionData autoCompletePredictionData) {
        sourceAddressAdapter = new AutoCompleteSuggestionsAdapter(this, R.layout.row_auto_complete_place_suggestion_item, autoCompletePredictionData.getPredictionDataList());
        source.setAdapter(sourceAddressAdapter);
    }

    @Override
    public void updateDestinationLocationText(AutoCompletePredictionData autoCompletePredictionData) {
        destinationAddressAdapter = new AutoCompleteSuggestionsAdapter(this, R.layout.row_auto_complete_place_suggestion_item, autoCompletePredictionData.getPredictionDataList());
        destination.setAdapter(destinationAddressAdapter);
    }

    @Override
    public void updateTrafficData(TrafficData trafficData) {
        trafficDataList = trafficData.getResourceSets().get(0).getResources();
        TrafficDataAdapter trafficDataAdapter = new TrafficDataAdapter(trafficDataList, R.layout.row_traffic_data_item);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        trafficDataView.setLayoutManager(layoutManager);
        trafficDataView.setItemAnimator(new DefaultItemAnimator());
        trafficDataView.setAdapter(trafficDataAdapter);
    }

    @Override
    public void setTrafficButtonEnabled(Boolean aBoolean) {
        trafficInfoButton.setEnabled(aBoolean);
    }
}
