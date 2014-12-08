package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

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
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class Practice3Activity extends Activity {
	protected Point centralPoint = new Point(1280,880);
	protected Point[] pointArray = {
		new Point(1480,880),
		new Point(1280,680),
		new Point(1080,880),
		new Point(1280,1080)};
		
	protected Point[] dottedLineArray = {
		new Point(780+324, 380+370),
		new Point(780+214, 380+500)};
	
	protected Point[] creamPosArray = {
		new Point(780+324, 380+370),
		new Point(780+324, 380+620),
		new Point(780+548, 380+690),
		new Point(780+710, 380+512),	
		new Point(780+588, 380+310)};
	
	private final static int TEACH_HAND_OFFSET_X = 45;
	private final static int TEACH_HAND_OFFSET_Y = 665;
	private final static int TEACH_HAND_DOWN_OFFSET_X = 70;
	private final static int TEACH_HAND_DOWN_OFFSET_Y = 720;
	private final static int TEACH_HAND_BTN_OFFSET_X = 250;
	private final static int TEACH_HAND_BTN_OFFSET_Y = 775;
	
	private final static int HELICAL_OFFSET_X = 500;
	private final static int HELICAL_OFFSET_Y = 500;
	
	protected final int CREAM_DIST = 30;
	protected final int INIT_CREAM_RATIO = 10;
	protected final int CREAM_MAX_RATIO = 20;
	protected final int SMALL_CREAM_SIZE = 75;
	protected final int LARGE_CREAM_SIZE = 150;
	
	protected final int MIX_PROGRESS_START = 1;
	protected final int MIX_PROGRESS_HALF  = 2;
	protected final int MIX_PROGRESS_END = 3;
	protected final int CREAM_PROGRESS_END = 5;
	
	protected final int MIX_TIME   = 600;
	protected final int CREAM_TIME = 600;
	
	public static OvenHandler ovenHandler;
	protected int boxSize;
	protected int creamBoxSize;
	protected int curProgress;
	protected boolean canTouchOven = false;
	protected boolean butterSqueezing = false;
	protected String userName = null;
	private int mBadges = 0;
	private int mHighScore = 0;
	private int mWinCount = 0;
	private int score = 0;
	
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
	ImageView teachHandView;
	
	FramesSequenceAnimation pressAnim;
	FramesSequenceAnimation btnAnim;
	AnimationDrawable mixStirAnim;
	AnimationDrawable ovenAnimation;
	
	protected DrawableRelativeLayout game3RelativeLayout;
	protected Context gameContext;
	private SPenEventLibrary mSPenEventLibrary;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		gameContext = this;
		
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
		
		ovenHandler = new OvenHandler();
		
		PlayPalUtility.playTeachVoice(this, 301, 302);
		
		curProgress = 0;
		boxSize = 100;
		creamBoxSize = 75;
		gameContext = this;
		
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
		teachHandView = (ImageView)findViewById(R.id.Game3_teachHand);
		
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
				
				if(curProgress < CREAM_PROGRESS_END-1){
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
					params.width = params.height = (int)(SMALL_CREAM_SIZE*ratio/20.0);
					params.setMargins(	cakeView.getLeft()+(int)event.getX()-params.height/2, 
										cakeView.getTop()+(int)event.getY()-params.width/2, 
										0, 0);
					
					curButterView.setLayoutParams(params);
					
					if(dist>=CREAM_DIST){
						PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_POP, Practice3Activity.this);
						curButterView = null;
						
						ratio = INIT_CREAM_RATIO;	
						startPoint = new Point((int)event.getX(),(int)event.getY());
					}
				}
				else {
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
				
				Log.d("Penpal","pressing");
				butterSqueezing = true;				
				
				ratio = INIT_CREAM_RATIO;
				startPoint = new Point((int)event.getX(),(int)event.getY());
				
				if(btnAnim!=null)
					btnAnim.stop();
				if(curProgress == MIX_PROGRESS_END){
					setTeachHandLinear(dottedLineArray[0].x-TEACH_HAND_BTN_OFFSET_X, dottedLineArray[0].y-TEACH_HAND_BTN_OFFSET_Y, dottedLineArray[1].x-dottedLineArray[0].x, dottedLineArray[1].y-dottedLineArray[0].y);
				}
			}

			@Override
			public void onHoverButtonUp(View arg0, MotionEvent event) {
				PlayPalUtility.curEntry = new RecordEntry(
						new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_HOVER_BTN_END);
				
				Log.d("Penpal","releasing");
				butterSqueezing = false;
				
				ratio = 0;
				curButterView = null;
			}
			
		});

		PlayPalUtility.setDebugMode(false);
		PlayPalUtility.initDrawView(game3RelativeLayout, this, (DrawView)findViewById(R.id.drawLineView));
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
		
		
		PlayPalUtility.setHoverTarget(true, eggbeatView);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	params.setMargins(centralPoint.x-TEACH_HAND_DOWN_OFFSET_X, centralPoint.y-TEACH_HAND_DOWN_OFFSET_Y, 0, 0);
    	teachHandView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.teach_hand3_down));
    	teachHandView.setLayoutParams(params);
		teachHandView.setVisibility(ImageView.VISIBLE);
			
		setTeachHandCircular(centralPoint.x-TEACH_HAND_DOWN_OFFSET_X, centralPoint.y-TEACH_HAND_DOWN_OFFSET_Y, 240);
		
	}	
	
	@Override
	protected void onPause() {
	    super.onPause();
	    BackgroundMusicHandler.recyle();
		PlayPalUtility.clearAllVoice();
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
				PlayPalUtility.clearDrawView();
				PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
				PlayPalUtility.clearGestureSets();
				
				Intent newAct = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("userName", userName);
	            newAct.putExtras(bundle);
				newAct.setClass(Practice3Activity.this, MainActivity.class);
				startActivityForResult(newAct, 0);
				Practice3Activity.this.finish();
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
			
			
			anim = AnimationsContainer.getInstance().createGame3StirAnim(mixView2,2);
		}
		else if( curProgress == MIX_PROGRESS_END){
			PlayPalUtility.playTeachVoice(gameContext, 303, 304);
			mixView.setVisibility(ImageView.GONE);
			
			teachHandView.clearAnimation();
			teachHandView.setVisibility(ImageView.INVISIBLE);

			helicalView.setVisibility(ImageView.GONE);

			//score += PlayPalUtility.killTimeBar();
			eggbeatView.setImageResource(0);
			Animation mixAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
			mixAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {	
					currentFoodView.setVisibility(ImageView.GONE);
					currentFoodView.clearAnimation();
					
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
			PlayPalUtility.unregisterLineGesture(game3RelativeLayout);

			
			anim = null;
		}
		
		if(anim != null)
			anim.start();
		game3RelativeLayout.invalidate();
		return 1;	
	}
	
	protected Integer handleCakeAction(View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		teachHandView.clearAnimation();
		
		PlayPalUtility.playTeachVoice(this, 308);
		
		btnAnim.start();
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	params.setMargins(creamPosArray[4].x-TEACH_HAND_BTN_OFFSET_X, creamPosArray[4].y-TEACH_HAND_BTN_OFFSET_Y, 0, 0);
    	teachHandView.setLayoutParams(params);
       
		cakeCreamHintView.setVisibility(ImageView.VISIBLE);
		cakeCreamHintView.bringToFront();
		game3RelativeLayout.invalidate();
		PlayPalUtility.clearGestureSets();
		PlayPalUtility.unregisterHoverLineGesture(game3RelativeLayout);
		PlayPalUtility.clearDrawView();
		
		PlayPalUtility.registerSingleHoverPoint(false,game3RelativeLayout, this, new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return handleCream(cakeCreamView);
			}
		});
		
		PlayPalUtility.setLineGesture(true);
		PlayPalUtility.initialLineGestureParams(false, false, boxSize/2, creamPosArray[4]);
		
	
		return 1;
	}
	
	protected Integer handleCream(View view){
		PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_CREAM, this);
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		int idx = PlayPalUtility.getLastTriggerSetIndex();
		PlayPalUtility.cancelGestureSet(idx);
		
		if(curProgress == CREAM_PROGRESS_END){
			
			mSPenEventLibrary.setSPenHoverListener(cakeView,null);
			teachHandView.clearAnimation();
			teachHandView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.teach_hand3_down));
			
			PlayPalUtility.playTeachVoice(this, 309, 310);
			
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(1560-TEACH_HAND_DOWN_OFFSET_X, 600-TEACH_HAND_DOWN_OFFSET_Y, 0, 0);
			teachHandView.setLayoutParams(params);
			
			teachHandView.setVisibility(ImageView.VISIBLE);
			
			//pressAnim = AnimationsContainer.getInstance().createGame3TeachHandAnim(teachHandView);
			//pressAnim.start();
			
			teachHandView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event){
					if(event.getAction() == MotionEvent.ACTION_DOWN) {
						setTeachHandLinear(1560-TEACH_HAND_DOWN_OFFSET_X, 600-TEACH_HAND_DOWN_OFFSET_Y, centralPoint.x-1560, centralPoint.y-600);
						v.setOnTouchListener(null);
					}
					return false;
				}
			});
			
			
			cakeDottedLineView.setVisibility(ImageView.INVISIBLE);
			cakeStrawberryView.setVisibility(ImageView.VISIBLE);
			cakeStrawberryView.bringToFront();
			game3RelativeLayout.invalidate();
			PlayPalUtility.setAlphaAnimation(cakeStrawberryView,true);
			
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterHoverLineGesture(game3RelativeLayout);
			
			PlayPalUtility.registerLineGesture(game3RelativeLayout, this, new Callable<Integer>() {
				public Integer call() {
					return handleCutting(null);		
				}
			});
			
			PlayPalUtility.clearDrawView();
			PlayPalUtility.initialLineGestureParams(false, false, boxSize, new Point(1560,600) ,centralPoint);
			PlayPalUtility.setStraightStroke(new Point(1560,600) ,centralPoint);
			
			eggbeatView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.game1_knife));
		}
		return 1;
	}	
	
	protected Integer handleCutting(View view) {
		//finishing game3
		//score += PlayPalUtility.killTimeBar();
		score = 0;
		PlayPalUtility.setLineGesture(false);
		PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
		PlayPalUtility.clearGestureSets();
		PlayPalUtility.clearDrawView();
		
		PlayPalUtility.playTeachVoice(this, 311);
		
		BackgroundMusicHandler.setCanRecycle(false);
    	
    	Intent newAct = new Intent();
		newAct.setClass(Practice3Activity.this, MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("userName", userName);
        newAct.putExtras(bundle);
		
		Timer timer = new Timer(true);
		timer.schedule(new WaitTimerTask(this, newAct), 5000);
				
		return 1;
	}
	
	
	private void setTeachHandLinear(int bX, int bY, int xOffset, int yOffset) {
		teachHandView.setVisibility(View.VISIBLE);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(bX, bY, 0, 0);
		teachHandView.setLayoutParams(params);
		
		Animation am = new TranslateAnimation(0, xOffset, 0, yOffset);
		am.setDuration(2000);
		am.setRepeatCount(-1);
		
		teachHandView.startAnimation(am);
	}
	
	private void setTeachHandCircular(int bX, int bY, int range) {
		teachHandView.setVisibility(View.VISIBLE);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(bX, bY, 0, 0);
		teachHandView.setLayoutParams(params);
		
		Animation am = new CircularTranslateAnimation(teachHandView, range);
		am.setDuration(2000);
		am.setRepeatCount(-1);
		am.setFillEnabled(true);
		am.setFillAfter(true);
		am.setFillBefore(true);
		teachHandView.startAnimation(am);
	}

	
	@SuppressLint("FloatMath")
	static float calcDistance(Point p1, Point p2){
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		
		return FloatMath.sqrt(dx*dx + dy*dy);
	}
	
	
	class OvenHandler extends Handler {
		public void handleMessage(Message msg){
			Animation ovenAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
			ovenAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {	
					ovenView2.setVisibility(ImageView.GONE);
					ovenView2.clearAnimation();
					
					PlayPalUtility.playTeachVoice(gameContext, 306, 307); 
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			
			ovenView.setAnimation(ovenAnim);;
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
					
					teachHandView.setImageBitmap(null);
					teachHandView.setVisibility(ImageView.VISIBLE);
					
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                	params.setMargins(dottedLineArray[0].x-TEACH_HAND_BTN_OFFSET_X, dottedLineArray[0].y-TEACH_HAND_BTN_OFFSET_Y, 0, 0);
                	teachHandView.setLayoutParams(params);

                	btnAnim = AnimationsContainer.getInstance().createGame3TeachHandBtnAnim(teachHandView);
					btnAnim.start();
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
			
			PlayPalUtility.registerHoverLineGesture(game3RelativeLayout, gameContext, new Callable<Integer>() {
				public Integer call() {
					return handleCakeAction(cakeView);
				}
			});
			
			
			PlayPalUtility.initialLineGestureParams(false, false, creamBoxSize, 
					dottedLineArray[0],
					dottedLineArray[1]);
		}
	}
	
	
	class ovenTimerTask extends TimerTask{
		public void run(){
			Message msg = new Message();
            Practice3Activity.ovenHandler.sendMessage(msg);
            PlayPalUtility.playTeachVoice(gameContext, 305);
            this.cancel();
		}
	}
	
}
