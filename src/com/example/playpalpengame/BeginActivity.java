package com.example.playpalpengame;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
				String userName = ((Spinner)findViewById(R.id.userNameSpinner)).getSelectedItem().toString();
				bundle.putString("userName", userName);
	            newAct.putExtras(bundle);
	            startActivityForResult(newAct ,0);
	            BeginActivity.this.finish();
			}
		});
		
		Button registerBtn = (Button)findViewById(R.id.registerBtn);
		registerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder alert = new AlertDialog.Builder(BeginActivity.this);

				alert.setTitle("New player registration");
				alert.setMessage("Please enter new player's name:");

				// Set an EditText view to get user input 
				final EditText input = new EditText(BeginActivity.this);
				alert.setView(input);

				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Editable value = input.getText();
						
						// Create the new user's data
						BeginActivity.createNewPlayerData(value.toString());

					  	Intent newAct = new Intent();
						newAct.setClass( BeginActivity.this, MainActivity.class );
						newAct.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle bundle = new Bundle();
						
						String userName = value.toString();
						
					  	bundle.putString("userName", userName);
			            newAct.putExtras(bundle);
			            startActivityForResult(newAct ,0);
			            BeginActivity.this.finish();
					}
				});

				alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
					}
				});
				alert.show();
			}
		});
		
		ArrayList<AnalysisMessage> resultList = new ArrayList<AnalysisMessage>();
		resultList = TherapyMainActivity.loadRecord(); 
		if(resultList != null) {
			String[] nameList = TherapyMainActivity.getAllNames(resultList);
			Spinner nameSpinner = (Spinner)findViewById(R.id.userNameSpinner);
			TherapyMainActivity.connectSource(this, nameSpinner, nameList);
		}
		else {
			Log.d("EndTest", "ResultList in BeginActivity is null.");
		}
	}
	
	public static void createNewPlayerData(String newName) {
		
	}
}
