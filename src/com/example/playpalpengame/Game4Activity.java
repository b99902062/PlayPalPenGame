package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

import android.os.Bundle;
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
	protected final int CREAM_MAX_RATIO = 10;
	protected boolean canTouchOven = false;
	protected boolean butterSqueezing = false;
	
	protected final int DOUGH_TIME  = 600;
	protected final int COOKIE_TIME = 600;
	protected final int CREAM_TIME  = 1800;
	
	protected final int DOUGH_PROGRESS_END  = 4;
	protected final int COOKIE_PROGRESS_END = DOUGH_PROGRESS_END + 8;
	protected final int CREAM_PROGRESS_END  = COOKIE_PROGRESS_END + 80;	
	
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
	protected Context gameContext;
	
	private int mBadges = 0;
	private int mHighScore = 0;
	private int mWinCount = 0;
	private int score = 0;
	
	protected Point centerPoint = new Point(1280,800);
	protected int dx = 300;
	protected int dy = 160;
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
	protected Point[]  cookiePosArray = {
		new Point(420,440),
		new Point(420,920),
		new Point(860,440),
		new Point(860,920),
		new Point(1300,440),
		new Point(1300,920),
		new Point(1760,440),
		new Point(1760,920)};
	
	protected int[] cookieResArray = {
		R.drawable.game4_cookie1,	
		R.drawable.game4_cookie2,
		R.drawable.game4_cookie3
	};
	
	protected int[] cookieResArray2 = {
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
	
	protected int[] cookieAnimArray = {
		R.anim.game4_cookie1_animation,
		R.anim.game4_cookie2_animation,
		R.anim.game4_cookie3_animation
	};
	
	protected int[] creamArray = {
		R.drawable.game4_cream1,
		R.drawable.game4_cream2,
		R.drawable.game4_cream3,
		R.drawable.game4_cream4,
		R.drawable.game4_cream5,
		R.drawable.game4_cream6			
	};
	
	//TO BE FIXED
	protected Point[][] cookieCreamOffsetArray = new Point[][]{
			//T
			{new Point(0,-150),  new Point(-25,-100), new Point(25,-100), new Point(-50,-50), new Point(0,-50),
			 new Point(50,-50),  new Point(-75,0),    new Point(-25,0),   new Point(25,0),    new Point(75,0),
			 new Point(-100,50),new Point(-50,50),  new Point(0,50),   new Point(50,50),  new Point(100,50), new Point(100,50)},
			//S
			 {new Point(-150,-150),new Point(-50,-150),new Point(50,-150),new Point(150,-150),
			  new Point(-150,-50), new Point(-50,-50), new Point(50,-50), new Point(150,-50),
			  new Point(-150,50),  new Point(-50,50),  new Point(50,50),  new Point(150,50),
			  new Point(-150,150), new Point(-50,150), new Point(50,150), new Point(150,150)}, 
			//C
			{new Point(-150,-150),new Point(-50,-150),new Point(50,-150),new Point(150,-150),
		     new Point(-150,-50), new Point(-50,-50), new Point(50,-50), new Point(150,-50),
		     new Point(-150,50),  new Point(-50,50),  new Point(50,50),  new Point(150,50),
		     new Point(-150,150), new Point(-50,150), new Point(50,150), new Point(150,150)}};
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		PlayPalUtility.setDebugMode(true);
		
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
		
		curProgress = 0;
		boxSize = 50;
		
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

		PlayPalUtility.initDrawView(game4RelativeLayout, this);
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
				startActivityForResult(newAct, 0);
				Game4Activity.this.finish();
				return 0;
			}
		});
		PlayPalUtility.initialProgressBar(DOUGH_TIME, PlayPalUtility.TIME_MODE);
		PenRecorder.registerRecorder(game4RelativeLayout, this, userName, "4-1");
		
		PlayPalUtility.setHoverTarget(true, laddleView);
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
						curButterView = new ImageView(gameContext);
						curButterView.setImageResource(creamArray[curCookie.creamColor]);
						game4RelativeLayout.addView(curButterView);
						ratio = CREAM_INIT_RATIO;	
						startPoint = new Point((int)event.getX(),(int)event.getY());
					}
					
					RelativeLayout.LayoutParams params = (LayoutParams) curButterView.getLayoutParams();			
					params.width = params.height = (int)(CREAM_SIZE*ratio/20.0);
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
					curButterView.setImageResource(R.drawable.game3_cream);
					game4RelativeLayout.addView(curButterView);
					ratio = 0;
					
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
					return handleCookieProgress(cookieArray[0].view);
				}
			});
	}
	
	protected Integer handleDoughProgress (View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		if(curProgress == DOUGH_PROGRESS_END){
			score += PlayPalUtility.killTimeBar();
			
			laddleView.setImageResource(R.drawable.game4_thinknife);
			PenRecorder.outputJSON();

			PlayPalUtility.clearGestureSets();
			PlayPalUtility.clearDrawView();
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
	
	protected Integer handleDoughAction (View view){
		int x,y,w,h,dxy=100;
		ImageView curDough = (ImageView)findViewById(doughViewArray[Math.min(curProgress+1, DOUGH_NUM-1)]);
		curDough.setVisibility(ImageView.VISIBLE);
		Point p = PlayPalUtility.curEntry.point;
		
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
	
	
	protected Integer handleCookieProgress (View view){
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
						
						for(int j=0; j<16; j++)
							PlayPalUtility.initialLineGestureParams(false, true, CREAM_BOX_SIZE, pointAddition(curCookie.center, cookieCreamOffsetArray[curCookie.type][j]));
						
						Animation cookieAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTLEFT_TO_CUR);
						cookieAnim.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationEnd(Animation anim) {
								if(isFirstCookie) {
									PlayPalUtility.initialProgressBar(CREAM_TIME, PlayPalUtility.TIME_MODE);
									laddleView.setImageResource(R.drawable.game4_squeezer);
									isFirstCookie = false;
								}
								curCookie.view.clearAnimation();
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationStart(Animation animation) {
							}
						});
						curCookie.view.setAnimation(cookieAnim);
						cookieAnim.startNow();
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}
				});
				//curCookie.view.bringToFront();
				game4RelativeLayout.invalidate();
				
				curCookie.view.setAnimation(cookieAnim);
				cookieAnim.startNow();	
			}
			
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.registerSingleHoverPoint(true, game4RelativeLayout, this, new Callable<Integer>() {
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
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		int idx = PlayPalUtility.getLastTriggerSetIndex();
		PlayPalUtility.cancelGestureSet(idx);
		
		if(curProgress >= CREAM_PROGRESS_END){
			score += PlayPalUtility.killTimeBar();
			PlayPalUtility.setLineGesture(false);
			PlayPalUtility.unregisterLineGesture(game4RelativeLayout);
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.clearDrawView();
			PenRecorder.outputJSON();
			PenRecorder.registerRecorder(game4RelativeLayout, this, userName, "4-4");
			
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
			Game4Activity.this.finish();
			return 0;
		}
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
		protected int cookieRadius = 175;
		protected int temp_Length  = (int)(cookieRadius/Math.sqrt(2));
		protected int temp_Length2 = (int)(cookieRadius*Math.sin(Math.PI/3));
		protected int temp_Length3 = (int)(cookieRadius*Math.cos(Math.PI/3));
		
		protected Point center;
		protected ImageView view;
		protected Point[][] offsetArray = new Point[][]{
				//TRIANGULAR_COOKIE
				{new Point(0,-cookieRadius), new Point(-temp_Length2,temp_Length3), new Point(temp_Length2,temp_Length3), new Point(0,-cookieRadius)},
				//SQUARE_COOKIE
				{new Point(temp_Length,temp_Length), new Point(temp_Length,-temp_Length), new Point(-temp_Length,-temp_Length), new Point(-temp_Length,temp_Length) },
				//CIRCLE_COOKIE
				{new Point(cookieRadius,0),  new Point(0,-cookieRadius),  new Point(-cookieRadius,0), new Point(0,cookieRadius)}};
		
		
		public Cookie(int _id, int _t, ImageView _v){
			id   = _id;
			type = _t;
			view = _v;
			view.setVisibility(ImageView.VISIBLE);		
			view.setBackgroundResource(cookieResArray[type]);
			center = new Point(view.getLeft()+200, view.getTop()+200);
			
		}
		
		public void beCutted(){
			PlayPalUtility.eraseStroke(this.id);
			view.setBackgroundResource(cookieAnimArray[type]);
			AnimationDrawable cutAnim = (AnimationDrawable) view.getBackground();
			cutAnim.start();
		}
		
		public void beBaked(){
			view.setBackgroundResource(cookieResArray3[type]);//Color.TRANSPARENT
			
			Random rand = new Random();
			center = pointAddition(center, new Point( rand.nextInt(200)-100, rand.nextInt(200)-100) );
			
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
				pointAddition(this.center, offsetArray[this.type][3]));
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
	}
}