package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;


public class Game3Activity extends Activity {
	
	protected int[] foodResArray = {
		R.drawable.game3_mix1,
		R.drawable.game3_mix2,
		R.drawable.game3_mix3,
		
		R.drawable.game3_cake1,	
		R.drawable.game3_cake2,
		R.drawable.game3_cake3,
		R.drawable.game3_cake4,
		R.drawable.game3_cake5 };
	
	
	protected Point centerPoint = new Point(1280,800);	//center of 2560*1600
	protected Point[] pointArray = {
			new Point(1660,800),
			new Point(1280,400),
			new Point(900,800),
			new Point(1280,1200),
			
			new Point(1860,800),
			new Point(1280,200),
			new Point(700,800),
			new Point(1280,1400), };
		
	protected int boxSize;
	protected int cakeProgress;
	protected int mixingProgress;
	protected int curProgress;
		
	
	
	ImageView mixView;
	ImageView ovenView;
	ImageView eggbeatView;
	ImageView dottedLineView;
	ImageView currentFoodView;

	AnimationDrawable ovenAnimation;
	protected RelativeLayout game3RelativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game3);

		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);
		
		curProgress    = 0;
		mixingProgress = 0;
		boxSize = 100;
		
		
		mixView =  (ImageView)findViewById(R.id.Game3_mix);
		ovenView = (ImageView)findViewById(R.id.Game3_oven);
		eggbeatView = (ImageView)findViewById(R.id.Game3_eggbeat);
		dottedLineView = (ImageView) findViewById(R.id.dottedLineView);
		game3RelativeLayout = (RelativeLayout) findViewById(R.id.Game3RelativeLayout);
		
		setFoodListener(mixView);

		//setMixListener(mixView);
		
		PlayPalUtility.registerLineGesture(game3RelativeLayout, this, new Callable<Integer>() {
			public Integer call() {
				return handleLineAction(mixView);
			}
		});

		PlayPalUtility.setLineGesture(true);
		PlayPalUtility.initialLineGestureParams(boxSize, centerPoint, 
				pointArray[4*mixingProgress],
				pointArray[4*mixingProgress+1],
				pointArray[4*mixingProgress+2],
				pointArray[4*mixingProgress+3]);
		
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(PlayPalUtility.getPoint(0, 0).x - boxSize, PlayPalUtility.getPoint(0, 0).y + boxSize, 0, 0);		
		dottedLineView.setLayoutParams(params);
		dottedLineView.setVisibility(ImageView.VISIBLE);
		
		
		
		
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

	protected void setMixListener(View targetView){		
		targetView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(curProgress != 0)
					return;
				
				Log.d("PenPalGame",""+mixingProgress);
				if(mixingProgress>=2){
					Animation mixAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
					mixAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							mixView.setVisibility(ImageView.VISIBLE);
							
							ovenView.setBackgroundResource(R.drawable.game3_oven_animation);
							ovenAnimation = (AnimationDrawable) ovenView.getBackground();
							ovenAnimation.start();
							
							curProgress++;
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
					
					mixView.setVisibility(ImageView.GONE);
					return;
				}
				
				mixingProgress++;
				//((ImageView) view).setImageResource(doughArray[mixingProgress]);
				Log.d("PenPalGame",""+mixingProgress);
			}
		});
	}
	
	
	protected void setOvenListener(View targetView){
		targetView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(curProgress != 1)
					return;
				
				ovenAnimation.stop();
				curProgress++;
			}
		});
	}
	
	protected void setCakeListener(View targetView){
	targetView.setOnClickListener(new View.OnClickListener(){
		@Override
		public void onClick(View view){
			if(curProgress != 2)
				return;
			
			curProgress++;
			}
		});
	}
	
	
	protected Integer handleLineAction(View view){
		Log.d("PenPalGame","mixingProgress "+mixingProgress);		
		
		mixingProgress++;
		currentFoodView.setImageResource(foodResArray[mixingProgress]);
		
		
		if(mixingProgress < 2){
			PlayPalUtility.changeGestureParams(false, 0, 
					centerPoint, 
					pointArray[4*mixingProgress],
					pointArray[4*mixingProgress+1],
					pointArray[4*mixingProgress+2],
					pointArray[4*mixingProgress+3]);
		}
		else if( mixingProgress == 2){
			Animation mixAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
			mixAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {	
					mixView.setVisibility(ImageView.GONE);
					mixView.clearAnimation();
					
					PlayPalUtility.setLineGesture(false);
					PlayPalUtility.clearGestureSets();
					dottedLineView.setVisibility(ImageView.INVISIBLE);
					
					setFoodListener(ovenView);
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
			
			ovenView.setBackgroundResource(R.drawable.game3_oven_animation);
			ovenAnimation = (AnimationDrawable) ovenView.getBackground();
			ovenAnimation.start();
			
			curProgress++;
		}
		else if(curProgress == 2)
		{
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.d("Penpalgame","thread sleep error");
				e.printStackTrace();
			}
			
		}
		
		
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(PlayPalUtility.getPoint(0, 0).x - boxSize, PlayPalUtility.getPoint(0, 0).y + boxSize, 0, 0);
		dottedLineView.setLayoutParams(params);
		
		
		return 1;	
	}
}
