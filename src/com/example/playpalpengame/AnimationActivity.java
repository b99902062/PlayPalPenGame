package com.example.playpalpengame;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class AnimationActivity extends Activity {
	
	int gameIndex;
	int[] monsterAnimArray = {0, R.anim.monster1_animation, R.anim.monster2_animation, R.anim.monster3_animation, R.anim.monster4_animation};
	protected AnimationDrawable monsterAnim;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_animation);
		
		Bundle bundle = getIntent().getExtras();
		gameIndex = bundle.getInt("GameIndex");
				
		ImageView monsterView = (ImageView)findViewById(R.id.monsterView);
		
		if(0<gameIndex && gameIndex<=4)
    		monsterView.setBackgroundResource(monsterAnimArray[gameIndex]);
		else
    		Log.d("Animation","game index out of bound");
    	
		monsterAnim = (AnimationDrawable) monsterView.getBackground();
		monsterAnim.start();
		
    	return;
	}
	
}
