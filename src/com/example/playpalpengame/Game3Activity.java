package com.example.playpalpengame;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;


public class Game3Activity extends Activity {
	
	protected int mixingProgress;
	protected int[] mixArray = {
		R.drawable.game3_mix1,
		R.drawable.game3_mix2,
		R.drawable.game3_mix3};
	
	View mixView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game3);

		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);

		mixView = findViewById(R.id.Game3_mix);
		setMixListener(mixView);

		mixingProgress = 0;
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
				if(mixingProgress>=3){
					Animation mixAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
					mixAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							mixView.clearAnimation();
							mixView.setVisibility(ImageView.GONE);
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
					return;
				}
				((ImageView) view).setImageResource(mixArray[mixingProgress]);
				mixingProgress++;
				
				Log.d("PenPalGame",""+mixingProgress);
			}
		});
	}
}
