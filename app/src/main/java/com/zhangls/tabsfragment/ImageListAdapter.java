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
import android.widget.AbsListView.OnScrollListener;

/**
 * 该类是常规列表适配器的抽象，主要针对几个较为通用的功能进行了抽象和优化：
 * <p>1. 列表滑至底部异步加载更多项相关功能，参见{@link AsyncLoadingAdapter}
 * <p>2.图片的异步加载和显示框架，内置了对快速滑动时的显示策略优化，可以有效的提高列表流畅度
 *
 * @version 1
 */
public abstract class ImageListAdapter extends AsyncLoadingAdapter {
    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================


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
        Holder holder;
        View view;
        if (null == convertView) {
            holder = getHolder(position);
            convertView = holder.getRootView();
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (null == holder) {
            return new View(getContext());
        }
        view = holder.getRootView();

        return view;
    }

    protected abstract Holder getHolder(int position);


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
}
