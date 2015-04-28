package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

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
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;

import com.example.playpalpengame.Game4Activity.Cookie;
import com.example.playpalpengame.Game4Activity.cookieCuttingHandler;
import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenHoverListener;

public class Practice4Activity extends Activity {
	protected final int CREAM_BOX_SIZE = 50;
	protected final int CREAM_COLOR_NUM = 6;
	protected final int CREAM_SIZE = 100;
	protected final int CREAM_DIST = 10;
	protected final int CREAM_INIT_RATIO = 5;
	protected final int CREAM_MAX_RATIO = 8;
	
	protected boolean canTouchOven = false;
	protected boolean butterSqueezing = false;
	
	protected final int DOUGH_TIME  = 600;
	protected final int COOKIE_TIME = 600;
	protected final int CREAM_TIME  = 1800;
	
	private final static int TEACH_HAND_OFFSET_X = 45;
	private final static int TEACH_HAND_OFFSET_Y = 665;
	private final static int TEACH_HAND_DOWN_OFFSET_X = 70;
	private final static int TEACH_HAND_DOWN_OFFSET_Y = 720;
	private final static int TEACH_HAND_BTN_OFFSET_X = 260;
	private final static int TEACH_HAND_BTN_OFFSET_Y = 790;
	
	
	protected final int DOUGH_PROGRESS_END  = 1;
	protected final int COOKIE_PROGRESS_END = 3;
	protected final int CREAM_PROGRESS_HALF = 11;
	protected final int CREAM_PROGRESS_END  = 19;	
	
	protected final int COOKIE_NUM = 2;
	protected final int DOUGH_NUM = 5;
	
	private boolean isFirstCookie = true;
	
	public Point pointAddition(Point p1, Point p2){
		return new Point(p1.x+p2.x, p1.y+p2.y);
	}
	
	protected FramesSequenceAnimation btnAnim;
	protected FramesSequenceAnimation pressAnim;
	protected TextView  progressCountText;
	protected DrawableRelativeLayout game4RelativeLayout;
	protected SPenEventLibrary mSPenEventLibrary;
	
	protected ImageView doughView;
	protected ImageView laddleView;
	private ImageView teachHandView;
	
	protected int boxSize;
	protected int curProgress;
	protected int curCookieType;
	
	protected String userName;
	protected Context gameContext;
	
	private int mBadges = 0;
	private int mHighScore = 0;
	private int mWinCount = 0;
	private int score = 0;
	
	protected static cookieCuttingHandler cookieCuttingHandler;
	protected Point centerPoint = new Point(1280,800);
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
		
	protected Cookie[] cookieArray = new Cookie[2];
	
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

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		PlayPalUtility.setDebugMode(false);

		gameContext = this;
		Bundle bundle = getIntent().getExtras();
		userName = bundle.getString("userName");
		mBadges = bundle.getInt("GameBadges");
		mHighScore = bundle.getInt("GameHighScore");
		mWinCount = bundle.getInt("GameWinCount");
		
		PlayPalUtility.playTeachVoice(this, 401, 402);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_game4);

		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);		
		
		progressCountText = (TextView)findViewById(R.id.testProgressCount);
		doughView = (ImageView)findViewById(doughViewArray[0]);
		laddleView = (ImageView)findViewById(R.id.Game4_ladle);		
		game4RelativeLayout = (DrawableRelativeLayout) findViewById(R.id.Game4RelativeLayout);
		teachHandView = (ImageView)findViewById(R.id.Game4_teachHand);
		cookieCuttingHandler = new cookieCuttingHandler();		
		
		curProgress = 0;
		boxSize = 70;
		
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
                    	laddleView.setVisibility(ImageView.INVISIBLE);
                        break;
                }
                return true;
            }
        });	
				
		gameContext = this;
		mSPenEventLibrary = new SPenEventLibrary();
		
		PlayPalUtility.setHoverTarget(true, laddleView);
		
		setTeachHandLinear(centerPoint.x - TEACH_HAND_DOWN_OFFSET_X, centerPoint.y - TEACH_HAND_DOWN_OFFSET_Y, doughPosArray[0][3].x - centerPoint.x, doughPosArray[0][3].y - centerPoint.y);
		teachHandView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.teach_hand4_down));
		teachHandView.setVisibility(ImageView.VISIBLE);
		
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
	
	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PlayPalUtility.killTimeBar();
				PlayPalUtility.setLineGesture(false);
				PlayPalUtility.unregisterLineGesture(game4RelativeLayout);
				PlayPalUtility.clearGestureSets();
				PlayPalUtility.clearDrawView();
				
				Intent newAct = new Intent();
				newAct.setClass(Practice4Activity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", 4);
				bundle.putString("userName", userName);
	            newAct.putExtras(bundle);				
				startActivityForResult(newAct, 0);
				Practice4Activity.this.finish();
			}
		});
	}
	
	private void setTeachHandLinear(int bX, int bY, int xOffset, int yOffset) {
		teachHandView.setVisibility(View.VISIBLE);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(bX, bY, 0, 0);
		teachHandView.setLayoutParams(params);
		
		Animation am = new TranslateAnimation(0, xOffset, 0, yOffset);
		am.setDuration(2000);
		am.setRepeatCount(-1);
		
		teachHandView.startAnimation(am);
	}
	
	protected void initCookieView(){
		teachHandView.clearAnimation();		
		Random ran = new Random();
		for(int i=0; i<cookieArray.length; i++){
			if(i == 0 )
				cookieArray[i] = new Cookie(i, 2, (ImageView)findViewById(cookieViewArray[4]));
			else
				cookieArray[i] = new Cookie(i, 2, (ImageView)findViewById(cookieViewArray[6]));
			
			final Cookie curCookie = cookieArray[i];
			curCookie.setCreamColor();
			curCookie.setGestureDottedLine();
			curCookie.view.setVisibility(ImageView.INVISIBLE);
			
			mSPenEventLibrary.setSPenHoverListener(curCookie.view, new SPenHoverListener(){
				Point startPoint;
				ImageView curButterView;
				int ratio = CREAM_INIT_RATIO;
				int w = 50;
				int h = 50;
				
				@Override
				public boolean onHover(View arg0, MotionEvent event) {
					if(!butterSqueezing){
						return false;
					}
					
					if(startPoint == null)
						startPoint = new Point((int)event.getX(),(int)event.getY());
					
					PlayPalUtility.curEntry = new RecordEntry(new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_HOVER_BTN_MOVE);
					
					if(ratio < CREAM_MAX_RATIO)
						ratio++;
						
					Point curPoint = new Point((int)event.getX(),(int)event.getY());
					float dist = calcDistance(startPoint, curPoint);
				
					if(dist>=CREAM_DIST){
						PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_POP, Practice4Activity.this);
						curButterView = new ImageView(gameContext);
						curButterView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, creamArray[curCookie.creamColor]));
						game4RelativeLayout.addView(curButterView);
						
						ratio = CREAM_INIT_RATIO;	
						startPoint = new Point((int)event.getX(),(int)event.getY());
					}
					
					if(curButterView != null){
						RelativeLayout.LayoutParams params = (LayoutParams) curButterView.getLayoutParams();			
						params.width = params.height = (int)(CREAM_SIZE*ratio/CREAM_MAX_RATIO);
						params.setMargins(curCookie.view.getLeft()+(int)event.getX()-params.height/2, 
								curCookie.view.getTop()+(int)event.getY()-params.width/2, 0, 0);	
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
					
					curButterView = new ImageView(gameContext);
					game4RelativeLayout.addView(curButterView);
					
					ratio = CREAM_INIT_RATIO;
					startPoint = new Point((int)event.getX(),(int)event.getY());
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
		}
		
		cookieArray[0].setGesturePoint();
		
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

			PlayPalUtility.clearGestureSets();
			PlayPalUtility.clearDrawView();
			
			teachHandView.clearAnimation();
			PlayPalUtility.playTeachVoice(this, 403, 404);
			
			initCookieView();
			
			setTeachHandCircular(cookieArray[0].center.x-TEACH_HAND_DOWN_OFFSET_X, cookieArray[0].center.y-TEACH_HAND_DOWN_OFFSET_Y, 160);
			teachHandView.setVisibility(ImageView.VISIBLE);
		}
		else if(curProgress < DOUGH_PROGRESS_END){
			
			PlayPalUtility.clearGestureSets();			
			PlayPalUtility.initialLineGestureParams(false, true, boxSize, doughPosArray[curProgress][0], doughPosArray[curProgress][1], doughPosArray[curProgress][2], doughPosArray[curProgress][3]);

			PlayPalUtility.clearDrawView();
			PlayPalUtility.setStraightStroke(centerPoint,doughPosArray[curProgress][3]);
		}
 		
		return 1;
	}
	
	
	//dealing with its size
	protected Integer handleDoughAction (View view){
		int x,y,w,h,dxy=100;
		ImageView curDough = (ImageView)findViewById(doughViewArray[Math.min(curProgress+1, DOUGH_NUM-1)]);
		curDough.setVisibility(ImageView.VISIBLE);
		
		int subProgress = PlayPalUtility.getLastTriggerPointIndex();
		Point p = doughPosArray[curProgress][subProgress+1];
		
		switch(curProgress){
			case 0:
				x = centerPoint.x-dxy;
				y = p.y; 
				break;
	
			case 1:
				x = centerPoint.x-dxy;
				y = centerPoint.y-dxy;
				break;
				
			case 2:
				x = p.x;
				y = centerPoint.y-dxy;
				break;
			
			case 3:
				x = p.x;
				y = p.y;
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
		
		
		if(curProgress == COOKIE_PROGRESS_END-1 ){
			
			
			setTeachHandCircular(cookieArray[1].center.x-TEACH_HAND_DOWN_OFFSET_X, cookieArray[1].center.y-TEACH_HAND_DOWN_OFFSET_Y, 160);
			cookieArray[0].view.setVisibility(ImageView.VISIBLE);
			cookieArray[0].beCutted();
			cookieArray[1].setGesturePoint();
		}
		else if(curProgress == COOKIE_PROGRESS_END){
			teachHandView.clearAnimation();
			teachHandView.setVisibility(ImageView.INVISIBLE);
				
			cookieArray[1].view.setVisibility(ImageView.VISIBLE);
			cookieArray[1].beCutted();
			
			score += PlayPalUtility.killTimeBar();
			PlayPalUtility.clearDrawView();
			
			for (int i=0; i<2; i++)
			{
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
						
						if(isFirstCookie){
							PlayPalUtility.playTeachVoice(gameContext, 405, 406);
							isFirstCookie = false;
						}
						
						PlayPalUtility.setAlphaAnimation(curCookie.view, true);
						laddleView.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, R.drawable.game4_squeezer));
						
						teachHandView.setVisibility(ImageView.VISIBLE);
						FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params.setMargins(curCookie.center.x-TEACH_HAND_BTN_OFFSET_X, curCookie.center.y-TEACH_HAND_BTN_OFFSET_Y-50, 0, 0);
						teachHandView.setLayoutParams(params);
						teachHandView.bringToFront();
						game4RelativeLayout.invalidate();							
						
						btnAnim = AnimationsContainer.getInstance().createGame4TeachHandBtnAnim(teachHandView);
						btnAnim.start();
						//PlayPalUtility.setLineGesture(false);
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
			
			
				
			for(int j=0; j<16; j++)
				PlayPalUtility.initialLineGestureParams(false, true, CREAM_BOX_SIZE, pointAddition(cookieArray[0].center, cookieCreamOffsetArray[cookieArray[0].type][j]));
			
			
		
			PlayPalUtility.registerSingleHoverPoint(true, game4RelativeLayout, this, new Callable<Integer>() {
				public Integer call() {
					return handleCookieCreamAction(cookieArray[0].view);
				}
			});
	
			PlayPalUtility.setLineGesture(true);
			
		}
		return 1;
	}
	
	
	
	protected Integer handleCookieCreamAction(View view){    
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		int idx = PlayPalUtility.getLastTriggerSetIndex();
		PlayPalUtility.cancelGestureSet(idx);
		

		if(curProgress == CREAM_PROGRESS_HALF-1){
			PlayPalUtility.playTeachVoice(this, 407);
			
			cookieArray[0].doFinish();
			
			PlayPalUtility.setLastSingleHoverPoint(true);
		}
		else if(curProgress == CREAM_PROGRESS_HALF){
			
			teachHandView.clearAnimation();
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(cookieArray[1].center.x-TEACH_HAND_BTN_OFFSET_X, cookieArray[1].center.y-TEACH_HAND_BTN_OFFSET_Y, 0, 0);
			teachHandView.setLayoutParams(params);
										
			PlayPalUtility.clearGestureSets();
			for(int j=0; j<16; j++)
				PlayPalUtility.initialLineGestureParams(false, true, CREAM_BOX_SIZE, pointAddition(cookieArray[1].center, cookieCreamOffsetArray[cookieArray[1].type][j]));
			PlayPalUtility.setLastSingleHoverPoint(false);
		}else if(curProgress == CREAM_PROGRESS_END-1){
			PlayPalUtility.playTeachVoice(this, 408);
			cookieArray[1].doFinish();
			
			PlayPalUtility.setLastSingleHoverPoint(true);
		}else if(curProgress >= CREAM_PROGRESS_END){
			
			score += PlayPalUtility.killTimeBar();
			
			PlayPalUtility.setLineGesture(false);
			PlayPalUtility.unregisterLineGesture(game4RelativeLayout);
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.clearDrawView();
			
			
			
			BackgroundMusicHandler.setCanRecycle(false);
	    	
	    	Intent newAct = new Intent();
			newAct.setClass(Practice4Activity.this, MainActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("userName", userName);
	        newAct.putExtras(bundle);
			
			Timer timer = new Timer(true);
			timer.schedule(new WaitTimerTask(this, newAct), 5000);
			
		}
		
		game4RelativeLayout.invalidate();
		return 1;
	}
	
	@SuppressLint("FloatMath")
	static float calcDistance(Point p1, Point p2){
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		
		return FloatMath.sqrt(dx*dx + dy*dy);
	}
	
	private void setTeachHandCircular(int bX, int bY, int range) {
		teachHandView.setVisibility(View.VISIBLE);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(bX, bY, 0, 0);
		teachHandView.setLayoutParams(params);
		
		Animation am = new CircularTranslateAnimation(teachHandView, range);
		am.setDuration(2000);
		am.setRepeatCount(1);//bug #13
		//am.setFillEnabled(true);
		//am.setFillAfter(true);
		//am.setFillBefore(true);
		teachHandView.startAnimation(am);
	}
	
	
	public class Cookie{
		protected final int TRIANGULAR_COOKIE=0;
		protected final int SQUARE_COOKIE	 =1;
		protected final int CIRCLE_COOKIE	 =2;
		
		protected int id;
		protected int type;
		protected int creamColor;
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
			id   = _id;
			type = _t;
			view = _v;
			view.setVisibility(ImageView.VISIBLE);
			
			center = new Point(view.getLeft()+200, view.getTop()+200);
		}
		
		public void beCutted(){
			PlayPalUtility.eraseStroke(0);
			//FramesSequenceAnimation cutAnim = AnimationsContainer.getInstance().createGame4CookieAnim(view, type);
			//cutAnim.start();
			this.view.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, cuttingCookieArray[this.type]));
			Timer timer = new Timer(true);
			timer.schedule(new cookieCuttingTask(id), 750, 500);
		}
		
		public void beBaked(){
			view.setImageBitmap(BitmapHandler.getLocalBitmap(gameContext, cookieResArray3[type]));
		}
		
		
		
		public void setCreamColor(int c){
			if(c>=0 && c < CREAM_COLOR_NUM)
				creamColor = c;
			else
				setCreamColor();
		}
		
		public void setCreamColor(){
			Random rand = new Random();
			creamColor = rand.nextInt(CREAM_COLOR_NUM);
		}
		
		public int getCreamColor(){
			return creamColor;
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
		
		public void doFinish(){
			
			FramesSequenceAnimation largeAnim = AnimationsContainer.getInstance().createGame4BigCookieAnim(view, type);
			PlayPalUtility.playSoundEffect (PlayPalUtility.SOUND_COOKIE_POP, Game4Activity.gameContext);
			largeAnim.start(); 
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
            Practice4Activity.cookieCuttingHandler.sendMessage(msg);
            this.cancel();
		}
	}
}