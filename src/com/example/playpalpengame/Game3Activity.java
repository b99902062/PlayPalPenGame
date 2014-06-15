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
	protected int curProgress;
		
	
	
	ImageView mixView;
	ImageView ovenView;
	ImageView cakeView;
	ImageView eggbeatView;
	
	//ImageView dottedLineView;
	ImageView currentFoodView;
	DrawView drawView;

	AnimationDrawable ovenAnimation;
	protected RelativeLayout game3RelativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game3);

		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);
		
		curProgress = 0;
		boxSize = 100;
		
		
		mixView =  (ImageView)findViewById(R.id.Game3_mix);
		ovenView = (ImageView)findViewById(R.id.Game3_oven);
		cakeView = (ImageView)findViewById(R.id.Game3_cake);
		eggbeatView = (ImageView)findViewById(R.id.Game3_beat);
		//dottedLineView = (ImageView) findViewById(R.id.dottedLineView);
		
		game3RelativeLayout = (RelativeLayout) findViewById(R.id.Game3RelativeLayout);
		game3RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                    	eggbeatView.setVisibility(ImageView.VISIBLE);
                        Log.d("PlayPal", "Enter");
                        break;
                     
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX() - 200, (int)event.getY() - 200 , 0, 0);
                    	eggbeatView.setLayoutParams(params);
                    	Log.d("PlayPal", "Move");
                        break;

                    case MotionEvent.ACTION_HOVER_EXIT:
                    	eggbeatView.setVisibility(ImageView.INVISIBLE);
                    	Log.d("PlayPal", "Exit");
                        break;
                }
                return true;
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
		PlayPalUtility.initialLineGestureParams(boxSize, centerPoint, 
				pointArray[4*curProgress],
				pointArray[4*curProgress+1],
				pointArray[4*curProgress+2],
				pointArray[4*curProgress+3]);
		/*	
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(PlayPalUtility.getPoint(0, 0).x - boxSize, PlayPalUtility.getPoint(0, 0).y + boxSize, 0, 0);		
		dottedLineView.setLayoutParams(params);
		dottedLineView.setVisibility(ImageView.VISIBLE);
		*/
	
		
		/*
		game3RelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    	eggbeatView.setVisibility(ImageView.VISIBLE);
                        //Log.d("PlayPal", "Enter");
                        break;
                     
                    case MotionEvent.ACTION_MOVE:
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX() - 200, (int)event.getY() - 200 , 0, 0);
                    	eggbeatView.setLayoutParams(params);
                    	//Log.d("PlayPal", "Move");
                        break;

                    case MotionEvent.ACTION_UP:
                    	eggbeatView.setVisibility(ImageView.INVISIBLE);
                    	//Log.d("PlayPal", "Exit");
                        break;
                }
                return true;
            }
        });
        */	
	}	
	
	
	public class DrawView extends View{
		int progress;
		Paint paint;
		Canvas canvas;
		
		public DrawView(Context context) {
			super(context);
			progress = 0;
			canvas=new Canvas();

			paint = new Paint();
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
		@Override  
	    protected void onDraw(Canvas canvas) {  
	        super.onDraw(canvas);
	
			int radius;
			if(progress == 0 ){
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
			else if (progress == 2){
				
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
	
	protected Integer handleLineAction(View view){
		curProgress++;
		Log.d("PenPalGame","curProgress "+curProgress);
		
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
			currentFoodView.setAnimation(mixAnim);
			mixAnim.startNow();
			
			ovenView.setBackgroundResource(R.anim.game3_oven_animation);
			ovenAnimation = (AnimationDrawable) ovenView.getBackground();
			ovenAnimation.start();
		}
		
		else if(curProgress == 3)
		{
			ovenAnimation.stop();
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Log.d("Penpalgame","thread sleep error");
				e.printStackTrace();
			}
			
			Animation ovenAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
			ovenAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {	
					ovenView.setVisibility(ImageView.GONE);
					ovenView.clearAnimation();
					
					PlayPalUtility.setLineGesture(false);
					PlayPalUtility.clearGestureSets();
					
					setFoodListener(cakeView);
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
		}
		else if(curProgress<5){
			
			
		}
		
		
		drawView.progress = curProgress;
		return 1;	
	}
	
	
	
	
	
	/*	ImageView butterView = new ImageView(this);
		butterView.setImageResource(R.drawable.game3_butter);
		game3RelativeLayout.addView(butterView);
	 	
	 	ratio = 0
	  	w = 100
	    h = 100
		
	   
	    if(ratio<100)
	    	ratio++;
	    	
	  	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w*ratio/100.0, h*ratio/100.0);    ;
	  	butterView.setLayoutParams(params);

	 * 
	 */
}
