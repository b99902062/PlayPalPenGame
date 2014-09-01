package com.example.playpalpengame;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

public class BeginActivity extends Activity {
	private static boolean isTheFirstRecord = false;
	private RelativeLayout rightPanel = null;
	private boolean isRightPanelOn = false;
	private static boolean isMale = true;
	private static int userAge = 0;
	private static Bitmap picData;
	private ListView userListView;
	private String tmpUserName = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_begin);

		isTheFirstRecord = false;
		
		userListView = (ListView)findViewById(R.id.userListView);
		
		((EditText)findViewById(R.id.userNameText)).setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus){
		        if(v.getId() == R.id.userNameText && !hasFocus) {
		            InputMethodManager imm =  (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		        }
		    }
		});
		
		userListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				HashMap<String, Object> obj = (HashMap<String, Object>)userListView.getItemAtPosition(position);
				tmpUserName = obj.get("itemUserName").toString();
				Log.d("EndTest", obj.get("itemUserName").toString());
				
				String headFileName = "/sdcard/Android/data/com.example.playpalgame/" + tmpUserName + ".png";
				File f = new File(headFileName);
				if(f.exists()) {
					Bitmap bMap = BitmapFactory.decodeFile(headFileName);
					((ImageView)findViewById(R.id.loginHeadView)).setImageBitmap(bMap);;
				}
				else {
					((ImageView)findViewById(R.id.loginHeadView)).setImageResource(R.drawable.login_head);
				}
			}
		});
		
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
			
			ArrayList<HashMap<String, Object>> item = new ArrayList<HashMap<String, Object>>();
			for(int i=0; i<resultList.size(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("itemHeadImage", "/sdcard/Android/data/com.example.playpalgame/" + resultList.get(i).userName + ".png");
				map.put("itemUserName", resultList.get(i).userName);
				String genderStr = resultList.get(i).isMale ? "Male" : "Female";
				map.put("itemMoreInfo", genderStr + " ( " + resultList.get(i).age + " )");
				item.add(map);
			}

			CustomAdapter customAdapter = new CustomAdapter( this, item, R.layout.user_list_layout,
					new String[] {"itemHeadImage","itemUserName", "itemMoreInfo"},
					new int[] {R.id.itemHeadImage, R.id.itemUserName,R.id.itemMoreInfo}
			);
			userListView.setAdapter(customAdapter);
		}
		else {
			Log.d("EndTest", "ResultList in BeginActivity is null.");
		}
		
		((ImageView)findViewById(R.id.takePicBtn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intentCamera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			    startActivityForResult(intentCamera, 0);
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		BackgroundMusicHandler.recyle();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		BackgroundMusicHandler.initMusic(this);
		BackgroundMusicHandler.setMusicSt(true);
	}
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      ImageView iv = (ImageView)findViewById(R.id.loginHeadView);
	      if (resultCode == RESULT_OK) {
	    	  Bundle extras = data.getExtras();
	          Bitmap bmp = (Bitmap) extras.get("data");
	          picData = bmp;
	          iv.setImageBitmap(bmp);
	      }
	      super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void setLoginBtn() {
		ImageView loginBtn = (ImageView)findViewById(R.id.loginLoginBtn);
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isRightPanelOn = true;
				rightPanel.setVisibility(View.VISIBLE);
				
				findViewById(R.id.userListView).setVisibility(View.VISIBLE);
				findViewById(R.id.userNameText).setVisibility(View.INVISIBLE);
				findViewById(R.id.genderLabel).setVisibility(View.INVISIBLE);
				findViewById(R.id.genderRadioGroup).setVisibility(View.INVISIBLE);
				findViewById(R.id.ageLabel).setVisibility(View.INVISIBLE);
				findViewById(R.id.ageLabel_1).setVisibility(View.INVISIBLE);
				findViewById(R.id.agePicker).setVisibility(View.INVISIBLE);
				findViewById(R.id.takePicBtn).setVisibility(View.INVISIBLE);
				
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
						if(tmpUserName == null || tmpUserName.equals(""))
							return;
						Intent newAct = new Intent();
						newAct.setClass( BeginActivity.this, MainActivity.class );
						newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle bundle = new Bundle();
						bundle.putString("userName", tmpUserName);
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
				findViewById(R.id.userListView).setVisibility(View.INVISIBLE);
				findViewById(R.id.userNameText).setVisibility(View.VISIBLE);
				findViewById(R.id.takePicBtn).setVisibility(View.VISIBLE);
				
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

			// Write to 
			File f = new File("/sdcard/Android/data/com.example.playpalgame/" + newName + ".png");
			f.createNewFile();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			picData.compress(CompressFormat.PNG, 0, bos);
			byte[] bitmapdata = bos.toByteArray();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.flush();
			fos.close();
	
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
