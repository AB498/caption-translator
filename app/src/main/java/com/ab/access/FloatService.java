package com.ab.access;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.ViewGroup.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class FloatService extends Service
{
	static String status = "";
	static String lines = "";
	static String responseBody="";
	static boolean translating=false;
	static int count=0;
	
	int width,height;
	
	static WindowManager.LayoutParams params;
    static WindowManager mWindowManager;
    static View mFloatingView;
    private View collapsedView;
    private View expandedView;
	BroadcastReceiver yourReceiver;
	MyReceiver mReceiver;
	MyService ms;
	Runnable r;
	Handler handler;
	boolean isTextShowing=true;
	static TextView floatText;
	BroadcastReceiver broadcastReceiver;
	DisplayMetrics displayMetrics;
    
	public FloatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
		
		
		displayMetrics = new DisplayMetrics();
		MainActivity.ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		height = displayMetrics.heightPixels;
		width = displayMetrics.widthPixels;
		
		handler=new Handler();
		ms = MyService.getInstance();
		//ms.doGesture(0,0);
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				
				if (intent.getAction().equals("a")) {
					Toast.makeText(context, "Started!", Toast.LENGTH_SHORT).show();
					int type = intent.getIntExtra("type", 0);
					
					mWindowManager.addView(mFloatingView,params);
					status="on";
					setListeners();
					
				}
				if (intent.getAction().equals("updateFLayout")) {
					Toast.makeText(context, "up layout", Toast.LENGTH_SHORT).show();
					String txt = intent.getStringExtra("txt");
					
				}
				
			}
		};
		registerReceiver(broadcastReceiver, new IntentFilter("a"));
		
		
		
		IntentFilter theFilter = new IntentFilter();
        theFilter.addAction("power");
        
		
		BroadcastRcv brdRcv = new BroadcastRcv();
		//mReceiver = new MyReceiver();
		this.registerReceiver(brdRcv, theFilter);
    

		Intent powerIntent = new Intent(this, BroadcastRcv.class);
		powerIntent.setAction("power");
		PendingIntent powerPendingIntent =
			PendingIntent.getBroadcast(this, 0, powerIntent, 0);
		
	
	Notification.Builder notificationBuilder = new Notification.Builder(this);
	
		Notification notification = notificationBuilder.setAutoCancel(true)
			.setVibrate((new long[]{0, 1000, 500, 2000}))
			.setStyle((new Notification.InboxStyle() ) )
			.setWhen(System.currentTimeMillis())
			.setSmallIcon(R.drawable.ic)
			.setContentTitle("Unread message")
			.setContentText("You have an unread message")
			.addAction(R.drawable.ic, "BUTTON", powerPendingIntent)
			.build();        
		
		startForeground(100, notification);

        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floatxml, null);
		floatText=mFloatingView.findViewById(R.id.floatTxt);
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
        }else{
            params = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
        }
		params.x=0;
		params.y=0;
	
		
        //getting windows services and adding the floating view to it
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
		on();
		}
	
	public void setListeners(){
		

        mFloatingView.findViewById(R.id.ll).setOnTouchListener(new View.OnTouchListener() {
				private int initialX;
				private int initialY;
				private float initialTouchX;
				private float initialTouchY;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							initialX = params.x;
							initialY = params.y;
							initialTouchX = event.getRawX();
							initialTouchY = event.getRawY();
							
							MainActivity.ctx.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
							height = displayMetrics.heightPixels;
							width = displayMetrics.widthPixels;
							
							return true;

						case MotionEvent.ACTION_UP:
							
							//Toast.makeText(getApplicationContext(),initialX+" "+params.x,Toast.LENGTH_SHORT).show();

							if(initialX==params.x){
								   
								   //setClipboard(getApplicationContext(),MyService.lastLog);
								   if(!isTextShowing){
									   floatText.setVisibility((ViewGroup.VISIBLE));
									   mFloatingView.findViewById(R.id.trg).setBackgroundColor(Color.BLUE);
									   mFloatingView.setAlpha(0.9f);
									   //floatText.setText(MyService.lastLog);
									   isTextShowing=true;
									   }else{
										   mFloatingView.setAlpha(0.5f);
										   mFloatingView.findViewById(R.id.trg).setBackgroundColor(Color.GREEN);
										   //floatText.setText("");
										   floatText.setVisibility((ViewGroup.GONE));
										   isTextShowing=false;
									   }
							   }
							return true;

						case MotionEvent.ACTION_MOVE:
							//this code is helping the widget to move around the screen with fingers
							//Toast.makeText(getApplicationContext(),width/2+" "+params.x,Toast.LENGTH_SHORT).show();
							
							params.x = initialX + (int) (event.getRawX() - initialTouchX);
							params.y = initialY + (int) (event.getRawY() - initialTouchY);
							if(params.x+params.width/2>width/2)params.x=width/2;
							if(params.x-params.width/2<-width/2)params.x=-width/2;
							if(params.y+params.height/2>height/2)params.y=height/2;
							if(params.y-params.height/2<-height/2)params.y=-height/2;
											
							mWindowManager.updateViewLayout(mFloatingView, params);
							return true;
					}
					return false;
				}
			});
		
		
	}
    @Override
    public void onDestroy() {
        super.onDestroy();
		unregisterReceiver(broadcastReceiver);
        off();
		stopSelf();
    }
	
	
	public void off(){
		
		if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
		status="off";
		
	}
	public void on(){
		
		//if (mFloatingView == null) 
		mWindowManager.addView(mFloatingView,params);
		status="on";
		setListeners();
		isTextShowing=false;
		
	}
	
	public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // do something
			Toast.makeText(context,"ee",Toast.LENGTH_SHORT).show();
        }

        // constructor
        public MyReceiver(){

        }
    }
	
	private void setClipboard(Context context, String text) {
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText(text);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
			clipboard.setPrimaryClip(clip);
		}
	}
	
	
	
	
	public static String get(String urls, String urlParameters) {
		
		translating=true;
		MyService.lastReq=MyService.lastLog;
		try
		{
			//InputStream response = new URL(urls).openStream();
			
			URL url = new URL(urls);
			URLConnection con = url.openConnection();
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			InputStream in = con.getInputStream();
			
			try (Scanner scanner = new Scanner(in)) {
				responseBody = scanner.useDelimiter("\\A").next();
				
				MainActivity.ctx.runOnUiThread(new Runnable() {

						public void run() {
							FloatService.count++;
							floatText.setText(FloatService.count+": "+MyService.lastLog+"\n"+responseBody.toString());
						}
					});
					in.close();
				
					translating=false;
					
				return responseBody;
			}
			
		}
		catch (final Exception e)
		{
//			
//			final class IJavascriptHandler {
//				IJavascriptHandler() {
//				}
//
//				// This annotation is required in Jelly Bean and later:
//				@JavascriptInterface
//				public void sendToAndroid(String text) {
//					// this is called from JS with passed value
//					//FloatService.floatText.setText(MyService.lastLog+"\n"+text);
//					
//					
//					Intent powerIntent = new Intent(MainActivity.ctx, BroadcastRcv.class);
//					powerIntent.setAction("upLayout");
//					powerIntent.putExtra("txt",text);
//					
//					MainActivity.ctx.sendBroadcast(powerIntent);
//					
//					Toast t = Toast.makeText(MainActivity.ctx, "broad", 2000);
//					//t.show();
//					
//
//				}
//			}
//			
//			
//			MainActivity.ctx.runOnUiThread(new Runnable() {
//
//					public void run() {
//						
//						//Toast t = Toast.makeText(MainActivity.ctx, "failed cn", 2000);
//						//t.show();
//						
//						
//												
//			WebView wv = new WebView(MainActivity.ctx);
//					wv.getSettings().setJavaScriptEnabled(true);
//						
//			
//					wv.addJavascriptInterface(new IJavascriptHandler(), "cpjs");
//			wv.loadUrl("https://www.bing.com/translator/?to=bn&text="+MyService.lastLog);
//						wv.loadUrl("javascript:"
//								   +"setTimeout(function(){res=document.querySelector('#tta_output_ta').value;if(res=='')res='failed';window.cpjs.sendToAndroid('"+MyService.lastLog+"'+res);},7000);"
//								   //+"else{setTimeout(function(){res=document.querySelector('#tta_output_ta').value;window.cpjs.sendToAndroid(res);},5000);} },5000);"
//					   +""
// 		   			   +""
//	 				   +""
//						+"void(0)");
//			
//			
//			
//				}
//			});
//			
			translating=false;
			return e.toString();
			
		}
		
	}
 }
