package com.example.playpalpengame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class Game1Activity extends Activity {

	protected final int step1MidProgressCount = 6;
	protected final int step1TotalProgressCount = 12;
	protected final int step2TotalProgressCount = 14;
	protected final int step3TotalProgressCount = 24;

	protected final static int FROM_OUTLEFT_TO_CUR = 1;
	protected final static int FROM_CUR_TO_OUTRIGHT = 2;
	/** Not implemented */
	protected final static int FROM_OUTRIGHT_TO_CUR = 3;
	/** Not implemented */
	protected final static int FROM_CUR_TO_OUTLEFT = 4;
	

	protected int progressCount;

	protected ImageView carrotView;
	protected ImageView cucumberView;
	protected ImageView potView;
	protected ImageView fireView;
	protected FrameLayout board2Layout;

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

		progressCount = 0;

		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);

		carrotView = (ImageView) findViewById(R.id.carrotView);
		setFoodListener(carrotView);

		cucumberView = (ImageView) findViewById(R.id.cucumberView);
		setFoodListener(cucumberView);

		potView = (ImageView) findViewById(R.id.potView);
		board2Layout = (FrameLayout) findViewById(R.id.board2FrameLayout);

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
			}
		});
	}

	protected void setFoodListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				progressCount++;

				((ImageView) view)
						.setImageResource(foodResArray[progressCount]);

				if (progressCount == step1MidProgressCount) {
					// Slide the cucumber
					Animation carrotAnim = CreateTranslateAnimation(Game1Activity.FROM_CUR_TO_OUTRIGHT);
					carrotAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
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
					carrotView.setVisibility(ImageView.GONE);

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
							boardView.setVisibility(ImageView.GONE);
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

	protected void setFragmentListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				progressCount++;
				potDropAnim.start();
				view.setVisibility(ImageView.GONE);

				if (progressCount == step2TotalProgressCount) {
					Animation boardAnim = CreateTranslateAnimation(Game1Activity.FROM_CUR_TO_OUTLEFT);
					boardAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							board2Layout.setVisibility(ImageView.GONE);

							fireView.setVisibility(ImageView.VISIBLE);
							fireAnim.start();
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
		});
	}

	protected void setPotListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/** Should be do circle */
				progressCount++;
				potDropAnim.start();

				if (progressCount == step3TotalProgressCount) {
					Log.d("PenPalGame", "WIN Game 1");
					Intent newAct = new Intent();
					newAct.setClass(Game1Activity.this, MainActivity.class);
					startActivityForResult(newAct, 0);
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
		ImageView tmpFood1 = new ImageView(this);
		tmpFood1.setImageResource(R.drawable.game1_food_1);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(125, 125, 0, 0);
		board2Layout.addView(tmpFood1, params);

		ImageView tmpFood2 = new ImageView(this);
		tmpFood2.setImageResource(R.drawable.game1_food_2);
		params.setMargins(225, 125, 0, 0);
		board2Layout.addView(tmpFood2, params);
	}
}
