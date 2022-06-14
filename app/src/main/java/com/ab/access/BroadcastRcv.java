package com.ab.access;

import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

import android.provider.Settings;

import android.widget.Toast;


public class BroadcastRcv extends BroadcastReceiver {


    @Override

    public void onReceive(Context context, Intent intent) {

		Intent i = new Intent("a");
		i.putExtra("type","eee");
		
		
		
		if(intent.getAction()=="power"){
			
		if (FloatService.status=="on") {

            Toast.makeText(context, "Stopped", Toast.LENGTH_SHORT).show();
		FloatService.mWindowManager.removeView(FloatService.mFloatingView);
			FloatService.status="off";
        } else {

            Toast.makeText(context, "Started", Toast.LENGTH_SHORT).show();
			//FloatService.mWindowManager.addView(FloatService.mFloatingView, FloatService.params);
			context.sendBroadcast(i);
			
        }
		}
		if(intent.getAction()=="upLayout"){
		
			//FloatService.floatText.setText(intent.getStringExtra("txt"));
			
			Intent upl = new Intent("updateFLayout");
			upl.putExtra("txt",intent.getStringExtra("txt"));
			
			context.sendBroadcast(upl);
			
			
			
			}
    }


    private static boolean isAirplaneModeOn(Context context) {

        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;

    }
}
