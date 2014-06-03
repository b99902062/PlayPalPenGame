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
				
				if(doughProgress>=4){
					Animation doughAnim = Game1Activity.CreateTranslateAnimation(Game1Activity.FROM_CUR_TO_OUTRIGHT);
					doughAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							doughView.clearAnimation();
							doughView.setVisibility(ImageView.GONE);
							curProgress++;
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
					return;
				}
				doughProgress++;
				((ImageView) view).setImageResource(doughResArray[doughProgress]);				
				Log.d("PenPalGame",""+doughProgress);
			}
		});
	}
	
	
	protected void setCookieListener(View targetView){
		targetView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(curProgress != 1)
					return;
				
				curProgress++;
			}
		});
	}
	
	protected void setCookieListener2(View targetView){
		targetView.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view){
				if(curProgress != 2)
					return;
				
				curProgress++;
			}
		});
	}
	
	protected void initCookieView(){
		Random ran = new Random();
		for(int i=0; i<cookieArray.length; i++){
			ImageView curView = (ImageView)findViewById(cookieArray[i]);
			int idx = ran.nextInt(3);
			curView.setBackgroundResource(cookieResArray[idx]);
		}
	}
}

