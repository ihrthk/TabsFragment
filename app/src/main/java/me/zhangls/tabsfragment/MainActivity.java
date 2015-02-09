package me.zhangls.tabsfragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;

import me.zhangls.tabsfragment.fragment.FragmentFactory;

/**
 * 主界面
 */
public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity);

        fragmentManager = getFragmentManager();
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.setOnCheckedChangeListener(this);

        //default
        findViewById(R.id.rb_attention).performClick();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //实例化需要的fragment
        Fragment fragment = FragmentFactory.getInstanceByIndex(checkedId);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }
}
