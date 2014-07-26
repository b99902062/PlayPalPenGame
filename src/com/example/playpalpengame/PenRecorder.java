package com.example.playpalpengame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
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
	private static String stageName;
	protected static Context context;
	protected static int passedTime;
	private static boolean isRecording = false;
	
	//must be called before game starting
	protected static void registerRecorder( RelativeLayout gameLayout, Context _context, String name, String stage){
		posArray = new ArrayList<Point>();
		context    = _context;
		playerName = name;
		stageName   = stage;
		passedTime = 0;
		
	}
	
	
	//call from utility when down
	protected static void startRecorder(){
		timer = new Timer( );
		RecordTimerTask recorderTask = new RecordTimerTask();
		timer.schedule(recorderTask, 0, 50);
	}
	
	//call from utility when up
	protected static void stopRecoreder(){
		timer.cancel();
	}
	
	//called after the game finished
	protected static void outputJSON(){
		Boolean isTheFirstRecord = false;
		String jsonString = "";
		String filePath = android.os.Environment.getExternalStorageDirectory()+ "/Android/data/com.example.playpalgame/analysis.json";
		FileOutputStream fout = null;
		RandomAccessFile analysisFile;
		
		
		try {
			File file = new File(filePath);
			if(!file.exists()){
				isTheFirstRecord = true;
				file.createNewFile();
				FileWriter fWriter = new FileWriter(file);
				//fWriter.write("{\"record\":[]}");
				fWriter.write("[]");
	        	fWriter.flush();
	        	fWriter.close();
			}
	
			analysisFile = new RandomAccessFile(filePath, "rw");
			analysisFile.seek(analysisFile.length()-1); 
				 
			JSONObject curRecord = new JSONObject();
			curRecord.put("name",playerName);
			curRecord.put("stage",stageName);
			 
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
			String result = (isTheFirstRecord)? curRecord.toString()+"]" : ","+curRecord.toString()+"]";
			
			analysisFile.write(result.getBytes());
			analysisFile.close();	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}

class RecordTimerTask extends TimerTask{	 
	 public void run(){
		 //Log.d("Recorder","recording"+ PenRecorder.passedTime);
		 PenRecorder.passedTime++;
		 PenRecorder.posArray.add(new Point((int)PlayPalUtility.curEvent.getX(), (int)PlayPalUtility.curEvent.getY()));
	 }
 }
