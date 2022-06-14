package com.ab.access;

import android.accessibilityservice.*;
import android.util.*;
import android.view.*;
import android.view.accessibility.*;
import android.widget.*;
import android.graphics.*;
import android.content.*;
import android.app.*;


public class MyService extends AccessibilityService {
	private static MyService mMS;
	int mDebugDepth=0;
	static String log;
	boolean txtTaken=false;
	AccessibilityNodeInfo mNodeInfo;
	static String lastLog, lastReq="";
	Activity ctx;
	
	public static MyService getInstance(){
			return mMS;
		}
	
	
	@Override

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
		this.ctx=MainActivity.ctx;
		//Toast.makeText(this, accessibilityEvent.toString(), Toast.LENGTH_SHORT).show();
		if(FloatService.status=="on"){
		mDebugDepth = 0; 
		log="";
		try{
			mNodeInfo = accessibilityEvent.getSource();
			printAllViews(mNodeInfo);
			}catch(Exception e){
				
			}
		//Toast.makeText(this, log, Toast.LENGTH_SHORT).show();
		//setClipboard(getApplicationContext(),log);
		}
		
	}
	
	private void printAllViews(AccessibilityNodeInfo mNodeInfo) {
        if (mNodeInfo == null) return;
        
		boolean txtTaken=false;
		
		for (int i = 0; i < mDebugDepth; i++) {
            //log += ".";
        }
		if (mNodeInfo.getClassName().equals(TextView.class.getName()) && mNodeInfo.getText().length()>5) {
			log+=mNodeInfo.getText();
			if(!txtTaken){
				lastLog=log;
				//FloatService.floatText.setText(lastLog+"\n...");
				//Toast.makeText(this, lastLog, Toast.LENGTH_SHORT).show();
				
				Thread thread = new Thread(new Runnable(){
						@Override
						public void run(){
							//code to do the HTTP request
							
							if(lastReq!=lastLog)FloatService.lines = lastLog + "\n" + FloatService.get("https://translated.glitch.me/get?from=&to=bn&text="+lastLog," ");
							MainActivity.ctx.runOnUiThread(new Runnable() {

									public void run() {
										//Toast.makeText(MainActivity.ctx, FloatService.lines, Toast.LENGTH_SHORT).show();
				 						//FloatService.floatText.setText(FloatService.lines);
										
									}
								});
							
						}
					});
				thread.start();
				
				txtTaken=true;
				
			}
			
		}
        //Log.d(TAG, log);
		if (mNodeInfo.getChildCount() < 1) return;
        
        mDebugDepth++;

        for (int i = 0; i < mNodeInfo.getChildCount(); i++) {
            printAllViews(mNodeInfo.getChild(i));
        }
        mDebugDepth--;
    }

    @Override

    public void onInterrupt() {


    }
	@Override
		protected void onServiceConnected() {
			super.onServiceConnected();
			Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
			
			//doGesture(0,1);
			mMS = this;
			
			//doGesture(0,0);
			/*
			
			Path clickPath = new Path();
			clickPath.moveTo(Float.intBitsToFloat(1), Float.intBitsToFloat(1));
			GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
			gestureBuilder.addStroke(new GestureDescription.StrokeDescription(clickPath, 0, 1));
			dispatchGesture(gestureBuilder.build(), null, null);
			*/
		}

		public void doGesture(int x, int y, long t){
			
			Point position=new Point(360,1150);
			GestureDescription.Builder builder = new GestureDescription.Builder();
			long ln=(long)(Math.pow(15-(Math.sqrt(1150-y)),2));
			if (!(ln>50))ln=50;
			
			Path p = new Path();
			p.moveTo(position.x, position.y);
			p.lineTo(x, y);
			
			builder.addStroke(new GestureDescription.StrokeDescription(p, 50,50));
			GestureDescription gesture = builder.build();
			boolean isDispatched =    dispatchGesture(gesture,null,null);
			
			Toast.makeText(getApplicationContext(), x+" "+(y-ln)+" : "+ln, Toast.LENGTH_SHORT).show();
			
		}

    @Override

    protected boolean onKeyEvent(KeyEvent event) {

		//if(mNodeInfo != null && lastNode!=null)Toast.makeText(this, lastNode.getText(), Toast.LENGTH_SHORT).show();
		
        int action = event.getAction();

        int keyCode = event.getKeyCode();

        // the service listens for both pressing and releasing the key

        // so the below code executes twice, i.e. you would encounter two Toasts

        // in order to avoid this, we wrap the code inside an if statement

        // which executes only when the key is released

		/*
        if (action == KeyEvent.ACTION_UP) {

            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

                Log.d("Check", "KeyUp");

                Toast.makeText(this, "KeyUp", Toast.LENGTH_SHORT).show();

            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

                Log.d("Check", "KeyDown");

                Toast.makeText(this, "KeyDown", Toast.LENGTH_SHORT).show();

            }

        }
*/
		return super.onKeyEvent(event);

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
	
	
}
