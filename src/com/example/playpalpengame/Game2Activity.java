package com.example.playpalpengame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.samsung.spensdk.SCanvasView;

public class Game2Activity extends Activity {

	protected int progressCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game2);

		RelativeLayout canvasContainer = (RelativeLayout) findViewById(R.id.Game2RelativeLayout);		
		SCanvasView SCanvas = new SCanvasView(this);        
		SCanvas.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		canvasContainer.addView(SCanvas);
		
		
		
		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);
		
		
		progressCount = 0;
	}
	
	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent newAct = new Intent();
				newAct.setClass( Game2Activity.this, MainActivity.class );
	            startActivityForResult(newAct ,0);
	            Game2Activity.this.finish();
			}
		});		
	}
	
}
