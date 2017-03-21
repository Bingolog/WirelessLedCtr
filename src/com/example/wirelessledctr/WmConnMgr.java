package com.example.wirelessledctr;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class WmConnMgr {
	
	public static final int TYPE_MOBILE = ConnectivityManager.TYPE_MOBILE;
	public static final int TYPE_ETH = ConnectivityManager.TYPE_ETHERNET;
	public static final int TYPE_WIFI = ConnectivityManager.TYPE_WIFI;
	public static final int TYPE_MOBILE_HIPRI = ConnectivityManager.TYPE_MOBILE_HIPRI;
	
	

	public static NetworkInfo getActiveNetworkInfo(Context context)
	{
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

	
		NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
		return networkInfo;
	}
	public static boolean CheckNetworkConnect(Context context,int nettype)
    {
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		
		//NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
		
	//	if(networkInfo.getType() == nettype)
		
   //mobile 3G Data Network
    //ConnectivityManager.TYPE_MOBILE
		NetworkInfo networkInfo = conMan.getNetworkInfo(nettype);
		if(null == networkInfo)
			return false;
		
    State mobile = networkInfo.getState();
    //txt3G.setText(mobile.toString());
    if(mobile == null)
    {
    	return false;
    }
    if(mobile.equals(State.CONNECTED))
    {
    	return true;
    }
    else
    {
    	return false;
    }
    /*
     * 

   WmLog.LOGD("mobile:"+mobile.toString());
   //wifi
    State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
   // txtWifi.setText(wifi.toString());
    WmLog.LOGD("wifi:"+mobile.toString());
    
   //���3G�����wifi���綼δ���ӣ��Ҳ��Ǵ�����������״̬ �����Network Setting���� ���û�������������
   if(mobile==State.CONNECTED||mobile==State.CONNECTING)
   return;
   if(wifi==State.CONNECTED||wifi==State.CONNECTING)
   return;

*/
   // startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//���������������ý���
   //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //�����ֻ��е�wifi�������ý���

   }
}
