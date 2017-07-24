package com.example.sekharn.trafficincidents.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.network.data.bingetraffic.Resources;

import java.util.List;

public class TrafficDataAdapter extends RecyclerView.Adapter<TrafficDataAdapter.MyViewHolder> {

    private List<Resources> trafficIncidents;

    private int mRowLayout;

    public TrafficDataAdapter(List<Resources> trafficIncidents, int rowLayout) {
        this.trafficIncidents = trafficIncidents;
        mRowLayout = rowLayout;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int i) {
        final Resources trafficInfo = trafficIncidents.get(i);
        viewHolder.description.setText(trafficInfo.getDescription());
        viewHolder.verified.setText("Verified: " + (trafficInfo.isVerified() ? "True" : "False"));
    }

    @Override
    public int getItemCount() {
        return trafficIncidents == null ? 0 : trafficIncidents.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView description;

        public TextView verified;

        public MyViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.description);
            verified = (TextView) itemView.findViewById(R.id.verified);
        }
    }
}
