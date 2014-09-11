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
import android.util.AttributeSet;
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

class RecordEntry{
	public final static int STATE_TOUCH_START = 1;
	public final static int STATE_TOUCH_MOVE = 2;
	public final static int STATE_TOUCH_END = 3;
	public final static int STATE_HOVER_START = 4;
	public final static int STATE_HOVER_MOVE = 5;
	public final static int STATE_HOVER_END = 6;
	public final static int STATE_HOVER_BTN_START = 7;
	public final static int STATE_HOVER_BTN_MOVE = 8;
	public final static int STATE_HOVER_BTN_END = 9;
	
	Point point;
	int state;
	
	RecordEntry(){
		point = new Point(0,0);
		state = 0;
	}
	RecordEntry(Point p, int s){
		point = p;
		state = s;
	}
}

public class PenRecorder{
	private static View recorderView;
	private static RecordTimerTask recorderTask;
	protected static ArrayList<RecordEntry> posArray;
	private static Timer timer;
	
	private static String playerName;
	private static String stageName;
	protected static Context context;
	protected static long passedTime;
	private static boolean isRecording = false;
	private static DrawableRelativeLayout drLayout = null;
	
	//must be called before game starting
	protected static void registerRecorder( DrawableRelativeLayout gameLayout, Context _context, String name, String stage){
		posArray = new ArrayList<RecordEntry>();
		context    = _context;
		playerName = name;
		stageName   = stage;
		passedTime = 0;
		drLayout = gameLayout;
		startRecorder(gameLayout);
	}
	
	public static void forceRecord() {
		if(recorderTask != null)
			recorderTask.forceRecord();
	}
	//call from utility when down
	protected static void startRecorder(DrawableRelativeLayout drLayout){
		timer = new Timer( );
		recorderTask = new RecordTimerTask(drLayout);
		timer.schedule(recorderTask, 0, 10);
		//recorderTask.forceRecord();
	}
	
	//call from utility when up
	protected static void stopRecorder(){
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	//called after the game finished
	protected static void outputJSON(){
		stopRecorder();
		drLayout.clearEntries();
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
			curRecord.put("time",(int)passedTime);
			 
			JSONArray pointJSONArray = new JSONArray();
			 
			for(int i=0; i<posArray.size(); i++){
				JSONArray curPoint = new JSONArray();
				
				curPoint.put(posArray.get(i).point.x);
				curPoint.put(posArray.get(i).point.y);
				curPoint.put(posArray.get(i).state);
				 
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
	private DrawableRelativeLayout drLayout = null;
	private long startTime;
	
	public RecordTimerTask(DrawableRelativeLayout layout) {
		drLayout = layout;
		startTime = System.currentTimeMillis();
	}
	
	public void run(){
		 PenRecorder.passedTime = System.currentTimeMillis() - startTime;
		 if(PlayPalUtility.curEntry != null) { 
			 PenRecorder.posArray.add(PlayPalUtility.curEntry);
			 if(PlayPalUtility.curEntry.state == RecordEntry.STATE_TOUCH_END)
				 drLayout.clearEntries();
			 else if(PlayPalUtility.curEntry.state == RecordEntry.STATE_TOUCH_START
				 ||  PlayPalUtility.curEntry.state == RecordEntry.STATE_TOUCH_MOVE)
				 drLayout.addEntry(PlayPalUtility.curEntry);
		 }
	 }
	 
	 //use when touch down/up occur
	 public void forceRecord(){
		 if(PlayPalUtility.curEntry != null) {
			 PenRecorder.posArray.add(PlayPalUtility.curEntry);
			 if(PlayPalUtility.curEntry.state == RecordEntry.STATE_TOUCH_END)
				 drLayout.clearEntries();
			 else if(PlayPalUtility.curEntry.state == RecordEntry.STATE_TOUCH_START
				 ||  PlayPalUtility.curEntry.state == RecordEntry.STATE_TOUCH_MOVE)
				 drLayout.addEntry(PlayPalUtility.curEntry);
		 }
	 }
 }

class DrawableRelativeLayout extends RelativeLayout {
	private ArrayList<RecordEntry> entryList;
	private Paint p;
	
	public DrawableRelativeLayout(Context context) {
		super(context);
		init();
	}
	
	public DrawableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public DrawableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    public void clearEntries() {
    	synchronized(entryList) { 
    		entryList.clear();
    	}
    }
    
    public void addEntry(RecordEntry newEntry) {
    	synchronized(entryList) { 
    		entryList.add(newEntry);
    	}
    }
    
    private void init() {
    	entryList = new ArrayList<RecordEntry>();
    	
    	p = new Paint();
		p.setColor(Color.DKGRAY);
		p.setStrokeWidth(10);
		p.setAntiAlias(true);
		
		setWillNotDraw(false);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
		
		int preX = -1;
		int preY = -1;
		synchronized(entryList) {
			for(RecordEntry entry: entryList) {
				canvas.drawCircle(entry.point.x, entry.point.y, 5, p);
				if(preX >= 0) 
					canvas.drawLine(preX, preY, entry.point.x, entry.point.y, p);
				preX = entry.point.x;
				preY = entry.point.y;
			}
		}
	}
}
