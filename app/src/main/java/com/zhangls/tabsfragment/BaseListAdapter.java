package com.zhangls.tabsfragment;

import android.app.Activity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListAdapter<Data> extends ImageListAdapter implements OnItemClickListener,
        OnItemLongClickListener {
    private static final String TAG = BaseListAdapter.class.getSimpleName();
    // ==========================================================================
    // Constants
    // ==========================================================================

    // ==========================================================================
    // Fields
    // ==========================================================================
    protected List<Data> mItems;

    private volatile boolean mHasMore;

    /**
     * 全部加载过的项数，包含未显示在列表里的重复项
     */
    private int mLoadedCount;

    private Activity mActivity;


    /**
     * loadmore 之前是否需要阻塞
     */
    private volatile boolean mBlockLoadMore = false;

    private Button mBtnRefresh;

    private RelativeLayout mSpinnerBg;

    // ==========================================================================
    // Constructors
    // ==========================================================================
    public BaseListAdapter(Activity activity, List<? extends Data> items, ListView listView,
                           boolean itemClickable) {
        super(activity);
        mActivity = activity;
        mItems = new ArrayList<Data>();
        if (null != items) {
            appendData(items);
            mLoadedCount = items.size();
            mHasMore = mLoadedCount >= getIncrement();
            setMoreEnabled(mHasMore);
        }
        if (null != listView) {
            setListView(listView);
            if (itemClickable) {
                listView.setOnItemClickListener(this);
                listView.setOnItemLongClickListener(this);
            }
        }
        mHasMore = true;

        // 初始化moreView，以解决请求数据线程比UI线程跑得更快时，刷新UI造成的mBtnRefresh等控件空指针异常
        getMoreView(0, null, null);
    }

    public BaseListAdapter(Activity activity, List<? extends Data> items, ListView listView) {
        this(activity, items, listView, true);
    }

    // ==========================================================================
    // Getters
    // ==========================================================================

    protected Activity getActivity() {
        return mActivity;
    }

    protected List<Data> getData() {
        return mItems;
    }


    // ==========================================================================
    // Setters
    // ==========================================================================
    protected void setHasMore(boolean hasMore) {
        mHasMore = hasMore;
    }

    // ==========================================================================
    // Methods
    // ==========================================================================

    /**
     * 阻塞更多项的加载
     */
    public void blockLoadMore() {
        mBlockLoadMore = true;
    }

    /**
     * 取消对更多项加载的阻塞
     */
    public void unblockLoadMore() {
        mBlockLoadMore = false;
    }


    private boolean addUniqueItem(Data item) {
        boolean duplicate = false;
        for (int i = 0; i < mItems.size(); i++) {
            if (isItemDuplicate(item, mItems.get(i))) {
                duplicate = true;
                break;
            }
        }
        if (!duplicate) {
            return mItems.add(item);
        } else {
            return false;
        }
    }


    protected int appendData(List<? extends Data> data) {
        if (null == data) {
            return 0;
        }
        int addedCount = 0;
        List<? extends Data> dataCopy = new ArrayList<Data>(data);
        for (Data item : dataCopy) {
            if (!filterItem(item) && addUniqueItem(item)) {
                addedCount++;
            }
        }
        return addedCount;
    }

    public final void setData(final List<? extends Data> data) {
        // 调用在UI线程
        setDataInner(data);
    }

    private void setDataInner(List<? extends Data> data) {
        handleSetDataInner(data);
        onSetData();
    }

    private void handleSetDataInner(List<? extends Data> data) {
        if (mItems != data) {
            mItems.clear();
            appendData(data);
        }
        handleLoadMore(data);
    }

    protected void handleLoadMore(List<? extends Data> data) {
        if (data != null) {
            mLoadedCount = data.size();
        } else {
            mLoadedCount = 0;
        }
        if (mLoadedCount < getIncrement()) {
            mHasMore = false;
            setMoreEnabled(false);
        } else {
            mHasMore = true;
            setMoreEnabled(true);
        }
    }

    protected void onSetData() {

    }

    @Override
    public Object getItem(int position) {
        if (position < 0 || position >= mItems.size()) {
            return null;
        }
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    protected int getItemViewTypeCount() {
        return 1;
    }

    @Override
    protected int getContentItemViewType(int position) {
        return VIEW_TYPE_ITEM;
    }

    @Override
    public boolean hasMore() {
        return mHasMore;
    }

    @Override
    protected View getItemView(int position, View convertView, ViewGroup parent) {
        final Object item = getItem(position);
        if (null == item) {
            return new View(mContext);
        }
        return super.getItemView(position, convertView, parent);
    }

    @Override
    protected int onLoadMore(int startPosition, int requestSize) throws Exception {
        Log.d(TAG, "Req " + mLoadedCount + " + " + requestSize);
        List<Data> moreItems = null;
        int responseSize = 0;
        int addedCount = 0;
        moreItems = getMoreData(getLoadedCount(), requestSize);
        responseSize = getResponseSize(moreItems);
        if (responseSize > 0) {
            mLoadedCount += responseSize;
            addedCount = appendData(moreItems);
        }
        Log.d(TAG, "Rsp " + responseSize + ", Cnt " + mItems.size());
        mHasMore = responseSize >= requestSize;

        return addedCount;
    }

    @Override
    protected void loadFailed(Exception result) {
        blockLoadMore();
        mHasMore = true;

        mBtnRefresh.setVisibility(View.VISIBLE);
        mSpinnerBg.setVisibility(View.INVISIBLE);

    }

    protected int getResponseSize(List<Data> moreItems) {
        return moreItems == null ? 0 : moreItems.size();
    }

    protected int getLoadedCount() {
        return mLoadedCount;
    }


    @Override
    public View getMoreView(int position, View convertView, ViewGroup parent) {
        View v = getActivity().inflate(R.layout.list_load_more);
        mBtnRefresh = (Button) v.findViewById(R.id.btn_refresh);
        mBtnRefresh.setBackgroundDrawable(mActivity.getThemeDrawable(R.drawable.btn_log));
        mBtnRefresh.setTextColor(mActivity.getThemeColor(R.color.btn_default));
        mBtnRefresh.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mActivity.getThemeDimensionPixel(R.dimen.list_item_op_text_size));
        LayoutParams btnLp = new LayoutParams(LayoutParams.FILL_PARENT,
                mActivity.getThemeDimensionPixel(R.dimen.list_item_op_height));
        mBtnRefresh.setLayoutParams(btnLp);
        mBtnRefresh.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRefresh();
            }

        });
        MarketImageView spinner = new MarketImageView(getActivity());
        spinner.setImageDrawable(getActivity().getThemeDrawable(R.drawable.splash_loading));
        mSpinnerBg = (RelativeLayout) v.findViewById(R.id.relative_spinner_bg);
        TextView textLoading = (TextView) v.findViewById(R.id.txt_loading);
        LayoutParams rp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        rp.addRule(RelativeLayout.CENTER_IN_PARENT);
        rp.addRule(RelativeLayout.RIGHT_OF, textLoading.getId());
        mSpinnerBg.addView(spinner, rp);
        return v;
    }

    /**
     * 判断是否是重复项的逻辑。需要时重写。
     */
    public boolean isItemDuplicate(Data item1, Data item2) {
        return false;
    }

    ;

    /**
     * 判断某个item是否需要被过滤掉。如果返回true，则该item不会被添加到列表数据源中。默认返回false。
     *
     * @param item
     * @return 需要被过滤返回true，否则返回false
     */
    protected boolean filterItem(Data item) {
        return false;
    }

    /**
     * 获取更多app item的逻辑
     *
     * @param startPosition
     * @param requestSize
     * @return Status code
     */
    protected abstract List<Data> getMoreData(int startPosition, int requestSize) throws Exception;


    protected void clickRefresh() {
        mBtnRefresh.setVisibility(View.INVISIBLE);
        mSpinnerBg.setVisibility(View.VISIBLE);
        unblockLoadMore();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Holder getImageItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }


    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

}
