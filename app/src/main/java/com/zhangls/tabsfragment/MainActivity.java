package com.zhangls.tabsfragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioGroup;

import fragment.FragmentFactory;

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
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = FragmentFactory.getInstanceByIndex(checkedId);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }
    //get local ip 需要网络权限
    //172.18.195.33
	public void scanNetworkInterface() {
		try {
			netinterface.clear();
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for(NetworkInterface netint : Collections.list(nets))
			{
				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)){
					StringTokenizer st = new StringTokenizer(inetAddress.getHostAddress(),".");
					if(st.countTokens() == 4) {
						if(!inetAddress.getHostAddress().equals("127.0.0.1")) {
					        netinterface.add(inetAddress);
						}
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] MACString2Bytes(String source) {
		byte[] result = new byte[6];
		StringTokenizer st = new StringTokenizer(source, ":");
		if(st.countTokens() == 6) {
			int index = 0;
			while(st.hasMoreTokens()) {
				result[index] = (byte)Integer.valueOf(st.nextToken(),16).intValue();
				index++;
			}
			return result;
		}
		return null;
	}
	public static String MACBytes2String(byte[] source) {
		String result="";
		if(source.length == 6) {
			int num = 0;
			num = 0x000000FF & source[0];
			if(num > 15) {
			    result = Integer.toHexString(num);
			} else {
				result = "0" + Integer.toHexString(num);
			}
			for(int i = 1 ; i < source.length ; i++) {
				num = 0x00000FF & (int)source[i];
				if(num > 15) {
				    result = result + ":" + Integer.toHexString(num);
				} else {
				    result = result + ":0" + Integer.toHexString(num);
				}
			}
			return result.toLowerCase();
		}
		return null;
	}
}
