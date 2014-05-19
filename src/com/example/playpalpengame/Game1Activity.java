package com.example.playpalpengame;

import com.samsung.spensdk.SCanvasView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class Game1Activity extends Activity {
	
	protected final int step1MidProgressCount = 6;
	protected final int step1TotalProgressCount = 12;
	protected final int step2TotalProgressCount = 20;
	protected int progressCount;
	protected int[] foodResArray = {R.drawable.game1_carrot_1, R.drawable.game1_carrot_2, 
			R.drawable.game1_carrot_3, R.drawable.game1_carrot_4, R.drawable.game1_carrot_5,
			R.drawable.game1_carrot_6, R.drawable.game1_cucumber_1, R.drawable.game1_cucumber_2,
			R.drawable.game1_cucumber_3, R.drawable.game1_cucumber_4, R.drawable.game1_cucumber_5,
			R.drawable.game1_cucumber_6};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game1);
		
		progressCount = 0;
		
		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn, R.id.homeBtn);
		
		View carrotView = findViewById(R.id.carrotView);
		setFoodListener(carrotView, R.id.carrotView);
		
		View boardView2 = findViewById(R.id.boardView2);
		setBoardListener(boardView2, R.id.boardView2);
		
		
	}
	
	protected void setHomeListener(View targetView, final int resId) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent newAct = new Intent();
				newAct.setClass( Game1Activity.this, MainActivity.class );
	            startActivityForResult(newAct ,0);
			}
		});		
	}
	
	protected void setFoodListener(View targetView, final int resId) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				progressCount++;

				ImageView carrotView = (ImageView)findViewById(R.id.carrotView);
				carrotView.setImageResource(foodResArray[progressCount]);
				
				if(progressCount == step1MidProgressCount) {
					// Slide the cucumber
					ImageView cucumberView = (ImageView)findViewById(R.id.cucumberView);
					Animation am = new TranslateAnimation(-500, 0, 0, 0);
					am.setDuration( 2000 );
					am.setRepeatCount( 0 );
					cucumberView.setAnimation(am);
					carrotView.setVisibility(ImageView.INVISIBLE);
					cucumberView.setVisibility(ImageView.VISIBLE);
					am.startNow();
				}
				else if(progressCount == step1TotalProgressCount) {
					ImageView cucumberView = (ImageView)findViewById(R.id.cucumberView);
					cucumberView.setVisibility(ImageView.INVISIBLE);
					
					ImageView boardView = (ImageView)findViewById(R.id.boardView);
					boardView.setVisibility(ImageView.INVISIBLE);
					
					ImageView boardView2 = (ImageView)findViewById(R.id.boardView2);
					boardView2.setVisibility(ImageView.VISIBLE);
					
					ImageView potView = (ImageView)findViewById(R.id.potView);
					potView.setVisibility(ImageView.VISIBLE);
				}
			}
		});		
	}
	
	protected void setBoardListener(View targetView, final int resId) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				progressCount++;
				
				if(progressCount == step2TotalProgressCount) {
					ImageView boardView2 = (ImageView)findViewById(R.id.boardView2);
					boardView2.setVisibility(ImageView.INVISIBLE);
					
					ImageView fireView = (ImageView)findViewById(R.id.fireView);
					fireView.setVisibility(ImageView.VISIBLE);
				}
			}
		});		
	}
	
	
}
