package com.example.playpalpengame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BeginActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_begin);

		ImageView therapyIcon = (ImageView)findViewById(R.id.therapyIcon);
		therapyIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent newAct = new Intent();
				newAct.setClass( BeginActivity.this, TherapyMainActivity.class );
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivityForResult(newAct ,0);
	            BeginActivity.this.finish();
			}
		});
		
		Button submitBtn = (Button)findViewById(R.id.submitBtn);
		submitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent newAct = new Intent();
				newAct.setClass( BeginActivity.this, MainActivity.class );
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				Bundle bundle = new Bundle();
				String userName = ((TextView)findViewById(R.id.userNameText)).getText().toString();
				bundle.putString("userName", userName);
	            newAct.putExtras(bundle);
	            startActivityForResult(newAct ,0);
	            BeginActivity.this.finish();
			}
		});
	}
}
