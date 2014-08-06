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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private int[][] badgesID = {{R.id.mainBadges1_1, R.id.mainBadges1_2, R.id.mainBadges1_3, R.id.mainBadges1_4, R.id.mainBadges1_5, R.id.mainBadges1_6},
								{R.id.mainBadges2_1, R.id.mainBadges2_2, R.id.mainBadges2_3, R.id.mainBadges2_4, R.id.mainBadges2_5, R.id.mainBadges2_6},
								{R.id.mainBadges3_1, R.id.mainBadges3_2, R.id.mainBadges3_3, R.id.mainBadges3_4, R.id.mainBadges3_5, R.id.mainBadges3_6},
								{R.id.mainBadges4_1, R.id.mainBadges4_2, R.id.mainBadges4_3, R.id.mainBadges4_4, R.id.mainBadges4_5, R.id.mainBadges4_6}};
	
	private static int[] badges = new int[4];
	private static int[] highScores = new int[4];
	private static int[] winCount = new int[4];
	
	private static String mUserName = null;
	
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
		
		java.util.Arrays.fill(badges, 0);
		ArrayList<RecordMessage>resultList = loadRecord();
		if(resultList != null)
			UpdateBadges();
		
		View playBtn1 = findViewById(R.id.mainPlayBtn1);
		setStallListener(playBtn1, 1);	
		View playBtn2 = findViewById(R.id.mainPlayBtn2);
		setStallListener(playBtn2, 2);
		View playBtn3 = findViewById(R.id.mainPlayBtn3);
		setStallListener(playBtn3, 3);
		View playBtn4 = findViewById(R.id.mainPlayBtn4);
		setStallListener(playBtn4, 4);
		
		ImageView therapyIcon = (ImageView)findViewById(R.id.therapyIcon);
		therapyIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent newAct = new Intent();
				newAct.setClass( MainActivity.this, TherapyMainActivity.class );
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivityForResult(newAct ,0);
	            MainActivity.this.finish();
			}
		});
		
		ImageView jarIcon = (ImageView)findViewById(R.id.jarIcon);
		jarIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent newAct = new Intent();
				newAct.setClass( MainActivity.this, JarActivity.class );
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putIntArray("GameWinCountArray", winCount);
	            newAct.putExtras(bundle);
				startActivityForResult(newAct ,0);
	            MainActivity.this.finish();
			}
		});
	}
	
	protected void setStallListener(View targetView, final int gameIndex) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent newAct = new Intent();
				newAct.setClass( MainActivity.this, LoadingActivity.class );
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", gameIndex);
				bundle.putString("userName", mUserName);
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
	       else {
	         reader.skipValue();
	       }
	     }
	     reader.endObject();
	     return new RecordMessage(userName, badges, highScores, winCount);
	 }
	
	public static String[] getAllNames(ArrayList<RecordMessage> targetList) {
		ArrayList<String> nameList = new ArrayList<String>();

		for (RecordMessage msg : targetList) {
			if (!nameList.contains(msg.userName))
				nameList.add(msg.userName);
		}
		return (String[]) nameList.toArray(new String[nameList.size()]);
	}
}

class RecordMessage {
	public String userName;
	public int[] badges = new int[4];
	public int[] highScores = new int[4];
	public int[] winCount = new int[4];
	
	public RecordMessage(String userName, int[] badges, int[] highScores, int[] winCount) {
		this.userName = userName;
		for(int i=0; i<4; i++) {
			this.badges[i] = badges[i];
			this.highScores[i] = highScores[i];
			this.winCount[i] = winCount[i];
		}
	}
}
