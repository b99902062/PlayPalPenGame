package com.example.playpalpengame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

public class TherapyMainActivity extends Activity {
	private static final int REPLAY_SPEED = 32;
	public final static int[] speedArr = {1, 2, 4, 8, 16, 32};
	private Timer replayTimer;
	private static ReplayTimerTask replayTimerTask;
	public static DrawCanvasView canvasView;
	public static ArrayList<String> dateList = new ArrayList<String>();
	public static Spinner dateSpinner = null;
	public static Spinner playerSpinner = null;
	public static Spinner stageSpinner = null;
	public static Spinner recordSpinner = null;
	public static ProgressBar loadingBar = null;
	public static SeekBar timeBar = null;
	public static TextView progressText = null;
	public static SeekBar replaySpeedBar = null;
	public static TextView replaySpeedText = null;
	public static ImageView replayTrackBtn = null;
	public static TextView genderLabel = null;
	public static TextView ageLabel = null;
	public static ImageView headImage = null;
	public static Context context;
	
	public static boolean isPause = true;
	
	public static ArrayList<RecordMessage> userInfoList;
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
		context = this;

		canvasView = ((DrawCanvasView)findViewById(R.id.canvasView));
		srcBackgroundView = (ImageView)findViewById(R.id.srcBackgroundView);
		dateSpinner = (Spinner) findViewById(R.id.dateSpinner);
		playerSpinner = (Spinner) findViewById(R.id.playerSpinner);
		stageSpinner = (Spinner) findViewById(R.id.stageSpinner);
		recordSpinner = (Spinner) findViewById(R.id.recordSpinner);
		loadingBar = (ProgressBar) findViewById(R.id.loadingBar);
		timeBar = (SeekBar) findViewById(R.id.timeBar);
		progressText = (TextView) findViewById(R.id.progressText);
		replaySpeedBar = (SeekBar) findViewById(R.id.replaySpeedBar);
		replaySpeedText = (TextView) findViewById(R.id.speedLabel);
		replayTrackBtn = (ImageView)findViewById(R.id.replayTrackBtn);
		genderLabel = (TextView) findViewById(R.id.genderLabel);
		ageLabel = (TextView) findViewById(R.id.ageLabel);
		headImage = (ImageView) findViewById(R.id.headImage);
		
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
		
		replayTrackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//((DrawCanvasView)findViewById(R.id.canvasView)).setLimit(0);
				if(isPause) {
					if(timeBar.getProgress() == timeBar.getMax())
						timeBar.setProgress(0);
					replayTrackBtn.setImageResource(R.drawable.pause_btn);
					setReplayTimer();
					isPause = false;
				}
				else {
					if(replayTimer != null)
						replayTimer.cancel();
					if(replayTimerTask != null)
						replayTimerTask.cancel();
					replayTrackBtn.setImageResource(R.drawable.replay_btn);
					isPause = true;
				}
			}
		});
		
		replaySpeedBar.setOnSeekBarChangeListener(
				new OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar arg0, int arg1,
							boolean arg2) {
						float percentage = ((SeekBar)findViewById(R.id.replaySpeedBar)).getProgress() / 100.f;
						int targetSpeed = (int) (percentage * speedArr.length);
						targetSpeed = targetSpeed == speedArr.length ? targetSpeed-1 : targetSpeed;
						replaySpeedText.setText(String.format("Replay Speed: %dX", speedArr[targetSpeed]));
						if(!isPause)
							setReplayTimer();
					}

					@Override
					public void onStartTrackingTouch(SeekBar arg0) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar arg0) {
					}
				});

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		dateList.clear();
		loadDateList();
		if(dateList.size() > 0) 
			new RecordLoader(dateList.get(0)).execute();
		
		while(resultList == null);
		
		if(resultList != null) {
			String[] nameStrArr = getAllNames(resultList);
			connectSource(this, playerSpinner, nameStrArr);
			
			initialSpinner();	
		} else {
			Toast.makeText(this, R.string.str_cannot_find_analysis_file, Toast.LENGTH_LONG).show();
		}
	}
	
	public void loadDateList() {
		final File folder = new File("/sdcard/Android/data/com.example.playpalgame");
		for (final File fileEntry : folder.listFiles()) {
	        if (!fileEntry.isDirectory() && !fileEntry.getName().contains("record") && fileEntry.getName().endsWith(".json")) {
	        	dateList.add(fileEntry.getName().replace(".json", ""));
	        	Log.d("Threapy", fileEntry.getName());
	        }
	    }

		connectSource(TherapyMainActivity.this, dateSpinner, dateList.toArray(new String[dateList.size()]));
	}
	
	public void setReplayTimer() {
		float percentage = ((SeekBar)findViewById(R.id.replaySpeedBar)).getProgress() / 100.f;
		int targetSpeed = (int) (percentage * speedArr.length);
		targetSpeed = targetSpeed == speedArr.length ? targetSpeed-1 : targetSpeed;
		
		if(replayTimer != null)
			replayTimer.cancel();
		if(replayTimerTask != null)
			replayTimerTask.cancel();
		replayTimer = new Timer(true);
		replayTimerTask = new ReplayTimerTask();
		replayTimer.schedule(replayTimerTask, 0, 32 / speedArr[targetSpeed]);
	}
	
	public static Handler replayTimerHandler = new Handler() {
        public void handleMessage(Message msg) {
        	TherapyMainActivity.canvasView.addLimit();
        	TherapyMainActivity.canvasView.invalidate();
        	timeBar.setProgress(TherapyMainActivity.canvasView.getLimit());
        	int limit = TherapyMainActivity.canvasView.getLimit() > timeBar.getMax() ? timeBar.getMax() : TherapyMainActivity.canvasView.getLimit();  
        	progressText.setText(String.format("%d/%d", limit, timeBar.getMax()));
        	if(TherapyMainActivity.canvasView.getLimit() > TherapyMainActivity.canvasView.getPointsCount()) {
        		replayTimerTask.cancel();
        		replayTrackBtn.setImageResource(R.drawable.replay_btn);
        		isPause = true;
        	}
        }
    };

	public static ArrayList<AnalysisMessage> loadRecord(String targetDate) {
		try {
			if(resultList != null)
				resultList.clear();
			if(targetPlayerList != null)
				targetPlayerList.clear();
			if(targetStageList != null)
				targetStageList.clear();
			
			FileInputStream input = new FileInputStream("/sdcard/Android/data/com.example.playpalgame/"+ targetDate +".json");
			
			JsonReader reader = new JsonReader(new InputStreamReader(input,
					"UTF-8"));
			ArrayList<AnalysisMessage> returnList = readMessagesArray(reader, targetDate);
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
		dateSpinner
		.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView adapterView,
					View view, int position, long id) {
				loadingBar.setVisibility(View.VISIBLE);
				resultList = null;
				new RecordLoader(adapterView.getSelectedItem().toString()).execute();
				while(resultList == null);
				String[] nameStrArr = getAllNames(resultList);
				TherapyMainActivity.connectSource(context, playerSpinner, nameStrArr);
				loadingBar.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void onNothingSelected(AdapterView arg0) {
			}
		});
		
		playerSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {
						for(int i=0; i<userInfoList.size(); i++) {
							if(userInfoList.get(i).userName.equals(adapterView.getSelectedItem().toString())) {
								if(userInfoList.get(i).isMale)
									TherapyMainActivity.genderLabel.setText("Gender: Male");
								else
									TherapyMainActivity.genderLabel.setText("Gender: Female");
								
								Calendar c = Calendar.getInstance();
								int ageY = c.get(Calendar.YEAR) - userInfoList.get(i).age/100;
								int ageM = c.get(Calendar.MONTH) + 1 - userInfoList.get(i).age % 100;
								if(ageM < 0) {
									ageY--;
									ageM += 12;
								}
								TherapyMainActivity.ageLabel.setText("Age: " + ageY + "y " + ageM + "m");
								
								String headFileName = "/sdcard/Android/data/com.example.playpalgame/" + adapterView.getSelectedItem().toString() + ".png";
								File f = new File(headFileName);
								if(f.exists()) {
									Bitmap bMap = BitmapFactory.decodeFile(headFileName);
									TherapyMainActivity.headImage.setImageBitmap(bMap);;
								}
								else 
									((ImageView)findViewById(R.id.loginHeadView)).setImageResource(R.drawable.login_head);
								
							}
						}
						
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
						
						((TextView)findViewById(R.id.recordNameTitle)).setText(adapterView.getSelectedItem().toString());
						
						canvasView.setLimit(-1);
						
						targetRecord = getTargetRecord(adapterView.getSelectedItem().toString(), targetStageList);
						if(targetRecord == null)
							Toast.makeText(TherapyMainActivity.this, R.string.str_target_record_not_exist, Toast.LENGTH_SHORT).show();
						canvasView.updatePointList(targetRecord.points);
						timeBar.setMax(targetRecord.points.size());
						((TextView)findViewById(R.id.progressText)).setText(String.format("0/%d", timeBar.getMax()));
						((TextView)findViewById(R.id.totalTimeText)).setText(String.format("Total time: %.3f sec", (targetRecord.period)/1000.f));
						canvasView.invalidate();
					}
					
					@Override
					public void onNothingSelected(AdapterView arg0) {
					}
				});

		timeBar.setOnSeekBarChangeListener(
				new OnSeekBarChangeListener() {
					@Override
					public void onProgressChanged(SeekBar arg0, int arg1,
							boolean arg2) {
						TherapyMainActivity.canvasView.setLimit(arg1);
						int limit = TherapyMainActivity.canvasView.getLimit() > timeBar.getMax() ? timeBar.getMax() : TherapyMainActivity.canvasView.getLimit();  
			        	progressText.setText(String.format("%d/%d", limit, timeBar.getMax()));
			        	TherapyMainActivity.canvasView.invalidate();
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
					}
				}
		);
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
	
	public static void connectSource(Context context, Spinner spinner, String[] srcStrArr, int styleResId) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_dropdown_item, srcStrArr);
		adapter.setDropDownViewResource(styleResId);
		spinner.setAdapter(adapter);
	}
	
	public static void connectSource(Context context, Spinner spinner, String[] srcStrArr) {
		connectSource(context, spinner, srcStrArr, android.R.layout.simple_spinner_dropdown_item);
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
			if (!recordList.contains(msg.curTime))
				recordList.add(msg.curTime);
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
			if(msg.curTime.equals(targetName))
				return msg;
		}
		return null;
	}

	public static ArrayList<AnalysisMessage> readMessagesArray(JsonReader reader, String targetDate)
			throws IOException {
		ArrayList<AnalysisMessage> messages = new ArrayList<AnalysisMessage>();

		reader.beginArray();
		while (reader.hasNext()) 
			messages.add(readMessage(reader, targetDate));
		reader.endArray();
		return messages;
	}

	public static AnalysisMessage readMessage(JsonReader reader, String targetDate) throws IOException {
		AnalysisMessage msg = new AnalysisMessage();

		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("name"))
				msg.userName = reader.nextString();
			else if (name.equals("stage"))
				msg.stage = reader.nextString();
			else if (name.equals("curTime"))
				msg.curTime = reader.nextString();
			else if (name.equals("period"))
				msg.period = reader.nextLong();
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
	public String curTime = null;
	public long period = -1;
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
		p.setStrokeWidth(10);
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
			else if(pnt.state == RecordEntry.STATE_TOUCH_START
				||  pnt.state == RecordEntry.STATE_TOUCH_MOVE
				||  pnt.state == RecordEntry.STATE_TOUCH_END)
				p.setColor(Color.RED);
			else
				p.setColor(Color.GREEN);
			int newX = pnt.point.x * 8/10;
			int newY = pnt.point.y * 8/10;
			canvas.drawCircle(newX, newY, 5, p);
			if(isLineUp) {
				if(preX >= 0 && preStatus != RecordEntry.STATE_HOVER_END && preStatus != RecordEntry.STATE_TOUCH_END && preStatus != RecordEntry.STATE_HOVER_BTN_END)
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
	private String date;
	
	public RecordLoader(String targetDate) {
		date = targetDate;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		TherapyMainActivity.resultList = TherapyMainActivity.loadRecord(date);
		TherapyMainActivity.userInfoList = MainActivity.loadRecord();
		return true;
	}
	
	@Override  
    protected void onPostExecute(Boolean result) {
		TherapyMainActivity.loadingBar.setVisibility(ProgressBar.GONE);
	}
};