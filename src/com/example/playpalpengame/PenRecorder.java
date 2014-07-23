package com.example.playpalpengame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenHoverListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


public class PenRecorder{
	private static View recorderView;
	private static RecordTimerTask recorderTask;
	protected static ArrayList<Point> posArray;
	private static Timer timer;
	
	
	private static String playerName;
	private static int stageNum;
	protected static Context context;
	protected static int passedTime;
	private static boolean isRecording = false;
	
	protected static void registerRecorder( RelativeLayout gameLayout, Context _context, String name, int stage){
		Log.d("Recorder","finish registering");
		if(posArray == null)
			posArray = new ArrayList<Point>();
		
		context    = _context;
		playerName = name;
		stageNum   = stage;
		passedTime = 0;
		
	}
	
	
	
	protected static void startRecorder(){
		timer = new Timer( );
		RecordTimerTask recorderTask = new RecordTimerTask();
		timer.schedule(recorderTask, 0, 50);
		
		
	}
	
	
	protected static void stopRecoreder(){
		timer.cancel();
	}
	
	
	protected static void outputJSON(){
		 try {
			 String jsonString = readJSONFile();        
			 JSONObject jsonResponse = new JSONObject(jsonString);
			 JSONArray  jsonArray = jsonResponse.getJSONArray("record");
			 
			 Log.d("Recorder",jsonArray.toString());

			
			 JSONObject curRecord = new JSONObject();
			 curRecord.put("name",playerName);
			 curRecord.put("stage",stageNum);
			 
			 SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			 String date = df.format(Calendar.getInstance().getTime());
			 curRecord.put("date",date);
			 curRecord.put("time",passedTime);
			 
			 
			 
			 JSONArray pointJSONArray = new JSONArray();
			 
			 for(int i=0; i<posArray.size(); i++){
				 JSONArray curPoint = new JSONArray();
				 curPoint.put(posArray.get(i).x);
				 curPoint.put(posArray.get(i).y);
				 
				 pointJSONArray.put(curPoint);
			 }
		 
		
			curRecord.put("point", pointJSONArray);
			jsonArray.put(curRecord);			
			
			JSONObject newObj = new JSONObject();
			newObj.put("record",jsonArray);
			
			FileOutputStream fout = new FileOutputStream(android.os.Environment.getExternalStorageDirectory()+ "/Android/data/com.example.playpalgame/analysis.json",false);
			fout.write(newObj.toString().getBytes());
			fout.flush();
			fout.close();		
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	private static String readJSONFile(){
		StringBuilder sb = new StringBuilder();
		FileInputStream fin = null;
        try {           
        	fin = new FileInputStream(android.os.Environment.getExternalStorageDirectory()+ "/Android/data/com.example.playpalgame/analysis.json");
            byte[] data = new byte[fin.available()];
            while (fin.read(data) != -1) {
              sb.append(new String(data));
            }
            fin.close();
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
	
}

class RecordTimerTask extends TimerTask{	 
	 public void run(){
		 Log.d("Recorder","recording"+ PenRecorder.passedTime);
		 PenRecorder.passedTime++;
		 
		 PenRecorder.posArray.add(new Point((int)PlayPalUtility.curEvent.getX(), (int)PlayPalUtility.curEvent.getY()));
	 }
 }
