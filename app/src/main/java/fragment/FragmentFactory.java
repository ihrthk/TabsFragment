package fragment;

import android.app.Fragment;

import com.zhangls.tabsfragment.R;

/**
 * Created by admin on 13-11-23.
 */
public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index) {
        Fragment fragment = null;
        switch (index) {
            case R.id.rb_attention:
                fragment = new AttentionFragment();
                break;
            case R.id.rb_atme:
                fragment = new AtmeFragment();
                break;
            case R.id.rb_comment:
                fragment = new CommentFragment();
                break;
            case R.id.rb_mylist:
                fragment = new MyListFragment();
                break;
            case R.id.rb_global:
                fragment = new GlobalFragment();
                break;
        }
        return fragment;
    }
}
