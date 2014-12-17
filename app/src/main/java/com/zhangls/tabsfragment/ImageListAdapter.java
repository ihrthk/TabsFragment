/*
 * File Name: BaseAppListAdapter.java 
 * History:
 * Created by Siyang.Miao on 2011-9-2
 */
package com.zhangls.tabsfragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.List;

/**
 * 该类是常规列表适配器的抽象，主要针对几个较为通用的功能进行了抽象和优化：
 * <p>1. 列表滑至底部异步加载更多项相关功能，参见{@link AsyncLoadingAdapter}
 * <p>2.图片的异步加载和显示框架，内置了对快速滑动时的显示策略优化，可以有效的提高列表流畅度
 *
 * @version 1
 */
public abstract class ImageListAdapter extends AsyncLoadingAdapter implements AbsListView.RecyclerListener {
    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================
    private List<ImageItem> mDisplayedItems;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    public ImageListAdapter(Context context) {
        super(context);
    }

    // ==========================================================================
    // Setters
    // ==========================================================================
    @Override
    public void setListView(AbsListView listView) {
        super.setListView(listView);
    }


    // ==========================================================================
    // Methods
    // ==========================================================================
    @Override
    protected View getItemView(final int position, View convertView, ViewGroup parent) {
        ImageItem imageItem;
        View view;
        if (null == convertView) {
            imageItem = getImageItem(position);
            convertView = imageItem.getRootView();
            convertView.setTag(imageItem);
        } else {
            imageItem = (ImageItem) convertView.getTag();
        }
        if (null == imageItem) {
            return new View(getContext());
        }

        view = imageItem.getRootView();
        view.setTag(imageItem);
        mDisplayedItems.add(imageItem);
        loadImage(imageItem);


        return view;
    }

    /**
     * 子类可重写此方法实现加载图片的逻辑
     *
     * @param item
     */
    protected void loadImage(ImageItem item) {
        item.loadImages();
    }

    /**
     * 子类可重写此方法实现取消图片加载的逻辑
     *
     * @param item
     * @param view
     */
    protected void cancelLoadImage(ImageItem item) {
        item.cancelLoadImages();
    }

    protected abstract ImageItem getImageItem(int position);


    @Override
    public void onMovedToScrapHeap(View view) {
        if (null == view) {
            return;
        }
        Object tag = view.getTag();
        if (tag instanceof ImageItem) {
            ImageItem holder = (ImageItem) tag;
            // 取消该view的图片下载任务
            // NOTE: 此处若为item中的ImageView设置图片，会使列表明显变卡
            cancelLoadImage(holder);
            mDisplayedItems.remove(holder);
        }
    }
    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
}
