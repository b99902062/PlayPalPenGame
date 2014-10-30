package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;

import com.example.playpalpengame.Game3Activity.OvenHandler;
import com.example.playpalpengame.Game3Activity.ovenTimerTask;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenHoverListener;

public class Game3Activity extends Activity {
	
	protected Point centralPoint = new Point(1280,880);
	protected Point[] pointArray = {
		new Point(1480,880),
		new Point(1280,680),
		new Point(1080,880),
		new Point(1280,1080)};
		
	protected Point[] dottedLineArray = {
		new Point(780+444, 380+216),
		new Point(780+324, 380+370),
		new Point(780+214, 380+490),
		new Point(780+315, 380+598),
		new Point(780+332, 380+710),
		new Point(780+480, 380+740),
		new Point(780+630, 380+704),
		new Point(780+760, 380+614),
		new Point(780+752, 380+480),
		new Point(780+715, 380+330),
		new Point(780+580, 380+310)};
	
	protected Point[] creamPosArray = {
		new Point(780+324, 380+370),
		new Point(780+324, 380+620),
		new Point(780+548, 380+690),
		new Point(780+710, 380+512),	
		new Point(780+588, 380+310)};
		
	protected final int CREAM_DIST = 30;
	protected final int INIT_CREAM_RATIO = 10;
	protected final int CREAM_MAX_RATIO = 20;
	protected final int SMALL_CREAM_SIZE = 75;
	protected final int LARGE_CREAM_SIZE = 150;
	
	protected final int MIX_PROGRESS_START = 1;
	protected final int MIX_PROGRESS_HALF  = 5;
	protected final int MIX_PROGRESS_END = 10;
	protected final int DOTTED_LINE_PROGRESS_END = 21;
	protected final int CREAM_PROGRESS_END = 26;
	
	protected final int MIX_TIME   = 900;
	protected final int CREAM_TIME = 900;
	protected final int CAKE_TIME = 450;
	protected final int CUT_TIME = 150;
	public static OvenHandler ovenHandler;
	protected int boxSize,creamBoxSize;
	protected int curProgress;
	protected boolean canTouchOven = false;
	protected boolean butterSqueezing = false;
	protected String userName = null;
	private int mBadges = 0;
	private int mHighScore = 0;
	private int mWinCount = 0;
	private int score = 0;
	private int timeReminderStat = 0;
	private boolean isFirstAlarm = true;
	private MediaPlayer tickMP = null;
	
	TextView  progressCountText;
	ImageView bowlView;
	
	ImageView mixView, mixView2;
	ImageView ovenView, ovenView2;
	ImageView cakeView;
	ImageView cakeDottedLineView;
	ImageView cakeCreamView;
	ImageView cakeStrawberryView;
	ImageView eggbeatView;
	ImageView helicalView;
	ImageView currentFoodView;
	ImageView cakeCreamHintView;

	AnimationDrawable mixStirAnim;
	AnimationDrawable ovenAnimation;
	protected DrawableRelativeLayout game3RelativeLayout;
	protected Game3Activity gameContext;
	private SPenEventLibrary mSPenEventLibrary;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		userName = bundle.getString("userName");
		mBadges = bundle.getInt("GameBadges");
		mHighScore = bundle.getInt("GameHighScore");
		mWinCount = bundle.getInt("GameWinCount");
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_game3);

		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);
		
		curProgress = 0;
		boxSize = 100;
		creamBoxSize = 70;
		gameContext = this;
		ovenHandler = new OvenHandler();
		
		progressCountText = (TextView)findViewById(R.id.testProgressCount);
		bowlView = (ImageView)findViewById(R.id.Game3_bowl);
		mixView  =  (ImageView)findViewById(R.id.Game3_mix);
		mixView2 =  (ImageView)findViewById(R.id.Game3_mix2);
		ovenView = (ImageView)findViewById(R.id.Game3_oven);
		ovenView2 = (ImageView)findViewById(R.id.Game3_oven2);
		cakeView = (ImageView)findViewById(R.id.Game3_cake);
		cakeDottedLineView = (ImageView)findViewById(R.id.Game3_cakeDottedLine);
		eggbeatView = (ImageView)findViewById(R.id.Game3_beat);
		cakeCreamView = (ImageView)findViewById(R.id.Game3_cakeCream);
		cakeStrawberryView = (ImageView)findViewById(R.id.Game3_cakeStrawberry);
		helicalView = (ImageView)findViewById(R.id.Game3_helicalView);
		cakeCreamHintView = (ImageView)findViewById(R.id.Game3_cakeCreamHintView);
		
		game3RelativeLayout = (DrawableRelativeLayout) findViewById(R.id.Game3RelativeLayout);
		game3RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_START);
                    	PenRecorder.forceRecord();
                    	eggbeatView.setVisibility(ImageView.VISIBLE);
                        break;
                     
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_MOVE);
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX(), (int)event.getY(), 0, 0);
                    	eggbeatView.setLayoutParams(params);
                        break;

                    case MotionEvent.ACTION_HOVER_EXIT:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_END);
                    	PenRecorder.forceRecord();
                    	eggbeatView.setVisibility(ImageView.INVISIBLE);
                        break;
                }
                return true;
            }
        });	
		
		
		
		mSPenEventLibrary = new SPenEventLibrary();
		mSPenEventLibrary.setSPenHoverListener(cakeView, new SPenHoverListener(){
			Point startPoint;
			ImageView curButterView;
			int ratio = INIT_CREAM_RATIO;
			int w = 50;
			int h = 50;
					
			@Override
			public boolean onHover(View arg0, MotionEvent event) {
				if(!butterSqueezing){
					return false;
				}

				PlayPalUtility.curEntry = new RecordEntry(
						new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_HOVER_BTN_MOVE);
				
				if(curProgress < DOTTED_LINE_PROGRESS_END){
					if(ratio == INIT_CREAM_RATIO || curButterView == null){
						curButterView = new ImageView(gameContext);
						curButterView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.game3_cream));
						game3RelativeLayout.addView(curButterView);
					}
					
					if(ratio < CREAM_MAX_RATIO)
						ratio++;
					
					Point curPoint = new Point((int)event.getX(),(int)event.getY());
					float dist = calcDistance(startPoint, curPoint);
				
					
					RelativeLayout.LayoutParams params = (LayoutParams) curButterView.getLayoutParams();			
					params.width = params.height = (int)(SMALL_CREAM_SIZE*ratio/CREAM_MAX_RATIO);
					params.setMargins(	cakeView.getLeft()+(int)event.getX()-params.height/2, 
										cakeView.getTop()+(int)event.getY()-params.width/2, 
										0, 0);
					
					curButterView.setLayoutParams(params);
					
					if(dist>=CREAM_DIST){
						PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_POP, Game3Activity.this);
						curButterView = null;
						
						ratio = INIT_CREAM_RATIO;	
						startPoint = new Point((int)event.getX(),(int)event.getY());
					}
				}
				else{
					if(ratio == INIT_CREAM_RATIO){
						curButterView = new ImageView(gameContext);
						curButterView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.game3_cream2));
						game3RelativeLayout.addView(curButterView);
					}
					if(ratio < CREAM_MAX_RATIO)
						ratio++;
								
					RelativeLayout.LayoutParams params = (LayoutParams) curButterView.getLayoutParams();		
					params.width = params.height = (int)(LARGE_CREAM_SIZE*ratio/20.0);
					params.setMargins(	cakeView.getLeft()+(int)event.getX()-params.height/2, 
							cakeView.getTop()+(int)event.getY()-params.width/2, 
							0, 0);
					curButterView.setLayoutParams(params);
				}
				return false;
			}

			@Override
			public void onHoverButtonDown(View arg0, MotionEvent event) {
				PlayPalUtility.curEntry = new RecordEntry(
						new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_HOVER_BTN_START);
				PenRecorder.forceRecord();
				
				Log.d("Penpal","pressing");
				butterSqueezing = true;				
				
				ratio = INIT_CREAM_RATIO;
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

		PlayPalUtility.setDebugMode(false);
		PenRecorder.registerRecorder(game3RelativeLayout, this, userName, "3-1");
		PlayPalUtility.initDrawView(game3RelativeLayout, this);
		setFoodListener(mixView);
		
		PlayPalUtility.registerLineGesture(game3RelativeLayout, this, new Callable<Integer>() {
			public Integer call() {
				return handleStirring(mixView);		
			}
		});
				
		PlayPalUtility.setLineGesture(true);
		PlayPalUtility.initialLineGestureParams(true, false, boxSize, 
				pointArray[0], 
				pointArray[1], 
				pointArray[2], 
				pointArray[3]);
		
		
		PlayPalUtility.registerProgressBar((ProgressBar)findViewById(R.id.progressBarRed), (ImageView)findViewById(R.id.progressMark), (ImageView)findViewById(R.id.progressBar), new Callable<Integer>() {
			public Integer call() {
				PlayPalUtility.killTimeBar();
				PlayPalUtility.setLineGesture(false);
				PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
				PlayPalUtility.clearGestureSets();
				PlayPalUtility.clearDrawView();
				
				Intent newAct = new Intent();
				newAct.setClass(Game3Activity.this, AnimationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", 3);
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
		PlayPalUtility.initialProgressBar(MIX_TIME, PlayPalUtility.TIME_MODE);
		
		PlayPalUtility.setHoverTarget(true, eggbeatView);
		
	}	
	
	@Override
	protected void onPause() {
	    super.onPause();
	    writeToSettings();
	    
	    turnOffTick();
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
		setBackFromSettings();
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
	
	private void writeToSettings(){
		SharedPreferences settings = getSharedPreferences("PLAY_PAL_TMP_INFO", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("CUR_PROGRESS", curProgress);
		editor.putInt("CUR_TIMEBAR_MAX", PlayPalUtility.getProgressBarMaxVal());
		editor.putInt("CUR_TIMEBAR_VAL", PlayPalUtility.getProgressBarCurVal());
		editor.putInt("CUR_SCORE", score);
		editor.commit();
	}
	
	private void setBackFromSettings(){
		SharedPreferences settings = getSharedPreferences("PLAY_PAL_TMP_INFO", 0);
		curProgress = settings.getInt("CUR_PROGRESS", -1);
		if(curProgress < 0) {
			curProgress = 0;
			return;
		}
		else{
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
	
	protected void setFoodListener(View targetView) {
		currentFoodView = (ImageView) targetView;
		currentFoodView.setVisibility(ImageView.VISIBLE);
	}
	
	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PlayPalUtility.killTimeBar();
				PlayPalUtility.setLineGesture(false);
				PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
				PlayPalUtility.clearGestureSets();
				PlayPalUtility.clearDrawView();
				BackgroundMusicHandler.setCanRecycle(false);
				
				
				Intent newAct = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("userName", userName);
	            newAct.putExtras(bundle);
				newAct.setClass(Game3Activity.this, MainActivity.class);
				startActivityForResult(newAct, 0);
				Game3Activity.this.finish();
			}
		});
	}
	
	protected void setOvenListener(View targetView){
		PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_OVEN, this);
		setFoodListener(ovenView2);
		
		Timer timer = new Timer(true);
		timer.schedule(new ovenTimerTask(), 3500, 1000);
	}
		
	protected Integer handleStirring(View view){
		PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_MIX, this);
		FramesSequenceAnimation anim = null;
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		
		if(curProgress < MIX_PROGRESS_HALF){
			setFoodListener(mixView);
			anim = AnimationsContainer.getInstance().createGame3StirAnim(mixView,1);
		}
		else if(curProgress == MIX_PROGRESS_HALF){
			setFoodListener(mixView2);
			PlayPalUtility.setAlphaAnimation(mixView, false);
			PlayPalUtility.setAlphaAnimation(mixView2, true);
			
			mixView.setVisibility(ImageView.GONE);
			anim = AnimationsContainer.getInstance().createGame3StirAnim(mixView2,2);
		}
		else if(curProgress < MIX_PROGRESS_END){
			mixView.setVisibility(ImageView.GONE);
			anim = AnimationsContainer.getInstance().createGame3StirAnim(mixView2,2);
		}
		else if( curProgress == MIX_PROGRESS_END){
			helicalView.setVisibility(ImageView.GONE);

			score += PlayPalUtility.killTimeBar();
			eggbeatView.setImageResource(0);
			Animation mixAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
			mixAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {	
					currentFoodView.setVisibility(ImageView.GONE);
					currentFoodView.clearAnimation();
					
					PlayPalUtility.clearDrawView();
					PlayPalUtility.setLineGesture(false);
					PlayPalUtility.clearGestureSets();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			
			currentFoodView.setAnimation(mixAnim);
			mixAnim.startNow();
			
			Animation bowlAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
			bowlAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {	
					bowlView.setVisibility(ImageView.GONE);
					bowlView.clearAnimation();
					
					PlayPalUtility.setLineGesture(false);
					PlayPalUtility.clearGestureSets();
					
					PlayPalUtility.setAlphaAnimation(ovenView, false);
					PlayPalUtility.setAlphaAnimation(ovenView2, true);
					
					setOvenListener(ovenView2);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			
			bowlView.setAnimation(bowlAnim);
			bowlAnim.startNow();
			
			canTouchOven = true;

			PlayPalUtility.clearDrawView();
			PlayPalUtility.setLineGesture(false);
            PlayPalUtility.clearGestureSets();
        	PlayPalUtility.clearDrawView();
			PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
			
			PenRecorder.outputJSON();
			PenRecorder.registerRecorder(game3RelativeLayout, this, userName, "3-2");
			
			anim = null;
		}
		
		if(anim != null)
			anim.start();
		game3RelativeLayout.invalidate();
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
	
	protected Integer handleCakeAction(View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		
		if(curProgress == DOTTED_LINE_PROGRESS_END){
			cakeCreamHintView.setVisibility(ImageView.VISIBLE);
			cakeCreamHintView.bringToFront();
			game3RelativeLayout.invalidate();
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.clearDrawView();
			PlayPalUtility.unregisterHoverLineGesture(game3RelativeLayout);
			score += PlayPalUtility.killTimeBar();
			isFirstAlarm = true;
			findViewById(R.id.timeReminder).setVisibility(View.INVISIBLE);
			turnOffTick();
			PlayPalUtility.initialProgressBar(CAKE_TIME, PlayPalUtility.TIME_MODE);
			
			PlayPalUtility.registerSingleHoverPoint(false,game3RelativeLayout, this, new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					return handleCream(cakeCreamView);
				}
			});
			
			PlayPalUtility.setLineGesture(true);
			for(int i=0; i<5; i++)
				PlayPalUtility.initialLineGestureParams(false, false, boxSize/2, creamPosArray[i]);
			
			PenRecorder.outputJSON();
			PenRecorder.registerRecorder(game3RelativeLayout, this, userName, "3-3");
		}
		return 1;
	}
	
	protected Integer handleCream(View view){
		PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_CREAM, this);
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		int idx = PlayPalUtility.getLastTriggerSetIndex();
		PlayPalUtility.cancelGestureSet(idx);
		
		if(curProgress == CREAM_PROGRESS_END){
			
			mSPenEventLibrary.setSPenHoverListener(cakeView, null);
			cakeDottedLineView.setVisibility(ImageView.INVISIBLE);
			cakeStrawberryView.setVisibility(ImageView.VISIBLE);
			cakeStrawberryView.bringToFront();
			game3RelativeLayout.invalidate();
			
			PlayPalUtility.setAlphaAnimation(cakeStrawberryView,true);
			
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterHoverLineGesture(game3RelativeLayout);
			
			PlayPalUtility.initialProgressBar(CUT_TIME, PlayPalUtility.TIME_MODE);
			PlayPalUtility.registerLineGesture(game3RelativeLayout, this, new Callable<Integer>() {
				public Integer call() {
					return handleCutting(null);		
				}
			});
			
			PenRecorder.outputJSON();
			PenRecorder.registerRecorder(game3RelativeLayout, this, userName, "3-4");
			
			PlayPalUtility.clearDrawView();
			PlayPalUtility.initialLineGestureParams(false, false, boxSize, new Point(1560,600) ,centralPoint,  new Point(1560,1160));
			PlayPalUtility.setStraightStroke(new Point(1560,600) ,centralPoint,  new Point(1560,1160));
			
			eggbeatView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.game1_knife));
		}
		return 1;
	}	
	
	protected Integer handleCutting(View view) {
		//finishing game3
		PenRecorder.outputJSON();
		score += PlayPalUtility.killTimeBar();
		PlayPalUtility.setLineGesture(false);
		PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
		PlayPalUtility.clearGestureSets();
		PlayPalUtility.clearDrawView();
		
		Intent newAct = new Intent();
		newAct.setClass(Game3Activity.this, AnimationActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("GameIndex", 3);
		bundle.putBoolean("isWin", true);
		bundle.putString("userName", userName);
		bundle.putInt("GameBadges", mBadges);
		bundle.putInt("GameHighScore", mHighScore);
		bundle.putInt("GameWinCount", mWinCount);
		bundle.putInt("NewScore", score);
        newAct.putExtras(bundle);
		startActivityForResult(newAct, 0);
		Game3Activity.this.finish();
		
		return 1;
	}
	
	@SuppressLint("FloatMath")
	static float calcDistance(Point p1, Point p2){
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		
		return FloatMath.sqrt(dx*dx + dy*dy);
	}
	
	
	class OvenHandler extends Handler {
        public void handleMessage(Message msg) {
        	Animation ovenAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
			ovenAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {	
					ovenView2.setVisibility(ImageView.GONE);
					ovenView2.clearAnimation();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			ovenView.setVisibility(ImageView.GONE);
			ovenView2.setAnimation(ovenAnim);
			ovenAnim.startNow();
			
			
			setFoodListener(cakeView);
			Animation cakeAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTLEFT_TO_CUR);
			cakeAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {
					eggbeatView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.game3_squeezer));
					cakeDottedLineView.setVisibility(ImageView.VISIBLE);
					PlayPalUtility.setLineGesture(true);
					isFirstAlarm = true;
					findViewById(R.id.timeReminder).setVisibility(View.INVISIBLE);
					turnOffTick();
					PlayPalUtility.initialProgressBar(CREAM_TIME, PlayPalUtility.TIME_MODE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			
			cakeView.setAnimation(cakeAnim);
			cakeAnim.startNow();
			
			PlayPalUtility.registerSingleHoverPoint(true, game3RelativeLayout, gameContext, new Callable<Integer>() {
				public Integer call() {
					return handleCakeAction(cakeView);
				}
			});
			
			for(int i=0; i<11; i++)
				PlayPalUtility.initialLineGestureParams(false, false, creamBoxSize, dottedLineArray[i]);	
		}
	};
	
	class ovenTimerTask extends TimerTask{
		public void run(){
			Message msg = new Message();
            Game3Activity.ovenHandler.sendMessage(msg);
            this.cancel();
		}
	}
}
