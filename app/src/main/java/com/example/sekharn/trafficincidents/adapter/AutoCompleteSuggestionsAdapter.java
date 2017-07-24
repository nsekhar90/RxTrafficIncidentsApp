package com.example.sekharn.trafficincidents.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.network.data.autocomplete.AutoCompletePredictionArrayData;

import java.util.ArrayList;

public class AutoCompleteSuggestionsAdapter extends ArrayAdapter<AutoCompletePredictionArrayData> {

    private ArrayList<AutoCompletePredictionArrayData> items;
    private int viewResourceId;

    public AutoCompleteSuggestionsAdapter(@NonNull Context context, @LayoutRes int viewResourceId, ArrayList<AutoCompletePredictionArrayData> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(viewResourceId, null);
        }

        AutoCompletePredictionArrayData place = items.get(position);
        if (place != null) {
            TextView mainTextLabel = (TextView) view.findViewById(R.id.main_text);
            if (mainTextLabel != null) {
                mainTextLabel.setText(place.getAutoCompletePredictionDataStructuredFormatting().getMainText());
            }

            TextView secondaryTextLabel = (TextView) view.findViewById(R.id.secondary_text);
            if (secondaryTextLabel != null) {
                secondaryTextLabel.setText(place.getAutoCompletePredictionDataStructuredFormatting().getSecondaryText());
            }

        }
        return view;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public AutoCompletePredictionArrayData getItem(int position) {
        return items.get(position);
    }
}
