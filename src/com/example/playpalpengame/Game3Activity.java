package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.os.Bundle;
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
	
	protected int[] foodResArray = {
		R.drawable.game3_mix1,
		R.drawable.game3_mix2,
		R.drawable.game3_mix3,
		R.drawable.game3_cake1,
		R.drawable.game3_cake1,	
		R.drawable.game3_cake2,
		R.drawable.game3_cake3 };
	
	
	protected Point centralPoint = new Point(1280,880);
	protected Point[] pointArray = {
			new Point(1480,880),
			new Point(1280,680),
			new Point(1080,880),
			new Point(1280,1080)};
		
	protected int boxSize;
	protected int curProgress;
	protected boolean canTouchOven = false;
	protected boolean butterSqueezing = false;
	
	TextView  progressCountText;
	ImageView bowlView;
	ImageView mixView;
	ImageView ovenView;
	ImageView cakeView;
	ImageView cakeDottedLineView;
	ImageView butterView;
	ImageView eggbeatView;
	
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
		ovenView = (ImageView)findViewById(R.id.Game3_oven);
		cakeView = (ImageView)findViewById(R.id.Game3_cake);
		cakeDottedLineView = (ImageView)findViewById(R.id.Game3_cakeDottedLine);
		butterView  = (ImageView)findViewById(R.id.Game3_butter);
		eggbeatView = (ImageView)findViewById(R.id.Game3_beat);
		
		mixView.setBackgroundResource(R.anim.game3_mix_stir_animation);
		mixStirAnim = (AnimationDrawable) mixView.getBackground();
		
		game3RelativeLayout = (RelativeLayout) findViewById(R.id.Game3RelativeLayout);
		game3RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                    	eggbeatView.setVisibility(ImageView.VISIBLE);
                        //Log.d("PlayPal", "Enter");
                        break;
                     
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX() - 200, (int)event.getY() - 200 , 0, 0);
                    	eggbeatView.setLayoutParams(params);
                    	//Log.d("PlayPal", "Move");
                        break;

                    case MotionEvent.ACTION_HOVER_EXIT:
                    	eggbeatView.setVisibility(ImageView.INVISIBLE);
                    	//Log.d("PlayPal", "Exit");
                        break;
                }
                return true;
            }
        });	
		
		mSPenEventLibrary = new SPenEventLibrary();
		mSPenEventLibrary.setSPenHoverListener(cakeView, new SPenHoverListener(){
			ImageView curButterView;
			int ratio = 0;
			int w = 200;
			int h = 200;
					
			@Override
			public boolean onHover(View arg0, MotionEvent event) {
				//Log.d("Penpal","hovering"+ratio);
				if(!butterSqueezing)
					return false;
				
				
				if(curButterView == null){
            		curButterView = new ImageView(gameContext);	
    				curButterView.setImageResource(R.drawable.game3_butter);
    				game3RelativeLayout.addView(curButterView);
				
				}

				if(ratio == 99 && curProgress < 7){
					curProgress++;progressCountText.setText("ProgressCount: " + new String("" + curProgress));
					drawView.invalidate();

					Log.d("Penpal","oven curprogress"+curProgress);
					currentFoodView.setImageResource(foodResArray[curProgress]);
					
					
					if(curProgress == 7){
						PlayPalUtility.setLineGesture(true);
						PlayPalUtility.initialLineGestureParams(false, true, boxSize, pointArray[0],pointArray[2]);
						PlayPalUtility.initialLineGestureParams(false, true, boxSize, pointArray[1],pointArray[3]);
					}
				}
				
				if(ratio < 100)
					ratio++;
				
				RelativeLayout.LayoutParams params = (LayoutParams) curButterView.getLayoutParams();
				params.height = (int)(w*ratio/100.0);
				params.width  = (int)(h*ratio/100.0);
				
				params.setMargins(	cakeView.getLeft()+(int)event.getX()-params.height/2, 
									cakeView.getTop()+(int)event.getY()-params.width/2, 
									0, 0);
				
				curButterView.setLayoutParams(params);
				
				return false;
			}

			@Override
			public void onHoverButtonDown(View arg0, MotionEvent arg1) {
				Log.d("Penpal","pressing");
				butterSqueezing = true;
				ratio = 0;
				curButterView = new ImageView(gameContext);	
				curButterView.setImageResource(R.drawable.game3_butter);
				game3RelativeLayout.addView(curButterView);
			}

			@Override
			public void onHoverButtonUp(View arg0, MotionEvent arg1) {
				butterSqueezing = false;
				Log.d("Penpal","releasing");
				ratio = 0;
				curButterView = null;
			}
			
		});

		PlayPalUtility.initDrawView(game3RelativeLayout, this);
		PlayPalUtility.setCircleStroke(centralPoint, 200);
		
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
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!canTouchOven){
					Log.d("Penpal_oven","can't be touched agian");
					return;
				}
				
				canTouchOven = false;
				ovenAnimation.stop();
				
				Animation ovenAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
				ovenAnim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationEnd(Animation anim) {	
						ovenView.setVisibility(ImageView.GONE);
						ovenView.clearAnimation();
						
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
				ovenView.setAnimation(ovenAnim);
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
				
				
				PlayPalUtility.registerLineGesture(game3RelativeLayout, gameContext, new Callable<Integer>() {
					public Integer call() {
						return handleLineAction2(cakeView);
					}
				});
			}
		});
		
	}
		
	
	protected Integer handleStirring(View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		Log.d("PenPalGame","curProgress "+curProgress);
		
		mixStirAnim.setVisible(true,true);
		mixStirAnim.start();
		
		if( curProgress == 10){
			currentFoodView.setImageResource(R.drawable.game3_mix3);
			
			
			Animation mixAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
			mixAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {	
					mixView.setVisibility(ImageView.GONE);
					mixView.clearAnimation();
					
					PlayPalUtility.setLineGesture(false);
					PlayPalUtility.clearGestureSets();
					
					setFoodListener(ovenView);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			mixView.setAnimation(mixAnim);
			mixAnim.startNow();
			
			ovenView.setBackgroundResource(R.anim.game3_oven_animation);
			ovenAnimation = (AnimationDrawable) ovenView.getBackground();
			ovenAnimation.start();
			
			
			canTouchOven = true;
			curProgress++;
			progressCountText.setText("ProgressCount: " + new String("" + curProgress));
			Log.d("Penpal","oven curprogress"+curProgress);
			setOvenListener(ovenView);
			
			PlayPalUtility.clearDrawView();
			PlayPalUtility.setLineGesture(false);
            PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
		}
		
		return 1;	
	}
	
	protected Integer handleLineAction2(View view){
		curProgress++;
		progressCountText.setText("ProgressCount: " + new String("" + curProgress));
		Log.d("Penpal","curProgress"+curProgress);
		
		if(curProgress<9){	
			currentFoodView.setImageResource(foodResArray[curProgress]);
		}
		else{
			PlayPalUtility.setLineGesture(false);
            PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
			
			Intent newAct = new Intent();
			newAct.setClass(Game3Activity.this, MainActivity.class);
			startActivityForResult(newAct, 0);
			Game3Activity.this.finish();
			
		}
		return 1;
	}
}
