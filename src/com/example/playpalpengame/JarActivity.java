package com.example.playpalpengame;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class JarActivity extends Activity implements SensorEventListener {
	private static final int[] starResArray = {R.drawable.star_5, R.drawable.star_1, R.drawable.star_2, R.drawable.star_3};
	private int[] mWinCount;
	private static LinkedList<StarStat> starArr;
	private Timer timer = null;
	private TimerTask timerTask = null;
	private long lastUpdate;
	private SensorManager sensorManager;
	private String mUserName;
	
	public native void initWorld();
	public native boolean putIntoJar(int index);
	public native boolean updateAngle(float x, float y, float z);
	public native float[] getPosition(int idx);
	public native void worldStep();
	
	public static final int Num_Layers = 5;
	public static final int Star_Size = 200;
	public static final float PTM_Ratio = 1000;
	public static final int FPS = 60;
	
	
	TextView Coor;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	protected void setHomeListener(View targetView) {
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {				
				Intent newAct = new Intent();
				newAct.setClass(JarActivity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userName", mUserName);
	            newAct.putExtras(bundle);
				startActivityForResult(newAct, 0);
				JarActivity.this.finish();
				return true;
			}
		});
	}
	
	public static Handler starHandler = new Handler() {
		public void handleMessage(Message msg) {
			int id    = msg.getData().getInt("ID");
			float xPos  = msg.getData().getFloat("XPos");
			float yPos  = msg.getData().getFloat("YPos");
			float angle = msg.getData().getFloat("Angle");
			ImageView starImg = starArr.get(id).view;
			
			if(id == 1)
				Log.d("starHandler","ID:"+id+" X:"+xPos+" Y:"+yPos);
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins((int)(xPos-Star_Size/2+980), (int)(yPos-Star_Size/2+300), 0, 0);
			params.width = Star_Size;
			params.height = Star_Size;
			starImg.setRotation(-angle*180/(float)Math.PI);
			starImg.setVisibility(ImageView.VISIBLE);
			starImg.setLayoutParams(params);
        	
		}
	};

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float[] values = event.values;
			float x = values[0];
			float y = values[1];
			float z = values[2];
			
			//compromized to Box2D's corrdination system
			updateAngle(-x,-y,z);
			
			Coor.setText("X: "+x+"Y: "+y+"Z: "+z);
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("onpause","here");
		sensorManager.unregisterListener( this );
		timer.cancel();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("onResume","here");
		
		setContentView(R.layout.activity_jar);
		setHomeListener(findViewById(R.id.homeBtn));
		
		System.loadLibrary("JarSimulation");
		
		Bundle bundle = getIntent().getExtras();
		mUserName = bundle.getString("userName");
		mWinCount = bundle.getIntArray("GameWinCountArray");
		
		
		RelativeLayout jarLayout = (RelativeLayout)findViewById(R.id.jarRelativeLayout);
		Coor=(TextView)findViewById(R.id.coor);

		initWorld();
		starArr = new LinkedList<StarStat>();
		for(int i=0; i<mWinCount.length; i++) {
			for(int j=0; j<mWinCount[i]; j++) {
				ImageView newStar = new ImageView(this);
				newStar.setImageResource(starResArray[i]);
				putIntoJar((int)(Math.random()*Num_Layers));
				starArr.add(new StarStat(i * 10000 + j, newStar));
				
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            	params.setMargins(0, 0, 0, 0);
            	newStar.setVisibility(ImageView.INVISIBLE);
            	newStar.setLayoutParams(params);
				
				jarLayout.addView(newStar);
			}
		}
		
		
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener( this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		
		timer = new Timer(true);
		timerTask = new UpdateTask();
		timer.schedule(timerTask, 0, 1000/FPS);
	}
	
	class UpdateTask extends TimerTask{
		public void run() {
			worldStep();
			
			for(int id=0; id<starArr.size(); id++){
				float[] pos = getPosition(id);
				Message msg = new Message();
		    	Bundle dataBundle = new Bundle();
		    	dataBundle.putInt("ID", (int)id);
		    	dataBundle.putFloat("XPos", (int)(pos[0]*PTM_Ratio));
		    	dataBundle.putFloat("YPos", (int)(1000-pos[1]*PTM_Ratio));
		    	dataBundle.putFloat("Angle", pos[2]);
		    	//transform from box2D's coord. system to android corrd. system
		    	msg.setData(dataBundle);
		    	JarActivity.starHandler.sendMessage(msg);
			}
		}
	}
}

class StarStat {
	public int ID;
	public ImageView view;
	
	public StarStat(int id, ImageView v) {
		ID = id;
		view = v;
	}
}