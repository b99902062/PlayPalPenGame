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
	private final int TRIANGULAR_COOKIE = 0;
	private final int CIRCLE_COOKIE		= 1;
	private final int SQUARE_COOKIE 	= 2;
	
	public Point pointAddition(Point p1, Point p2){
		return new Point(p1.x+p2.x, p1.y+p2.y);
	}
	
	
	public class Cookie{
		protected int type;
		protected Point center;
		protected ImageView view;
		protected Point[][] offsetArray = new Point[][]{
				{new Point(100,0), new Point(-70,100),new Point(-70,-100),new Point(100,0),  new Point(100,0)},//TRIANGULAR_COOKIE
				{new Point(100,0), new Point(-100,0), new Point(0,-100),  new Point(0,100),  new Point(100,0)},//CIRCLE_COOKIE
				{new Point(70,-70), new Point(-70,-70), new Point(-70,70), new Point(70,70), new Point(70,-70)}};//SQUARE_COOKIE
		
		public Cookie(int _t, ImageView _v){
			type = _t;
			view = _v;
			view.setVisibility(ImageView.VISIBLE);		
			view.setImageResource(cookieResArray[type]);
			Log.d("PenPal_game4"," "+view.getLeft()+"  "+view.getTop());
			center = new Point(view.getLeft()+200, view.getTop()+200);
			
		}
		
		public void setGesturePoint(){
			if(type==TRIANGULAR_COOKIE || type==CIRCLE_COOKIE || type==SQUARE_COOKIE)
				PlayPalUtility.initialLineGestureParams(boxSize,
					pointAddition(this.center, offsetArray[this.type][0]), 
					pointAddition(this.center, offsetArray[this.type][1]),
					pointAddition(this.center, offsetArray[this.type][2]),
					pointAddition(this.center, offsetArray[this.type][3]),
					pointAddition(this.center, offsetArray[this.type][4]));
		}			
	}
	
	protected RelativeLayout game4RelativeLayout;
	protected ImageView doughView;
	protected ImageView laddleView;
	
	protected int boxSize;
	protected int curProgress;
	protected int curCookieType;
	
	protected Point centerPoint = new Point(1280,800);
	protected int[] doughResArray = {
		R.drawable.game4_dough1,
		R.drawable.game4_dough2,
		R.drawable.game4_dough3,
		R.drawable.game4_dough4,
		R.drawable.game4_dough5	};
	
	protected Point[] doughPosArray = {
		new Point(1660,1200),
		new Point(1660,400),
		new Point(900,400),
		new Point(900,1200)};
	
	protected Cookie[] cookieArray = new Cookie[8]; 
	protected Point[] cookiePosArray = {
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
		R.drawable.game4_cookie3,
		
		R.drawable.game4_cookie4,
		R.drawable.game4_cookie5,
		R.drawable.game4_cookie6
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
		laddleView = (ImageView)findViewById(R.id.Game4_ladle);
		
		game4RelativeLayout = (RelativeLayout) findViewById(R.id.Game4RelativeLayout);
		
		curProgress = 0;
		boxSize = 100;
		
		PlayPalUtility.registerLineGesture(game4RelativeLayout, this, new Callable<Integer>() {
			public Integer call() {
				return handleLineAction(doughView);
			}
		});

		PlayPalUtility.setLineGesture(true);
		PlayPalUtility.initialLineGestureParams(boxSize, centerPoint, doughPosArray[0]);
		
		
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
	
	protected void initCookieView(){
		Random ran = new Random();
		for(int i=0; i<cookieArray.length; i++){
			cookieArray[i] = new Cookie(
				ran.nextInt(3),
				(ImageView)findViewById(cookieViewArray[i]));
			
			cookieArray[i].setGesturePoint();
			
			Animation cookieAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTLEFT_TO_CUR);
			cookieArray[i].view.setAnimation(cookieAnim);
			cookieAnim.startNow();
		}
		
		PlayPalUtility.registerLineGesture(game4RelativeLayout, this, new Callable<Integer>() {
			public Integer call() {
				return handleLineAction2(cookieArray[0].view);//don't need view info.
			}
		});
	}
	
	protected Integer handleLineAction (View view){
		curProgress++;
		Log.d("PenPalGame","curProgress "+curProgress);
		
		if(curProgress < 4){
			doughView.setImageResource(doughResArray[curProgress]);
			doughView.invalidate();	
			PlayPalUtility.changeGestureParams(false, 0, 
					centerPoint, 
					doughPosArray[curProgress]);
		}
		else if(curProgress == 4){
			Animation doughAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
			doughAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {	
					doughView.setVisibility(ImageView.GONE);
					doughView.clearAnimation();
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
		
			PlayPalUtility.clearGestureSets();
			initCookieView();
		}
		
		return 1;
	}
	
	protected Integer handleLineAction2 (View view){
		curProgress++;
		Log.d("PenPalGame","curProgress "+curProgress);
		
		int idx = PlayPalUtility.getLastTriggerSetIndex();
		PlayPalUtility.cancelGestureSet(idx);
		cookieArray[idx].view.setVisibility(ImageView.INVISIBLE);
		
		Random ran = new Random();
		Point newPos = pointAddition(cookiePosArray[idx],new Point( ran.nextInt(200)-50,ran.nextInt(200)-50) );//random num in (-50,150)
		cookieArray[idx].center = newPos;
		
		if(curProgress == 12){
			//TODO
			
		}
		
		return 1;
	}
}