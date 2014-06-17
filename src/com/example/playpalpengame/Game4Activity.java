package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Random;
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

import com.samsung.spensdk.applistener.SPenHoverListener;

public class Game4Activity extends Activity {
	public class cookie{
		int type;
		Point center;
		ImageView view;
		
		
	}
	
	protected RelativeLayout game4RelativeLayout;
	protected ImageView doughView;
	protected ImageView laddleView;
	
	protected int curProgress;
	protected int curCookieType;
	
	 
	
	protected Point centerPoint = new Point(1280,800);
	protected Point[] pointArray = {
			new Point(1660,1200),
			new Point(1660,400),
			new Point(900,400),
			new Point(900,1200)};
		
	protected int boxSize;
	
	protected int[] doughResArray = {
		R.drawable.game4_dough1,
		R.drawable.game4_dough2,
		R.drawable.game4_dough3,
		R.drawable.game4_dough4,
		R.drawable.game4_dough5	};
	
	protected cookie[] cookieArray = new cookie[8]; 
	
	protected int[] cookieResArray = {
		R.drawable.game4_cookie1,
		R.drawable.game4_cookie2,	
		R.drawable.game4_cookie3,
		R.drawable.game4_cookie4,
		R.drawable.game4_cookie5,
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
	
		

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game4);

		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);

		doughView = (ImageView)findViewById(R.id.Game4_dough);

		game4RelativeLayout = (RelativeLayout) findViewById(R.id.Game4RelativeLayout);
		
		curProgress = 0;
		boxSize = 100;
		
		PlayPalUtility.registerLineGesture(game4RelativeLayout, this, new Callable<Integer>() {
			public Integer call() {
				return handleLineAction(doughView);
			}
		});

		PlayPalUtility.setLineGesture(true);
		PlayPalUtility.initialLineGestureParams(boxSize, centerPoint, pointArray[0]);
		
		
		game4RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                    	laddleView.setVisibility(ImageView.VISIBLE);
                        break;
                     
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX() - 200, (int)event.getY() - 200 , 0, 0);
                    	laddleView.setLayoutParams(params);
                        break;

                    case MotionEvent.ACTION_HOVER_EXIT:
                    	laddleView.setVisibility(ImageView.INVISIBLE);
                        break;
                }
                return true;
            }
        });	
		
		
	}	

	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent newAct = new Intent();
				newAct.setClass(Game4Activity.this, MainActivity.class);
				startActivityForResult(newAct, 0);
				Game4Activity.this.finish();
			}
		});
	}

/*
	protected void setDoughListener(View targetView){		
		targetView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(curProgress != 0)
					return;
				
				if(doughProgress>=3){
					Animation doughAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
					doughAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							doughView.setVisibility(ImageView.GONE);
							doughView.clearAnimation();
							doughView.setOnClickListener(null);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}
					});
					doughView.setAnimation(doughAnim);
					doughAnim.startNow();
					
					curProgress++;//set to 1		
					initCookieView();
					for(int i=0; i<cookieArray.length; i++){
						ImageView curCookie = (ImageView)findViewById(cookieArray[i]);
						curCookie.setVisibility(ImageView.VISIBLE);
						setCookieListener(curCookie);
						
						Animation cookieAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTLEFT_TO_CUR);
						curCookie.setAnimation(cookieAnim);
						cookieAnim.startNow();
					}
					
					return;
				}
				
				curProgress++;
				((ImageView) view).setImageResource(doughResArray[doughProgress]);				
				Log.d("PenPalGame",""+curProgress);
			}
		});
	}
*/	
	
	protected void setCookieListener(View targetView){
		curCookieType = (Integer)targetView.getTag();
		targetView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(curProgress != 1)
					return;
				
				switch(curCookieType){
					case 1:
						Log.d("PenPal","cookie type 1");
						break;
						
					case 2:
						Log.d("PenPal","cookie type 2");
						break;
					
					case 3:
						Log.d("PenPal","cookie type 3");
						break;
						
					default:
						Log.d("PenPal","error cookie type");
				}
				view.setBackgroundResource(cookieResArray[curCookieType+3]);
				curProgress++;//set to 2
			}
		});
	}
	
	protected void setCookieListener2(View targetView){
		curCookieType = (Integer)targetView.getTag();
		targetView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(curProgress != 2)
					return;
				switch(curCookieType){
					case 0:
						Log.d("PenPal","cookie type 1");
						
						break;
						
					case 1:
						Log.d("PenPal","cookie type 2");
						break;
					
					case 2:
						Log.d("PenPal","cookie type 3");
						break;
						
					default:
						Log.d("PenPal","error cookie type");
				}		
				view.setVisibility(ImageView.GONE);
				curProgress++;//set to 3
			}
		});
	}
	
	protected void initCookieView(){
		Random ran = new Random();
		for(int i=0; i<cookieArray.length; i++){
			
			cookieArray[i].view = (ImageView)findViewById(cookieViewArray[i]);
			cookieArray[i].type = ran.nextInt(3);			
			cookieArray[i].view.setBackgroundResource(cookieResArray[cookieArray[i].type]);
		}
	}
	
	protected Integer handleLineAction (View view){
		curProgress++;
		Log.d("PenPalGame","curProgress "+curProgress);
		
		if(curProgress < 4){
			doughView.setImageResource(doughResArray[curProgress]);
			doughView.invalidate();
			
			PlayPalUtility.changeGestureParams(false, 0, 
					centerPoint, 
					pointArray[curProgress]);
		}
		
		return 1;
	}
}

