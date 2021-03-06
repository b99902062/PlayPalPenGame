package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class Game1Activity extends Activity {
	protected final int step1MidProgressCount = 5;
	protected final int step1TotalProgressCount = 10;
	protected final int step2TotalProgressCount = 	25;
	protected final int step3TotalProgressCount = 35;
	
	private final int step1TotalTime = 1200;
	private final int step2TotalTime = 2700;
	private final int step3TotalTime = 900;
	
	private final int foodOffsetX = 380;
	private final int foodOffsetY = 380;
	
	protected final int originBeginCenterX = 500;
	protected final int originBeginCenterY = 500;
	protected final int originEndCenterX = 500;
	protected final int originEndCenterY = 1200;
	protected final int boxSize = 100;
	protected final int lineInterval = 250;
	
	protected final int potLeftTopX = 1100;
	protected final int potLeftTopY = 700;
	protected final int potBoxSize = 120;
	protected final int potBoxInterval = 350;
	
	protected boolean isFoodCanTouch;
	protected boolean isDoneDropFood;
	protected int progressCount;
	private int score = 0;
	private int timeReminderStat = 0;
	private boolean isFirstAlarm = true;
	
	protected Game1Activity self;
	
	private MediaPlayer fireMP = null;
	private MediaPlayer tickMP = null;
	
	private String mUserName = null;
	private int mBadges = 0;
	private int mHighScore = 0;
	private int mWinCount = 0;
	protected ArrayList<View> foodInPot;

	protected ImageView currentFoodView;
	
	protected ImageView carrotView;
	protected ImageView cucumberView;
	protected ImageView potView;
	protected ImageView fireView;
	protected ImageView knifeView;
	protected RelativeLayout board2Layout;
	protected DrawableRelativeLayout game1RelativeLayout;
	
	protected TextView progressCountText;

	protected FramesSequenceAnimation potDropAnim;
	protected AnimationDrawable potStirAnim;
	protected FramesSequenceAnimation fireAnim;
	
	protected Point[] carrotCutBeginPointArray = {new Point(231, 406), new Point(465, 340), new Point(740, 311), new Point(979, 300), new Point(1195, 318)};
	protected Point[] carrotCutEndPointArray = {new Point(270, 715), new Point(650, 747), new Point(952, 759), new Point(1175, 736), new Point(1354, 677)};
	protected Point[] cucumberCutBeginPointArray = {new Point(347, 309), new Point(561, 334), new Point(795, 386), new Point(1077, 404), new Point(1429, 288)};
	protected Point[] cucumberCutEndPointArray = {new Point(154, 615), new Point(350, 700), new Point(581, 781), new Point(886, 831), new Point(1204, 845)};
	
	protected int[] foodResArray = { R.drawable.game1_carrot_1,
			R.drawable.game1_carrot_2, R.drawable.game1_carrot_3,
			R.drawable.game1_carrot_4, R.drawable.game1_carrot_5,
			R.drawable.game1_carrot_6,
			R.drawable.game1_cucumber_2, R.drawable.game1_cucumber_3,
			R.drawable.game1_cucumber_4, R.drawable.game1_cucumber_5,
			R.drawable.game1_cucumber_6 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_game1);
		PlayPalUtility.setDebugMode(false);
		
		Bundle bundle = getIntent().getExtras();
		mUserName = bundle.getString("userName");
		mBadges = bundle.getInt("GameBadges");
		mHighScore = bundle.getInt("GameHighScore");
		mWinCount = bundle.getInt("GameWinCount");
		
		doInitial();
		
		PlayPalUtility.registerProgressBar((ProgressBar)findViewById(R.id.progressBarRed), (ImageView)findViewById(R.id.progressMark), (ImageView)findViewById(R.id.progressBar), new Callable<Integer>() {
			public Integer call() {
				clearAll();
				
				Intent newAct = new Intent();
				newAct.setClass(Game1Activity.this, AnimationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", 1);
				bundle.putBoolean("isWin", false);
				bundle.putString("userName", mUserName);
				bundle.putInt("GameBadges", mBadges);
				bundle.putInt("GameHighScore", mHighScore);
				bundle.putInt("GameWinCount", mWinCount);
				bundle.putInt("NewScore", -1);
	            newAct.putExtras(bundle);
	            
	            findViewById(R.id.failFeedbackView).setVisibility(View.VISIBLE);
	            
	            Timer timer = new Timer(true);
				timer.schedule(new WaitTimerTask(self, newAct), 2000);
				return 0;
			}
		}, new Callable<Integer>() {
			public Integer call() {
				return doTimeReminder();
			}
		});
		isFirstAlarm = true;
		findViewById(R.id.timeReminder).setVisibility(View.INVISIBLE);
		turnOffTick();
		PlayPalUtility.initialProgressBar(step1TotalTime, PlayPalUtility.TIME_MODE);
		
		game1RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
    					PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_START);
    					PenRecorder.forceRecord();
                    	knifeView.setVisibility(ImageView.VISIBLE);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_MOVE);
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX(), (int)event.getY() , 0, 0);
                    	knifeView.setLayoutParams(params);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_END);
                    	PenRecorder.forceRecord();
                    	knifeView.setVisibility(ImageView.INVISIBLE);
                        break;
                }
                return true;
            }
        });

		PlayPalUtility.registerLineGesture(game1RelativeLayout, this, new Callable<Integer>() {
			public Integer call() {
				return handleLineAction();
			}
		});
		PlayPalUtility.registerFailFeedback((ImageView)findViewById(R.id.failFeedbackView));
		PlayPalUtility.setHoverTarget(true, knifeView);
		PlayPalUtility.setLineGesture(true);
		Point beginPnt = new Point(foodOffsetX + carrotCutBeginPointArray[progressCount].x, foodOffsetY + carrotCutBeginPointArray[progressCount].y);
		Point endPnt = new Point(foodOffsetX + carrotCutEndPointArray[progressCount].x, foodOffsetY + carrotCutEndPointArray[progressCount].y);
		PlayPalUtility.initialLineGestureParams(false, false, boxSize, beginPnt, endPnt);

		PlayPalUtility.initDrawView(game1RelativeLayout, this, (DrawView)findViewById(R.id.drawLineView));
		DrawGestureLine();
		PenRecorder.registerRecorder(game1RelativeLayout, this, mUserName, "1-1");
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		writeToSettings();
		
		clearAll();
		BackgroundMusicHandler.recyle();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		BitmapHandler.recycleBitmaps();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		BackgroundMusicHandler.initMusic(this);
		BackgroundMusicHandler.setMusicSt(true);
		
		setBackFromSettings();
		System.gc();
	}
	
	@Override
	public void onBackPressed() {
	}
	
	private void writeToSettings() {
		SharedPreferences settings = getSharedPreferences("PLAY_PAL_TMP_INFO", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("CUR_PROGRESS", progressCount);
		editor.putInt("CUR_TIMEBAR_MAX", PlayPalUtility.getProgressBarMaxVal());
		editor.putInt("CUR_TIMEBAR_VAL", PlayPalUtility.getProgressBarCurVal());
		editor.putInt("CUR_SCORE", score);
		editor.commit();
	}
	
	private void setBackFromSettings() {
		SharedPreferences settings = getSharedPreferences("PLAY_PAL_TMP_INFO", 0);
		progressCount = settings.getInt("CUR_PROGRESS", -1);
		if(progressCount < 0) {
			progressCount = 0;
			return;
		}
		else {
			isFirstAlarm = true;
			findViewById(R.id.timeReminder).setVisibility(View.INVISIBLE);
			PlayPalUtility.initialProgressBar(settings.getInt("CUR_TIMEBAR_MAX", 0), PlayPalUtility.TIME_MODE);
			PlayPalUtility.setProgressBarCurVal(settings.getInt("CUR_TIMEBAR_VAL", 0));
			score = settings.getInt("CUR_SCORE", 0);
			setBackProgressCountPart();
			
			settings.edit().clear().commit();
		}
	}
	
	private void setBackProgressCountPart() {
		if(progressCount < step1TotalProgressCount) {
			//Carrot
			PlayPalUtility.registerLineGesture(findViewById(R.id.game1RelativeLayout), this, new Callable<Integer>() {
				public Integer call() {
					return handleLineAction();
				}
			});
			
			PlayPalUtility.registerFailFeedback((ImageView)findViewById(R.id.failFeedbackView));
			PlayPalUtility.setHoverTarget(true, (ImageView)findViewById(R.id.knifeView));
			PlayPalUtility.setLineGesture(true);
			Point beginPnt, endPnt;
			if(progressCount < step1MidProgressCount) {
				beginPnt = new Point(foodOffsetX + carrotCutBeginPointArray[progressCount].x, foodOffsetY + carrotCutBeginPointArray[progressCount].y);
				endPnt = new Point(foodOffsetX + carrotCutEndPointArray[progressCount].x, foodOffsetY + carrotCutEndPointArray[progressCount].y);
			}
			else {
				beginPnt = new Point(foodOffsetX + cucumberCutBeginPointArray[progressCount - step1MidProgressCount].x, foodOffsetY + cucumberCutBeginPointArray[progressCount - step1MidProgressCount].y);
				endPnt = new Point(foodOffsetX + cucumberCutEndPointArray[progressCount - step1MidProgressCount].x, foodOffsetY + cucumberCutEndPointArray[progressCount - step1MidProgressCount].y);
			}
			PlayPalUtility.initialLineGestureParams(false, false, boxSize, beginPnt, endPnt);

			PlayPalUtility.initDrawView((RelativeLayout) findViewById(R.id.game1RelativeLayout), this, (DrawView)findViewById(R.id.drawLineView));
			DrawGestureLine();
		}
		else if(progressCount < step2TotalProgressCount) {
			//Fragment
		}
		else {
			//Stir
			PlayPalUtility.registerLineGesture((RelativeLayout) findViewById(R.id.game1RelativeLayout), this, new Callable<Integer>() {
				public Integer call() {
					return doPotStir();
				}
			});
			PlayPalUtility.setLineGesture(true);
			PlayPalUtility.initialLineGestureParams(true, false, potBoxSize, new Point(potLeftTopX, potLeftTopY), new Point(potLeftTopX + potBoxInterval, potLeftTopY), new Point(potLeftTopX + potBoxInterval, potLeftTopY + potBoxInterval), new Point(potLeftTopX, potLeftTopY + potBoxInterval));
		}
	}
	
	private void clearAll() {
		if(fireMP != null) {
			if(fireMP.isPlaying())
				fireMP.stop();
			fireMP.release();
			fireMP = null;
		}
		turnOffTick();
		
		score += PlayPalUtility.killTimeBar();
		PlayPalUtility.setLineGesture(false);
		PlayPalUtility.unregisterLineGesture(game1RelativeLayout);
		PlayPalUtility.unregisterFailFeedback();
		PlayPalUtility.clearGestureSets();
		PlayPalUtility.clearDrawView();
	}
	
	private void turnOffTick() {
		if(tickMP != null) {
	    	if(tickMP.isPlaying())
	    		tickMP.stop();
	    	tickMP.release();
	    	tickMP = null;
	    }
	}
	
	private void doInitial() {
		self = this;
		
		isFoodCanTouch = true;
		isDoneDropFood = false;
		progressCount = 0;
		foodInPot = new ArrayList<View>();

		progressCountText = (TextView) findViewById(R.id.testProgressCount);
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		
		potView = (ImageView) findViewById(R.id.potView);
		board2Layout = (RelativeLayout) findViewById(R.id.board2RelativeLayout);

		potDropAnim = AnimationsContainer.getInstance().createGame1PotDropAnim(potView);

		fireView = (ImageView) findViewById(R.id.fireView);
		fireAnim = AnimationsContainer.getInstance().createGame1FireAnim(fireView);

		setHomeListener(findViewById(R.id.homeBtn));

		knifeView = (ImageView) findViewById(R.id.knifeView);
		
		carrotView = (ImageView) findViewById(R.id.carrotView);
		setFoodListener(carrotView);
		
		cucumberView = (ImageView) findViewById(R.id.cucumberView);
		
		game1RelativeLayout = (DrawableRelativeLayout) findViewById(R.id.game1RelativeLayout);
	}

	protected void setHomeListener(View targetView) {
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				clearAll();
				BackgroundMusicHandler.setCanRecycle(false);
				
				Intent newAct = new Intent();
				newAct.setClass(Game1Activity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userName", mUserName);
	            newAct.putExtras(bundle);
				startActivityForResult(newAct, 0);
				Game1Activity.this.finish();
				return true;
			}
		});
	}

	protected void setFoodListener(View targetView) {
		currentFoodView = (ImageView) targetView;
	}

	protected void RemoveFromBoard(View view) {
		progressCount++;
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_SPLIT_POT, this);
		
		view.setVisibility(ImageView.GONE);
		potDropAnim.start();

		if (progressCount == step2TotalProgressCount) {
			score += PlayPalUtility.killTimeBar();
			PenRecorder.outputJSON();
			Animation boardAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
			boardAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {
					board2Layout.clearAnimation();
					board2Layout.setVisibility(ImageView.GONE);
					
					knifeView.setImageBitmap(BitmapHandler.getLocalBitmap(self, R.drawable.game1_ladle));

					isFirstAlarm = true;
					findViewById(R.id.timeReminder).setVisibility(View.INVISIBLE);
					turnOffTick();
					PlayPalUtility.initialProgressBar(step3TotalTime, PlayPalUtility.TIME_MODE);
					
					fireView.setVisibility(ImageView.VISIBLE);
					fireAnim.start();

					potView.setImageBitmap(BitmapHandler.getLocalBitmap(self, R.drawable.game1_pot_3));

					fireMP = PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_STIR_POT, self, true);
					
					//potView.setBackgroundResource(R.anim.pot_stir_animation);
					//potStirAnim = (AnimationDrawable) potView.getBackground();
					
					
					
					PlayPalUtility.registerLineGesture(game1RelativeLayout, self, new Callable<Integer>() {
						public Integer call() {
							return doPotStir();
						}
					});
					PlayPalUtility.setLineGesture(true);
					PlayPalUtility.initialLineGestureParams(true, false, potBoxSize, new Point(potLeftTopX, potLeftTopY), new Point(potLeftTopX + potBoxInterval, potLeftTopY), new Point(potLeftTopX + potBoxInterval, potLeftTopY + potBoxInterval), new Point(potLeftTopX, potLeftTopY + potBoxInterval));
					
					ImageView helicalView = (ImageView)findViewById(R.id.helicalView);
					helicalView.setVisibility(ImageView.VISIBLE);
					
					PenRecorder.registerRecorder(game1RelativeLayout, Game1Activity.this, mUserName, "1-4");
					
					isDoneDropFood = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			board2Layout.setAnimation(boardAnim);
			boardAnim.startNow();
		}
	}
	
	protected void setFragmentListener(View targetView) {
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) { 
					PlayPalUtility.curEntry = new RecordEntry(
						new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_TOUCH_START);
					PenRecorder.forceRecord();
				}
				else if(event.getAction() == MotionEvent.ACTION_MOVE)
					PlayPalUtility.curEntry = new RecordEntry(
							new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_TOUCH_MOVE);
				else {
					PlayPalUtility.curEntry = new RecordEntry(
							new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_TOUCH_END);
					PenRecorder.forceRecord();
				}
					
				int minXBoardBound = 0;
				int maxXBoardBound = 1000;
				int minYBoardBound = 380;
				int maxYBoardBound = 1380;
				
				LayoutParams layoutParams = (LayoutParams) view
						.getLayoutParams();
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						int x_cord = (int) event.getRawX();
						int y_cord = (int) event.getRawY();
						
						x_cord -= 60;
						y_cord -= 120;
	
						if (x_cord > maxXBoardBound) {
							if(!foodInPot.contains(view)) {
								foodInPot.add(view);
								RemoveFromBoard(view);
							}
						}
						if (x_cord < minXBoardBound) 
							x_cord = minXBoardBound;
						if (y_cord > maxYBoardBound) 
							y_cord = maxYBoardBound;
						if (y_cord < minYBoardBound) 
							y_cord = minYBoardBound;
	
						layoutParams.setMargins(x_cord - minXBoardBound, y_cord - minYBoardBound, 0, 0);
						view.setLayoutParams(layoutParams);
						break;
					case MotionEvent.ACTION_UP:
						break;
					default:
						break;
				}
				return true;
			}
		});
	}
	
	protected Integer doTimeReminder() {
		if(isFirstAlarm) {
			PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_TIME_REMINDER, this, false);
			tickMP = PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_TIMER_TICK, this, true);
			isFirstAlarm = false;
		}
		findViewById(R.id.timeReminder).setVisibility(View.VISIBLE);
		if(timeReminderStat == 1) {
			((ImageView)findViewById(R.id.timeReminder)).setImageBitmap(BitmapHandler.getLocalBitmap(self, R.drawable.time_reminder_2));
			timeReminderStat = 0;
		}
		else {
			((ImageView)findViewById(R.id.timeReminder)).setImageBitmap(BitmapHandler.getLocalBitmap(self, R.drawable.time_reminder_1));
			timeReminderStat = 1;
		}
		return 0;
	}

	protected Integer doPotStir() {
		progressCount++;
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		//PlayPalUtility.doProgress();
		
		//potStirAnim.setVisible(true, true);
		//potStirAnim.start();
		FramesSequenceAnimation anim = AnimationsContainer.getInstance().createGame1PotStirAnim(potView);
		anim.start();
		

		if (progressCount == step3TotalProgressCount) {			
			clearAll();
			PenRecorder.outputJSON();
			
			Log.d("EndTest", String.format("Game1Score: %d", score));
			
			Intent newAct = new Intent();
			newAct.setClass(Game1Activity.this, AnimationActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("GameIndex", 1);
			bundle.putBoolean("isWin", true);
			bundle.putString("userName", mUserName);
			bundle.putInt("GameBadges", mBadges);
			bundle.putInt("GameHighScore", mHighScore);
			bundle.putInt("GameWinCount", mWinCount);
			bundle.putInt("NewScore", score);
            newAct.putExtras(bundle);
			startActivityForResult(newAct, 0);
			Game1Activity.this.finish();
		}
		
		return 1;
	}

	protected void randomSetupFood() {
		// for test
		int[] foodResId = {R.drawable.game1_food_1, R.drawable.game1_food_2};
		
		for (int i=0; i<5; i++) {
			for (int j=0; j<3; j++) {
				ImageView tmpFood = new ImageView(this);
				tmpFood.setImageBitmap(BitmapHandler.getLocalBitmap(self, foodResId[(int)(Math.random()*2)]));
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				int xMargin = (int)(Math.random()*151);
				int yMargin = (int)(Math.random()*201);
				params.setMargins(i * 150 + xMargin, j * 201 + yMargin, 0, 0);
				setFragmentListener(tmpFood);
				board2Layout.addView(tmpFood, params);
			}
		}
	}
	
	protected Integer handleLineAction() {
		if (!isFoodCanTouch)
			return 0;
		progressCount++;
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_CUT_FOOD, this);
		//PlayPalUtility.doProgress();
		
		Point beginPnt = new Point(0, 0);
		Point endPnt = new Point(0, 0);
		if(progressCount < step1MidProgressCount) {
			beginPnt = new Point(foodOffsetX + carrotCutBeginPointArray[progressCount].x, foodOffsetY + carrotCutBeginPointArray[progressCount].y);
			endPnt = new Point(foodOffsetX + carrotCutEndPointArray[progressCount].x, foodOffsetY + carrotCutEndPointArray[progressCount].y);
		}
		else if(progressCount < step1TotalProgressCount){
			beginPnt = new Point(foodOffsetX + cucumberCutBeginPointArray[progressCount - step1MidProgressCount].x, foodOffsetY + cucumberCutBeginPointArray[progressCount - step1MidProgressCount].y);
			endPnt = new Point(foodOffsetX + cucumberCutEndPointArray[progressCount - step1MidProgressCount].x, foodOffsetY + cucumberCutEndPointArray[progressCount - step1MidProgressCount].y);
		}
			
		PlayPalUtility.changeGestureParams(false, 0, beginPnt, endPnt);
		
		((ImageView) currentFoodView).setImageBitmap(BitmapHandler.getLocalBitmap(self, foodResArray[progressCount]));

		if (progressCount == step1MidProgressCount) {
			// Slide the cucumber
			isFoodCanTouch = false;
			PlayPalUtility.clearDrawView();
			PlayPalUtility.pauseProgress();
			
			PenRecorder.outputJSON();
			
			PlayPalUtility.setLineGesture(false);
			PlayPalUtility.clearGestureSets();
			Animation carrotAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
			carrotAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {
					isFoodCanTouch = true;
					carrotView.clearAnimation();
					carrotView.setVisibility(ImageView.GONE);
					
					Animation cucumberAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTLEFT_TO_CUR);
					cucumberView.setAnimation(cucumberAnim);
					cucumberView.setVisibility(ImageView.VISIBLE);
					setFoodListener(cucumberView);
					
					cucumberAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							PlayPalUtility.resumeProgress();
							PenRecorder.registerRecorder(game1RelativeLayout, Game1Activity.this, mUserName, "1-2");
							
							PlayPalUtility.setLineGesture(true);
							Point beginPnt = new Point(foodOffsetX + cucumberCutBeginPointArray[0].x, foodOffsetY + cucumberCutBeginPointArray[0].y);
							Point endPnt = new Point(foodOffsetX + cucumberCutEndPointArray[0].x, foodOffsetY + cucumberCutEndPointArray[0].y);
							PlayPalUtility.initialLineGestureParams(false, false, boxSize, beginPnt, endPnt);
							DrawGestureLine();
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}
					});
					
					cucumberAnim.startNow();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			carrotView.setAnimation(carrotAnim);
			carrotAnim.startNow();
		} else if (progressCount == step1TotalProgressCount) {
			isFoodCanTouch = false;
			PlayPalUtility.clearDrawView();
			score += PlayPalUtility.killTimeBar();
			
			PenRecorder.outputJSON();

			Animation cucumberAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
			Animation boardAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);

			ImageView boardView = (ImageView) findViewById(R.id.boardView);
			boardView.setAnimation(boardAnim);
			boardAnim.startNow();

			PlayPalUtility.setLineGesture(false);
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterLineGesture(game1RelativeLayout);

			cucumberView.setAnimation(cucumberAnim);
			cucumberAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {
					ImageView boardView = (ImageView) findViewById(R.id.boardView);
					boardView.clearAnimation();
					boardView.setVisibility(ImageView.GONE);
					cucumberView.clearAnimation();
					cucumberView.setVisibility(ImageView.GONE);

					potView.setVisibility(ImageView.VISIBLE);
					Animation fadeIn = new AlphaAnimation(0, 1);
					fadeIn.setInterpolator(new DecelerateInterpolator());
					fadeIn.setDuration(2000);
					potView.setAnimation(fadeIn);

					board2Layout.setVisibility(ImageView.VISIBLE);
					Animation board2Anim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTLEFT_TO_CUR);
					board2Anim.setAnimationListener(new AnimationListener()  {

						@Override
						public void onAnimationEnd(Animation arg0) {
							findViewById(R.id.timeReminder).setVisibility(View.INVISIBLE);
							turnOffTick();
							PlayPalUtility.initialProgressBar(step2TotalTime, PlayPalUtility.TIME_MODE);
							PenRecorder.registerRecorder(game1RelativeLayout, Game1Activity.this, mUserName, "1-3");
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}
						
					});
					board2Layout.setAnimation(board2Anim);
					randomSetupFood();

					board2Anim.startNow();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			cucumberAnim.startNow();
		} else {
			DrawGestureLine();
		}
		
		return 1;
	}
	
	private void DrawGestureLine() {
		Log.d("Draw", "Call DrawGestureLine()");
		PlayPalUtility.clearDrawView();
		Point pnt1 = PlayPalUtility.getPoint(0, 0);
		Point pnt2 = PlayPalUtility.getPoint(0, 1);
		PlayPalUtility.setStraightStroke(pnt1, pnt2);
	}
}
