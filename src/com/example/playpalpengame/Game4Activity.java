package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Random;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;


public class Game4Activity extends Activity {
	
	protected int curProgress;
	protected int doughProgress;
	protected int cookieProgress;
	
	protected int[] doughResArray = {
		R.drawable.game4_dough1,
		R.drawable.game4_dough2,
		R.drawable.game4_dough3};
	
	protected int[] cookieResArray = {
		R.drawable.game4_cookie1,
		R.drawable.game4_cookie2,	
		R.drawable.game4_cookie3,
		R.drawable.game4_cookie4,
		R.drawable.game4_cookie5,
	};
	protected int[] cookieArray = {
		R.id.Game4_cookie0,	
		R.id.Game4_cookie1,
		R.id.Game4_cookie2,
		R.id.Game4_cookie3,
		R.id.Game4_cookie4,
		R.id.Game4_cookie5,
		R.id.Game4_cookie6,
		R.id.Game4_cookie7,
	};
	
	int  curCookieType;
	View doughView;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game4);

		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);

		doughView = findViewById(R.id.Game4_dough);
		setDoughListener(doughView);

		doughProgress = 0;
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
				
				doughProgress++;
				((ImageView) view).setImageResource(doughResArray[doughProgress]);				
				Log.d("PenPalGame",""+doughProgress);
			}
		});
	}
	
	
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
			ImageView curView = (ImageView)findViewById(cookieArray[i]);
			int idx = ran.nextInt(3);
			curView.setBackgroundResource(cookieResArray[idx]);
			curView.setTag(idx);
		}
	}
}

