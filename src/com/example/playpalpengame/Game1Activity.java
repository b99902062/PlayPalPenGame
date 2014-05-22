package com.example.playpalpengame;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class Game1Activity extends Activity {

	protected final int step1MidProgressCount = 5;
	protected final int step1TotalProgressCount = 11;
	protected final int step2TotalProgressCount = 35;
	protected final int step3TotalProgressCount = 45;

	protected final static int FROM_OUTLEFT_TO_CUR = 1;
	protected final static int FROM_CUR_TO_OUTRIGHT = 2;
	/** Not implemented */
	protected final static int FROM_OUTRIGHT_TO_CUR = 3;
	/** Not implemented */
	protected final static int FROM_CUR_TO_OUTLEFT = 4;

	protected boolean isFoodCanTouch;
	protected boolean isDoneDropFood;
	protected int progressCount;
	
	protected ArrayList<View> foodInPot;

	protected ImageView carrotView;
	protected ImageView cucumberView;
	protected ImageView potView;
	protected ImageView fireView;
	protected RelativeLayout board2Layout;
	
	protected TextView progressCountText;

	protected AnimationDrawable potDropAnim;
	protected AnimationDrawable fireAnim;

	protected int[] foodResArray = { R.drawable.game1_carrot_1,
			R.drawable.game1_carrot_2, R.drawable.game1_carrot_3,
			R.drawable.game1_carrot_4, R.drawable.game1_carrot_5,
			R.drawable.game1_carrot_6, R.drawable.game1_cucumber_1,
			R.drawable.game1_cucumber_2, R.drawable.game1_cucumber_3,
			R.drawable.game1_cucumber_4, R.drawable.game1_cucumber_5,
			R.drawable.game1_cucumber_6 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game1);

		isFoodCanTouch = true;
		isDoneDropFood = false;
		progressCount = 0;
		foodInPot = new ArrayList<View>();

		progressCountText = (TextView) findViewById(R.id.testProgressCount);
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		
		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);

		carrotView = (ImageView) findViewById(R.id.carrotView);
		setFoodListener(carrotView);

		cucumberView = (ImageView) findViewById(R.id.cucumberView);
		setFoodListener(cucumberView);

		potView = (ImageView) findViewById(R.id.potView);
		board2Layout = (RelativeLayout) findViewById(R.id.board2RelativeLayout);

		potView.setBackgroundResource(R.anim.pot_drop_animation);
		potDropAnim = (AnimationDrawable) potView.getBackground();
		setPotListener(potView);

		fireView = (ImageView) findViewById(R.id.fireView);
		fireView.setBackgroundResource(R.anim.pot_fire_animation);
		fireAnim = (AnimationDrawable) fireView.getBackground();
	}

	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent newAct = new Intent();
				newAct.setClass(Game1Activity.this, MainActivity.class);
				startActivityForResult(newAct, 0);
				Game1Activity.this.finish();
			}
		});
	}

	protected void setFoodListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!isFoodCanTouch)
					return;
				progressCount++;
				progressCountText.setText("ProgressCount: " + new String("" + progressCount));

				((ImageView) view)
						.setImageResource(foodResArray[progressCount]);

				if (progressCount == step1MidProgressCount) {
					// Slide the cucumber
					isFoodCanTouch = false;
					Animation carrotAnim = CreateTranslateAnimation(Game1Activity.FROM_CUR_TO_OUTRIGHT);
					carrotAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							isFoodCanTouch = true;
							carrotView.clearAnimation();
							carrotView.setVisibility(ImageView.GONE);

							Animation cucumberAnim = CreateTranslateAnimation(Game1Activity.FROM_OUTLEFT_TO_CUR);
							cucumberView.setAnimation(cucumberAnim);
							cucumberView.setVisibility(ImageView.VISIBLE);
							cucumberAnim.startNow();
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}
					});
					carrotView.setAnimation(carrotAnim);
					carrotAnim.startNow();
				} else if (progressCount == step1TotalProgressCount) {
					isFoodCanTouch = false;

					Animation cucumberAnim = CreateTranslateAnimation(Game1Activity.FROM_CUR_TO_OUTRIGHT);
					Animation boardAnim = CreateTranslateAnimation(Game1Activity.FROM_CUR_TO_OUTRIGHT);

					ImageView boardView = (ImageView) findViewById(R.id.boardView);
					boardView.setAnimation(boardAnim);
					boardAnim.startNow();

					cucumberView.setAnimation(cucumberAnim);
					cucumberAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							ImageView boardView = (ImageView) findViewById(R.id.boardView);
							boardView.clearAnimation();
							boardView.setVisibility(ImageView.GONE);
							cucumberView.clearAnimation();
							cucumberView.setVisibility(ImageView.GONE);

							potView.setVisibility(ImageView.VISIBLE);
							Animation fadeIn = new AlphaAnimation(0, 1);
							fadeIn.setInterpolator(new DecelerateInterpolator());
							fadeIn.setDuration(2000);
							potView.setAnimation(fadeIn);

							board2Layout.setVisibility(ImageView.VISIBLE);
							Animation board2Anim = CreateTranslateAnimation(Game1Activity.FROM_OUTLEFT_TO_CUR);
							board2Layout.setAnimation(board2Anim);

							randomSetupFood();

							board2Anim.startNow();
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}
					});
					cucumberAnim.startNow();
				}
			}
		});
	}

	protected void DoOnClick(View view) {

		progressCount++;
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		view.setVisibility(ImageView.GONE);
		potDropAnim.start();

		if (progressCount == step2TotalProgressCount) {
			Animation boardAnim = CreateTranslateAnimation(Game1Activity.FROM_CUR_TO_OUTLEFT);
			boardAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {
					board2Layout.clearAnimation();
					board2Layout.setVisibility(ImageView.GONE);

					fireView.setVisibility(ImageView.VISIBLE);
					fireAnim.start();
					
					isDoneDropFood = true;
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			board2Layout.setAnimation(boardAnim);
			boardAnim.startNow();
		}
	}
	
	protected void setFragmentListener(View targetView) {
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				int minXBoardBound = 0;
				int maxXBoardBound = 1000;
				int minYBoardBound = 380;
				int maxYBoardBound = 1380;
				
				LayoutParams layoutParams = (LayoutParams) view
						.getLayoutParams();
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						break;
					case MotionEvent.ACTION_MOVE:
						int x_cord = (int) event.getRawX();
						int y_cord = (int) event.getRawY();
						
						x_cord -= 60;
						y_cord -= 120;
	
						if (x_cord > maxXBoardBound) {
							if(!foodInPot.contains(view)) {
								foodInPot.add(view);
								DoOnClick(view);
							}
						}
						if (x_cord < minXBoardBound) 
							x_cord = minXBoardBound;
						if (y_cord > maxYBoardBound) 
							y_cord = maxYBoardBound;
						if (y_cord < minYBoardBound) 
							y_cord = minYBoardBound;
	
						layoutParams.setMargins(x_cord - minXBoardBound, y_cord - minYBoardBound, 0, 0);
						view.setLayoutParams(layoutParams);
						break;
					default:
						break;
				}
				return true;
			}
		});
	}

	protected void setPotListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!isDoneDropFood)
					return;
				/** Should be do circle */
				progressCount++;
				progressCountText.setText("ProgressCount: " + new String("" + progressCount));
				potDropAnim.start();

				if (progressCount == step3TotalProgressCount) {
					Log.d("PenPalGame", "WIN Game 1");
					Intent newAct = new Intent();
					newAct.setClass(Game1Activity.this, MainActivity.class);
					startActivityForResult(newAct, 0);
					Game1Activity.this.finish();
				}
			}
		});
	}

	protected TranslateAnimation CreateTranslateAnimation(int translateType) {
		TranslateAnimation newAnim;

		if (translateType == Game1Activity.FROM_OUTLEFT_TO_CUR)
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
					(float) -1.0, Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		else if (translateType == Game1Activity.FROM_CUR_TO_OUTRIGHT)
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					(float) 0.0, Animation.RELATIVE_TO_PARENT, (float) 1.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		else if (translateType == Game1Activity.FROM_OUTRIGHT_TO_CUR)
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
					(float) 1.0, Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		else if (translateType == Game1Activity.FROM_CUR_TO_OUTLEFT)
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					(float) 0.0, Animation.RELATIVE_TO_PARENT, (float) -1.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		else
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					(float) 0.0, Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		newAnim.setDuration(2000);
		newAnim.setRepeatCount(0);
		return newAnim;
	}

	protected void randomSetupFood() {
		// for test
		int[] foodResId = {R.drawable.game1_food_1, R.drawable.game1_food_2};
		
		for (int i=0; i<6; i++) {
			for (int j=0; j<4; j++) {
				ImageView tmpFood = new ImageView(this);
				tmpFood.setImageResource(foodResId[(int)(Math.random()*2)]);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				int xMargin = (int)(Math.random()*151);
				int yMargin = (int)(Math.random()*201);
				params.setMargins(i * 150 + xMargin, j * 201 + yMargin, 0, 0);
				setFragmentListener(tmpFood);
				board2Layout.addView(tmpFood, params);
			}
		}
	}
}
