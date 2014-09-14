package com.example.playpalpengame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class MainActivity extends Activity implements SensorEventListener {
	private static final int[] starResArray = {R.drawable.jar_star_1, R.drawable.jar_star_2, R.drawable.jar_star_3, R.drawable.jar_star_4};
	private int[][] badgesID = {{R.id.mainBadges1_1, R.id.mainBadges1_2, R.id.mainBadges1_3, R.id.mainBadges1_4, R.id.mainBadges1_5, R.id.mainBadges1_6},
								{R.id.mainBadges2_1, R.id.mainBadges2_2, R.id.mainBadges2_3, R.id.mainBadges2_4, R.id.mainBadges2_5, R.id.mainBadges2_6},
								{R.id.mainBadges3_1, R.id.mainBadges3_2, R.id.mainBadges3_3, R.id.mainBadges3_4, R.id.mainBadges3_5, R.id.mainBadges3_6},
								{R.id.mainBadges4_1, R.id.mainBadges4_2, R.id.mainBadges4_3, R.id.mainBadges4_4, R.id.mainBadges4_5, R.id.mainBadges4_6}};
	
	private static int[] badges = new int[4];
	private static int[] highScores = new int[4];
	private static int[] winCount = new int[4];
	private static String mUserName = null;
	
	private static LinkedList<StarStat> starArr;
	private Timer timer = null;
	private TimerTask timerTask = null;
	
	
	/* jar world info */
	private SensorManager sensorManager;
	
	public native void initWorld();
	public native boolean putIntoJar(int index);
	public native boolean updateAngle(float x, float y, float z);
	public native float[] getPosition(int idx);
	public native void worldStep();
	
	public static final int Num_Layers = 3;
	public static final int Star_Size = 125;
	public static final float PTM_Ratio = 1500;
	public static final int FPS = 60;
	
	
	
	@Override
	public void onBackPressed() {
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_main);
		
		Bundle bundle = getIntent().getExtras();
		mUserName = bundle.getString("userName");
		
		setHeadView();
		
		java.util.Arrays.fill(badges, 0);
		ArrayList<RecordMessage>resultList = loadRecord();
		if(resultList != null)
			UpdateBadges();
		
		setStallListener(findViewById(R.id.mainPlayBtn1), 1, true);
		setStallListener(findViewById(R.id.mainPlayBtn2), 2, true);
		setStallListener(findViewById(R.id.mainPlayBtn3), 3, true);
		setStallListener(findViewById(R.id.mainPlayBtn4), 4, true);
		setStallListener(findViewById(R.id.mainPracticeBtn1), 1, false);
		setStallListener(findViewById(R.id.mainPracticeBtn2), 2, false);
		setStallListener(findViewById(R.id.mainPracticeBtn3), 3, false);
		setStallListener(findViewById(R.id.mainPracticeBtn4), 4, false);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		timer.cancel();
		
		
		RelativeLayout jarLayout = (RelativeLayout)findViewById(R.id.jarRelativeLayout);
		for(StarStat star:starArr){
			jarLayout.removeView(star.view);
		}
		
		
		jarLayout.invalidate();
		
		BackgroundMusicHandler.recyle();
		sensorManager.unregisterListener( this );
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		BackgroundMusicHandler.initMusic(this);
		BackgroundMusicHandler.setMusicSt(true);
			
		System.loadLibrary("JarSimulation");
		RelativeLayout jarLayout = (RelativeLayout)findViewById(R.id.jarRelativeLayout);
		jarLayout.invalidate();
		
		initWorld();
		starArr = new LinkedList<StarStat>();
		for(int i=0; i<winCount.length; i++) {
			for(int j=0; j<winCount[i]; j++) {
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
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float[] values = event.values;
			float x = values[0];
			float y = values[1];
			float z = values[2];
			
			//compromized to Box2D's corrdination system
			updateAngle(-x,-y,z);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	private void setHeadView() {
		ImageView headView = (ImageView)findViewById(R.id.mainExtraHeadView);
		String headFileName = "/sdcard/Android/data/com.example.playpalgame/" + mUserName + ".png";
		File f = new File(headFileName);
		if(f.exists()) {
			Bitmap bMap = BitmapFactory.decodeFile(headFileName);
			headView.setImageBitmap(bMap);;
		}
		else {
			headView.setImageResource(R.drawable.login_head);
		}
		
		((TextView)findViewById(R.id.mainExtraNameView)).setText(mUserName);
	}
	
	protected void setStallListener(View targetView, final int gameIndex, final boolean isPlayBtn) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				BackgroundMusicHandler.setCanRecycle(false);
				
				SharedPreferences settings = getSharedPreferences("PLAY_PAL_TMP_INFO", 0);
				settings.edit().clear().commit();
				
				Intent newAct = new Intent();
				newAct.setClass( MainActivity.this, LoadingActivity.class );
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", gameIndex);
				bundle.putString("userName", mUserName);
				bundle.putBoolean("isPlayBtn", isPlayBtn);
				bundle.putInt("GameBadges", badges[gameIndex-1]);
				bundle.putInt("GameHighScore", highScores[gameIndex-1]);
				bundle.putInt("GameWinCount", winCount[gameIndex-1]);
	            newAct.putExtras(bundle);
	            startActivityForResult(newAct ,0);
	            MainActivity.this.finish();
			}
		});		
	}
	
	public static ArrayList<RecordMessage> loadRecord() {
		try {
			File f = new File("/sdcard/Android/data/com.example.playpalgame/record.json");
			if(!f.exists()) {
				String newStr = "[]";
				try {
					File newTextFile = new File("/sdcard/Android/data/com.example.playpalgame/record.json");
					FileWriter fileWriter = new FileWriter(newTextFile);
		            fileWriter.write(newStr);
		            fileWriter.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			}
			FileInputStream input = new FileInputStream("/sdcard/Android/data/com.example.playpalgame/record.json");
			JsonReader reader = new JsonReader(new InputStreamReader(input, "UTF-8"));
		    ArrayList<RecordMessage> returnList = readMessagesArray(reader);
			reader.close();
			
			for(RecordMessage msg : returnList) {
				if(msg.userName.equals(mUserName)) {
					badges = msg.badges;
					highScores = msg.highScores;
					winCount = msg.winCount;
				}
			}
			return returnList;
			//FileOutputStream output = new FileOutputStream("/sdcard/Android/data/com.example.playpalgame/record.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void UpdateBadges() {
		for(int i=0; i<4; i++) {
			int badgeResult = badges[i];
			for(int j=0; j<6; j++) {
				if(((badgeResult >> j) & 0x1) == 0) {
					ImageView badgeView = (ImageView)findViewById(badgesID[i][j]);
					badgeView.setImageResource(R.drawable.main_badge_empty);
				}
					
			}
		}
	}
	
	public static ArrayList<RecordMessage> readMessagesArray(JsonReader reader) throws IOException {
	     ArrayList<RecordMessage> messages = new ArrayList<RecordMessage>();

	     reader.beginArray();
	     while (reader.hasNext()) 
	       messages.add(readMessage(reader));
	     reader.endArray();
	     return messages;
	   }

	public static RecordMessage readMessage(JsonReader reader) throws IOException {
		String userName = null;
		boolean isMale = false;
		int age = 0;
		int[] badges = new int[4];
		int[] highScores = new int[4];
		int[] winCount = new int[4];
		
		reader.beginObject();
	    while (reader.hasNext()) {
	       String name = reader.nextName();
	       if (name.equals("name")) 
	    	   userName = reader.nextString();
	       else if (name.contains("gameBadge")) {
	    	   for(int i=1; i<=4; i++) {
	    		   if(name.contains(Integer.toString(i)))
	    			   badges[i-1] = reader.nextInt(); 
	    	   }
	       }
	       else if (name.contains("gameHighScore")) {
	    	   for(int i=1; i<=4; i++) {
	    		   if(name.contains(Integer.toString(i)))
	    			   highScores[i-1] = reader.nextInt(); 
	    	   }
	       }
	       else if (name.contains("gameWinCount")) {
	    	   for(int i=1; i<=4; i++) {
	    		   if(name.contains(Integer.toString(i)))
	    			   winCount[i-1] = reader.nextInt(); 
	    	   }
	       }
	       else if (name.equals("isMale")) 
	    	   isMale = reader.nextBoolean();
	       else if (name.equals("age")) 
	    	   age = reader.nextInt();
	       else {
	         reader.skipValue();
	       }
	     }
	     reader.endObject();
	     return new RecordMessage(userName, badges, highScores, winCount, isMale, age);
	 }
	
	public static String[] getAllNames(ArrayList<RecordMessage> targetList) {
		ArrayList<String> nameList = new ArrayList<String>();

		for (RecordMessage msg : targetList) {
			if (!nameList.contains(msg.userName))
				nameList.add(msg.userName);
		}
		return (String[]) nameList.toArray(new String[nameList.size()]);
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
		    	MainActivity.starHandler.sendMessage(msg);
			}
		}
	}
	
	public static Handler starHandler = new Handler() {
		public void handleMessage(Message msg) {
			int id    = msg.getData().getInt("ID");
			float xPos  = msg.getData().getFloat("XPos");
			float yPos  = msg.getData().getFloat("YPos");
			float angle = msg.getData().getFloat("Angle");
			ImageView starImg = starArr.get(id).view;
			
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins((int)(xPos-Star_Size/2+240), (int)(yPos-Star_Size/2+300), 0, 0);
			params.width = Star_Size;
			params.height = Star_Size;
			starImg.setRotation(-angle*180/(float)Math.PI);
			starImg.setVisibility(ImageView.VISIBLE);
			starImg.setLayoutParams(params);
        	
		}
	};
}

class StarStat {
	public int ID;
	public ImageView view;
	
	public StarStat(int id, ImageView v) {
		ID = id;
		view = v;
	}
}

class RecordMessage {
	public String userName;
	public boolean isMale;
	public int age;
	public int[] badges = new int[4];
	public int[] highScores = new int[4];
	public int[] winCount = new int[4];
	
	public RecordMessage(String userName, int[] badges, int[] highScores, int[] winCount, boolean isMale, int age) {
		this.userName = userName;
		this.isMale = isMale;
		this.age = age;
		for(int i=0; i<4; i++) {
			this.badges[i] = badges[i];
			this.highScores[i] = highScores[i];
			this.winCount[i] = winCount[i];
		}
	}
}
