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
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenHoverListener;


public class Game3Activity extends Activity {
	
	/*
	protected int[] foodResArray = {
		R.drawable.game3_mix1,
		R.drawable.game3_mix2,
		R.drawable.game3_mix3,
		R.drawable.game3_cake1,
		R.drawable.game3_cake1,	
		R.drawable.game3_cake2,
		R.drawable.game3_cake3 };
*/	
	
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
	protected final int MIX_PROGRESS_FIRST = 3;
	protected final int MIX_PROGRESS_SECOND = 6;
	protected final int MIX_PROGRESS_END = 10;
	protected final int CREAM_PROGRESS_END = 17;
	
	protected int boxSize;
	protected int curProgress;
	protected boolean canTouchOven = false;
	protected boolean butterSqueezing = false;
	
	TextView  progressCountText;
	ImageView bowlView;
	ImageView mixView;
	ImageView mixView2;
	ImageView ovenView;
	ImageView ovenView2;
	ImageView cakeView;
	ImageView cakeDottedLineView;
	ImageView cakeCreamView;
	ImageView cakeStrawberryView;
	ImageView eggbeatView;
	ImageView squeezerView;
	ImageView helicalView;
	
	//ImageView dottedLineView;
	ImageView currentFoodView;
	DrawView drawView;

	AnimationDrawable mixStirAnim;
	AnimationDrawable ovenAnimation;
	protected RelativeLayout game3RelativeLayout;
	protected Context gameContext;
	private SPenEventLibrary mSPenEventLibrary;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
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
		mixView =  (ImageView)findViewById(R.id.Game3_mix);
		mixView2 =  (ImageView)findViewById(R.id.Game3_mix2);
		ovenView = (ImageView)findViewById(R.id.Game3_oven);
		ovenView2 = (ImageView)findViewById(R.id.Game3_oven2);
		cakeView = (ImageView)findViewById(R.id.Game3_cake);
		cakeDottedLineView = (ImageView)findViewById(R.id.Game3_cakeDottedLine);
		eggbeatView = (ImageView)findViewById(R.id.Game3_beat);
		squeezerView = (ImageView)findViewById(R.id.Game3_beat);
		cakeCreamView = (ImageView)findViewById(R.id.Game3_cakeCream);
		cakeStrawberryView = (ImageView)findViewById(R.id.Game3_cakeStrawberry);
		helicalView = (ImageView)findViewById(R.id.Game3_helicalView);
		
		mixView.setBackgroundResource(R.anim.game3_mix_stir_animation);
		mixStirAnim = (AnimationDrawable) mixView.getBackground();
		
		game3RelativeLayout = (RelativeLayout) findViewById(R.id.Game3RelativeLayout);
		game3RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
            	
            	ImageView hoverItem;
            	if(curProgress<10)
            		hoverItem = eggbeatView;
            	else
            		hoverItem = squeezerView;//others
            		
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                    	hoverItem.setVisibility(ImageView.VISIBLE);
                        //Log.d("PlayPal", "Enter");
                        break;
                     
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX() - 200, (int)event.getY() - 200 , 0, 0);
                    	hoverItem.setLayoutParams(params);
                    	//Log.d("PlayPal", "Move");
                        break;

                    case MotionEvent.ACTION_HOVER_EXIT:
                    	hoverItem.setVisibility(ImageView.INVISIBLE);
                    	//Log.d("PlayPal", "Exit");
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
				
				if(curProgress<12){
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
				Log.d("Penpal","releasing");
				butterSqueezing = false;
				
				ratio = 0;
				curButterView = null;
			}
			
		});

		
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
	}	
	
	
	protected void setFoodListener(View targetView) {
		currentFoodView = (ImageView) targetView;
		currentFoodView.setVisibility(ImageView.VISIBLE);
	}
	
	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				PlayPalUtility.setLineGesture(false);
	            PlayPalUtility.clearGestureSets();
				PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
				
				Intent newAct = new Intent();
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
						cakeDottedLineView.setVisibility(ImageView.VISIBLE);
						
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

				PlayPalUtility.setLineGesture(true);
			}
		});
		
	}
		
	
	protected Integer handleStirring(View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		Log.d("PenPalGame","curProgress "+curProgress);
				
		if( curProgress == MIX_PROGRESS_SECOND){
			mixView2.setBackgroundResource(R.anim.game3_mix_stir_animation2);
			mixStirAnim = (AnimationDrawable) mixView2.getBackground();
			mixStirAnim.setVisible(true,true);
			mixStirAnim.start();
			
			PlayPalUtility.setAlphaAnimation(mixView, false);
			PlayPalUtility.setAlphaAnimation(mixView2, true);
			
			mixView.setVisibility(ImageView.GONE);
			setFoodListener(mixView2);
		}
		else if( curProgress == MIX_PROGRESS_END){
			helicalView.setVisibility(ImageView.GONE);
						
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
		}
		else{
			mixStirAnim.setVisible(true,true);
			mixStirAnim.start();
		}
		
		return 1;	
	}
	
	protected Integer handleCakeAction(View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		//cakeCreamView.setVisibility(ImageView.VISIBLE);
		PlayPalUtility.clearGestureSets();
		PlayPalUtility.unregisterHoverLineGesture(game3RelativeLayout);
		
		
		PlayPalUtility.registerHoverLineGesture(game3RelativeLayout, this, new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return handleCream(cakeCreamView);
			}
		});
		
		PlayPalUtility.setLineGesture(true);
		for(int i=0; i<5; i++)
			PlayPalUtility.initialLineGestureParams(false, false, boxSize/2, creamPosArray[i], creamPosArray[i]);
			
		
		return 1;
	}
	
	protected Integer handleCream(View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		
		int idx = PlayPalUtility.getLastTriggerSetIndex();
		PlayPalUtility.cancelGestureSet(idx);
		
		if(curProgress == CREAM_PROGRESS_END){
			cakeStrawberryView.setVisibility(ImageView.VISIBLE);
			PlayPalUtility.setAlphaAnimation(cakeStrawberryView,true);
			
			
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterHoverLineGesture(game3RelativeLayout);
			
			PlayPalUtility.registerLineGesture(game3RelativeLayout, this, new Callable<Integer>() {
				public Integer call() {
					return handleCutting(null);		
				}
			});
			PlayPalUtility.initialLineGestureParams(false, false, boxSize, new Point(1560,600) ,centralPoint,  new Point(1560,1160));
			PlayPalUtility.setStraightStroke(new Point(1560,600) ,centralPoint,  new Point(1560,1160));
			
		}
		return 1;
	}	
	
	protected Integer handleCutting(View view)
	{
		//finished game3
		PlayPalUtility.setLineGesture(false);
		PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
		PlayPalUtility.clearGestureSets();
		PlayPalUtility.clearDrawView();
		Intent newAct = new Intent();
		newAct.setClass(Game3Activity.this, AnimationActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("GameIndex", 3);//TODO
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
