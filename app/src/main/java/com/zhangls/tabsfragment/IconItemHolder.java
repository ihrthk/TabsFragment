package com.zhangls.tabsfragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;


public abstract class IconItemHolder<Data> extends BaseHolder<Data> implements ImageItem, AsyncImageLoader.ResultListener {
    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================

    protected AsyncImageLoader mLoader;


    // ==========================================================================
    // Constructors
    // ==========================================================================
    public IconItemHolder(Activity activity, ImageListAdapter adapter, Data data) {
        super(activity, data);
        mLoader = AsyncImageLoader.getInstance(activity);
    }

    public void setIcon(Drawable drawable) {
        ImageView iconView = getIconView();
        iconView.setBackgroundDrawable(drawable);
    }


    public abstract ImageView getIconView();

    public abstract String getIconKey();

    @Override
    public void loadImages() {
        // 取消该item之前的任务
        mLoader.cancel(getIconKey());
        setIcon(null);
        mLoader.load(getIconKey());
    }

    @Override
    public void cancelLoadImages() {
        mLoader.cancel(getIconKey());
    }


    @Override
    public void onLoadComplete(String key, Drawable drawable) {
        if (key.equals(getIconKey())) {
            ImageView iconView = getIconView();
            iconView.setBackgroundDrawable(drawable);
        }
    }



}
