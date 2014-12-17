package com.zhangls.tabsfragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseHolder<Data> {
    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================
    private Context mContext;

    private int mPosition;

    private Data mData;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    public BaseHolder(Context context, Data data) {
        mContext = context;
        mData = data;
    }

    // ==========================================================================
    // Getters
    // ==========================================================================
    public Context getContext() {
        return mContext;
    }

    // ==========================================================================
    // Setters
    // ==========================================================================

    // ==========================================================================
    // Methods
    // ==========================================================================
    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

    public View inflate(int resId) {
        return LayoutInflater.from(mContext).inflate(resId, null);
    }


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
}
