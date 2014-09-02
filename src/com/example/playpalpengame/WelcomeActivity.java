package com.example.playpalpengame;

import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class WelcomeActivity extends Activity {
	public static Context self;
	FramesSequenceAnimation anim = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_welcome);
		
		self = this;
		
		ImageView welcomeAnimView = (ImageView)findViewById(R.id.welcomeAnimView);
		anim = AnimationsContainer.getInstance().createWelcomeAnim(welcomeAnimView);
		anim.setStoppedAnimListener(new Callable<Integer>() {
			public Integer call() {
				Intent newAct = new Intent();
				newAct.setClass( WelcomeActivity.this, BeginActivity.class );
				newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	            startActivityForResult(newAct ,0);
	            WelcomeActivity.this.finish();
				
				return 0;
			}
		});
		anim.start();
	}
}
