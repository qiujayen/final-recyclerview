package com.jay.widget;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

/**
 * Created by jay on 2017/11/7 上午9:19
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> views = new SparseArray<>();

    public RecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public <V extends View> V getView(@IdRes int resId) {
        View view = views.get(resId);
        if (view == null) {
            view = itemView.findViewById(resId);
        }
        return (V) view;
    }

    public RecyclerViewHolder setText(@IdRes int resId, CharSequence text) {
        TextView textView = getView(resId);
        textView.setText(text);
        return this;
    }
}
