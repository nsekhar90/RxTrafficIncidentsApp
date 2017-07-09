package com.example.sekharn.trafficincidents.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.util.Database;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CallableObservableActivity extends AppCompatActivity {

    private Button naiveButton;
    private Button cleverButton;
    private TextView resultTextView;
    private ProgressBar progressBar;

    public static void start(Context context) {
        Intent intent = new Intent(context, CallableObservableActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callable_observable);
        naiveButton = (Button) findViewById(R.id.normal_callback);
        cleverButton = (Button) findViewById(R.id.observable_callback);
        resultTextView = (TextView) findViewById(R.id.result);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        naiveButton.setOnClickListener(view -> {
            showProgress();
            String result = Database.readValue();
            resultTextView.setText(result);
            hideProgress();
        });

        cleverButton.setOnClickListener(view -> {
            showProgress();
            Observable.fromCallable(Database::readValue)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        resultTextView.setText(result);
                        hideProgress();
                    });
        });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
