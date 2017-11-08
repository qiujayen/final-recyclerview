package com.jay.widget;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jay on 2017/11/7 上午9:18
 */
public abstract class RecyclerArrayAdapter<T> extends RecyclerView.Adapter<RecyclerViewHolder> {

    private final LayoutInflater mInflater;
    private final int mResource;
    private List<T> mObjects;

    public RecyclerArrayAdapter(Context context, @LayoutRes int resource) {
        this(context, resource, new ArrayList<T>());
    }

    public RecyclerArrayAdapter(Context context, @LayoutRes int resource, @NonNull T[] objects) {
        this(context, resource, Arrays.asList(objects));
    }

    public RecyclerArrayAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<T> objects) {
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        mObjects = objects;
    }

    @Override
    public final RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(mResource, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mObjects == null ? 0 : mObjects.size();
    }

    public List<T> getObjects() {
        return mObjects;
    }

    public T getItem(int position) {
        return mObjects.get(position);
    }

    public void setAll(@NonNull List<T> objects) {
        mObjects = objects;
    }

    public void add(@Nullable T object) {
        if (mObjects == null) mObjects = new ArrayList<>();
        mObjects.add(object);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addAll(@NonNull List<T> objects) {
        if (mObjects == null) mObjects = new ArrayList<>();
        mObjects.addAll(objects);
        int size = objects.size();
        notifyItemRangeInserted(getItemCount() - size, size);
    }

    public void addAll(T... items) {
        List<T> asList = Arrays.asList(items);
        addAll(asList);
    }

    public void insert(@Nullable T object, int index) {
        if (mObjects == null) return;
        mObjects.add(index, object);
        notifyItemInserted(index);
    }

    public void remove(@Nullable T object) {
        if (mObjects == null) return;
        int indexOf = mObjects.indexOf(object);
        remove(indexOf);
    }

    public void remove(int position) {
        if (mObjects == null) return;
        mObjects.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        if (mObjects != null) mObjects.clear();
        notifyDataSetChanged();
    }
}
