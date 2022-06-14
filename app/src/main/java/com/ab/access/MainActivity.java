package com.ab.access;

import android.app.*;
import android.content.*;
import android.net.*;
import android.os.*;
import android.provider.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;

public class MainActivity extends Activity 
{
	Button btn;
	TextView txv;
	Handler hd;
	Runnable r;
	static Activity ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		ctx=this;
		txv=findViewById(R.id.txt);
		btn=findViewById(R.id.btn);
		hd=new Handler();
		
		r = new Runnable() {
			@Override 
			public void run() {
				try {
					txv.setText(Math.random()+"");
					
					//this function can change value of mInterval.
				} finally {
					// 100% guarantee that this always happens, even if
					// your update method throws an exception
					hd.postDelayed(r, 50);
				}
			}
		};
		//hd.post(r);
		btn.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					// TODO: Implement this method
					if(!isMyServiceRunning(FloatService.class)){
						startService(new Intent(MainActivity.this, FloatService.class));
					}else{
						stopService(new Intent(MainActivity.this, FloatService.class));
					}
					txv.setText(Math.random()+"");
					
				}
				
			
		});
		if(!checkAccessibilityPermission()){

            Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();

        }
		
		MyService ms = MyService.getInstance();
		if(ms!=null){
			
			//ms.doGesture(0,300);
			
		}else{
			Toast.makeText(this,"no Srv",Toast.LENGTH_SHORT).show();
		}
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //startService(new Intent(MainActivity.this, FloatService.class));
            
        } else if (Settings.canDrawOverlays(this)) {
            //startService(new Intent(MainActivity.this, FloatService.class));
            
        } else {
            askPermission();
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
        }
    

    }
	
	private void askPermission() {
        Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        startActivity(myIntent);
    
	}

    // method to check is the user has permitted the accessibility permission

    // if not then prompt user to the system's Settings activity

    public boolean checkAccessibilityPermission () {

        int accessEnabled = 0;

        try {

            accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);

        } catch (Settings.SettingNotFoundException e) {

            e.printStackTrace();

        }

        if (accessEnabled == 0) {

            // if not construct intent to request permission

            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // request permission via start activity for result

            startActivity(intent);

            return false;

        } else {

            return true;
		
		}
	}
	
	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
