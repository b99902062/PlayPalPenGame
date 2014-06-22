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
			new Point(1280,1400)};
		
	protected int boxSize;
	protected int curProgress;
	protected boolean canTouchOven = false;
	protected boolean butterSqueezing = false;
	
	
	ImageView mixView;
	ImageView ovenView;
	ImageView cakeView;
	ImageView butterView;
	ImageView eggbeatView;
	
	//ImageView dottedLineView;
	ImageView currentFoodView;
	DrawView drawView;

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
		
		
		mixView =  (ImageView)findViewById(R.id.Game3_mix);
		ovenView = (ImageView)findViewById(R.id.Game3_oven);
		cakeView = (ImageView)findViewById(R.id.Game3_cake);
		butterView  = (ImageView)findViewById(R.id.Game3_butter);
		eggbeatView = (ImageView)findViewById(R.id.Game3_beat);
		//dottedLineView = (ImageView) findViewById(R.id.dottedLineView);
		
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
					curProgress++;
					drawView.progress = curProgress;
					drawView.invalidate();

					Log.d("Penpal","oven curprogress"+curProgress);
					currentFoodView.setImageResource(foodResArray[curProgress]);
					
					
					if(curProgress == 7){
						//bi-directional line gesture
						PlayPalUtility.setLineGesture(true);
						PlayPalUtility.initialLineGestureParams(false, true, boxSize, pointArray[0],pointArray[2]);
						PlayPalUtility.initialLineGestureParams(false, true, boxSize, pointArray[2],pointArray[0]);
						PlayPalUtility.initialLineGestureParams(false, true, boxSize, pointArray[1],pointArray[3]);
						PlayPalUtility.initialLineGestureParams(false, true, boxSize, pointArray[3],pointArray[1]);
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

		drawView = new DrawView(this);
		drawView.setMinimumHeight(2160);
		drawView.setMinimumWidth(1600);
		game3RelativeLayout.addView(drawView);
		
		setFoodListener(mixView);
		
		PlayPalUtility.registerLineGesture(game3RelativeLayout, this, new Callable<Integer>() {
			public Integer call() {
				return handleLineAction(mixView);
			}
		});

		PlayPalUtility.setLineGesture(true);
		PlayPalUtility.initialLineGestureParams(false, true, boxSize, centerPoint, 
				pointArray[4*curProgress],
				pointArray[4*curProgress+1],
				pointArray[4*curProgress+2],
				pointArray[4*curProgress+3]);
	}	
	
	
	public class DrawView extends View{
		int progress;
		Paint paint;
		Canvas canvas;
		
		private void setPenEffect(){
	        paint.setAntiAlias(true);
	        paint.setStrokeWidth(10);
            paint.setStyle(Paint.Style.STROKE);        
            paint.setColor(Color.BLACK);
            
            Path path = new Path();
            path.moveTo(0, getHeight()/2);
            path.lineTo(getWidth(), getHeight()/2);
            
            PathEffect effects = new DashPathEffect( new float[]{5,5,5,5}, 1);
            paint.setPathEffect(effects); 
		}
		
		public DrawView(Context context) {
			super(context);
			progress = 0;
			canvas= new Canvas();
			paint = new Paint();
		}
		
		@Override  
	    protected void onDraw(Canvas canvas) {  
	        super.onDraw(canvas);
	        this.setPenEffect();
			int radius;
			if(progress == 0){
				radius = 400;
				RectF oval1 = new RectF(centerPoint.x, centerPoint.y-radius/2,
										centerPoint.x+radius, centerPoint.y+radius/2);
				canvas.drawArc(oval1, 0, 180, false, paint);
				
				RectF oval2 = new RectF(centerPoint.x-radius, centerPoint.y-radius,
										centerPoint.x+radius, centerPoint.y+radius);
				canvas.drawArc(oval2, 90, 270, false, paint);
			}
			else if(progress == 1){
				radius = 600;
				RectF oval1 = new RectF(centerPoint.x, centerPoint.y-radius/2,
										centerPoint.x+radius, centerPoint.y+radius/2);
				canvas.drawArc(oval1, 0, 180, false, paint);
				
				RectF oval2 = new RectF(centerPoint.x-radius, centerPoint.y-radius,
										centerPoint.x+radius, centerPoint.y+radius);
				canvas.drawArc(oval2, 90, 270, false, paint);
			}
			else if (progress > 6 && progress<9){
				
				canvas.drawLine(pointArray[0].x, pointArray[0].y, 
								pointArray[2].x, pointArray[2].y, paint);
				
				canvas.drawLine(pointArray[1].x, pointArray[1].y, 
								pointArray[3].x, pointArray[3].y, paint);				
			}
		}
		
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
						Log.d("Penpal","oven curprogress"+curProgress);
						drawView.progress = curProgress;
						drawView.invalidate();
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
		
	
	protected Integer handleLineAction(View view){
		curProgress++;
		Log.d("PenPalGame","curProgress "+curProgress);
		drawView.progress = curProgress;
		drawView.invalidate();
		
		if(curProgress < 2){
			currentFoodView.setImageResource(foodResArray[curProgress]);
			currentFoodView.invalidate();
			PlayPalUtility.changeGestureParams(false, 0, 
					centerPoint, 
					pointArray[4*curProgress],
					pointArray[4*curProgress+1],
					pointArray[4*curProgress+2],
					pointArray[4*curProgress+3]);
		}
		else if( curProgress == 2){
			currentFoodView.setImageResource(foodResArray[curProgress]);
			
			
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
			Log.d("Penpal","oven curprogress"+curProgress);
			setOvenListener(ovenView);
			
			PlayPalUtility.setLineGesture(false);
            PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterLineGesture(game3RelativeLayout);
		}
		
		return 1;	
	}
	
	protected Integer handleLineAction2(View view){
		curProgress++;
		Log.d("Penpal","curProgress"+curProgress);
		drawView.progress = curProgress;
		drawView.invalidate();
		
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
