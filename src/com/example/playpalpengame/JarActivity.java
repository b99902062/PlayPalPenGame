package com.example.playpalpengame;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class JarActivity extends Activity {
	private static final int[] starResArray = {R.drawable.star_5, R.drawable.star_1, R.drawable.star_2, R.drawable.star_3};
	private int[] mWinCount;
	private static LinkedList<StarStat> starArr;
	private Timer timer = null;
	private TimerTask timerTask = null;
	
	public native boolean putIntoJar(int index);
	
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
		timerTask = new CheckOrientationTask();
		timer.schedule(timerTask, 0, 10);
	}
	
	public static Handler starHandler = new Handler() {
		public void handleMessage(Message msg) {
			float[] idArray = msg.getData().getFloatArray("IDArray");
			float[] xArray = msg.getData().getFloatArray("XArray");
			float[] yArray = msg.getData().getFloatArray("YArray");
			
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
		}
	};
}

class CheckOrientationTask extends TimerTask {
	private float[] rotationMat;
	private float[] rotationValue;
	
	/** Since passing structure is a little bit trivial between c and java */
	private float[] resultIDArr;
	private float[] resultXArr;
	private float[] resultYArr;
	
	public native boolean updateAngle(float x, float y, float z);
	public native boolean getNewLocArr(float[] resultIDArr, float[] resultXArr, float[] resultYArr);
	
    public void run() {
    	SensorManager.getOrientation(rotationMat, rotationValue);
    	updateAngle(rotationValue[0], rotationValue[1], rotationValue[2]);
    	getNewLocArr(resultIDArr, resultXArr, resultYArr);
    	Message msg = new Message();
    	Bundle dataBundle = new Bundle();
    	dataBundle.putFloatArray("IDArray", resultIDArr);
    	dataBundle.putFloatArray("XArray", resultXArr);
    	dataBundle.putFloatArray("YArray", resultYArr);
    	JarActivity.starHandler.sendMessage(msg);
    }
};


class StarStat {
	public int ID;
	public ImageView view;
	
	public StarStat(int id, ImageView v) {
		ID = id;
		view = v;
	}
}