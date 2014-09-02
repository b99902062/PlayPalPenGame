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
	
	private String mUserName = null;
	private int gameIndex;
	private int badge;
	private int highScore;
	private int winCount;
	private boolean isPlayBtn;
	private Timer timer = new Timer(true);
	private int[] stallResIdArray = {0, R.drawable.main_stall_1, R.drawable.main_stall_2, R.drawable.main_stall_3, R.drawable.main_stall_4};
	private int[] monsterResIdArray = {0, R.drawable.main_monster_1, R.drawable.main_monster_2, R.drawable.main_monster_3, R.drawable.main_monster_4};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_loading);
		
		Bundle bundle = getIntent().getExtras();
		gameIndex = bundle.getInt("GameIndex");
		mUserName = bundle.getString("userName");
		isPlayBtn = bundle.getBoolean("isPlayBtn");
		badge = bundle.getInt("GameBadges");
		highScore = bundle.getInt("GameHighScore");
		winCount = bundle.getInt("GameWinCount");

		ImageView stallView = (ImageView)findViewById(R.id.stallView);
		stallView.setImageResource(stallResIdArray[gameIndex]);
		
		ImageView monsterView = (ImageView)findViewById(R.id.monsterView);
		monsterView.setImageResource(monsterResIdArray[gameIndex]);
	
		timer.schedule(new timerTask(), 2000);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		BackgroundMusicHandler.recyle();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		BackgroundMusicHandler.initMusic(this);
		BackgroundMusicHandler.setMusicSt(true);
	}
	
	public class timerTask extends TimerTask {
	    public void run() {
	    	BackgroundMusicHandler.setCanRecycle(false);
	    	
	    	Intent newAct = new Intent();
	    	if(isPlayBtn) {
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
	    	}
	    	else {
	    		if(gameIndex == 1)
		    		newAct.setClass( LoadingActivity.this, Practice1Activity.class );
		    	else if(gameIndex == 2)
		    		newAct.setClass( LoadingActivity.this, Practice2Activity.class );
		    	else if(gameIndex == 3)
		    		newAct.setClass( LoadingActivity.this, Practice3Activity.class );
		    	else if(gameIndex == 4)
		    		newAct.setClass( LoadingActivity.this, Practice4Activity.class );
		    	else
		    		return;
	    	}
			Bundle bundle = new Bundle();
			bundle.putString("userName", mUserName);
			bundle.putInt("GameBadges", badge);
			bundle.putInt("GameHighScore", highScore);
			bundle.putInt("GameWinCount", winCount);
            newAct.putExtras(bundle);
            startActivityForResult(newAct ,0);
            LoadingActivity.this.finish();
	    }
	  };
}
