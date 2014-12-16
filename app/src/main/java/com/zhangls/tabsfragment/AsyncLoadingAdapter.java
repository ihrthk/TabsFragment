package com.zhangls.tabsfragment;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;


/**
 * 分步加载列表适配器
 *
 * @version 1
 */
public abstract class AsyncLoadingAdapter extends BaseAdapter {
    // ==========================================================================
    // Constants

    // ==========================================================================
    /**
     * 默认每次加载更多的项数
     */
    public static final int DEFAULT_INCREMENT = 20;
    /**
     * 默认预加载提前量
     */
    public static final int DEFAULT_PRELOAD_COUNT = 5;
    /**
     * 最大项数
     */
    public static final int ITEM_COUNT_LIMIT = Integer.MAX_VALUE - 1;

    protected static final int VIEW_TYPE_MORE = 1;
    protected static final int VIEW_TYPE_ITEM = 2;

    private static final String TAG = AsyncLoadingAdapter.class.getSimpleName();

    // ==========================================================================
    // Fields
    // ==========================================================================
    protected Context mContext;
    // private int mItemCount;
    private boolean mLoading;
    private boolean mMoreEnabled;
    private int mItemLimit;

    protected AbsListView mAbsListView;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    public AsyncLoadingAdapter(Context context) {
        mContext = context;
        // mItemCount = 0;
        mLoading = false;
        mMoreEnabled = true;
        mItemLimit = ITEM_COUNT_LIMIT;
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
    public void setMoreEnabled(boolean enabled) {
        mMoreEnabled = enabled;
    }

    public boolean setItemLimit(int limit) {
        if (limit == Integer.MAX_VALUE) {
            Log.e(TAG, "Item limit should be less than Integer.MAX_VALUE " + Integer.MAX_VALUE);
            return false;
        }
        mItemLimit = limit;
        return true;
    }

    public void setListView(AbsListView listView) {
        mAbsListView = listView;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * 获取列表当前实际项数
     *
     * @return 列表当前实际项数
     */
    public abstract int getItemCount();

    /**
     * 获取列表每次加载更多时加载的项数。默认值为{@link #DEFAULT_INCREMENT}。
     *
     * @return 每次加载更多时加载的项数
     */
    public int getIncrement() {
        return DEFAULT_INCREMENT;
    }

    /**
     * 获取列表预加载提前量。假设提前量是2，那么列表在滚动到倒数第2项时，就会提前开 始下一页更多项的加载。默认值为{@link #DEFAULT_PRELOAD_COUNT}。
     *
     * @return 预加载提前量
     */
    public int getPreloadCount() {
        return DEFAULT_PRELOAD_COUNT;
    }

    ;

    /**
     * 是否还有更多项
     *
     * @return true表示还可以继续加载，false表示全部项已经加载完毕。
     */
    public abstract boolean hasMore();

    /**
     * 子类重写该方法实现加载更多项的逻辑。该方法会在非UI线程内异步执行。
     *
     * @param startPosition 加载更多的起始项位置
     * @param requestSize   请求加载的项数
     * @return 实际加载的项数
     */
    protected abstract int onLoadMore(int startPosition, int requestSize) throws Exception;

    /**
     * 获取列表内容项类型数
     *
     * @return 列表内容项类型数
     */
    protected int getItemViewTypeCount() {
        return 1;
    }

    /**
     * 获取指定位置的列表项的类型
     *
     * @param position 在列表中的位置
     * @return 该列表项的类型
     */
    protected int getContentItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    /**
     * 获取列表项的视图
     *
     * @param position    在列表中的位置
     * @param convertView 可重用视图
     * @param parent      父视图
     * @return 该列表项的视图
     */
    protected abstract View getItemView(int position, View convertView, ViewGroup parent);

    /**
     * 获取“更多”项的视图
     *
     * @param position    在列表中的位置
     * @param convertView 可重用视图
     * @param parent      父视图
     * @return 该列表项的视图
     */
    public abstract View getMoreView(int position, View convertView, ViewGroup parent);


    protected int getItemLimit() {
        return mItemLimit;
    }

    /**
     * 加载更多
     */
    private synchronized void loadMore() {
        if (!mLoading) {
            mLoading = true;
        } else {
            // Already loading
            return;
        }

        new AsyncTask<Integer, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... params) {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                int itemCount = getItemCount();
                int increment = Math.min(getIncrement(), getItemLimit() - getItemCount());
                boolean result = true;
                try {
                    onLoadMore(itemCount, increment);
                } catch (Exception e) {
                    e.printStackTrace();
                    result = false;
                }
                return result;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (!result) {
                    loadFailed();
                }

                notifyDataSetChanged();
                mLoading = false;
            }

        }.execute();

    }

    protected abstract void loadFailed();

    @Override
    public final int getCount() {
        int itemCount = getItemCount();
        if (hasMore() && (itemCount < getItemLimit()) && mMoreEnabled) {
            return itemCount + 1;
        } else {
            return itemCount;
        }
    }

    @Override
    public final int getViewTypeCount() {
        // + 2 加载更多与底部填充视图
        return getItemViewTypeCount() + 1;
    }

    @Override
    public final int getItemViewType(int position) {
        if (position < getItemCount()) {
            return getContentItemViewType(position);
        }
        return VIEW_TYPE_MORE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (getItemViewType(position) != VIEW_TYPE_MORE) {
            view = getItemView(position, convertView, parent);
        } else {
            view = getMoreView(position, convertView, parent);
        }

        final int itemCount = getItemCount();
        if ((position >= itemCount - 1 - getPreloadCount()) &&
                (itemCount < getItemLimit()) && hasMore() && mMoreEnabled) {
            // load more items
            loadMore();
        }
        if (view == null) {
            Log.e(TAG, "Found NULL view at " + position + "!");
            return new View(getContext());
        }
        return view;
    }


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
}
