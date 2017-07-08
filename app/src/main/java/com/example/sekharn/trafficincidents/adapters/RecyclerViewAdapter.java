package com.example.sekharn.trafficincidents.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sekharn.trafficincidents.R;
import com.example.sekharn.trafficincidents.network.data.UsersAppData;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<UsersAppData> mApplications;

    private int mRowLayout;

    public RecyclerViewAdapter(List<UsersAppData> applications, int rowLayout) {
        mApplications = applications;
        mRowLayout = rowLayout;
    }

    public void addApplications(List<UsersAppData> applications) {
        mApplications.clear();
        mApplications.addAll(applications);
        notifyDataSetChanged();
    }

    public void addApplication(int position, UsersAppData appInfo) {
        if (position < 0) {
            position = 0;
        }
        mApplications.add(position, appInfo);
        notifyItemInserted(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(mRowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final UsersAppData appInfo = mApplications.get(i);
        viewHolder.name.setText(appInfo.getAppName());
        getBitmap(appInfo.getAppIcon())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        viewHolder.image.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return mApplications == null ? 0 : mApplications.size();
    }

    private Observable<Bitmap> getBitmap(final String icon) {
        return Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(ObservableEmitter<Bitmap> emitter) throws Exception {
                emitter.onNext(BitmapFactory.decodeFile(icon));
                emitter.onComplete();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;

        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
