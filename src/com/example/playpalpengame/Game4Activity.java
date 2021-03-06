package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenHoverListener;


public class Game4Activity extends Activity {
	
	protected final int CREAM_BOX_SIZE = 50;
	protected final int CREAM_COLOR_NUM = 6;
	protected final int CREAM_SIZE = 100;
	protected final int CREAM_DIST = 10;
	protected final int CREAM_INIT_RATIO = 5;
	protected final int CREAM_MAX_RATIO = 8;
	protected boolean canTouchOven = false;
	protected boolean butterSqueezing = false;
	
	protected final int DOUGH_TIME  = 1080;
	protected final int COOKIE_TIME = 1500;
	protected final int CREAM_TIME  = 2700;
	
	protected final int DOUGH_PROGRESS_END  = 4;
	protected final int COOKIE_PROGRESS_END = DOUGH_PROGRESS_END + 8;
	protected final int CREAM_PROGRESS_END  = COOKIE_PROGRESS_END + 8;	
	
	protected final int COOKIE_NUM = 8;
	protected final int DOUGH_NUM = 5;
	
	private boolean isFirstCookie = true;
	
	public Point pointAddition(Point p1, Point p2){
		return new Point(p1.x+p2.x, p1.y+p2.y);
	}
	
	
	protected TextView  progressCountText;
	protected DrawableRelativeLayout game4RelativeLayout;
	protected SPenEventLibrary mSPenEventLibrary;
	
	protected ImageView doughView;
	protected ImageView laddleView;
	
	protected int boxSize;
	protected int curProgress;
	protected int curCookieType;
	
	protected String userName;
	protected static Game4Activity gameContext;
	
	private int mBadges = 0;
	private int mHighScore = 0;
	private int mWinCount = 0;
	private int score = 0;
	private int timeReminderStat = 0;
	private boolean isFirstAlarm = true;
	private MediaPlayer tickMP = null;
	
	protected Point centerPoint = new Point(1280,875);
	protected int dx = 275;
	protected int dy = 120;
	protected Point[][] doughPosArray = {
			{centerPoint, new Point(centerPoint.x + 1*dx, centerPoint.y -1*dy), new Point(centerPoint.x + 2*dx, centerPoint.y -2*dy), new Point(centerPoint.x + 3*dx, centerPoint.y -3*dy)},
			{centerPoint, new Point(centerPoint.x + 1*dx, centerPoint.y +1*dy), new Point(centerPoint.x + 2*dx, centerPoint.y +2*dy), new Point(centerPoint.x + 3*dx, centerPoint.y +3*dy)},
			{centerPoint, new Point(centerPoint.x - 1*dx, centerPoint.y +1*dy), new Point(centerPoint.x - 2*dx, centerPoint.y +2*dy), new Point(centerPoint.x - 3*dx, centerPoint.y +3*dy)},
			{centerPoint, new Point(centerPoint.x - 1*dx, centerPoint.y -1*dy), new Point(centerPoint.x - 2*dx, centerPoint.y -2*dy), new Point(centerPoint.x - 3*dx, centerPoint.y -3*dy)}
			};
	
	protected int[] doughViewArray = {
		R.id.Game4_dough,
		R.id.Game4_dough1,
		R.id.Game4_dough2,
		R.id.Game4_dough3,
		R.id.Game4_dough4};
	
	protected int[] doughResArray = {
		R.drawable.game4_dough1,
		R.drawable.game4_dough2_s,
		R.drawable.game4_dough2,
		R.drawable.game4_dough3_s,
		R.drawable.game4_dough3,
		R.drawable.game4_dough4_s,
		R.drawable.game4_dough4,			
	};
		
	protected Cookie[] cookieArray = new Cookie[8]; 

	protected int[] cuttingCookieArray = {
		R.drawable.game4_cookie1,	
		R.drawable.game4_cookie2,
		R.drawable.game4_cookie3
	};
	
	protected int[] cuttedCookieArray = {
		R.drawable.game4_cookie1_cutted,	
		R.drawable.game4_cookie2_cutted,
		R.drawable.game4_cookie3_cutted
	};
	
	protected int[] cookieResArray3 = {
			R.drawable.game4_cookie1_baked,	
			R.drawable.game4_cookie2_baked,
			R.drawable.game4_cookie3_baked
		}; 
	
	protected int[] cookieViewArray = {
		R.id.Game4_cookie0,	
		R.id.Game4_cookie1,
		R.id.Game4_cookie2,
		R.id.Game4_cookie3,
		R.id.Game4_cookie4,
		R.id.Game4_cookie5,
		R.id.Game4_cookie6,
		R.id.Game4_cookie7,
	};
	
	protected int[] creamArray = {
		R.drawable.game4_cream1,
		R.drawable.game4_cream2,
		R.drawable.game4_cream3,
		R.drawable.game4_cream4,
		R.drawable.game4_cream5,
		R.drawable.game4_cream6			
	};
	
	protected Point[][] cookieCreamOffsetArray = new Point[][]{
			//T
			{new Point(0,-150),  
			 new Point(-25,-100),new Point(25,-100), 
			 new Point(-50,-50), new Point(0,-50), new Point(50,-50),  
			 new Point(-75,50),	 new Point(-25,50),   new Point(25,50),	new Point(75,50),
			 new Point(-100,150),new Point(-50,150),  new Point(0,150), new Point(50,150),  new Point(100,150), new Point(100,150)},
			//S
			 {new Point(-120,-120),new Point(-40,-120),new Point(40,-120),new Point(120,-120),
			  new Point(-120,-40), new Point(-40,-40), new Point(40,-40), new Point(120,-40),
			  new Point(-120,40),  new Point(-40,40),  new Point(40,40),  new Point(120,40),
			  new Point(-120,120), new Point(-40,120), new Point(40,120), new Point(120,120)}, 
			//C
			{new Point(-120,-120),new Point(-40,-120),new Point(40,-120),new Point(120,-120),
		     new Point(-120,-40), new Point(-40,-40), new Point(40,-40), new Point(120,-40),
		     new Point(-120,40),  new Point(-40,40),  new Point(40,40),  new Point(120,40),
		     new Point(-120,120), new Point(-40,120), new Point(40,120), new Point(120,120)}};
	
	public static cookieCuttingHandler cookieCuttingHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		PlayPalUtility.setDebugMode(false);
		
		Bundle bundle = getIntent().getExtras();
		userName = bundle.getString("userName");
		mBadges = bundle.getInt("GameBadges");
		mHighScore = bundle.getInt("GameHighScore");
		mWinCount = bundle.getInt("GameWinCount");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_game4);
		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);		
		
		progressCountText = (TextView)findViewById(R.id.testProgressCount);
		doughView = (ImageView)findViewById(doughViewArray[0]);
		laddleView = (ImageView)findViewById(R.id.Game4_ladle);		
		game4RelativeLayout = (DrawableRelativeLayout) findViewById(R.id.Game4RelativeLayout);
		cookieCuttingHandler = new cookieCuttingHandler();
		
		curProgress = 0;
		boxSize = 60;
		
		PlayPalUtility.registerLineGesture(game4RelativeLayout, this, 
			new Callable<Integer>() {
				public Integer call() {
					return handleDoughProgress(doughView);
				}
			},
			new Callable<Integer>() {
				public Integer call() {
					return handleDoughAction(doughView);
				}	
			});
		
		PlayPalUtility.setLineGesture(true);
		PlayPalUtility.initialLineGestureParams(false, true, boxSize, doughPosArray[curProgress][0], doughPosArray[curProgress][1], doughPosArray[curProgress][2], doughPosArray[curProgress][3]);

		PlayPalUtility.initDrawView(game4RelativeLayout, this, (DrawView)findViewById(R.id.drawLineView));
		PlayPalUtility.setStraightStroke(doughPosArray[curProgress][0],doughPosArray[curProgress][3]);
		
		game4RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                    	PlayPalUtility.curEntry = new RecordEntry(
        						new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_START);
        				PenRecorder.forceRecord();
                    	laddleView.setVisibility(ImageView.VISIBLE);
                        break;
                     
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	PlayPalUtility.curEntry = new RecordEntry(
        						new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_MOVE);
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX(), (int)event.getY(), 0, 0);
                    	laddleView.setLayoutParams(params);
                        break;

                    case MotionEvent.ACTION_HOVER_EXIT:
                    	PlayPalUtility.curEntry = new RecordEntry(
        						new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_END);
        				PenRecorder.forceRecord();
                    	laddleView.setVisibility(ImageView.INVISIBLE);
                        break;
                }
                return true;
            }
        });	
				
		gameContext = this;
		mSPenEventLibrary = new SPenEventLibrary();
		
		PlayPalUtility.registerProgressBar((ProgressBar)findViewById(R.id.progressBarRed), (ImageView)findViewById(R.id.progressMark), (ImageView)findViewById(R.id.progressBar), new Callable<Integer>() {
			public Integer call() {
				PlayPalUtility.killTimeBar();
				PlayPalUtility.setLineGesture(false);
				PlayPalUtility.unregisterLineGesture(game4RelativeLayout);
				PlayPalUtility.clearGestureSets();
				PlayPalUtility.clearDrawView();
				
				Intent newAct = new Intent();
				newAct.setClass(Game4Activity.this, AnimationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", 4);
				bundle.putBoolean("isWin", false);
				bundle.putString("userName", userName);
				bundle.putInt("GameBadges", mBadges);
				bundle.putInt("GameHighScore", mHighScore);
				bundle.putInt("GameWinCount", mWinCount);
				bundle.putInt("NewScore", -1);
	            newAct.putExtras(bundle);
				
	            findViewById(R.id.failFeedbackView).setVisibility(View.VISIBLE);
	            
	            Timer timer = new Timer(true);
				timer.schedule(new WaitTimerTask(gameContext, newAct), 2000);
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
		PlayPalUtility.initialProgressBar(DOUGH_TIME, PlayPalUtility.TIME_MODE);
		PenRecorder.registerRecorder(game4RelativeLayout, this, userName, "4-1");
		
		PlayPalUtility.setHoverTarget(true, laddleView);
	}	

	@Override
	protected void onPause() {
	    super.onPause();
	    BackgroundMusicHandler.recyle();
	    writeToSettings();
	    turnOffTick();
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
		
		System.gc();
	}
	
	@Override
	public void onBackPressed() {
	}
	
	private void turnOffTick() {
		if(tickMP != null) {
	    	if(tickMP.isPlaying())
	    		tickMP.stop();
	    	tickMP.release();
	    	tickMP = null;
	    }
	}
	
	private void writeToSettings() {
		SharedPreferences settings = getSharedPreferences("PLAY_PAL_TMP_INFO", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("CUR_PROGRESS", curProgress);
		editor.putInt("CUR_TIMEBAR_MAX", PlayPalUtility.getProgressBarMaxVal());
		editor.putInt("CUR_TIMEBAR_VAL", PlayPalUtility.getProgressBarCurVal());
		editor.putInt("CUR_SCORE", score);
		editor.commit();
	}
	
	private void setBackFromSettings() {
		SharedPreferences settings = getSharedPreferences("PLAY_PAL_TMP_INFO", 0);
		curProgress = settings.getInt("CUR_PROGRESS", -1);
		if(curProgress < 0) {
			curProgress = 0;
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
	
	private void setBackProgressCountPart(){
			
	}
	
	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PlayPalUtility.killTimeBar();
				PlayPalUtility.setLineGesture(false);
				PlayPalUtility.unregisterLineGesture(game4RelativeLayout);
				PlayPalUtility.clearGestureSets();
				PlayPalUtility.clearDrawView();
				BackgroundMusicHandler.setCanRecycle(false);
				
				Intent newAct = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", 4);
				bundle.putString("userName", userName);
	            newAct.putExtras(bundle);				
				newAct.setClass(Game4Activity.this, MainActivity.class);
				startActivityForResult(newAct, 0);
				Game4Activity.this.finish();
			}
		});
	}
	
	protected void initCookieView(){
		Random ran = new Random();
		for(int i=0; i<cookieArray.length; i++){
			cookieArray[i] = new Cookie(i, ran.nextInt(3), (ImageView)findViewById(cookieViewArray[i]));
			cookieArray[i].setCreamColor();
			cookieArray[i].setGesturePoint();
			cookieArray[i].setGestureDottedLine();
			cookieArray[i].view.setVisibility(ImageView.INVISIBLE);
			
			final Cookie curCookie = cookieArray[i];
			
			mSPenEventLibrary.setSPenHoverListener(curCookie.view, new SPenHoverListener(){
				Point startPoint;
				ImageView curButterView;
				int ratio = CREAM_INIT_RATIO;
				int w = 50;
				int h = 50;
						
				@Override
				public boolean onHover(View arg0, MotionEvent event) {
					if(curButterView == null || !butterSqueezing){
						return false;
					}
					PlayPalUtility.curEntry = new RecordEntry(
							new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_HOVER_BTN_MOVE);
					
					if(ratio < CREAM_MAX_RATIO)
						ratio++;
						
					Point curPoint = new Point((int)event.getX(),(int)event.getY());
					float dist = calcDistance(startPoint, curPoint);
				
					if(dist>=CREAM_DIST){
						PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_POP, Game4Activity.this);
						curButterView = new ImageView(gameContext);
						curButterView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, creamArray[curCookie.creamColor]));
						game4RelativeLayout.addView(curButterView);
						ratio = CREAM_INIT_RATIO;	
						startPoint = new Point((int)event.getX(),(int)event.getY());
					}
					
					RelativeLayout.LayoutParams params = (LayoutParams) curButterView.getLayoutParams();			
					params.width = params.height = (int)(CREAM_SIZE*ratio/CREAM_MAX_RATIO);
					params.setMargins(	curCookie.view.getLeft()+(int)event.getX()-params.height/2, 
							curCookie.view.getTop()+(int)event.getY()-params.width/2, 
										0, 0);
					
					curButterView.setLayoutParams(params);
					return false;
				}
	
				@Override
				public void onHoverButtonDown(View arg0, MotionEvent event) {
					PlayPalUtility.curEntry = new RecordEntry(
							new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_HOVER_BTN_START);
					PenRecorder.forceRecord();
					Log.d("Penpal","pressing");
					butterSqueezing = true;				
					
					curButterView = new ImageView(gameContext);
					game4RelativeLayout.addView(curButterView);
					ratio = CREAM_INIT_RATIO;
					
					startPoint = new Point((int)event.getX(),(int)event.getY());
				}
	
				@Override
				public void onHoverButtonUp(View arg0, MotionEvent event) {
					PlayPalUtility.curEntry = new RecordEntry(
							new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_HOVER_BTN_END);
					PenRecorder.forceRecord();
					Log.d("Penpal","releasing");
					butterSqueezing = false;
					
					ratio = 0;
					curButterView = null;
				}
				
			});			
		}
		
		PlayPalUtility.registerLineGesture(game4RelativeLayout, this, 
			new Callable<Integer>(){
				public Integer call() {
					return handleCookieProgress();
				}
			});
	}
	
	protected Integer handleDoughProgress (View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		if(curProgress == DOUGH_PROGRESS_END){
			score += PlayPalUtility.killTimeBar();
			
			laddleView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.game4_thinknife));
			PenRecorder.outputJSON();

			PlayPalUtility.clearGestureSets();
			PlayPalUtility.clearDrawView();
			isFirstAlarm = true;
			findViewById(R.id.timeReminder).setVisibility(View.INVISIBLE);
			turnOffTick();
			PlayPalUtility.initialProgressBar(CREAM_TIME, PlayPalUtility.TIME_MODE);
			initCookieView();

			PenRecorder.registerRecorder(game4RelativeLayout, this, userName, "4-2");
		}
		else if(curProgress < DOUGH_PROGRESS_END){
			
			PlayPalUtility.clearGestureSets();			
			PlayPalUtility.initialLineGestureParams(false, true, boxSize, doughPosArray[curProgress][0], doughPosArray[curProgress][1], doughPosArray[curProgress][2], doughPosArray[curProgress][3]);
			
			PlayPalUtility.clearDrawView();
			PlayPalUtility.setStraightStroke(centerPoint,doughPosArray[curProgress][3]);
		}
 		
		return 1;
	}
	
	protected Integer doTimeReminder() {
		if(isFirstAlarm) {
			PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_TIME_REMINDER, this, false);
			tickMP = PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_TIMER_TICK, this, true);
			isFirstAlarm = false;
		}
		findViewById(R.id.timeReminder).setVisibility(View.VISIBLE);
		if(timeReminderStat == 1) {
			((ImageView)findViewById(R.id.timeReminder)).setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.time_reminder_2));
			timeReminderStat = 0;
		}
		else {
			((ImageView)findViewById(R.id.timeReminder)).setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.time_reminder_1));
			timeReminderStat = 1;
		}
		return 0;
	}
	
	protected Integer handleDoughAction (View view){
		int x,y,w,h,dxy=100;
		ImageView curDough = (ImageView)findViewById(doughViewArray[Math.min(curProgress+1, DOUGH_NUM-1)]);
		curDough.setVisibility(ImageView.VISIBLE);
		
		//Log.d("game4",""+PlayPalUtility.getLastTriggerSetIndex());
		
		int subProgress = PlayPalUtility.getLastTriggerPointIndex();
		Point p = doughPosArray[curProgress][subProgress+1];
		if(subProgress == 0)
			PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_ROLLING, this);
		
		switch(curProgress){
			case 0:
				x = centerPoint.x-dxy;
				y = p.y-dxy; 
				break;
	
			case 1:
				x = centerPoint.x-dxy;
				y = centerPoint.y-dxy;
				break;
				
			case 2:
				x = p.x-dxy;
				y = centerPoint.y-dxy;
				break;
			
			case 3:
				x = p.x-dxy;
				y = p.y-dxy;
				break;
				
			default:
				curDough = null;
				x = centerPoint.x;
				y = centerPoint.y;
		}
		
		w = curDough.getLayoutParams().width  = Math.abs(centerPoint.x-p.x) + 2*dxy;
		h = curDough.getLayoutParams().height = Math.abs(centerPoint.y-p.y) + 2*dxy;
	
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
    	params.setMargins(x, y , 0, 0);
    	curDough.setLayoutParams(params);
		curDough.setScaleType(ImageView.ScaleType.FIT_XY);
		curDough.invalidate();
		
		return 1;
	}
	
	
	protected Integer handleCookieProgress (){
		PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_CUT_DOUGH, this);
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		int idx = PlayPalUtility.getLastTriggerSetIndex();
		PlayPalUtility.cancelGestureSet(idx);
		cookieArray[idx].view.setVisibility(ImageView.VISIBLE);
		cookieArray[idx].beCutted();
		
		if(curProgress == COOKIE_PROGRESS_END){
			score += PlayPalUtility.killTimeBar();
			PlayPalUtility.clearDrawView();
			
			for(int i=0; i<DOUGH_NUM; i++){
				final ImageView curDoughView = (ImageView)findViewById(doughViewArray[i]);
				
				Animation doughAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
				doughAnim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation anim) {	
						curDoughView.setVisibility(ImageView.GONE);
						curDoughView.clearAnimation();
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}
				});
				curDoughView.setAnimation(doughAnim);
				doughAnim.startNow();
			}
			
			for(int i=0; i<COOKIE_NUM; i++){	
				final Cookie curCookie = cookieArray[i];
				Animation cookieAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
				cookieAnim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation anim) {
						curCookie.view.clearAnimation();
						curCookie.beBaked();
						
						PlayPalUtility.setAlphaAnimation(curCookie.view, true);
						if(isFirstCookie) {
							isFirstAlarm = true;
							findViewById(R.id.timeReminder).setVisibility(View.INVISIBLE);
							PlayPalUtility.initialProgressBar(CREAM_TIME, PlayPalUtility.TIME_MODE);
							laddleView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.game4_squeezer));
							isFirstCookie = false;
						}


					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}
				});
				game4RelativeLayout.invalidate();
				
				curCookie.view.setAnimation(cookieAnim);
				cookieAnim.startNow();	
			}
			
			
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterLineGesture(game4RelativeLayout);

			
			for(int i=0; i<COOKIE_NUM; i++){
				final Cookie curCookie = cookieArray[i];
				curCookie.setPos();
				for(int j=0; j<16; j++){
					PlayPalUtility.initialLineGestureParams(false, true, CREAM_BOX_SIZE, pointAddition(curCookie.center, cookieCreamOffsetArray[curCookie.type][j]));
				}
			}
			
			PlayPalUtility.registerSingleHoverPoint(true, game4RelativeLayout, this, new Callable<Integer>() {
				@Override
				public Integer call() {
					return handleCookieCreamAction(cookieArray[0].view);
				}
			});
			PlayPalUtility.setLineGesture(true);
			PenRecorder.outputJSON();
			PenRecorder.registerRecorder(game4RelativeLayout, this, userName, "4-3");
		}
		
		return 1;
	}
	
	
	protected Integer handleCookieCreamAction(View view){
		int idx = PlayPalUtility.getLastTriggerSetIndex();
		cookieArray[idx/16].doButter();
		PlayPalUtility.cancelGestureSet(idx);

		return 1;
	}
	
	@SuppressLint("FloatMath")
	static float calcDistance(Point p1, Point p2){
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		
		return FloatMath.sqrt(dx*dx + dy*dy);
	}
	
	public class Cookie{
		protected final int TRIANGULAR_COOKIE=0;
		protected final int SQUARE_COOKIE	 =1;
		protected final int CIRCLE_COOKIE	 =2;
		
		protected int id;
		protected int type;
		protected int creamColor;
		protected int numCream;
		protected int cookieRadius = 160;
		protected int temp_Length  = (int)(cookieRadius/Math.sqrt(2));
		protected int temp_Length2 = (int)(cookieRadius*Math.sin(Math.PI/3));
		protected int temp_Length3 = (int)(cookieRadius*Math.cos(Math.PI/3));
		protected int sqrt_Length = (int)(cookieRadius*Math.cos(Math.PI/4));
		
		protected Point center;
		protected ImageView view;
		protected Point[][] offsetArray = new Point[][]{
				//TRIANGULAR_COOKIE
				{new Point(0,-cookieRadius), new Point(-temp_Length2,temp_Length3), new Point(temp_Length2,temp_Length3), new Point(0,-cookieRadius),
				 new Point(0,-cookieRadius), new Point(-temp_Length2,temp_Length3), new Point(temp_Length2,temp_Length3), new Point(0,-cookieRadius)},
				//SQUARE_COOKIE
				{new Point(temp_Length,temp_Length), new Point(temp_Length,-temp_Length), new Point(-temp_Length,-temp_Length), new Point(-temp_Length,temp_Length),
				 new Point(temp_Length,temp_Length), new Point(temp_Length,-temp_Length), new Point(-temp_Length,-temp_Length), new Point(-temp_Length,temp_Length),},
				//CIRCLE_COOKIE
				{new Point(cookieRadius,0),  new Point(sqrt_Length,-sqrt_Length), new Point(0,-cookieRadius), new Point(-sqrt_Length,-sqrt_Length),
				 new Point(-cookieRadius,0), new Point(-sqrt_Length,sqrt_Length), new Point(0,cookieRadius),  new Point(sqrt_Length, sqrt_Length)}};
		
		public Cookie(int _id, int _t, ImageView _v){
			numCream = 0;
			id   = _id;
			type = _t;
			view = _v;
			view.setVisibility(ImageView.VISIBLE);
			
			center = new Point(view.getLeft()+200, view.getTop()+200);
		}
		
		public void beCutted(){
			PlayPalUtility.eraseStroke(this.id);			
			
			//FramesSequenceAnimation cutAnim = AnimationsContainer.getInstance().createGame4CookieAnim(view, type);
			//cutAnim.start();
			this.view.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, cuttingCookieArray[this.type]));
			Timer timer = new Timer(true);
			timer.schedule(new cookieCuttingTask(id), 750, 500);
		}
		
		public void setPos(){
			Random rand = new Random();
			center = pointAddition(center, new Point( rand.nextInt(150)-75, rand.nextInt(150)-75) );
		}
		
		public void beBaked(){
			view.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, cookieResArray3[type]));
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        	params.setMargins(center.x - 200, center.y - 200 , 0, 0);
        	this.view.setLayoutParams(params);        	
        	
		}
		
		public void setCreamColor(){
			Random rand = new Random();
			creamColor = rand.nextInt(CREAM_COLOR_NUM);
		}
		
		public void setGesturePoint(){ 
			PlayPalUtility.initialLineGestureParams(false, false, boxSize,
				pointAddition(this.center, offsetArray[this.type][0]), 
				pointAddition(this.center, offsetArray[this.type][1]),
				pointAddition(this.center, offsetArray[this.type][2]),
				pointAddition(this.center, offsetArray[this.type][3]),
				pointAddition(this.center, offsetArray[this.type][4]),
				pointAddition(this.center, offsetArray[this.type][5]),
				pointAddition(this.center, offsetArray[this.type][6]),
				pointAddition(this.center, offsetArray[this.type][7]));
		}
		
		public void setGestureDottedLine(){
			if(this.type == TRIANGULAR_COOKIE || this.type == SQUARE_COOKIE){
				PlayPalUtility.setStraightStroke(this.id,
					pointAddition(this.center, offsetArray[this.type][0]),
					pointAddition(this.center, offsetArray[this.type][1]),
					pointAddition(this.center, offsetArray[this.type][2]),
					pointAddition(this.center, offsetArray[this.type][3]),
					pointAddition(this.center, offsetArray[this.type][0]));
			}
			else{
				PlayPalUtility.setCircleStroke(this.center, cookieRadius);
			}
		}
		
		public void doButter(){
			numCream++;
			
			if(numCream == 9 && curProgress == CREAM_PROGRESS_END-1){
				FramesSequenceAnimation largeAnim = AnimationsContainer.getInstance().createGame4BigCookieAnim(view, type);
				PlayPalUtility.playSoundEffect (PlayPalUtility.SOUND_COOKIE_POP, Game4Activity.gameContext);
				largeAnim.start();
				curProgress++;
				PlayPalUtility.setLastSingleHoverPoint(true);
			}
			else if(numCream == 10){
				FramesSequenceAnimation largeAnim = AnimationsContainer.getInstance().createGame4BigCookieAnim(view, type);
				PlayPalUtility.playSoundEffect (PlayPalUtility.SOUND_COOKIE_POP, Game4Activity.gameContext);
				largeAnim.start();
				curProgress++;				
			}			
			
			if(curProgress >= CREAM_PROGRESS_END){
				score += PlayPalUtility.killTimeBar();
				PlayPalUtility.setLineGesture(false);
				PlayPalUtility.unregisterLineGesture(game4RelativeLayout);
				PlayPalUtility.clearGestureSets();
				PlayPalUtility.clearDrawView();
				PenRecorder.outputJSON();
				
				Intent newAct = new Intent();
				newAct.setClass(Game4Activity.this, AnimationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", 4);
				bundle.putBoolean("isWin", true);
				bundle.putString("userName", userName);
				bundle.putInt("GameBadges", mBadges);
				bundle.putInt("GameHighScore", mHighScore);
				bundle.putInt("GameWinCount", mWinCount);
				bundle.putInt("NewScore", score);
	            newAct.putExtras(bundle);
				startActivityForResult(newAct, 0);
			}
		}
	}
	
	public class cookieCuttingHandler extends Handler{
		public void handleMessage(Message msg) {
			int idx = msg.what;
			Cookie curCookie = cookieArray[idx];
			curCookie.view.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, cuttedCookieArray[curCookie.type]));
		}
	}
	
	class cookieCuttingTask extends TimerTask{
		int cookieIDX;
		cookieCuttingTask(int idx){
			cookieIDX = idx;
		}
		public void run(){
			Message msg = new Message();
			msg.what = cookieIDX;
            Game4Activity.cookieCuttingHandler.sendMessage(msg);
            this.cancel();
		}
	}
}
