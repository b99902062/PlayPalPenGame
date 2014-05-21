package com.example.playpalpengame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Game2Activity extends Activity {

	protected int progressCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game2);
		
		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn, R.id.homeBtn);
		
		progressCount = 0;
	}
	
	protected void setHomeListener(View targetView, final int resId) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent newAct = new Intent();
				newAct.setClass( Game2Activity.this, MainActivity.class );
	            startActivityForResult(newAct ,0);
			}
		});		
	}
	
}
