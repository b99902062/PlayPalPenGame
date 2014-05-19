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
		
		View playBtn1 = findViewById(R.id.mainStall1);
		setStallListener(playBtn1, R.id.mainStall1);	
		View playBtn2 = findViewById(R.id.mainStall2);
		setStallListener(playBtn2, R.id.mainStall2);
		View playBtn3 = findViewById(R.id.mainStall3);
		setStallListener(playBtn3, R.id.mainStall3);
		View playBtn4 = findViewById(R.id.mainStall4);
		setStallListener(playBtn4, R.id.mainStall4);
		
	}
	
	protected void setStallListener(View targetView, final int resId) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent newAct = new Intent();
				newAct.setClass( MainActivity.this, LoadingActivity.class );
				Bundle bundle = new Bundle();
				bundle.putInt("stallResId", resId);
	            newAct.putExtras(bundle);
	            startActivityForResult(newAct ,0);
			}
		});		
	}
}
