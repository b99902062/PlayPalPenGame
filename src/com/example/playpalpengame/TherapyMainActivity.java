package com.example.playpalpengame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap.SimpleEntry;
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
import android.os.AsyncTask;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class TherapyMainActivity extends Activity {
	private static final int REPLAY_SPEED = 1;
	
	private Timer replayTimer;
	private static ReplayTimerTask replayTimerTask;
	public static DrawCanvasView canvasView;
	public static Spinner playerSpinner = null;
	public static Spinner stageSpinner = null;
	public static Spinner recordSpinner = null;
	public static ProgressBar loadingBar = null;
	public static ArrayList<AnalysisMessage> resultList;
	public static ArrayList<AnalysisMessage> targetPlayerList;
	public static ArrayList<AnalysisMessage> targetStageList;
	public static AnalysisMessage targetRecord;
	private SimpleEntry[] srcBackgroundList = {new SimpleEntry("1-1", new StageBackgroundInfo(R.drawable.game1_carrot_1, 304, 304, 304, 176)),
			new SimpleEntry("1-2", new StageBackgroundInfo(R.drawable.game1_cucumber_1, 304, 304, 304, 176)),
			new SimpleEntry("1-3", new StageBackgroundInfo(R.drawable.game1_pot_1, 624, 304, 624, 176)),
			new SimpleEntry("1-4", new StageBackgroundInfo(R.drawable.game1_pot_1, 624, 304, 624, 176)),
			new SimpleEntry("2-1", new StageBackgroundInfo(R.drawable.game2_basket_1, 1568, 304, 0, 176)),
			new SimpleEntry("2-2", new StageBackgroundInfo(R.drawable.game2_grill, 304, 304, 304, 176)),
			new SimpleEntry("3-1", new StageBackgroundInfo(R.drawable.game3_mixbowl, 624, 304, 624, 176)),
			new SimpleEntry("3-2", new StageBackgroundInfo(R.drawable.game3_cake1, 624, 304, 624, 176)),
			new SimpleEntry("3-3", new StageBackgroundInfo(R.drawable.game3_cake1, 624, 304, 624, 176)),
			new SimpleEntry("3-4", new StageBackgroundInfo(R.drawable.game3_cake1, 624, 304, 624, 176)),
			new SimpleEntry("4-1", new StageBackgroundInfo(R.drawable.game4_plate, 304, 304, 304, 176)),
			new SimpleEntry("4-2", new StageBackgroundInfo(R.drawable.game4_plate, 304, 304, 304, 176)),
			new SimpleEntry("4-3", new StageBackgroundInfo(R.drawable.game4_plate, 304, 304, 304, 176))
	};
	
	private ImageView srcBackgroundView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_therapy_main);

		canvasView = ((DrawCanvasView)findViewById(R.id.canvasView));
		srcBackgroundView = (ImageView)findViewById(R.id.srcBackgroundView);
		playerSpinner = (Spinner) findViewById(R.id.playerSpinner);
		stageSpinner = (Spinner) findViewById(R.id.stageSpinner);
		recordSpinner = (Spinner) findViewById(R.id.recordSpinner);
		loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
		
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
		
		CheckBox isLineUpCheckbox = (CheckBox) findViewById(R.id.isLineUpCheckbox);
		isLineUpCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				TherapyMainActivity.canvasView.setLineUp(arg1);
				TherapyMainActivity.canvasView.invalidate();
			}
		});
		
		CheckBox isShowHoverCheckbox = (CheckBox) findViewById(R.id.isShowHoverCheckbox);
		isShowHoverCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				TherapyMainActivity.canvasView.setShowHover(arg1);
				TherapyMainActivity.canvasView.invalidate();
			}
		});
		
		new RecordLoader().execute();
		
		while(resultList == null);
		
		if(resultList != null) {
			initialSpinner();
			
			Button replayTrackBtn = (Button)findViewById(R.id.replayTrackBtn);
			replayTrackBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((DrawCanvasView)findViewById(R.id.canvasView)).setLimit(0);
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
		int targetSpeed = REPLAY_SPEED + (int) (REPLAY_SPEED * 299 * (1 - percentage));
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

	public static ArrayList<AnalysisMessage> loadRecord() {
		try {
			//FileInputStream input = new FileInputStream(Resources.getSystem().getString(R.string.str_analysis_json_location));
			FileInputStream input = new FileInputStream("/sdcard/Android/data/com.example.playpalgame/analysis.json");
			
			JsonReader reader = new JsonReader(new InputStreamReader(input,
					"UTF-8"));
			ArrayList<AnalysisMessage> returnList = readMessagesArray(reader);
			reader.close();
			return returnList;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void initialSpinner() {
		String[] nameStrArr = getAllNames(resultList);
		connectSource(this, playerSpinner, nameStrArr);

		playerSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {
						targetPlayerList = getTargetPlayerList(adapterView.getSelectedItem().toString(), resultList);
						String[] stageStrArr = getAllStages(targetPlayerList);
						connectSource(TherapyMainActivity.this, stageSpinner, stageStrArr);
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
						connectSource(TherapyMainActivity.this, recordSpinner, recordStrArr);
						
						setSrcBackground(adapterView.getSelectedItem().toString());
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
						if(replayTimer != null)
							replayTimer.cancel();
						if(replayTimerTask != null)
							replayTimerTask.cancel();
						
						canvasView.setLimit(-1);
						
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
	
	public void setSrcBackground(String curStage) {
		for(SimpleEntry pair : srcBackgroundList) {
			if(pair.getKey().equals(curStage)) {
				StageBackgroundInfo info = (StageBackgroundInfo) pair.getValue();
				srcBackgroundView.setImageResource(info.drawableId);
				RelativeLayout.LayoutParams params = (LayoutParams) srcBackgroundView.getLayoutParams();
				params.setMargins(info.marginLeft, info.marginTop, info.marginRight, info.marginBottom);
				srcBackgroundView.setLayoutParams(params);
				break;
			}
		}
	}
	
	public static void connectSource(Context context, Spinner spinner, String[] srcStrArr) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_dropdown_item, srcStrArr);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	public static  String[] getAllNames(ArrayList<AnalysisMessage> targetList) {
		ArrayList<String> nameList = new ArrayList<String>();

		for (AnalysisMessage msg : targetList) {
			if (!nameList.contains(msg.userName))
				nameList.add(msg.userName);
		}
		return (String[]) nameList.toArray(new String[nameList.size()]);
	}
	
	public static String[] getAllStages(ArrayList<AnalysisMessage> targetList) {
		ArrayList<String> stageList = new ArrayList<String>();

		for (AnalysisMessage msg : targetList) {
			if (!stageList.contains(msg.stage))
				stageList.add(msg.stage);
		}
		return (String[]) stageList.toArray(new String[stageList.size()]);
	}

	public static String[] getAllRecords(ArrayList<AnalysisMessage> targetList) {
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
	
	public static ArrayList<AnalysisMessage> getTargetStageList(String targetName, ArrayList<AnalysisMessage> targetList) {
		ArrayList<AnalysisMessage> returnList = new ArrayList<AnalysisMessage>();
		for(AnalysisMessage msg : targetList) {
			if(msg.stage.equals(targetName))
				returnList.add(msg);
		}
		return returnList;
	}
	
	public static AnalysisMessage getTargetRecord(String targetName, ArrayList<AnalysisMessage> targetList) {
		for(AnalysisMessage msg : targetList) {
			if(msg.date.equals(targetName))
				return msg;
		}
		return null;
	}

	public static ArrayList<AnalysisMessage> readMessagesArray(JsonReader reader)
			throws IOException {
		ArrayList<AnalysisMessage> messages = new ArrayList<AnalysisMessage>();

		reader.beginArray();
		while (reader.hasNext()) 
			messages.add(readMessage(reader));
		reader.endArray();
		return messages;
	}

	public static AnalysisMessage readMessage(JsonReader reader) throws IOException {
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
					int status = 0;
					reader.beginArray();
					int counter = 0;
					while (reader.hasNext()) {
						if (counter == 0)
							x = reader.nextInt();
						else if (counter == 1)
							y = reader.nextInt();
						else if (counter == 2) 
							status = reader.nextInt();
						else
							Log.d("EndTest", "WTF");
						counter++;
					}
					msg.points.add(new RecordEntry(new Point(x, y), status));
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
	public ArrayList<RecordEntry> points = new ArrayList<RecordEntry>();

	public AnalysisMessage() {
	}
}

class DrawCanvasView extends View {
	private Paint p;
	private ArrayList<RecordEntry> pointList = new ArrayList<RecordEntry>();
	private int pointLimit = 0;
	private boolean isLineUp = false;
	private boolean isShowHover = false;
	private int preX = -1;
	private int preY = -1;
	private int preStatus = 0;
	
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
    	
    	p = new Paint();
		p.setColor(Color.WHITE);
		p.setAntiAlias(true);
    }
    
    public void setLineUp(boolean value) {
    	isLineUp = value;
    }
    
    public void setShowHover(boolean value) {
    	isShowHover = value;
    }
    
    public void updatePointList(ArrayList<RecordEntry> newPointList) {
    	pointList.clear();
    	pointList = (ArrayList<RecordEntry>) newPointList.clone();
    }
    
    public void addLimit() {
    	pointLimit++;
    }
    
    public void setLimit(int value) {
    	pointLimit = value;
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
		
		int pointCounter = 0;
		
		preX = -1;
		preY = -1;
		preStatus = 0;
		
		for(RecordEntry pnt: pointList) {
			if(pointLimit >= 0 && pointCounter >= pointLimit)
				break;
			pointCounter++;
			if(pnt.state == RecordEntry.STATE_HOVER_START
			|| pnt.state == RecordEntry.STATE_HOVER_MOVE
			|| pnt.state == RecordEntry.STATE_HOVER_END) {
				if(!isShowHover)
					continue;
				p.setColor(Color.WHITE);
			}
			else
				p.setColor(Color.RED);
			int newX = pnt.point.x * 8/10;
			int newY = pnt.point.y * 8/10;
			canvas.drawCircle(newX, newY, 5, p);
			if(isLineUp) {
				if(preX >= 0 && preStatus != RecordEntry.STATE_HOVER_END && preStatus != RecordEntry.STATE_TOUCH_END)
					canvas.drawLine(preX, preY, newX, newY, p);
				preX = newX;
				preY = newY;
				preStatus = pnt.state;
			}
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

class StageBackgroundInfo {
	public int drawableId;
	public int marginLeft;
	public int marginTop;
	public int marginRight;
	public int marginBottom;
	
	public StageBackgroundInfo(int d, int m1, int m2, int m3, int m4) {
		drawableId = d;
		marginLeft = m1;
		marginTop = m2;
		marginRight = m3;
		marginBottom = m4;
	}
};


class RecordLoader extends AsyncTask<Void, Integer, Boolean> {
	@Override
	protected Boolean doInBackground(Void... arg0) {
		TherapyMainActivity.resultList = TherapyMainActivity.loadRecord();
		return true;
	}
	
	@Override  
    protected void onPostExecute(Boolean result) {
		TherapyMainActivity.loadingBar.setVisibility(ProgressBar.GONE);
	}
};