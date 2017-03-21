package com.wm.hardwarelab;
import android.util.Log;

public class HardwareControler
{
	
	/* WM_audioswitch */
	static public native int setGPIOState( int gpioState);  
	
	/* WM_powerswitch */
	static public native int setPowerSwitchGPIOState( int gpioState);  
	
	/* WM_LEDS */
	static public native int setLedState( int ledID, int ledState );
	
    static {
        try {
        	System.loadLibrary("WMhardware");
        } catch (UnsatisfiedLinkError e) {
            Log.d("HardwareControler", "WMgpio library not found!");
        }
    }
}