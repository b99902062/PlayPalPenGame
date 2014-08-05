package com.example.playpalpengame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class BeginActivity extends Activity {
	private static boolean isTheFirstRecord = false;
	private RelativeLayout rightPanel = null;
	private boolean isRightPanelOn = false;
	private static boolean isMale = true;
	private static int userAge = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_begin);

		isTheFirstRecord = false;
		
		ImageView therapyIcon = (ImageView)findViewById(R.id.therapyIcon);
		therapyIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent newAct = new Intent();
				newAct.setClass( BeginActivity.this, TherapyMainActivity.class );
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivityForResult(newAct ,0);
	            BeginActivity.this.finish();
			}
		});
		
		rightPanel = (RelativeLayout)findViewById(R.id.rightPanel);
		
		setLoginBtn();
		setRegisterBtn();
		setLayoutListener();
		((RadioGroup)findViewById(R.id.genderRadioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.genderRadioButton1)
					isMale = true;
				else
					isMale = false;
			}
		});

		ArrayList<RecordMessage> resultList = new ArrayList<RecordMessage>();
		resultList = MainActivity.loadRecord(); 
		if(resultList != null) {
			String[] nameList = MainActivity.getAllNames(resultList);
			if(nameList.length == 0)
				isTheFirstRecord = true;
			Spinner nameSpinner = (Spinner)findViewById(R.id.userNameSpinner);
			TherapyMainActivity.connectSource(this, nameSpinner, nameList);
		}
		else {
			Log.d("EndTest", "ResultList in BeginActivity is null.");
		}
	}
	
	private void setLoginBtn() {
		ImageView loginBtn = (ImageView)findViewById(R.id.loginLoginBtn);
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isRightPanelOn = true;
				rightPanel.setVisibility(View.VISIBLE);
				
				findViewById(R.id.userNameSpinner).setVisibility(View.VISIBLE);
				findViewById(R.id.userNameText).setVisibility(View.INVISIBLE);
				findViewById(R.id.genderLabel).setVisibility(View.INVISIBLE);
				findViewById(R.id.genderRadioGroup).setVisibility(View.INVISIBLE);
				findViewById(R.id.ageLabel).setVisibility(View.INVISIBLE);
				findViewById(R.id.ageLabel_1).setVisibility(View.INVISIBLE);
				findViewById(R.id.agePicker).setVisibility(View.INVISIBLE);
				
				if(!isRightPanelOn) {
					Animation rightPanelAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTRIGHT_TO_CUR, 1000);
					rightPanel.clearAnimation();
					rightPanel.setAnimation(rightPanelAnim);
					rightPanelAnim.startNow();
				}
				
				ImageView submitBtn = (ImageView)findViewById(R.id.submitBtn);
				submitBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						Intent newAct = new Intent();
						newAct.setClass( BeginActivity.this, MainActivity.class );
						newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle bundle = new Bundle();
						String userName = ((Spinner)findViewById(R.id.userNameSpinner)).getSelectedItem().toString();
						bundle.putString("userName", userName);
			            newAct.putExtras(bundle);
			            startActivityForResult(newAct ,0);
			            BeginActivity.this.finish();
					}
				});
			}
		});
	}
	
	private void setRegisterBtn() {
		ImageView registerBtn = (ImageView)findViewById(R.id.loginRegisterBtn);
		registerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isRightPanelOn = true;
				rightPanel.setVisibility(View.VISIBLE);
				findViewById(R.id.userNameSpinner).setVisibility(View.INVISIBLE);
				findViewById(R.id.userNameText).setVisibility(View.VISIBLE);
				
				NumberPicker agePicker = (NumberPicker)findViewById(R.id.agePicker);
				agePicker.setMaxValue(18);
				agePicker.setMinValue(0);
				agePicker.setValue(3);
				
				findViewById(R.id.genderLabel).setVisibility(View.VISIBLE);
				findViewById(R.id.genderRadioGroup).setVisibility(View.VISIBLE);
				findViewById(R.id.ageLabel).setVisibility(View.VISIBLE);
				findViewById(R.id.ageLabel_1).setVisibility(View.VISIBLE);
				findViewById(R.id.agePicker).setVisibility(View.VISIBLE);
				
				if(!isRightPanelOn) {
					Animation rightPanelAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTRIGHT_TO_CUR, 1000);
					rightPanel.clearAnimation();
					rightPanel.setAnimation(rightPanelAnim);
					rightPanelAnim.startNow();
				}
				
				ImageView submitBtn = (ImageView)findViewById(R.id.submitBtn);
				submitBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						// Add gender and something~~~
						String userName = ((EditText)findViewById(R.id.userNameText)).getText().toString();
						if(userName.equals(""))
							return;
						
						userAge = ((NumberPicker)findViewById(R.id.agePicker)).getValue();
						BeginActivity.createNewPlayerData(userName);
						
						Intent newAct = new Intent();
						newAct.setClass( BeginActivity.this, MainActivity.class );
						newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle bundle = new Bundle();
						bundle.putString("userName", userName);
			            newAct.putExtras(bundle);
			            startActivityForResult(newAct ,0);
			            BeginActivity.this.finish();
					}
				});
			}
		});
	}
	
	private void setLayoutListener() {
		findViewById(R.id.beginRelativeLayout).setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					Log.d("EndTest", "OnTouch");
					findViewById(R.id.userNameText).clearFocus();
					if(!isRightPanelOn || event.getX() > 1560)
						return true;
					Log.d("EndTest", "Play GONE anim");
					Animation rightPanelAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT, 500);
					rightPanelAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							isRightPanelOn = false;
							rightPanel.setVisibility(View.INVISIBLE);
						}
	
						@Override
						public void onAnimationRepeat(Animation arg0) {
						}
	
						@Override
						public void onAnimationStart(Animation arg0) {
						}
					});
					rightPanel.clearAnimation();
					rightPanel.setAnimation(rightPanelAnim);
					rightPanelAnim.startNow();
				}
				return true;
			}
			
		});
	}
	
	public static void createNewPlayerData(String newName) {
		RandomAccessFile analysisFile;
		//String filePath = Resources.getSystem().getString(R.string.str_record_json_location);
		String filePath = "/sdcard/Android/data/com.example.playpalgame/record.json";
		
		try {
			File file = new File(filePath);
			if(!file.exists()){
				isTheFirstRecord = true;
				file.createNewFile();
				FileWriter fWriter = new FileWriter(file);
				fWriter.write("[");
	        	fWriter.flush();
	        	fWriter.close();
			}
	
			analysisFile = new RandomAccessFile(filePath, "rw");
			analysisFile.seek(analysisFile.length()-1); 
				 
			JSONObject curRecord = new JSONObject();
			
			curRecord.put("gameBadge4", 0);
			curRecord.put("gameHighScore4", 0);
			curRecord.put("gameWinCount4", 0);
			curRecord.put("gameBadge3", 0);
			curRecord.put("gameHighScore3", 0);
			curRecord.put("gameWinCount3", 0);
			curRecord.put("gameBadge2", 0);
			curRecord.put("gameHighScore2", 0);
			curRecord.put("gameWinCount2", 0);
			curRecord.put("gameBadge1", 0);
			curRecord.put("gameHighScore1", 0);
			curRecord.put("gameWinCount1", 0);
			curRecord.put("isMale", isMale);
			curRecord.put("age", userAge);
			curRecord.put("name", newName);
			
			String result = (isTheFirstRecord)? curRecord.toString()+"]" : ","+curRecord.toString()+"]";
			
			Log.d("EndTest", result);
			
			analysisFile.write(result.getBytes());
			analysisFile.close();	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
