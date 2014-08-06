package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.os.AsyncTask;
import android.os.Bundle;
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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;

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
		new Point(780+374, 380+260),
		new Point(780+324, 380+370),
		new Point(780+260, 380+420),
		new Point(780+214, 380+500),
		new Point(780+266, 380+588),
		new Point(780+324, 380+620),
		new Point(780+332, 380+698),
		new Point(780+382, 380+786),
		new Point(780+490, 380+748),
		new Point(780+548, 380+690),
		new Point(780+638, 380+704),
		new Point(780+756, 380+682),
		new Point(780+764, 380+614),
		new Point(780+710, 380+512),
		new Point(780+752, 380+412),
		new Point(780+710, 380+316),
		new Point(780+588, 380+310)};
	
	protected Point[] creamPosArray = {
		new Point(780+324, 380+370),
		new Point(780+324, 380+620),
		new Point(780+548, 380+690),
		new Point(780+710, 380+512),	
		new Point(780+588, 380+310)};
	
	
	
	protected final int CREAM_MAX_RATIO = 20;
	protected final int SMALL_CREAM_SIZE = 50;
	protected final int LARGE_CREAM_SIZE = 150;
	
	protected final int MIX_PROGRESS_START = 1;
	protected final int MIX_PROGRESS_HALF  = 5;
	protected final int MIX_PROGRESS_END = 10;
	protected final int CREAM_PROGRESS_END = 17;
	
	protected final int MIX_TIME   = 600;
	protected final int CREAM_TIME = 600;
	
	protected int boxSize;
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

	AnimationDrawable mixStirAnim;
	AnimationDrawable ovenAnimation;
	protected DrawableRelativeLayout game3RelativeLayout;
	protected Context gameContext;
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
			int ratio = 0;
			int w = 50;
			int h = 50;
					
			@Override
			public boolean onHover(View arg0, MotionEvent event) {
				if(curButterView == null || !butterSqueezing){
					return false;
				}

				PlayPalUtility.curEntry = new RecordEntry(
						new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_HOVER_BTN_MOVE);
				
				if(curProgress < 12){
					if(ratio < CREAM_MAX_RATIO)
						ratio++;
					
					Point curPoint = new Point((int)event.getX(),(int)event.getY());
					float dist = calcDistance(startPoint, curPoint);
				
					if(dist>=25){
						curButterView = new ImageView(gameContext);
						curButterView.setImageResource(R.drawable.game3_cream);
						game3RelativeLayout.addView(curButterView);
						ratio = 0;	
						startPoint = new Point((int)event.getX(),(int)event.getY());
					}
					
					RelativeLayout.LayoutParams params = (LayoutParams) curButterView.getLayoutParams();			
					params.width = params.height = (int)(SMALL_CREAM_SIZE*ratio/20.0);
					params.setMargins(	cakeView.getLeft()+(int)event.getX()-params.height/2, 
										cakeView.getTop()+(int)event.getY()-params.width/2, 
										0, 0);
					
					curButterView.setLayoutParams(params);
				}
				else{
					if(ratio == 0){
						curButterView = new ImageView(gameContext);
						curButterView.setImageResource(R.drawable.game3_cream2);
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
				
				curButterView = new ImageView(gameContext);	
				curButterView.setImageResource(R.drawable.game3_cream);
				game3RelativeLayout.addView(curButterView);
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
				startActivityForResult(newAct, 0);
				Game3Activity.this.finish();
				return 0;
			}
		});
		PlayPalUtility.initialProgressBar(MIX_TIME, PlayPalUtility.TIME_MODE);
		
		PlayPalUtility.setHoverTarget(true, eggbeatView);
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
				PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
				PlayPalUtility.clearGestureSets();
				
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
		setFoodListener(ovenView2);
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!canTouchOven){
					Log.d("Penpal_oven","can't be touched agian");
					return;
				}
				
				canTouchOven = false;
				//ovenAnimation.stop();
				
				Animation ovenAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
				ovenAnim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation anim) {	
						ovenView2.setVisibility(ImageView.GONE);
						ovenView2.clearAnimation();
						
						curProgress++;
						progressCountText.setText("ProgressCount: " + new String("" + curProgress));
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}
				});
				currentFoodView.setAnimation(ovenAnim);
				ovenAnim.startNow();
				
				setFoodListener(cakeView);
				Animation cakeAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTLEFT_TO_CUR);
				cakeAnim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation anim) {
						eggbeatView.setImageResource(R.drawable.game3_squeezer);
						cakeDottedLineView.setVisibility(ImageView.VISIBLE);
						PlayPalUtility.setLineGesture(true);
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
				
				PlayPalUtility.registerHoverLineGesture(game3RelativeLayout, gameContext, new Callable<Integer>() {
					public Integer call() {
						return handleCakeAction(cakeView);
					}
				});
				
				PlayPalUtility.initialLineGestureParams(false, false, boxSize/2, 
						dottedLineArray[0],
						dottedLineArray[1],
						dottedLineArray[2],
						dottedLineArray[3],
						dottedLineArray[4],
						dottedLineArray[5],
						dottedLineArray[6],
						dottedLineArray[7],
						dottedLineArray[8],
						dottedLineArray[9],
						dottedLineArray[10],
						dottedLineArray[11],
						dottedLineArray[12],
						dottedLineArray[13],
						dottedLineArray[14],
						dottedLineArray[15],
						dottedLineArray[16],
						dottedLineArray[17]);
			}
		});
	}
		
	protected Integer handleStirring(View view){
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
			
			PenRecorder.outputJSON();
			PenRecorder.registerRecorder(game3RelativeLayout, this, userName, "3-2");
			
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
		
		cakeCreamHintView.setVisibility(ImageView.VISIBLE);
		PlayPalUtility.clearGestureSets();
		PlayPalUtility.unregisterHoverLineGesture(game3RelativeLayout);
		score += PlayPalUtility.killTimeBar();
		PlayPalUtility.initialProgressBar(CREAM_TIME, PlayPalUtility.TIME_MODE);
		
		PlayPalUtility.registerSingleHoverPoint(game3RelativeLayout, this, new Callable<Integer>() {
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
		
		return 1;
	}
	
	protected Integer handleCream(View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		int idx = PlayPalUtility.getLastTriggerSetIndex();
		PlayPalUtility.cancelGestureSet(idx);
		
		if(curProgress == CREAM_PROGRESS_END){
			cakeDottedLineView.setVisibility(ImageView.INVISIBLE);
			cakeStrawberryView.setVisibility(ImageView.VISIBLE);
			PlayPalUtility.setAlphaAnimation(cakeStrawberryView,true);
			
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterHoverLineGesture(game3RelativeLayout);
			
			PlayPalUtility.registerLineGesture(game3RelativeLayout, this, new Callable<Integer>() {
				public Integer call() {
					return handleCutting(null);		
				}
			});
			
			PenRecorder.outputJSON();
			PenRecorder.registerRecorder(game3RelativeLayout, this, userName, "3-4");
			
			PlayPalUtility.initialLineGestureParams(false, false, boxSize, new Point(1560,600) ,centralPoint,  new Point(1560,1160));
			PlayPalUtility.setStraightStroke(new Point(1560,600) ,centralPoint,  new Point(1560,1160));
			
			eggbeatView.setImageResource(R.drawable.game1_knife);
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
	
}
