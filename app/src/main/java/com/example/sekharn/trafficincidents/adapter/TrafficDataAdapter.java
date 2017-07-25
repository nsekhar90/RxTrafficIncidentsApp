package com.example.sekharn.trafficincidents.adapter;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sekharn.trafficincidents.databinding.TrafficDataViewBinding;
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
        TrafficDataViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), mRowLayout, viewGroup, false);
        return new MyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, int i) {
        final Resources trafficInfo = (trafficIncidents.get(i));
        viewHolder.binding.setTrafficModel(trafficInfo);
    }

    @Override
    public int getItemCount() {
        return trafficIncidents == null ? 0 : trafficIncidents.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private TrafficDataViewBinding binding;

        MyViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
