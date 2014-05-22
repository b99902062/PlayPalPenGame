package com.example.playpalpengame;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LoadingActivity extends Activity {
	
	int gameIndex;
	Timer timer = new Timer(true);
	int[] stallResIdArray = {0, R.drawable.main_stall_1, R.drawable.main_stall_2, R.drawable.main_stall_3, R.drawable.main_stall_4};
	int[] monsterResIdArray = {0, R.drawable.main_monster_1, R.drawable.main_monster_2, R.drawable.main_monster_3, R.drawable.main_monster_4};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		Bundle bundle = getIntent().getExtras();
		gameIndex = bundle.getInt("GameIndex");
				
		ImageView stallView = new ImageView(this);
		stallView.setImageResource(stallResIdArray[gameIndex]);
		
		ImageView monsterView = new ImageView(this);
		monsterView.setImageResource(monsterResIdArray[gameIndex]);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		float density = this.getResources().getDisplayMetrics().density;
		params.setMargins((int)(465 * density), (int)( 250 * density), 0, 0);
		stallView.setLayoutParams(params);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params2.setMargins((int)(615 * density), (int)(300 * density), 0, 0);
		monsterView.setLayoutParams(params);
		
		RelativeLayout targetLayout = (RelativeLayout) findViewById(R.id.loadingRelativeLayout);
		targetLayout.addView(stallView);
		targetLayout.addView(monsterView);
		
		timer.schedule(new timerTask(), 2000);	
	}
	
	public class timerTask extends TimerTask {
	    public void run() {
	    	Intent newAct = new Intent();
	    	if(gameIndex == 1)
	    		newAct.setClass( LoadingActivity.this, Game1Activity.class );
	    	else if(gameIndex == 2)
	    		newAct.setClass( LoadingActivity.this, Game2Activity.class );
	    	else if(gameIndex == 3)
	    		;
	    		//newAct.setClass( LoadingActivity.this, Game3Activity.class );
	    	else if(gameIndex == 4)
	    		;
	    		//newAct.setClass( LoadingActivity.this, Game4Activity.class );
	    	else
	    		return;
            startActivityForResult(newAct ,0);
            LoadingActivity.this.finish();
	    }
	  };
}
