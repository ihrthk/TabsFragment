package com.zhangls.tabsfragment;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public abstract class IconItemHolder<Data> extends BaseHolder<Data> implements ImageItem, ImageLoader {
    // ==========================================================================
    // Constants
    // ==========================================================================
    // /**
    // * 控制栏收缩和展开的比例
    // */
    public static final float SCALE = 1.87f;

    // ==========================================================================
    // Fields
    // ==========================================================================
    private RelativeLayout mRootView;

    private RelativeLayout mGrpBody;

    private RelativeLayout mGrpTop;

    private View mDivider;

    private FrameLayout mGrpOptionMenuContainer;

    // private ListPopupMenu mPopupMenu;

    private View mGrpContent;

    private View mGrpOp;

    private IconView mImgIcon;

    private ImageView mDecorIcon;

    private boolean mOptionMenuShown = false;
    private boolean mOptionMenuEnabled = true;

    private OnExpandMenuStateListener mExpandMenuListener = null;

    protected AsyncImageLoader mLoader;

    private boolean mIconLoaded;

    // private Drawable mIconDrawable;

    private Drawable mDecorDrawable;

    private Object mIconKey;

    private Object mDecorKey;

    private ImageListAdapter mAdapter;

    private boolean mBlockLayout = false;

    private boolean mLoadFromMem;

    // private boolean mDispatchEvent = false;
    private ImageView mArrow;

    private Object mFadeIconKey;

    private RelativeLayout mContainer;

    private View mRankView;

    private boolean mAlwaysShowIcon = false;
    
    // ==========================================================================
    // Constructors
    // ==========================================================================
    public IconItemHolder(MarketBaseActivity activity, ImageListAdapter adapter, Data data) {
        super(activity, data);
        mAdapter = adapter;
        mLoader = AsyncImageLoader.getInstance(activity);
        initView();
    }

    // ==========================================================================
    // Getters
    // ==========================================================================
    public RelativeLayout getContainer() {
        return mContainer;
    }

    public RelativeLayout getBodyView() {
        return mGrpBody;
    }

    public void setBodyBackgroundDrawable(Drawable d) {
        mGrpBody.setBackgroundDrawable(d);
    }

    public View getIconLayout() {
        return mImgIcon;
    }

    public View getOptionMenu() {
        return mGrpOptionMenuContainer;
    }

    // public ListPopupMenu getPopupMenu() {
    // return mPopupMenu;
    // }

    public View getContentView() {
        return mGrpContent;
    }

    protected View getOperationView() {
        return mGrpOp;
    }

    protected RelativeLayout getTopView() {
        return mGrpTop;
    }

    protected int getGrpBodyPaddingLeft() {
        return getActivity().getThemeDimensionPixel(R.dimen.list_item_inner_margin);
    }

    protected int getOperationViewPaddingRight() {
        return getActivity().getThemeDimensionPixel(R.dimen.list_right_padding);
    }

    protected int getOperationViewPaddingTop() {
        return 0;
    }

    protected int getOperationViewPaddingLeft() {
        return 0;
    }

    // ==========================================================================
    // Setters
    // ==========================================================================
    public void setOnExpandMenuStateListener(OnExpandMenuStateListener listener) {
        mExpandMenuListener = listener;
    }

    public void setIcon(Drawable drawable, boolean fadeIn) {
        if (null != mImgIcon) {
            if (fadeIn) {
                fadeIn = mFadeIconKey != getIconKey();
                if (fadeIn) {
                    mFadeIconKey = getIconKey();
                } else {
                    LogUtils.i("ignore repeat fadein. Key:" + getIconKey());
                    // FIXME make sure "return" is ok
                    return;
                }
            }
            mImgIcon.setForegroundDrawable(drawable, fadeIn);
        }
    }

    public void setDecorImage(Drawable drawable) {
        // if (null != mImgIcon) {
        // mImgIcon.setDecorDrawable(drawable);
        // }
        if (mDecorIcon != null) {
            mDecorIcon.setBackgroundDrawable(drawable);
        }
    }

    public void setIconDecorImage(Drawable drawable) {
        if (mImgIcon != null) {
            mImgIcon.setDecorDrawable(drawable);
        }
    }

    public void setImageListAdapter(ImageListAdapter adapter) {
        mAdapter = adapter;
    }

    public void setAlwaysShowIcon(boolean show) {
        mAlwaysShowIcon = show;
    }
    
    // ==========================================================================
    // Methods
    // ==========================================================================
    private void initView() {
        RelativeLayout.LayoutParams params;
        // 静态layout部分
        mRootView = new RelativeLayout(getActivity()) {
            // 初始加载时，不拦截requestLayout
            private boolean mLayouted = false;

            @Override
            public void requestLayout() {
                if (!mLayouted || !mBlockLayout) {
                    super.requestLayout();
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                mLayouted = true;
                super.onLayout(changed, l, t, r, b);
            }

        };
        mContainer = new RelativeLayout(getActivity());
        mContainer.setId(R.id.grp_container_top);

        // Icon前面的排序图标或文字
        RelativeLayout rankViewLay = new RelativeLayout(getActivity());
        mRankView = createRankView();
        if (null != mRankView) {
            params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, getItemHeight());
            mRootView.addView(mContainer, params);

            rankViewLay.setId(R.id.list_rank_icon);
            params = new RelativeLayout.LayoutParams(getActivity().dip2px(28), LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            rankViewLay.addView(mRankView, params);

            params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            mContainer.addView(rankViewLay, params);
            mContainer.setBackgroundDrawable(GraphicUtils.getRepeatDrawble(getActivity().getThemeDrawable(
                    R.drawable.bg_list_item)));
        } else {
            mRootView.addView(mContainer);
        }
        mGrpBody = (RelativeLayout) inflate(R.layout.icon_item);
        if (null == mRankView) {
            mGrpBody.setBackgroundDrawable(GraphicUtils.getRepeatDrawble(getActivity().getThemeDrawable(
                    R.drawable.bg_list_item)));
        }
        mGrpBody.setId(R.id.grp_body);
        int height = getItemHeight();
        params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, height);
        if (null == mRankView) {
            // params.leftMargin = getActivity().getThemeDimensionPixel(R.dimen.list_item_inner_margin);
        } else {
            params.addRule(RelativeLayout.RIGHT_OF, rankViewLay.getId());
        }
        // mGrpBody.setPadding(getGrpBodyPaddingLeft(), 0, 0, 0);
        // params.addRule(RelativeLayout.CENTER_VERTICAL);
        mContainer.addView(mGrpBody, params);

        mGrpTop = (RelativeLayout) mGrpBody.findViewById(R.id.grp_top);
        int paddingRight = getBodyPaddingRight();
        mGrpTop.setPadding(0, 0, paddingRight, 0);
        // 左侧图标区域
        // 图标
        mImgIcon = new IconView(getThemeContext()) {
            // 初始加载时，不拦截requestLayout
            private boolean mLayouted = false;

            @Override
            public void requestLayout() {
                if (!mLayouted) {
                    super.requestLayout();
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                mLayouted = true;
                super.onLayout(changed, l, t, r, b);
            }
        };
        mImgIcon.setId(R.id.list_icon);
        mImgIcon.setDefaultResource(R.drawable.ic_app_default);
        int side = getIconSide();
        params = new RelativeLayout.LayoutParams(side, side);
        // params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.topMargin = getIconTopMargin();
        if (null == mRankView) {
            params.leftMargin = getIconLeftMargin();
        }
        params.rightMargin = getIconRightMargin();
        mGrpTop.addView(mImgIcon, params);

        mDecorIcon = new ImageView(getActivity()) {
            // 初始加载时，不拦截requestLayout
            private boolean mLayouted = false;

            @Override
            public void requestLayout() {
                if (!mLayouted) {
                    super.requestLayout();
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                mLayouted = true;
                super.onLayout(changed, l, t, r, b);
            }
        };
        int w = getDecorIconWidth();
        int h = getDecorIconHeight();
        params = new RelativeLayout.LayoutParams(w, h);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        // params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.leftMargin = getDecorIconMarginLeft();
        params.topMargin = 0;
        mGrpBody.addView(mDecorIcon, params);

        // 子类动态生成的layout部分
        LayoutParams associatedParams = null;
        // 右侧操作区域
        mGrpOp = createOperationView();
        if (null != mGrpOp) {
            mGrpOp.setId(R.id.grp_op);
            params = new RelativeLayout.LayoutParams(getOperatorWidth(), getOperatorHeight());
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mGrpOp.setPadding(getOperationViewPaddingLeft(), getOperationViewPaddingTop(), getActivity().dip2px(5), 0);
            mContainer.addView(mGrpOp, params);
        }
        // 分割线
        if (null != mGrpOp) {
            mDivider = createOperatorDivider();
            if (mDivider != null) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(2, LayoutParams.FILL_PARENT);
                lp.addRule(RelativeLayout.LEFT_OF, mGrpOp.getId());
                lp.topMargin = getActivity().getThemeDimensionPixel(R.dimen.list_divider_margin_top);
                mDivider.setId(R.id.grp_divider);
                mContainer.addView(mDivider, lp);
            }
        }
        associatedParams = null;

        // // 底部弹出菜单
        View mGrpExpandOpMenu = createExpandOpMenu();
        if (null != mGrpExpandOpMenu) {
            mArrow = new ImageView(getActivity());
            mArrow.setImageResource(R.drawable.arrow_up);
            mArrow.setId(R.id.grp_arrow);
            params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.rightMargin = getActivity().dip2px(2);
            if (!showDownArrow()) {
                mArrow.setVisibility(View.GONE);
            }
            mGrpTop.addView(mArrow, params);

            mGrpOptionMenuContainer = new FrameLayout(getThemeContext());
            params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            if (null != mContainer) {
                params.addRule(RelativeLayout.BELOW, mContainer.getId());
            }
            // 弹出菜单默认处于收起状态
            mGrpOptionMenuContainer.setVisibility(View.GONE);
            mRootView.addView(mGrpOptionMenuContainer, params);

            associatedParams = mGrpExpandOpMenu.getLayoutParams();
            FrameLayout.LayoutParams fparams;
            if (null != associatedParams) {
                fparams = new FrameLayout.LayoutParams(associatedParams.width, associatedParams.height);
            } else {
                fparams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            }
            fparams.topMargin = getActivity().dip2px(5);
            fparams.bottomMargin = getActivity().dip2px(5);
            mGrpOptionMenuContainer.addView(mGrpExpandOpMenu, fparams);

        }

        // 中部信息显示区域
        mGrpContent = createContentView();
        if (null != mGrpContent) {
            mGrpContent.setId(R.id.grp_content);
            params = setupContentLayoutParams(associatedParams, params);
            mGrpTop.addView(mGrpContent, params);
        }

    }

    public View createRankView() {
        return null;
    }

    protected View createOperatorDivider() {
        View divider = new View(getActivity());
        divider.setFocusable(false);
        divider.setBackgroundResource(R.drawable.list_operator_divider);
        return divider;
    }

    protected RelativeLayout.LayoutParams setupOperationLayoutParams(LayoutParams associatedParams,
            RelativeLayout.LayoutParams params) {

        if (null != mGrpOp) {
            associatedParams = mGrpOp.getLayoutParams();
            if (null != associatedParams) {
                params = new RelativeLayout.LayoutParams(associatedParams.width, associatedParams.height);
            } else {
                params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);

        }
        return params;
    }

    protected RelativeLayout.LayoutParams setupContentLayoutParams(LayoutParams associatedParams,
            RelativeLayout.LayoutParams params) {

        associatedParams = mGrpContent.getLayoutParams();
        if (null != associatedParams) {
            params = new RelativeLayout.LayoutParams(associatedParams.width, associatedParams.height);
        } else {
            params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        }
        if (null != mImgIcon) {
            params.addRule(RelativeLayout.RIGHT_OF, mImgIcon.getId());
        }
        if (null != mArrow) {
            params.addRule(RelativeLayout.LEFT_OF, mArrow.getId());
        } else if (null != mDivider) {
            params.addRule(RelativeLayout.LEFT_OF, mDivider.getId());
        } else if (null != mGrpOp) {
            params.addRule(RelativeLayout.LEFT_OF, mGrpOp.getId());
        }
        // params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.leftMargin = getThemeResources().getDimensionPixelSize(R.dimen.list_item_inner_margin);
        params.rightMargin = params.leftMargin;

        return params;
    }

    public void blockLayoutRequest() {
        mBlockLayout = true;
    }

    public void unblockLayoutRequest() {
        mBlockLayout = false;
    }

    public void setOperationEnabled(boolean enabled) {
        View opView = getOperationView();
        if (null != opView) {
            opView.setEnabled(enabled);
        }
    }

    public void setOptionMenuBackgroundColor(int color) {
        if (null != mGrpOptionMenuContainer) {
            blockLayoutRequest();
            mGrpOptionMenuContainer.setBackgroundColor(color);
            unblockLayoutRequest();
        }
    }

    public void setOptionMenuBackgroundDrawable(Drawable drawable) {
        if (null != mGrpOptionMenuContainer) {
            blockLayoutRequest();
            mGrpOptionMenuContainer.setBackgroundDrawable(drawable);
            unblockLayoutRequest();
        }
    }

    public void setOptionMenuEnabled(boolean enabled) {
        this.mOptionMenuEnabled = enabled;
    }

    public void triggerOptionMenu(boolean playAnim) {
        if (mOptionMenuShown) {
            hideOptionMenu(playAnim);
        } else {
            showOptionMenu(playAnim);
        }
    }

    public void showOptionMenu(final boolean playAnim) {
        if (!mOptionMenuEnabled) {
            return;
        }
        if (mGrpOptionMenuContainer != null) {
            mGrpOptionMenuContainer.setVisibility(View.VISIBLE);
        }
        if (mArrow != null) {
            mArrow.setImageResource(R.drawable.arrow_down);
        }
        if (mExpandMenuListener != null) {
            mExpandMenuListener.onShow(this);
        }
        mOptionMenuShown = true;

    }


    public void hideOptionMenu(boolean playAnim) {
        // if (!mOptionMenuShown)
        // return;
        mOptionMenuShown = false;
        if (mGrpOptionMenuContainer != null) {
            mGrpOptionMenuContainer.setVisibility(View.GONE);
        }
        if (mArrow != null) {
            mArrow.setImageResource(R.drawable.arrow_up);
        }
        if (mExpandMenuListener != null) {
            mExpandMenuListener.onHide(this);
        }
    }

    public void setOptionItemVisibility(int option, boolean visible) {
        blockLayoutRequest();
        unblockLayoutRequest();
    }

    public void setOptionItemEnabled(int option, boolean enabled) {

    }

    public void autoScrollList(final Object showOptionMenuAt) {
        final AbsListView absLst = mAdapter.getListView();
        // if(Build.VERSION.SDK_INT < 8){
        View v = getRootView();
        if (absLst instanceof SectionListView) {
            measureView(getOptionMenu());
            SectionListView lv = (SectionListView) absLst;
            int listViewH = lv.getHeight() - lv.getBottomOverlayHeight();
            int itemH = (int) (v.getHeight() + getOptionMenu().getMeasuredHeight());
            int position = getPosition();
            if (v.getTop() + itemH > listViewH) {
                lv.setSelectionFromTop(position + lv.getHeaderViewsCount(), listViewH - itemH);
            } else {

            }
        }

    }

    public void measureView(View child) {
        LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public View getRootView() {
        return mRootView;
    }

    @Override
    public void loadImages() {
        // 取消该item之前的任务
        mLoader.cancel(mIconKey, this);
        // TODO Need cancel?
        mLoader.cancel(mDecorKey, this);
        setIcon(null, false);
        setDecorImage(null);

        mIconLoaded = false;
        mDecorDrawable = null;

        mLoadFromMem = false;
        mIconKey = getIconKey();
        mLoader.load(mIconKey, this);
        mDecorKey = getDecorKey();
        mLoader.load(mDecorKey, this);
    }

    @Override
    public void cancelLoadImages() {
        mIconLoaded = false;
        // mIconDrawable = null;
        mDecorDrawable = null;
        mLoader.cancel(mIconKey, this);
        // TODO Need cancel?
        mLoader.cancel(mDecorKey, this);
    }

    @Override
    public Drawable getCachedImage(Object key) {
        if (!shouldDisplayImages()) {
            return null;
        }
        Drawable d = ImageMemCache.getIcon(key);
        if (d != null && !mLoadFromMem) {
            mLoadFromMem = true;
        }
        return d;
    }

    @Override
    public void onLoadComplete(Object key, Drawable d) {
        if (key.equals(getIconKey())) {
            ImageMemCache.putIcon(key, d);
            ImageMemCache.keepIcon(d);
            // 显示策略1：图标角标必须同时显示
            // if (null == mDecorDrawable) {
            // // 只有图标加载完成时，先把图标缓存起来
            // mIconDrawable = d;
            // } else {
            // // 图标和角标都加载完成时，才把两张图片设置到各自的view上，使其同步显示
            // setIcon(d);
            // setDecorImage(mDecorDrawable);
            // mIconDrawable = null;
            // mDecorDrawable = null;
            // }

            // 显示策略2：图标可先显示
            if (mLoadFromMem) {
                setIcon(d, false);
                mLoadFromMem = false;
            } else {
                setIcon(d, true);
            }
            mIconLoaded = true;
            // 如果角标在之前已经加载完成，将其一并显示到界面上
            if (null != mDecorDrawable) {
                setDecorImage(mDecorDrawable);
                mDecorDrawable = null;
            }
        } else if (key.equals(getDecorKey())) {
            ImageMemCache.putIcon(key, d);
            ImageMemCache.keepIcon(d);
            // 显示策略1：图标角标必须同时显示
            // if (null == mIconDrawable) {
            // // 只有角标加载完成时，先把角标缓存起来
            // mDecorDrawable = d;
            // } else {
            // // 等图标加载完成时，才把两张图片设置到各自的view上，使其同步显示
            // setIcon(mIconDrawable);
            // setDecorImage(d);
            // mIconDrawable = null;
            // mDecorDrawable = null;
            // }

            // 显示策略2：图标可先显示
            if (!mIconLoaded) {
                // 只有角标加载完成时，先把角标缓存起来
                mDecorDrawable = d;
            } else {
                // 图标已经加载完毕并显示在界面上，将角标也显示到界面上即可
                setDecorImage(d);
            }
        }
    }

    @Override
    public boolean onLoadStart(Object key) {
        if (key.equals(getIconKey())) {
            // setIcon(null);
            return shouldLoadImages();
        } else if (key.equals(getDecorKey())) {
            return shouldLoadImages();
            // setDecorImage(null);
        } else {
            return false;
        }
    }

    public boolean shouldDisplayImages() {
        if (mAlwaysShowIcon) {
            return true;
        }
        return SettingsManager.getInstance(getActivity()).isShowIcon();
    }

    public boolean shouldLoadImages() {
        return shouldRefreshImages();
    }

    public boolean shouldRefreshImages() {
        if (null != mAdapter) {
            return shouldDisplayImages() && mAdapter.shouldRefreshImage();
        } else {
            return shouldDisplayImages();
        }
    }

    public int getIconLeftMargin() {
        return getActivity().getDimensionPixel(R.dimen.list_icon_padding_left);
    }

    public int getIconRightMargin() {
        return getActivity().getDimensionPixel(R.dimen.list_icon_padding_right);
    }

    public int getIconTopMargin() {
        return getActivity().getDimensionPixel(R.dimen.list_icon_padding_top);
    }

    public int getIconSide() {
        return getActivity().getThemeDimensionPixel(R.dimen.list_icon_side);
    }

    public int getItemHeight() {
        return getActivity().getThemeDimensionPixel(R.dimen.list_item_height);
    }

    public int getDecorIconHeight() {
        return getActivity().getThemeDimensionPixel(R.dimen.list_decor_icon_height);
    }

    public int getDecorIconWidth() {
        return getActivity().getThemeDimensionPixel(R.dimen.list_decor_icon_width);
    }

    public int getBodyPaddingRight() {
        return getActivity().getThemeDimensionPixel(R.dimen.list_item_body_padding_right);
    }

    public int getOperatorWidth() {
        return LayoutParams.WRAP_CONTENT;
    }

    public int getOperatorHeight() {
        return LayoutParams.FILL_PARENT;
    }
    
    public boolean showDownArrow() {
        return true;
    }

    public abstract View createContentView();

    public abstract View createOperationView();

    public abstract View createExpandOpMenu();

    // public ListPopupMenu createPopupMenu() {
    // return null;
    // }

    public abstract Object getIconKey();

    public abstract Object getDecorKey();

    public int getDecorIconMarginLeft() {
        return 0;
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================
    public static interface OnExpandMenuStateListener {

        public abstract void onShow(IconItemHolder<?> holder);

        public abstract void onHide(IconItemHolder<?> holder);

    }

}
