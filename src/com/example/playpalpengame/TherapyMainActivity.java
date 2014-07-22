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
import android.graphics.Point;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class TherapyMainActivity extends Activity {
	private Spinner playerSpinner = null;
	private Spinner stageSpinner = null;
	private Spinner recordSpinner = null;
	private List<AnalysisMessage> resultList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_therapy_main);
		
		ImageView backBtn = (ImageView)findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newAct = new Intent();
				newAct.setClass( TherapyMainActivity.this, BeginActivity.class);
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivityForResult(newAct ,0);
	            TherapyMainActivity.this.finish();
			}
		});
		
		loadRecord();
		
		playerSpinner = (Spinner)findViewById(R.id.playerSpinner);
		stageSpinner = (Spinner)findViewById(R.id.stageSpinner);
		recordSpinner = (Spinner)findViewById(R.id.recordSpinner);
		
	
		String[] testStrArr = getAllNames();
		ArrayAdapter<String> playerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, testStrArr);
		playerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		playerSpinner.setAdapter(playerAdapter);
		
		playerSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
			public void onItemSelected(AdapterView adapterView, View view, int position, long id){
				Toast.makeText(TherapyMainActivity.this, "您選擇"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
			}
			
			public void onNothingSelected(AdapterView arg0) {
				Toast.makeText(TherapyMainActivity.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void loadRecord() {
		try {
			FileInputStream input = new FileInputStream("/sdcard/Android/data/com.example.playpalgame/analysis.json");
			
			JsonReader reader = new JsonReader(new InputStreamReader(input, "UTF-8"));
		    resultList = readMessagesArray(reader);
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getAllNames() {
		ArrayList<String> nameList = new ArrayList<String>();
		
		for(AnalysisMessage msg : resultList) {
			if(!nameList.contains(msg.userName))
				nameList.add(msg.userName);
		}
		return (String[]) nameList.toArray();
	}
	
	public List<AnalysisMessage> readMessagesArray(JsonReader reader) throws IOException {
	     List<AnalysisMessage> messages = new ArrayList<AnalysisMessage>();

	     reader.beginArray();
	     Log.d("Therapy", "Start to collect record.");
	     while (reader.hasNext()) {
	    	 messages.add(readMessage(reader));
	    	 Log.d("Therapy", "1 record collected.");
	     }
	     reader.endArray();
	     return messages;
	   }

	public AnalysisMessage readMessage(JsonReader reader) throws IOException {
		AnalysisMessage msg = new AnalysisMessage();
		
		reader.beginObject();
	    while (reader.hasNext()) {
	       String name = reader.nextName();
	       if (name.equals("name")) 
	    	   msg.userName = reader.nextString();
	       else if (name.equals("stage")) 
	           msg.stage = reader.nextString();
	       else if (name.equals("date")) 
	           msg.date = reader.nextInt();
	       else if (name.equals("time")) 
	           msg.time = reader.nextInt();
	       else if (name.equals("point")) {
	    	   reader.beginArray();
	    	   while(reader.hasNext()) {
	    		   reader.beginObject();
		    	   int x = 0;
		    	   int y = 0;
		    	   while(reader.hasNext()) {
		    		   if(name.equals("x")) 
		    			   x = reader.nextInt();
		    		   else if(name.equals("y")) 
		    			   y = reader.nextInt();
		    		   else
		    			   reader.skipValue();
		    	   }
		    	   msg.points.add(new Point(x, y));
	    		   reader.endObject();
	    	   }
	    	   reader.endArray();
	       }
	       else {
	         reader.skipValue();
	       }
	     }
	     reader.endObject();
	     return msg;
	 }
}

class AnalysisMessage {
	public String userName = null;
	public String stage = null;
	public int date = -1;
	public int time = -1;
	public ArrayList<Point> points = new ArrayList<Point>();
	
	public AnalysisMessage() {
	}
}

