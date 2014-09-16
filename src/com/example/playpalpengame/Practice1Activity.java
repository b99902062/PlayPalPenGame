package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class Practice1Activity extends Activity {
	protected final int step1MidProgressCount = 1;
	protected final int step1TotalProgressCount = 2;
	protected final int step2TotalProgressCount = 3;
	protected final int step3TotalProgressCount = 4;
	
	protected final int testTotalTime = 600;
	
	private final int foodOffsetX = 380;
	private final int foodOffsetY = 380;
	
	private final static int TEACH_HAND_OFFSET_X = 45;
	private final static int TEACH_HAND_OFFSET_Y = 665;
	private final static int TEACH_HAND_DOWN_OFFSET_X = 70;
	private final static int TEACH_HAND_DOWN_OFFSET_Y = 720;
	private final static int HELICAL_OFFSET_X = 500;
	private final static int HELICAL_OFFSET_Y = 500;
	
	protected final int originBeginCenterX = 500;
	protected final int originBeginCenterY = 500;
	protected final int originEndCenterX = 500;
	protected final int originEndCenterY = 1200;
	protected final int boxSize = 100;
	protected final int lineInterval = 250;
	
	protected final int potLeftTopX = 1100;
	protected final int potLeftTopY = 700;
	protected final int potBoxSize = 120;
	protected final int potBoxInterval = 350;
	
	protected boolean isFoodCanTouch;
	protected boolean isDoneDropFood;
	protected int progressCount;
	private int score = 0;
	
	protected Practice1Activity self;
	
	private MediaPlayer fireMP = null;
	
	private String mUserName = null;
	private int mBadges = 0;
	private int mHighScore = 0;
	private int mWinCount = 0;
	protected ArrayList<View> foodInPot;

	protected ImageView currentFoodView;
	
	protected ImageView carrotView;
	protected ImageView cucumberView;
	protected ImageView potView;
	protected ImageView fireView;
	protected ImageView knifeView;
	private ImageView teachHandView;
	protected RelativeLayout board2Layout;
	protected DrawableRelativeLayout game1RelativeLayout;
	
	protected TextView progressCountText;

	protected AnimationDrawable potDropAnim;
	protected AnimationDrawable potStirAnim;
	protected AnimationDrawable fireAnim;
	protected AnimationDrawable downAnim;
	
	protected Point[] carrotCutBeginPointArray = {new Point(231, 406), new Point(465, 340), new Point(740, 311), new Point(979, 300), new Point(1195, 318)};
	protected Point[] carrotCutEndPointArray = {new Point(270, 715), new Point(650, 747), new Point(952, 759), new Point(1175, 736), new Point(1354, 677)};
	protected Point[] cucumberCutBeginPointArray = {new Point(347, 309), new Point(561, 334), new Point(795, 386), new Point(1077, 404), new Point(1429, 288)};
	protected Point[] cucumberCutEndPointArray = {new Point(154, 615), new Point(350, 700), new Point(581, 781), new Point(886, 831), new Point(1204, 845)};
	
	protected int[] foodResArray = { R.drawable.game1_carrot_1,
			R.drawable.game1_carrot_2,
			R.drawable.game1_cucumber_2};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_game1);
		PlayPalUtility.setDebugMode(false);
		
		Bundle bundle = getIntent().getExtras();
		mUserName = bundle.getString("userName");
		mBadges = bundle.getInt("GameBadges");
		mHighScore = bundle.getInt("GameHighScore");
		mWinCount = bundle.getInt("GameWinCount");
		
		doInitial();
		
		game1RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
    					PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_START);
                    	knifeView.setVisibility(ImageView.VISIBLE);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_MOVE);
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX(), (int)event.getY() , 0, 0);
                    	knifeView.setLayoutParams(params);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_END);
                    	knifeView.setVisibility(ImageView.INVISIBLE);
                        break;
                }
                return true;
            }
        });

		PlayPalUtility.registerLineGesture(game1RelativeLayout, this, new Callable<Integer>() {
			public Integer call() {
				return handleLineAction();
			}
		});
		PlayPalUtility.registerFailFeedback((ImageView)findViewById(R.id.failFeedbackView));
		PlayPalUtility.setHoverTarget(true, knifeView);
		PlayPalUtility.setLineGesture(true);
		Point beginPnt = new Point(foodOffsetX + carrotCutBeginPointArray[progressCount].x, foodOffsetY + carrotCutBeginPointArray[progressCount].y);
		Point endPnt = new Point(foodOffsetX + carrotCutEndPointArray[progressCount].x, foodOffsetY + carrotCutEndPointArray[progressCount].y);
		PlayPalUtility.initialLineGestureParams(false, false, boxSize, beginPnt, endPnt);

		PlayPalUtility.initDrawView(game1RelativeLayout, this);
		DrawGestureLine();
		
		setTeachHandLinear(beginPnt.x - TEACH_HAND_DOWN_OFFSET_X, beginPnt.y - TEACH_HAND_DOWN_OFFSET_Y, endPnt.x - beginPnt.x, endPnt.y - beginPnt.y);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		clearAll();
		BackgroundMusicHandler.recyle();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		BackgroundMusicHandler.initMusic(this);
		BackgroundMusicHandler.setMusicSt(true);
	}
	
	@Override
	public void onBackPressed() {
	}
	
	private void clearAll() {
		if(fireMP != null) {
			fireMP.release();
			fireMP = null;
		}
		
		score += PlayPalUtility.killTimeBar();
		PlayPalUtility.setLineGesture(false);
		PlayPalUtility.unregisterLineGesture(game1RelativeLayout);
		PlayPalUtility.unregisterFailFeedback();
		PlayPalUtility.clearGestureSets();
		PlayPalUtility.clearDrawView();
	}
	
	private void doInitial() {
		self = this;
		
		isFoodCanTouch = true;
		isDoneDropFood = false;
		progressCount = 0;
		foodInPot = new ArrayList<View>();

		progressCountText = (TextView) findViewById(R.id.testProgressCount);
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		
		potView = (ImageView) findViewById(R.id.potView);
		board2Layout = (RelativeLayout) findViewById(R.id.board2RelativeLayout);

		potView.setBackgroundResource(R.anim.pot_drop_animation);
		potDropAnim = (AnimationDrawable) potView.getBackground();

		fireView = (ImageView) findViewById(R.id.fireView);
		fireView.setBackgroundResource(R.anim.pot_fire_animation);
		fireAnim = (AnimationDrawable) fireView.getBackground();

		setHomeListener(findViewById(R.id.homeBtn));

		knifeView = (ImageView) findViewById(R.id.knifeView);
		teachHandView = ((ImageView)findViewById(R.id.teachHandView));
		
		carrotView = (ImageView) findViewById(R.id.carrotView);
		setFoodListener(carrotView);
		
		cucumberView = (ImageView) findViewById(R.id.cucumberView);
		
		game1RelativeLayout = (DrawableRelativeLayout) findViewById(R.id.game1RelativeLayout);
	}
	
	private void setTeachHandLinear(int bX, int bY, int xOffset, int yOffset) {
		teachHandView.setVisibility(View.VISIBLE);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(bX, bY, 0, 0);
		teachHandView.setLayoutParams(params);
		
		Animation am = new TranslateAnimation(0, xOffset, 0, yOffset);
		am.setDuration(2000);
		am.setRepeatCount(-1);
		
		teachHandView.startAnimation(am);
	}
	
	private void setTeachHandCircular(int bX, int bY, int range) {
		teachHandView.setVisibility(View.VISIBLE);
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(bX, bY, 0, 0);
		teachHandView.setLayoutParams(params);
		
		Animation am = new CircularTranslateAnimation(teachHandView, range);
		am.setDuration(2000);
		am.setRepeatCount(-1);
		
		teachHandView.startAnimation(am);
	}

	protected void setHomeListener(View targetView) {
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				clearAll();
				BackgroundMusicHandler.setCanRecycle(false);
				
				Intent newAct = new Intent();
				newAct.setClass(Practice1Activity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userName", mUserName);
	            newAct.putExtras(bundle);
				startActivityForResult(newAct, 0);
				Practice1Activity.this.finish();
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
		PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_SPLIT_POT, this);
		
		view.setVisibility(ImageView.GONE);
		potDropAnim.setVisible(true, true);
		potDropAnim.start();

		if (progressCount == step2TotalProgressCount) {
			teachHandView.clearAnimation();
			teachHandView.setVisibility(View.INVISIBLE);
			
			score += PlayPalUtility.killTimeBar();
			Animation boardAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTLEFT);
			boardAnim.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation anim) {
					board2Layout.clearAnimation();
					board2Layout.setVisibility(ImageView.GONE);
					
					knifeView.setImageResource(R.drawable.game1_ladle);

					fireView.setVisibility(ImageView.VISIBLE);
					fireAnim.setVisible(true, true);
					fireAnim.start();
					
					fireMP = PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_STIR_POT, self, true);
					
					setTeachHandCircular(780 + HELICAL_OFFSET_X - TEACH_HAND_DOWN_OFFSET_X, 380 + HELICAL_OFFSET_Y  - TEACH_HAND_DOWN_OFFSET_Y, 240);
					//potView.setBackgroundResource(R.anim.pot_stir_animation);
					//potStirAnim = (AnimationDrawable) potView.getBackground();
					
					
					
					PlayPalUtility.registerLineGesture(game1RelativeLayout, self, new Callable<Integer>() {
						public Integer call() {
							return doPotStir();
						}
					});
					PlayPalUtility.setLineGesture(true);
					PlayPalUtility.initialLineGestureParams(true, false, potBoxSize, new Point(potLeftTopX, potLeftTopY), new Point(potLeftTopX + potBoxInterval, potLeftTopY), new Point(potLeftTopX + potBoxInterval, potLeftTopY + potBoxInterval), new Point(potLeftTopX, potLeftTopY + potBoxInterval));
					
					ImageView helicalView = (ImageView)findViewById(R.id.helicalView);
					helicalView.setVisibility(ImageView.VISIBLE);
					
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
				if(event.getAction() == MotionEvent.ACTION_DOWN) { 
					PlayPalUtility.curEntry = new RecordEntry(
						new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_TOUCH_START);
				}
				else if(event.getAction() == MotionEvent.ACTION_MOVE)
					PlayPalUtility.curEntry = new RecordEntry(
							new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_TOUCH_MOVE);
				else {
					PlayPalUtility.curEntry = new RecordEntry(
							new Point((int)event.getRawX(), (int)event.getRawY()), RecordEntry.STATE_TOUCH_END);
				}
					
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
					case MotionEvent.ACTION_UP:
						break;
					default:
						break;
				}
				return true;
			}
		});
	}

	protected Integer doPotStir() {
		progressCount++;
		progressCountText.setText("ProgressCount: " + new String("" + progressCount));
		//PlayPalUtility.doProgress();
		
		//potStirAnim.setVisible(true, true);
		//potStirAnim.start();
		FramesSequenceAnimation anim = AnimationsContainer.getInstance().createGame1PotStirAnim(potView);
		anim.start();
		

		if (progressCount == step3TotalProgressCount) {
			clearAll();			
			Log.d("EndTest", String.format("Game1Score: %d", score));
			
			Intent newAct = new Intent();
			newAct.setClass(Practice1Activity.this, MainActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("userName", mUserName);
            newAct.putExtras(bundle);
			startActivityForResult(newAct, 0);
			Practice1Activity.this.finish();
		}
		
		return 1;
	}

	protected void randomSetupFood() {
		// for test
		int[] foodResId = {R.drawable.game1_food_1, R.drawable.game1_food_2};
		
		for (int i=0; i<1; i++) {
			for (int j=0; j<1; j++) {
				ImageView tmpFood = new ImageView(this);
				tmpFood.setImageResource(foodResId[(int)(Math.random()*2)]);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				int xMargin = (int)(150);
				int yMargin = (int)(200);
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
		PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_CUT_FOOD, this);
		
		Point beginPnt = new Point(0, 0);
		Point endPnt = new Point(0, 0);
		if(progressCount < step1MidProgressCount) {
			beginPnt = new Point(foodOffsetX + carrotCutBeginPointArray[progressCount].x, foodOffsetY + carrotCutBeginPointArray[progressCount].y);
			endPnt = new Point(foodOffsetX + carrotCutEndPointArray[progressCount].x, foodOffsetY + carrotCutEndPointArray[progressCount].y);
		}
		else if(progressCount < step1TotalProgressCount){
			beginPnt = new Point(foodOffsetX + cucumberCutBeginPointArray[progressCount - step1MidProgressCount].x, foodOffsetY + cucumberCutBeginPointArray[progressCount - step1MidProgressCount].y);
			endPnt = new Point(foodOffsetX + cucumberCutEndPointArray[progressCount - step1MidProgressCount].x, foodOffsetY + cucumberCutEndPointArray[progressCount - step1MidProgressCount].y);
		}
			
		PlayPalUtility.changeGestureParams(false, 0, beginPnt, endPnt);
		
		((ImageView) currentFoodView)
				.setImageResource(foodResArray[progressCount]);

		if (progressCount == step1MidProgressCount) {
			// Slide the cucumber
			isFoodCanTouch = false;
			PlayPalUtility.clearDrawView();
			teachHandView.clearAnimation();
			teachHandView.setVisibility(View.INVISIBLE);
			
			PlayPalUtility.setLineGesture(false);
			PlayPalUtility.clearGestureSets();
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
							Point beginPnt = new Point(foodOffsetX + cucumberCutBeginPointArray[0].x, foodOffsetY + cucumberCutBeginPointArray[0].y);
							Point endPnt = new Point(foodOffsetX + cucumberCutEndPointArray[0].x, foodOffsetY + cucumberCutEndPointArray[0].y);
							PlayPalUtility.initialLineGestureParams(false, false, boxSize, beginPnt, endPnt);
							DrawGestureLine();
							
							setTeachHandLinear(beginPnt.x - TEACH_HAND_DOWN_OFFSET_X, beginPnt.y - TEACH_HAND_DOWN_OFFSET_Y, endPnt.x - beginPnt.x, endPnt.y - beginPnt.y);
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
			PlayPalUtility.clearDrawView();
			teachHandView.clearAnimation();
			teachHandView.setVisibility(View.INVISIBLE);
			score += PlayPalUtility.killTimeBar();
			
			Animation cucumberAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);
			Animation boardAnim = PlayPalUtility.CreateTranslateAnimation(PlayPalUtility.FROM_CUR_TO_OUTRIGHT);

			ImageView boardView = (ImageView) findViewById(R.id.boardView);
			boardView.setAnimation(boardAnim);
			boardAnim.startNow();

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
					board2Anim.setAnimationListener(new AnimationListener()  {

						@Override
						public void onAnimationEnd(Animation arg0) {
							// dirty hardcoded params :(
							setTeachHandLinear(150 + 20 - TEACH_HAND_DOWN_OFFSET_X, 200 + 380 + 20 - TEACH_HAND_DOWN_OFFSET_Y, 1000, 0);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationStart(Animation animation) {
						}
						
					});
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
		} else {
			DrawGestureLine();
		}
		
		return 1;
	}
	
	private void DrawGestureLine() {
		Log.d("Draw", "Call DrawGestureLine()");
		PlayPalUtility.clearDrawView();
		Point pnt1 = PlayPalUtility.getPoint(0, 0);
		Point pnt2 = PlayPalUtility.getPoint(0, 1);
		PlayPalUtility.setStraightStroke(pnt1, pnt2);
	}
}
