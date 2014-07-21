package com.example.playpalpengame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private int[][] badgesID = {{R.id.mainBadges1_1, R.id.mainBadges1_2, R.id.mainBadges1_3, R.id.mainBadges1_4, R.id.mainBadges1_5, R.id.mainBadges1_6},
								{R.id.mainBadges2_1, R.id.mainBadges2_2, R.id.mainBadges2_3, R.id.mainBadges2_4, R.id.mainBadges2_5, R.id.mainBadges2_6},
								{R.id.mainBadges3_1, R.id.mainBadges3_2, R.id.mainBadges3_3, R.id.mainBadges3_4, R.id.mainBadges3_5, R.id.mainBadges3_6},
								{R.id.mainBadges4_1, R.id.mainBadges4_2, R.id.mainBadges4_3, R.id.mainBadges4_4, R.id.mainBadges4_5, R.id.mainBadges4_6}};
	
	private int[] badges = new int[4];
	private int[] highScores = new int[4];
	
	private String mUserName = null;
	
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
		loadRecord();
		UpdateBadges();
		
		View playBtn1 = findViewById(R.id.mainPlayBtn1);
		setStallListener(playBtn1, 1);	
		View playBtn2 = findViewById(R.id.mainPlayBtn2);
		setStallListener(playBtn2, 2);
		View playBtn3 = findViewById(R.id.mainPlayBtn3);
		setStallListener(playBtn3, 3);
		View playBtn4 = findViewById(R.id.mainPlayBtn4);
		setStallListener(playBtn4, 4);
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
	            newAct.putExtras(bundle);
	            startActivityForResult(newAct ,0);
	            MainActivity.this.finish();
			}
		});		
	}
	
	private void loadRecord() {
		try {
			FileInputStream input = new FileInputStream("/sdcard/Android/data/com.example.playpalgame/record.json");
			
			JsonReader reader = new JsonReader(new InputStreamReader(input, "UTF-8"));
		    List<RecordMessage> resultList = readMessagesArray(reader);
			reader.close();
			
			for(RecordMessage msg : resultList) {
				Log.d("jsonTest", String.format("Name: %s", msg.userName));
				Log.d("jsonTest", String.format("Password: %s", msg.password));
				for(int i=0; i<4; i++) {
					Log.d("jsonTest", String.format("GameBadge_%d: %d", i+1, msg.badges[i]));
					Log.d("jsonTest", String.format("GameHS_%d: %d", i+1, msg.highScores[i]));
				}
				
				if(msg.userName.equals(mUserName)) {
					badges = msg.badges;
					highScores = msg.highScores;
				}
			}
			
			
			
			//FileOutputStream output = new FileOutputStream("/sdcard/Android/data/com.example.playpalgame/record.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public List<RecordMessage> readMessagesArray(JsonReader reader) throws IOException {
	     List<RecordMessage> messages = new ArrayList<RecordMessage>();

	     reader.beginArray();
	     while (reader.hasNext()) {
	       messages.add(readMessage(reader));
	     }
	     reader.endArray();
	     return messages;
	   }

	public RecordMessage readMessage(JsonReader reader) throws IOException {
		String userName = null;
		String password = null;
		int[] badges = new int[4];
		int[] highScores = new int[4];
		
		reader.beginObject();
	    while (reader.hasNext()) {
	       String name = reader.nextName();
	       if (name.equals("name")) 
	    	   userName = reader.nextString();
	       else if (name.equals("password")) 
	           password = reader.nextString();
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
	       else {
	         reader.skipValue();
	       }
	     }
	     reader.endObject();
	     return new RecordMessage(userName, password, badges, highScores);
	 }
}

class RecordMessage {
	public String userName;
	public String password;
	public int[] badges = new int[4];
	public int[] highScores = new int[4];
	
	public RecordMessage(String userName, String password, int[] badges, int[] highScores) {
		this.userName = userName;
		this.password = password;
		for(int i=0; i<4; i++) {
			this.badges[i] = badges[i];
			this.highScores[i] = highScores[i];
		}
	}
}
