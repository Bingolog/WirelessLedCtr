package com.example.wirelessledctr;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wm.hardwarelab.HardwareControler;
//import com.friendlyarm.AndroidSDK.HardwareControler;

public class WifilightingMainActivity extends Activity {

	Button mButton; 
	Button mButton2; 
	private ImageView ImageViewLed1;
	private ImageView ImageViewLed2;
	private ImageView ImageViewLed3;
	private ImageView ImageViewLed4;

	
	boolean start = true;
	private final static int port = 65525;
	SocketServerThread mSocketThread = null;
	
	String ctrl[] = {
			"Led1ONN",
			"Led2ONN",
			"Led3ONN",
			"Led4ONN",
			"Led1OFF",
			"Led2OFF",
			"Led3OFF",
			"Led4OFF",
	
	};
	
	ArrayList<LedSocketThread> list = new ArrayList<LedSocketThread>();
	
	protected void onCtrlLed(final ImageView view,final boolean on) {
		runOnUiThread(new Runnable() {
			public void run() {
				if(on)
				{
					view.setImageDrawable(getResources().getDrawable(R.drawable.led_on_new));
				}
				else
				{
					view.setImageDrawable(getResources().getDrawable(R.drawable.led_off_new));
				}
			}
		});
	}
	
	
	  public String get_string_ledStatus(int led,int on)
	  {
		  //mSocketThread.start();
		  String sled ="Led";
		  switch(led)
		  {
		  case 0:
			  sled+="1";
			  break;
		  case 1:
			  sled+="2";
			  break;
		  case 2:
			  sled+="3";
			  break;
		  case 3:
			  sled+="4";
			  break;
			  
			  default:
				  return "#########default##########";
		  }
		  
		  if(0 == on)
		  {
			  sled+="OFF";
		  }
		  else if(1 == on)
		  {
			  sled+="ONN";
		  }
		  
		  return sled;
	  }
	
	void notify_ledStauts_allclients(String staus)
	{
		int size = list.size();
		for(int i = 0;i < size;i++)
		{
			System.err.println("socket client thread:"+i);
			LedSocketThread thread = list.get(i);
			if(!thread.send_ledStatus(staus))
			{
				list.remove(i);				
				i--;
				size--;
				System.err.println("send_ledStatus ERR ########################### thread:"+i);
				System.err.println("send_ledStatus ERR111 ########################### thread:"+i);
				System.err.println("send_ledStatus ERR111222 ########################### thread:"+i);
			}
		}
	}
	
	synchronized void  setLedState( int ledID, int ledState , ImageView view)
	{
		 HardwareControler.setLedState(ledID, ledState);
		 onCtrlLed(view,true);
		 notify_ledStauts_allclients(get_string_ledStatus(ledID, ledState));
		 System.err.println("setLedState notify_ledStauts_allclients ");
	}
	
	synchronized void  setLedStateOff( int ledID, int ledState , ImageView view)
	{
		 HardwareControler.setLedState(ledID, ledState);
		 onCtrlLed(view,false);
		 notify_ledStauts_allclients(get_string_ledStatus(ledID, ledState));
		 System.err.println("setLedStateOff notify_ledStauts_allclients ");
	}
	
	class LedSocketThread extends Thread
	{
		Socket socket = null;
		byte[] byts = new byte[7];
		public LedSocketThread(Socket isocket)
		{
			socket = isocket;
		}
		
		public boolean send_ledStatus(String staus) {
			// TODO Auto-generated method stub
			if(socket == null)
			{
				System.err.println("socket == null");
				return false;
			}
			if(!socket.isClosed())
			{
				try {
					
					System.err.println("send_ledStatus write~~~~~~~~~~~~~~~~~:"+staus);
	
					socket.getOutputStream().write(staus.getBytes());
					socket.getOutputStream().flush();
					
					System.err.println("send_ledStatu~~~~~~~~~~~~~~~~~:"+staus);
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			else
			{
				System.err.println("socket isClosed");
				return false;
			}
			return true;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while(!isInterrupted()) {
				try {
					if(socket == null)
					{
						System.err.println("socket == null");
						break;
					}
					while(!socket.isClosed())
					{
						int len = socket.getInputStream().read(byts);
						if(len<0)
							break;
						String str= new String(byts,0,len);
	
						 System.out.println("~~~~~~~~~~~recv:"+str);
						 if(str.equals(ctrl[0]))
						 {
							 setLedState(0, 1,ImageViewLed1);
						 }
						 else if(str.equals(ctrl[1]))
						 {
							 setLedState(1, 1,ImageViewLed2);
						 }
						 else if(str.equals(ctrl[2]))
						 { 
							 setLedState(2, 1,ImageViewLed3);
						 }
						 else if(str.equals(ctrl[3]))
						 {
							 setLedState(3, 1,ImageViewLed4);
						 }
						 else if(str.equals(ctrl[4]))
						 {
							 setLedStateOff(0, 0,ImageViewLed1);
						 }
						 else if(str.equals(ctrl[5]))
						 {
							 setLedStateOff(1, 0,ImageViewLed2);
						 }
						 else if(str.equals(ctrl[6]))
						 {
							 setLedStateOff(2, 0,ImageViewLed3);
						 }
						 else if(str.equals(ctrl[7]))
						 {
							 setLedStateOff(3, 0,ImageViewLed4);
						 }	 
					}
			
					System.err.println("client close sokcet################################");
					break;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					break;
				}
			}
			
			synchronized (list) {
				int size = list.size();
				for(int i = 0;i<size;i++)
				{
					if(list.get(i).equals(this))
					{
						list.remove(i);
						System.err.println("list.remove thead ################################");
						i--;
						size--;
					}
				}
				
			}
			
			
		}
		
	}
	
	
	class SocketServerThread extends Thread {
		ServerSocket serverSocket = null;
		byte[] byts = new byte[1024];
		//Socket	 socket = null;
		public void closeServer()
    	{
    		start = false;
    		if(null != serverSocket)
    		{
				try {
//					if(socket!= null)
//					{
//						socket.close();
//					}
					
					serverSocket.close();
					serverSocket = null;
					list.clear();
					System.out.println("wifi closeServer serverSocket=null");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
		
		
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(!isInterrupted()) {
				try {
					if(serverSocket == null)
					{
						break;
					}
					
					Socket socket = serverSocket.accept();
					if(socket != null)
					{
						LedSocketThread th = new LedSocketThread(socket);
						System.out.println("accept a client:"+socket.getRemoteSocketAddress());
						list.add(th);
						th.start();
						
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	void toastInfo(String msg)
	{
		WmLog.show_prompt_msg(this, msg);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_ligthing_led_main);
		ImageViewLed1 = (ImageView)this.findViewById(R.id.imageView1);
		ImageViewLed2 = (ImageView)this.findViewById(R.id.imageView2);
		ImageViewLed3 = (ImageView)this.findViewById(R.id.imageView3);
		ImageViewLed4 = (ImageView)this.findViewById(R.id.imageView4);
		
		mButton = (Button) findViewById(R.id.button1);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(checkWifi())
				{
					if(mSocketThread==null)
					{
						mSocketThread = new SocketServerThread();
						mSocketThread.start();		
						toastInfo("服务器启动！！！");
					}
					else
					{
						toastInfo("服务器已启动！！！");
					}
					
					
				}
			}
		});
		
		mButton2 = (Button) findViewById(R.id.button2);
		
		mButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(checkWifi())
				{
					//wifiServer.getInstance().stopServer();
					if(mSocketThread!=null)
					{
						mSocketThread.closeServer();
						mSocketThread.interrupt();	
						mSocketThread = null;
						toastInfo("服务器关闭！！！");
					}

				}
			}
		});
	}
	
	
	boolean checkWifi()
	{
		TextView v = (TextView) this.findViewById(R.id.textWifiIP);
		if(!WmConnMgr.CheckNetworkConnect(this, WmConnMgr.TYPE_WIFI))
		{
			WmLog.show_prompt_msg(this, "WIFI未开启!!!");
			v.setText("");
			return false;
		}
		
		
		WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMan.getConnectionInfo();
		String mac = info.getMacAddress();// 获得本机的MAC地址
		String ssid = info.getSSID();// 获得本机所链接的WIFI名称

		int ipAddress = info.getIpAddress();
		String ipString = "";// 本机在WIFI状态下路由分配给的IP地址

		// 获得IP地址的方法一：
		if (ipAddress != 0) {
		       ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." 
				+ (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
		}
		
		
		v.setText(ipString);
		
		return true;
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		checkWifi();
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mSocketThread!=null)
		{
			mSocketThread.closeServer();
			mSocketThread.interrupt();
			mSocketThread = null;
		}
		
		HardwareControler.setLedState(0, 0);
		HardwareControler.setLedState(1, 0);
		HardwareControler.setLedState(2, 0);
		HardwareControler.setLedState(3, 0);
	}
	
	




}
