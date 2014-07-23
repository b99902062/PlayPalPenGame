package com.example.playpalpengame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class TherapyMainActivity extends Activity {
	private static final int REPLAY_SPEED = 10;
	
	private Timer replayTimer;
	private static ReplayTimerTask replayTimerTask;
	public static DrawCanvasView canvasView;
	private Spinner playerSpinner = null;
	private Spinner stageSpinner = null;
	private Spinner recordSpinner = null;
	private ArrayList<AnalysisMessage> resultList;
	private ArrayList<AnalysisMessage> targetPlayerList;
	private ArrayList<AnalysisMessage> targetStageList;
	private AnalysisMessage targetRecord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_therapy_main);

		canvasView = ((DrawCanvasView)findViewById(R.id.canvasView));
		
		ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent newAct = new Intent();
				newAct.setClass(TherapyMainActivity.this, BeginActivity.class);
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(newAct, 0);
				TherapyMainActivity.this.finish();
			}
		});

		if(loadRecord()) {
			playerSpinner = (Spinner) findViewById(R.id.playerSpinner);
			stageSpinner = (Spinner) findViewById(R.id.stageSpinner);
			recordSpinner = (Spinner) findViewById(R.id.recordSpinner);
	
			initialSpinner();
			
			Button replayTrackBtn = (Button)findViewById(R.id.replayTrackBtn);
			replayTrackBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((DrawCanvasView)findViewById(R.id.canvasView)).resetLimit();
					setReplayTimer();
				}
			});
			
			((SeekBar)findViewById(R.id.replaySpeedBar)).setOnSeekBarChangeListener(
					new OnSeekBarChangeListener() {
						@Override
						public void onProgressChanged(SeekBar arg0, int arg1,
								boolean arg2) {
							setReplayTimer();
						}

						@Override
						public void onStartTrackingTouch(SeekBar arg0) {
						}

						@Override
						public void onStopTrackingTouch(SeekBar arg0) {
						}
					});
		} else {
			Toast.makeText(this, R.string.str_cannot_find_analysis_file, Toast.LENGTH_LONG).show();
		}
	}
	
	public void setReplayTimer() {
		float percentage = ((SeekBar)findViewById(R.id.replaySpeedBar)).getProgress() / 100.f;
		int targetSpeed = REPLAY_SPEED + (int) (REPLAY_SPEED * 29 * (1 - percentage));
		if(replayTimer != null)
			replayTimer.cancel();
		if(replayTimerTask != null)
			replayTimerTask.cancel();
		replayTimer = new Timer(true);
		replayTimerTask = new ReplayTimerTask();
		replayTimer.schedule(replayTimerTask, 0, targetSpeed);
	}
	
	public static Handler replayTimerHandler = new Handler() {
        public void handleMessage(Message msg) {
        	TherapyMainActivity.canvasView.addLimit();
        	TherapyMainActivity.canvasView.invalidate();
        	if(TherapyMainActivity.canvasView.getLimit() > TherapyMainActivity.canvasView.getPointsCount()) {
        		replayTimerTask.cancel();
        	}
        }
    };

	private boolean loadRecord() {
		try {
			FileInputStream input = new FileInputStream(
					"/sdcard/Android/data/com.example.playpalgame/analysis.json");

			JsonReader reader = new JsonReader(new InputStreamReader(input,
					"UTF-8"));
			resultList = readMessagesArray(reader);
			reader.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void initialSpinner() {
		String[] nameStrArr = getAllNames(resultList);
		connectSource(playerSpinner, nameStrArr);

		playerSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {
						targetPlayerList = getTargetPlayerList(adapterView.getSelectedItem().toString(), resultList);
						String[] stageStrArr = getAllStages(targetPlayerList);
						connectSource(stageSpinner, stageStrArr);
					}
					
					@Override
					public void onNothingSelected(AdapterView arg0) {
					}
				});
		
		stageSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {
						targetStageList = getTargetStageList(adapterView.getSelectedItem().toString(), targetPlayerList);
						String[] recordStrArr = getAllRecords(targetStageList);
						connectSource(recordSpinner, recordStrArr);
					}
					
					@Override
					public void onNothingSelected(AdapterView arg0) {
					}
				});
		
		recordSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {
						targetRecord = getTargetRecord(adapterView.getSelectedItem().toString(), targetStageList);
						if(targetRecord == null)
							Toast.makeText(TherapyMainActivity.this, R.string.str_target_record_not_exist, Toast.LENGTH_SHORT).show();
						canvasView.updatePointList(targetRecord.points);
						canvasView.invalidate();
					}
					
					@Override
					public void onNothingSelected(AdapterView arg0) {
					}
				});
	}
	
	public void connectSource(Spinner spinner, String[] srcStrArr) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, srcStrArr);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	public String[] getAllNames(ArrayList<AnalysisMessage> targetList) {
		ArrayList<String> nameList = new ArrayList<String>();

		for (AnalysisMessage msg : targetList) {
			if (!nameList.contains(msg.userName))
				nameList.add(msg.userName);
		}
		return (String[]) nameList.toArray(new String[nameList.size()]);
	}
	
	public String[] getAllStages(ArrayList<AnalysisMessage> targetList) {
		ArrayList<String> stageList = new ArrayList<String>();

		for (AnalysisMessage msg : targetList) {
			if (!stageList.contains(msg.stage))
				stageList.add(msg.stage);
		}
		return (String[]) stageList.toArray(new String[stageList.size()]);
	}

	public String[] getAllRecords(ArrayList<AnalysisMessage> targetList) {
		ArrayList<String> recordList = new ArrayList<String>();

		for (AnalysisMessage msg : targetList) {
			if (!recordList.contains(msg.date))
				recordList.add(msg.date);
		}
		return (String[]) recordList.toArray(new String[recordList.size()]);
	}
	
	public ArrayList<AnalysisMessage> getTargetPlayerList(String targetName, ArrayList<AnalysisMessage> targetList) {
		ArrayList<AnalysisMessage> returnList = new ArrayList<AnalysisMessage>();
		for(AnalysisMessage msg : targetList) {
			if(msg.userName.equals(targetName))
				returnList.add(msg);
		}
		return returnList;
	}
	
	public ArrayList<AnalysisMessage> getTargetStageList(String targetName, ArrayList<AnalysisMessage> targetList) {
		ArrayList<AnalysisMessage> returnList = new ArrayList<AnalysisMessage>();
		for(AnalysisMessage msg : targetList) {
			if(msg.stage.equals(targetName))
				returnList.add(msg);
		}
		return returnList;
	}
	
	public AnalysisMessage getTargetRecord(String targetName, ArrayList<AnalysisMessage> targetList) {
		for(AnalysisMessage msg : targetList) {
			if(msg.date.equals(targetName))
				return msg;
		}
		return null;
	}

	public ArrayList<AnalysisMessage> readMessagesArray(JsonReader reader)
			throws IOException {
		ArrayList<AnalysisMessage> messages = new ArrayList<AnalysisMessage>();

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("record")) {
				reader.beginArray();
				while (reader.hasNext()) {
					messages.add(readMessage(reader));
					Log.d("Therapy", "1 record collected.");
				}
				reader.endArray();
			} else
				reader.skipValue();
		}
		reader.endObject();
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
				msg.date = reader.nextString();
			else if (name.equals("time"))
				msg.time = reader.nextInt();
			else if (name.equals("point")) {
				reader.beginArray();
				while (reader.hasNext()) {
					int x = -1;
					int y = -1;
					reader.beginArray();
					while (reader.hasNext()) {
						if(x < 0)
							x = reader.nextInt();
						else
							y = reader.nextInt();
					}
					msg.points.add(new Point(x, y));
					reader.endArray();
				}
				reader.endArray();
			} else {
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
	public String date = null;
	public int time = -1;
	public ArrayList<Point> points = new ArrayList<Point>();

	public AnalysisMessage() {
	}
}

class DrawCanvasView extends View {
	private ArrayList<Point> pointList = new ArrayList<Point>();
	private int pointLimit = 0;
	
	public DrawCanvasView(Context context) {
		super(context);
		init();
	}
	
    public DrawCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public DrawCanvasView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    public void init() {
    	setBackgroundColor(Color.BLACK);
    	Drawable background = getBackground();
    	background.setAlpha(80);
    	pointLimit = -1;
    }
    
    public void updatePointList(ArrayList<Point> newPointList) {
    	pointList.clear();
    	pointList = (ArrayList<Point>) newPointList.clone();
    }
    
    public void addLimit() {
    	pointLimit++;
    }
    
    public void resetLimit() {
    	pointLimit = 0;
    }
    
    public int getLimit() {
    	return pointLimit;
    }
    
    public int getPointsCount() {
    	return pointList.size();
    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		Paint p = new Paint();
		p.setColor(Color.WHITE);

		p.setAntiAlias(true);
		
		int pointCounter = 0;
		for(Point pnt: pointList) {
			if(pointLimit >= 0 && pointCounter >= pointLimit)
				break;
			pointCounter++;
			canvas.drawCircle(pnt.x * 8/10, pnt.y * 8/10, 5, p);
		}
	}
}

class ReplayTimerTask extends TimerTask {
	private boolean isPause = false;
	
    public void run() {
    	if(!isPause) {
    		Message msg = new Message();
            TherapyMainActivity.replayTimerHandler.sendMessage(msg);
    	}
    }
    
    public void pause() {
    	isPause = true;
    }
    
    public void resume() {
    	isPause = false;
    }
};