package com.zhangls.tabsfragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BaseHolder<Data> {
    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================
    private Activity mActivity;

    private int mPosition;

    private Data mData;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    public BaseHolder(Activity activity, Data data) {
        mActivity = activity;
        mData = data;
    }

    // ==========================================================================
    // Getters
    // ==========================================================================
    public Activity getActivity() {
        return mActivity;
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

    public Context getThemeContext() {
        return mActivity;
    }

    public Resources getThemeResources() {
        return mActivity.getResources();
    }

    public View inflate(int resId) {
        return LayoutInflater.from(mActivity).inflate(resId, null);
    }


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
}
