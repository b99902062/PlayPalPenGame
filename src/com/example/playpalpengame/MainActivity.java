package com.example.playpalpengame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		View playBtn1 = findViewById(R.id.mainPlayBtn1);
		setStallListener(playBtn1, 1);	
		View playBtn2 = findViewById(R.id.mainPlayBtn2);
		setStallListener(playBtn2, 2);
		View playBtn3 = findViewById(R.id.mainPlayBtn3);
		setStallListener(playBtn3, 3);
		View playBtn4 = findViewById(R.id.mainPlayBtn4);
		setStallListener(playBtn4, 4);
		
	}
	
	protected void setStallListener(View targetView, final int gameIndex) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent newAct = new Intent();
				newAct.setClass( MainActivity.this, LoadingActivity.class );
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", gameIndex);
	            newAct.putExtras(bundle);
	            startActivityForResult(newAct ,0);
	            MainActivity.this.finish();
			}
		});		
	}
}
