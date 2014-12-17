package com.zhangls.tabsfragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;

/**
 * Created by BSDC-ZLS on 2014/12/17.
 */
public class AsyncImageLoader {


    static AsyncImageLoader asyncImageLoader;

    public void cancel(String iconKey) {

    }

    public void load(String iconKey) {

    }

    public static synchronized AsyncImageLoader getInstance(Activity activity) {
        if (asyncImageLoader == null) {
            asyncImageLoader = new AsyncImageLoader();
        }
        return asyncImageLoader;
    }

    public interface ResultListener {
        public boolean onLoadStart(String key);

        public void onLoadComplete(String key, Drawable drawable);
    }
}
