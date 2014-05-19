package com.example.playpalpengame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class LoadingActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle bundle = getIntent().getExtras();
		int stallResId = bundle.getInt("stallResId");
		
		ImageView stallView = new ImageView(this);
		stallView.setImageResource(stallResId);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(465, 250, 0, 0);
		stallView.setLayoutParams(params);
	}
}
