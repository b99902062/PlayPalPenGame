package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class Game1Activity extends Activity {

	protected final int step1MidProgressCount = 5;
	protected final int step1TotalProgressCount = 11;
	protected final int step2TotalProgressCount = 35;
	protected final int step3TotalProgressCount = 45;
	
	protected final int originBeginCenterX = 1800;
	protected final int originBeginCenterY = 500;
	protected final int originEndCenterX = 1800;
	protected final int originEndCenterY = 1200;
	protected int boxSize = 100;
	protected int lineInterval = 250;

	protected boolean isFoodCanTouch;
	protected boolean isDoneDropFood;
	protected int progressCount;
	
	protected ArrayList<View> foodInPot;

	protected ImageView currentFoodView;
	
	protected ImageView carrotView;
	protected ImageView cucumberView;
	protected ImageView potView;
	protected ImageView fireView;
	protected ImageView knifeView;
	protected ImageView dottedLineView;
	protected RelativeLayout board2Layout;
	protected RelativeLayout game1RelativeLayout;
	
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

		knifeView = (ImageView) findViewById(R.id.knifeView);
		
		carrotView = (ImageView) findViewById(R.id.carrotView);
		setFoodListener(carrotView);
		
		cucumberView = (ImageView) findViewById(R.id.cucumberView);
		
		dottedLineView = (ImageView) findViewById(R.id.dottedLineView);
		dottedLineView.setVisibility(ImageView.VISIBLE);
		
		game1RelativeLayout = (RelativeLayout) findViewById(R.id.game1RelativeLayout);
		game1RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                    	knifeView.setVisibility(ImageView.VISIBLE);
                        Log.d("PlayPal", "Enter");
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX() - 200, (int)event.getY() - 200 , 0, 0);
                    	knifeView.setLayoutParams(params);
                    	Log.d("PlayPal", "Move");
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                    	knifeView.setVisibility(ImageView.INVISIBLE);
                    	Log.d("PlayPal", "Exit");
                        break;
                }
                return true;
            }
        });

		//Test
		PlayPalUtility.registerLineGesture(game1RelativeLayout, this, new Callable<Integer>() {
			public Integer call() {
				return handleLineAction();
			}
		});
		PlayPalUtility.setLineGesture(true);
		PlayPalUtility.initialLineGestureParams(boxSize, new Point(originBeginCenterX, originBeginCenterY), new Point(originEndCenterX, originEndCenterY));
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(PlayPalUtility.getPoint(0, 0).x - boxSize, PlayPalUtility.getPoint(0, 0).y + boxSize, 0, 0);		
		dottedLineView.setLayoutParams(params);
		
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
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				PlayPalUtility.setLineGesture(false);
	            PlayPalUtility.clearGestureSets();
				PlayPalUtility.unregisterLineGesture(game1RelativeLayout);
				
				Intent newAct = new Intent();
				newAct.setClass(Game1Activity.this, MainActivity.class);
				startActivityForResult(newAct, 0);
				Game1Activity.this.finish();
				return true;
			}
		});
	}

	protected void setFoodListener(View targetView) {
		currentFoodView = (ImageView) targetView;
	}

	protected void RemoveFromBoard(View view) {

		progressCount++;
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		view.setVisibility(ImageView.GONE);
		potDropAnim.start();

		if (progressCount == step2TotalProgressCount) {
			Animation boardAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
			boardAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {
					board2Layout.clearAnimation();
					board2Layout.setVisibility(ImageView.GONE);

					fireView.setVisibility(ImageView.VISIBLE);
					fireAnim.setVisible(true, true);
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
								RemoveFromBoard(view);
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
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if(!isDoneDropFood || event.getAction() != MotionEvent.ACTION_DOWN)
					return true;
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
				return true;
			}
		});
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
	
	protected Integer handleLineAction() {
		if (!isFoodCanTouch)
			return 0;
		progressCount++;
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		
		PlayPalUtility.changeGestureParams(true, 0, new Point(-lineInterval, 0), new Point(-lineInterval, 0));

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		Log.d("PlayPalUtility", String.format("GetPoint(0, 0) = (%d, %d)", PlayPalUtility.getPoint(0, 0).x, PlayPalUtility.getPoint(0, 0).y));
		params.setMargins(PlayPalUtility.getPoint(0, 0).x - boxSize, PlayPalUtility.getPoint(0, 0).y + boxSize, 0, 0);
		dottedLineView.setLayoutParams(params);
		
		((ImageView) currentFoodView)
				.setImageResource(foodResArray[progressCount]);

		if (progressCount == step1MidProgressCount) {
			// Slide the cucumber
			isFoodCanTouch = false;
			PlayPalUtility.setLineGesture(false);
			PlayPalUtility.clearGestureSets();
			dottedLineView.setVisibility(ImageView.INVISIBLE);
			Animation carrotAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
			carrotAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {
					isFoodCanTouch = true;
					carrotView.clearAnimation();
					carrotView.setVisibility(ImageView.GONE);
					
					Animation cucumberAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTLEFT_TO_CUR);
					cucumberView.setAnimation(cucumberAnim);
					cucumberView.setVisibility(ImageView.VISIBLE);
					setFoodListener(cucumberView);
					
					cucumberAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationEnd(Animation anim) {
							PlayPalUtility.setLineGesture(true);
							dottedLineView.setVisibility(ImageView.VISIBLE);
							PlayPalUtility.initialLineGestureParams(boxSize, new Point(originBeginCenterX, originBeginCenterY), new Point(originEndCenterX, originEndCenterY));
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							params.setMargins(PlayPalUtility.getPoint(0, 0).x - boxSize, PlayPalUtility.getPoint(0, 0).y + boxSize, 0, 0);
							dottedLineView.setLayoutParams(params);
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

			Animation cucumberAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
			Animation boardAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);

			ImageView boardView = (ImageView) findViewById(R.id.boardView);
			boardView.setAnimation(boardAnim);
			boardAnim.startNow();
			
			dottedLineView.setVisibility(ImageView.GONE);
			PlayPalUtility.setLineGesture(false);
			PlayPalUtility.clearGestureSets();
			PlayPalUtility.unregisterLineGesture(game1RelativeLayout);

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
					Animation board2Anim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_OUTLEFT_TO_CUR);
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
		
		return 1;
	}
}
