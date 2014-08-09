package com.example.playpalpengame;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
	
	public native void initWorld();
	public native boolean putIntoJar(int index);
	public native boolean updateAngle(float x, float y, float z);
	public native float[] getPosition(int idx);
	public native void worldStep();
	public static final int PTM_Ratio = 40;
	public static final int Star_Size = 100;
	
	TextView Coor;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_jar);
		
		System.loadLibrary("JarSimulation");
		starArr = new LinkedList<StarStat>();
		
		Bundle bundle = getIntent().getExtras();
		mWinCount = bundle.getIntArray("GameWinCountArray");
		
		RelativeLayout jarLayout = (RelativeLayout)findViewById(R.id.jarRelativeLayout);
		Coor=(TextView)findViewById(R.id.coor);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener( this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		
		
		initWorld();
		
		for(int i=0; i<mWinCount.length; i++) {
			for(int j=0; j<mWinCount[i]; j++) {
				ImageView newStar = new ImageView(this);
				newStar.setImageResource(starResArray[i]);
				putIntoJar(i * 10000 + j);
				starArr.add(new StarStat(i * 10000 + j, newStar));
				
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            	params.setMargins(0, 0, 0, 0);
            	newStar.setLayoutParams(params);
				
				jarLayout.addView(newStar);
			}
		}

		timer = new Timer(true);
		timerTask = new UpdateTask();
		timer.schedule(timerTask, 0, 10);//1/60sec
	}
	
	public static Handler starHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d("Handler",""+starArr.size());
			int id    = msg.getData().getInt("ID");
			int xPos  = msg.getData().getInt("XPos");
			int yPos  = msg.getData().getInt("YPos");
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(xPos-Star_Size/2, yPos-Star_Size/2, 0, 0);
			params.width = Star_Size;
			params.height = Star_Size;
        	starArr.get(id).view.setLayoutParams(params);
        	
			/*
			for(int i=0; i<idArray.length; i++) {
				for(int j=0; j<starArr.size(); j++) {
					if(idArray[i] == starArr.get(j).ID) {
						RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	                	params.setMargins((int)xArray[i], (int)yArray[i], 0, 0);
	                	starArr.get(j).view.setLayoutParams(params);
						break;
					}
				}
			}
			*/
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

			float accelerationSquareRoot = (x * x + y * y + z * z) / 
                                                   (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
			long actualTime = System.currentTimeMillis();

			if (accelerationSquareRoot >= 2)  {
				if (actualTime - lastUpdate < 200)  return;

				lastUpdate = actualTime;
				Toast.makeText(this, "Device was shaken", Toast.LENGTH_SHORT).show();	
			}
		}
		
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onPause() {					// unregister listener
		sensorManager.unregisterListener( this );
		super.onStop();
	}
	
	class UpdateTask extends TimerTask{
		public void run() {
			worldStep();
			
			for(int id=0; id<starArr.size(); id++){
				float[] pos = getPosition(id);
				Message msg = new Message();
		    	Bundle dataBundle = new Bundle();
		    	dataBundle.putInt("ID", (int)id);
		    	dataBundle.putInt("XPos", (int)(pos[0]*PTM_Ratio));
		    	dataBundle.putInt("YPos", (int)(1000-pos[1]*PTM_Ratio));
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