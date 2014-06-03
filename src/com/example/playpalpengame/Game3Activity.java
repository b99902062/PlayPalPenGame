package com.example.playpalpengame;

import java.util.ArrayList;

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
import android.graphics.drawable.AnimationDrawable;


public class Game3Activity extends Activity {
	
	protected int[] mixArray = {
		R.drawable.game3_mix1,
		R.drawable.game3_mix2,
		R.drawable.game3_mix3};
	
	protected int mixingProgress;
	protected int curProgress; 
	ImageView mixView;
	ImageView doughView;
	ImageView ovenView;
	ImageView eggbeatView;
	AnimationDrawable ovenAnimation;
	protected RelativeLayout game3RelativeLayout;

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game3);

		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);

		mixView =  (ImageView)findViewById(R.id.Game3_mix);
		doughView = (ImageView)findViewById(R.id.Game3_dough);
		ovenView = (ImageView)findViewById(R.id.Game3_oven);
		eggbeatView = (ImageView)findViewById(R.id.Game3_eggbeat);
		setMixListener(mixView);

		curProgress    = 0;
		mixingProgress = 0;
		
		
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
	}	




	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
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
					Animation mixAnim = Game1Activity.CreateTranslateAnimation(Game1Activity.FROM_CUR_TO_OUTRIGHT);
					mixAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							doughView.setVisibility(ImageView.VISIBLE);
							
							ovenView.setBackgroundResource(R.id.oven_animation);
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
				((ImageView) view).setImageResource(mixArray[mixingProgress]);
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
	
}
