package com.example.playpalpengame;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LoadingActivity extends Activity {
	
	String mUserName = null;
	int gameIndex;
	Timer timer = new Timer(true);
	int[] stallResIdArray = {0, R.drawable.main_stall_1, R.drawable.main_stall_2, R.drawable.main_stall_3, R.drawable.main_stall_4};
	int[] monsterResIdArray = {0, R.drawable.main_monster_1, R.drawable.main_monster_2, R.drawable.main_monster_3, R.drawable.main_monster_4};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_loading);
		
		Bundle bundle = getIntent().getExtras();
		gameIndex = bundle.getInt("GameIndex");
		mUserName = bundle.getString("userName");
				
		ImageView stallView = (ImageView)findViewById(R.id.stallView);
		stallView.setImageResource(stallResIdArray[gameIndex]);
		
		ImageView monsterView = (ImageView)findViewById(R.id.monsterView);
		monsterView.setImageResource(monsterResIdArray[gameIndex]);
	
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
	    		newAct.setClass( LoadingActivity.this, Game3Activity.class );
	    	else if(gameIndex == 4)
	    		newAct.setClass( LoadingActivity.this, Game4Activity.class );
	    	else
	    		return;
			Bundle bundle = new Bundle();
			bundle.putString("userName", mUserName);
            newAct.putExtras(bundle);
            startActivityForResult(newAct ,0);
            LoadingActivity.this.finish();
	    }
	  };
}
