package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class Game2Activity extends Activity {
	protected final int FISH_NUM = 10;
	protected final int FISH_BOUND_LEFT = 50;
	protected final int FISH_BOUND_RIGHT = 1600;
	protected final int FISH_BOUND_UP = 250;
	protected final int FISH_BOUND_DOWN = 1300;
	protected final int GESTURE_BOX_SIZE = 75;
	protected final int GESTURE_FIRST_OFFSET_X = 0;
	protected final int GESTURE_FIRST_OFFSET_Y = 0;
	protected final int GESTURE_SECOND_OFFSET_X = 100;
	protected final int GESTURE_SECOND_OFFSET_Y = 80;
	protected final int GESTURE_THIRD_OFFSET_X = 200;
	protected final int GESTURE_THIRD_OFFSET_Y = 0;
	protected final int CUT_BOX_SIZE = 50;
	
	protected final int step1TotalProgressCount = 10;
	protected final int step2TotalProgressCount = 14;
	
	protected final int testTotalTime = 500;
	
	private final static int fishW =  140;
	private final static int fishH = 80;
	
	private final Point[] fishOffset = {new Point(420, 430), new Point(860, 430), new Point(1300,430), new Point(1740, 430)};
	private final Point[] cutBeginOffset = {new Point(124, 263), new Point(301, 262), new Point(134, 459), new Point(278, 457)};
	private final Point[] cutEndOffset = {new Point(308, 438), new Point(123, 446), new Point(284, 600), new Point(134, 607)};

	private int score = 0;
	
	public static boolean isReady;
	
	private String mUserName = null;
	private int mBadges = 0;
	private int mHighScore = 0;
	private int mWinCount = 0;
	
	private int curFishIndex;
	protected boolean canPutInBasket = false;
	protected ImageView netView;
	protected ImageView basketView;
	protected ImageView grillView;
	protected ImageView fishView1;
	protected ImageView fishView2;
	protected ImageView fishView3;
	protected ImageView fishView4;
	protected DrawableRelativeLayout game2RelativeLayout;
	protected TextView testProgressCountText;
	
	protected int progressCount;
	protected static LinkedList<FishHandlerThread> fishThreadList;
	
	private int[] fishCutIdArray = {R.id.fishCut11, R.id.fishCut12, R.id.fishCut13, R.id.fishCut14, 
			R.id.fishCut21, R.id.fishCut22, R.id.fishCut23, R.id.fishCut24,
			R.id.fishCut31, R.id.fishCut32, R.id.fishCut33, R.id.fishCut34,
			R.id.fishCut41, R.id.fishCut42, R.id.fishCut43, R.id.fishCut44};
	private int[] fishIdArray = {R.id.fishView1, R.id.fishView2, R.id.fishView3, R.id.fishView4};
	private int[] fishDoneIdArray = {R.id.fishViewDone1, R.id.fishViewDone2, R.id.fishViewDone3, R.id.fishViewDone4};
	private boolean[] isFishCutArray = new boolean[16];
	private PointPair[] fishCutPointPairArray = new PointPair[16];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		isReady = false;
		
		setContentView(R.layout.activity_game2);
		PlayPalUtility.setDebugMode(false);
		
		fishThreadList = new LinkedList<FishHandlerThread>();

		Bundle bundle = getIntent().getExtras();
		mUserName = bundle.getString("userName");
		mBadges = bundle.getInt("GameBadges");
		mHighScore = bundle.getInt("GameHighScore");
		mWinCount = bundle.getInt("GameWinCount");
		
		progressCount = 0;
		PlayPalUtility.registerProgressBar((ProgressBar)findViewById(R.id.progressBarRed), (ImageView)findViewById(R.id.progressMark), (ImageView)findViewById(R.id.progressBar), new Callable<Integer>() {
			public Integer call() {
				Intent newAct = new Intent();
				newAct.setClass(Game2Activity.this, AnimationActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("GameIndex", 2);
				bundle.putBoolean("isWin", false);
				bundle.putString("userName", mUserName);
				bundle.putInt("GameBadges", mBadges);
				bundle.putInt("GameHighScore", mHighScore);
				bundle.putInt("GameWinCount", mWinCount);
	            newAct.putExtras(bundle);
	            
				startActivityForResult(newAct, 0);
				Game2Activity.this.finish();
				return 0;
			}
		});
		PlayPalUtility.initialProgressBar(testTotalTime, PlayPalUtility.TIME_MODE);
		
		View homeBtn = findViewById(R.id.homeBtn);
		setHomeListener(homeBtn);
		
		netView = (ImageView)findViewById(R.id.netView);
		basketView = (ImageView)findViewById(R.id.basketView);
		grillView = (ImageView)findViewById(R.id.grillView);
		fishView1 = (ImageView)findViewById(R.id.fishView1);
		fishView2 = (ImageView)findViewById(R.id.fishView2);
		fishView3 = (ImageView)findViewById(R.id.fishView3);
		fishView4 = (ImageView)findViewById(R.id.fishView4);
		
		testProgressCountText = (TextView)findViewById(R.id.testProgressCount2);
		
		game2RelativeLayout = (DrawableRelativeLayout)findViewById(R.id.Game2RelativeLayout);
		PlayPalUtility.registerLineGesture(game2RelativeLayout, this, new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return catchFish();
			}
		});
		PlayPalUtility.registerFailFeedback((ImageView)findViewById(R.id.failFeedbackView));
		PenRecorder.registerRecorder(game2RelativeLayout, this, mUserName, "2-1");
		PlayPalUtility.setLineGesture(true);
		PlayPalUtility.initDrawView(game2RelativeLayout, this);
		
		game2RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_START);
                    	PenRecorder.forceRecord();
                    	netView.setVisibility(ImageView.VISIBLE);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_MOVE);
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX() - 200, (int)event.getY() - 200 , 0, 0);
                    	if(canPutInBasket) {
                    		if(event.getX() - 200 > 1960 && event.getY() - 200 > 380 && event.getY() - 200 < 1380) {
                    			netView.setImageResource(R.drawable.game2_net);
                    			canPutInBasket = false;
                    			PlayPalUtility.setLineGesture(true);
                    			// Play the pu-ton animation
                    			PlayPalUtility.cancelGestureSet(curFishIndex);
                    			fishThreadList.get(curFishIndex).killThread();
                    			
                    			progressCount++;
                    			testProgressCountText.setText(String.format("ProgressCount: %d", progressCount));
                    			if(progressCount == step1TotalProgressCount) {
                    				score += PlayPalUtility.killTimeBar();
                    				PenRecorder.outputJSON();
                    				
                    				PlayPalUtility.clearGestureSets();
                    				PlayPalUtility.setLineGesture(false);
                    				PlayPalUtility.unregisterLineGesture(game2RelativeLayout);
                    				LoadStep2();
                    			}
                    		}
                    	}
                    		
                    	netView.setLayoutParams(params);
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                    	PlayPalUtility.curEntry = new RecordEntry(
    							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_END);
                    	PenRecorder.forceRecord();
                    	netView.setVisibility(ImageView.INVISIBLE);
                    	if(canPutInBasket) {
                    		PlayPalUtility.setLineGesture(true);
                    		fishThreadList.get(curFishIndex).moveTo((int)event.getX() - fishW, (int)event.getY() - fishH);
                    		fishThreadList.get(curFishIndex).doResume();
                    		fishThreadList.get(curFishIndex).fishView.setVisibility(ImageView.VISIBLE);
                    		netView.setImageResource(R.drawable.game2_net);
                    		canPutInBasket = false;
                    	}
                        break;
                }
                return true;
            }
        });
		
		createFish();
		isReady = true;
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    clearAll();
	}
	
	@Override
	public void onBackPressed() {
	}
	
	private void clearAll() {
		for(int i=0; i<fishThreadList.size(); i++) {
	    	if (fishThreadList.get(i) != null) {
	    		fishThreadList.get(i).killThread();
		        if (!fishThreadList.get(i).isInterrupted()) 
		        	fishThreadList.get(i).interrupt();
	    	}
	    }
	    fishThreadList.clear();
	    PlayPalUtility.killTimeBar();
	    PlayPalUtility.clearDrawView();
	    PlayPalUtility.clearGestureSets();
	    PlayPalUtility.setLineGesture(false);
		PlayPalUtility.unregisterLineGesture(game2RelativeLayout);
		PlayPalUtility.unregisterFailFeedback();
	    
	    isReady = false;
	}
	
	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				clearAll();
				Intent newAct = new Intent();
				newAct.setClass( Game2Activity.this, MainActivity.class );
				Bundle bundle = new Bundle();
				bundle.putString("userName", mUserName);
	            newAct.putExtras(bundle);
	            startActivityForResult(newAct ,0);
	            Game2Activity.this.finish();
			}
		});		
	}
	
	protected void createFish() {
		for(int i=0; i<FISH_NUM; i++) {
			FishHandlerThread fht = new FishHandlerThread(this);
			fishThreadList.add(fht);
			fht.start();
		}
	}
	
	protected Integer catchFish() {
		Log.d("PlayPalTest", "Catch fish");
		
		int index = PlayPalUtility.getLastTriggerSetIndex();
		Log.d("PlayPalTest", String.format("Fish index: %d", index));
		/** Do catch fish */
		if(index < 0)
			return -1;
		
		if(canPutInBasket) /** To avoid race condition */
			return 0;
		curFishIndex = index;
		canPutInBasket = true;
		PlayPalUtility.setLineGesture(false);
		fishThreadList.get(index).doPause();
		//PlayPalUtility.cancelGestureSet(index);
		//fishThreadList.get(index).killThread();
		fishThreadList.get(index).fishView.setVisibility(ImageView.INVISIBLE);
		netView.setImageResource(R.drawable.game2_net2);

		return 0;
	}
	
	protected void LoadStep2() {
		netView.setImageResource(R.drawable.game2_thin_knife);
		
		basketView.setVisibility(ImageView.GONE);
		grillView.setVisibility(ImageView.VISIBLE);
		fishView1.setVisibility(ImageView.VISIBLE);
		fishView2.setVisibility(ImageView.VISIBLE);
		fishView3.setVisibility(ImageView.VISIBLE);
		fishView4.setVisibility(ImageView.VISIBLE);
		
		java.util.Arrays.fill(isFishCutArray, false);
		
		PlayPalUtility.registerLineGesture(game2RelativeLayout, this, new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return performCross();
			}
		});
		for(int fishIndex=0; fishIndex<4; fishIndex++) {
			for(int cutIndex = 0; cutIndex<4; cutIndex++) {
				Point pnt1 = new Point(fishOffset[fishIndex].x + cutBeginOffset[cutIndex].x, fishOffset[fishIndex].y + cutBeginOffset[cutIndex].y);
				Point pnt2 = new Point(fishOffset[fishIndex].x + cutEndOffset[cutIndex].x, fishOffset[fishIndex].y + cutEndOffset[cutIndex].y);
				PlayPalUtility.initialLineGestureParams(false, false, CUT_BOX_SIZE, pnt1, pnt2);
				PlayPalUtility.setStraightStroke(pnt1, pnt2);
				fishCutPointPairArray[fishIndex*4 + cutIndex] = new PointPair(pnt1, pnt2);
			}
		}
		
		PlayPalUtility.setAlphaAnimation(grillView, true);
		PlayPalUtility.setAlphaAnimation(fishView1, true);
		PlayPalUtility.setAlphaAnimation(fishView2, true);
		PlayPalUtility.setAlphaAnimation(fishView3, true);
		PlayPalUtility.setAlphaAnimation(fishView4, true, new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return prepareCutting();
			}
		});
	}
	
	protected Integer prepareCutting() {
		PlayPalUtility.initialProgressBar(testTotalTime, PlayPalUtility.TIME_MODE);
		PlayPalUtility.setLineGesture(true);
		PenRecorder.registerRecorder(game2RelativeLayout, this, mUserName, "2-2");
		
		return 0;
	}
	
	protected Integer performCross() {
		int lastTriggerIndex = PlayPalUtility.getLastTriggerSetIndex();
		PlayPalUtility.cancelGestureSet(lastTriggerIndex);
		Log.d("LastTrigger", String.format("LastTrigger: %d", lastTriggerIndex));
		if(lastTriggerIndex < fishCutIdArray.length) {
			ImageView cutView = (ImageView)findViewById(fishCutIdArray[lastTriggerIndex]);
			cutView.setVisibility(ImageView.VISIBLE);
			isFishCutArray[lastTriggerIndex] = true;
			
			final int baseIndex = lastTriggerIndex/4;
			if(isFishCutArray[baseIndex * 4]
			&& isFishCutArray[baseIndex * 4 + 1]
			&& isFishCutArray[baseIndex * 4 + 2]		
			&& isFishCutArray[baseIndex * 4 + 3]) {
				ImageView fishView = (ImageView)findViewById(fishIdArray[baseIndex]);
				ImageView fishViewDone = (ImageView)findViewById(fishDoneIdArray[baseIndex]);
				PlayPalUtility.setAlphaAnimation(fishView, false, new Callable<Integer>() {
					private int index = baseIndex;
					@Override
					public Integer call() throws Exception {
						return DoFishDone(index);
					}
				});
				PlayPalUtility.setAlphaAnimation(fishViewDone, true);
				//fishView.setImageResource(R.drawable.game2_fish_done);
			}
		}
		redrawHintLine();
		return 0;
	}
	
	private void redrawHintLine() {
		PlayPalUtility.clearDrawView();
		for(int i=0; i<isFishCutArray.length; i++) {
			if(!isFishCutArray[i])
				PlayPalUtility.setStraightStroke(fishCutPointPairArray[i].p1, fishCutPointPairArray[i].p2);
		}
	}
	
	private Integer DoFishDone(int index) {
		ImageView fishView = (ImageView)findViewById(fishIdArray[index]);
		ImageView fishViewDone = (ImageView)findViewById(fishDoneIdArray[index]);
		fishView.setVisibility(ImageView.GONE);
		fishViewDone.setVisibility(ImageView.VISIBLE);
		

		progressCount++;
		testProgressCountText.setText(String.format("ProgressCount: %d", progressCount));
		if(progressCount == step2TotalProgressCount) {
			score += PlayPalUtility.killTimeBar();
			
			clearAll();
			
			PenRecorder.outputJSON();
			Intent newAct = new Intent();
			newAct.setClass(Game2Activity.this, AnimationActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("GameIndex", 2);
			bundle.putBoolean("isWin", true);
			bundle.putString("userName", mUserName);
			bundle.putInt("GameBadges", mBadges);
			bundle.putInt("GameHighScore", mHighScore);
			bundle.putInt("GameWinCount", mWinCount);
			bundle.putInt("NewScore", score);
            newAct.putExtras(bundle);
			startActivityForResult(newAct, 0);
			Game2Activity.this.finish();
		}
		
		
		return 0;
	}
	
	private static Handler fishLocationHandler = new Handler() {
        public void handleMessage(Message msg) {
        	if(!Game2Activity.isReady)
        		return;
        	if(msg.getData().getInt("index") >= fishThreadList.size())
        		return;
        	if(msg.getData().getInt("fishType") == 1)
            	fishThreadList.get(msg.getData().getInt("index")).fishView.setImageResource(R.drawable.game2_fish_2);
    		else
    			fishThreadList.get(msg.getData().getInt("index")).fishView.setImageResource(R.drawable.game2_fish_1);
        	
            int curX = msg.getData().getInt("nextX");
            int curY = msg.getData().getInt("nextY");
            
            int rotateAngle = msg.getData().getInt("rotateAngle");
            if(rotateAngle != 0)
            	fishThreadList.get(msg.getData().getInt("index")).doRotate(rotateAngle);
            
            fishThreadList.get(msg.getData().getInt("index")).setMargins(curX, curY, 0, 0);
            Log.d("PlayPalTest", String.format("Fish[%d], Test: (%d, %d)", msg.getData().getInt("index"), curX, curY));
            if(!fishThreadList.get(msg.getData().getInt("index")).isDead)
            	PlayPalUtility.changeGestureParams(false, msg.getData().getInt("index"), new Point(curX + fishW, curY + fishH));
        }
    };
    
	
	class FishHandlerThread extends Thread {	 
		public static final float ROTATE_PROB = 0.13f;
		public static final float MOVE_PROB = 0.55f;
		public static final int MAX_SPEED = 100;
		public static final int MIN_SPEED = 20;
		protected ImageView fishView;
        protected boolean isFish1 = false;
		protected int index;
		protected int curX;
		protected int curY;
		protected int velocity = MIN_SPEED;
		protected PointF orientation = new PointF(1f, 0f);
		
		protected boolean isDead = false;
		protected boolean isPause = false;
		
        FishHandlerThread(Game2Activity context) {
        	fishView = new ImageView(context);
			fishView.setImageResource(R.drawable.game2_fish_1);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			curX = (int)(FISH_BOUND_LEFT + Math.random()*(FISH_BOUND_RIGHT - FISH_BOUND_LEFT + 1));
			curY = (int)(FISH_BOUND_UP + Math.random()*(FISH_BOUND_DOWN - FISH_BOUND_UP + 1));
			params.setMargins(curX, curY, 0, 0);
			fishView.setLayoutParams(params);

			game2RelativeLayout.addView(fishView);
			
			index = PlayPalUtility.initialLineGestureParams(false, false, GESTURE_BOX_SIZE, new Point(curX + fishW, curY + fishH));
        }
        
        public void setMargins(int left, int top, int right, int down) {
        	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(left, top, right, down);
			fishView.setLayoutParams(params);
        }
        
        public void killThread() {
        	isDead = true;
        }
        
        @Override
        public void run() {
            while(!isDead) {
            	try {
            		if(!isPause)
            			doNextAction();
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        }
        
        protected void doNextAction() {
        	Bundle dataBundle = new Bundle();
    		Log.d("NewTest", String.format("Index: %d", index));
    		
        	if(Math.random() > getRotateProb()) {
        		int rotateAngle = 360 - (int) (Math.sqrt(Math.random() * 129600));
        		dataBundle.putInt("rotateAngle", rotateAngle);
        		changeOrientation(rotateAngle);        		
        	}
        	else 
        		dataBundle.putInt("rotateAngle", 0);
        	
        	Log.d("PlayPalOrientation", String.format("Orientation: (%f, %f)", orientation.x, orientation.y));
        	float speedUpProb = getSpeedUpProb(velocity);
        	float slowDownProb = getSlowDownProb(velocity);
        	float tmpRdm = (float) Math.random();
        	if(tmpRdm > speedUpProb) {
        		int speedUp = (int) (Math.random() * (MAX_SPEED - velocity + 1));
        		velocity += speedUp;
        	}
        	else if(tmpRdm < slowDownProb) {
        		int slowDown = (int) (Math.random() * (velocity - MIN_SPEED + 1));
        		velocity -= slowDown;
        	}
        	Log.d("PlayPalGame2", String.format("Velocity: %d", velocity));
        	if(Math.random() > MOVE_PROB)
        		moveAhead();
        	
            dataBundle.putInt("index", index);
            dataBundle.putInt("nextX", curX);
            dataBundle.putInt("nextY", curY);
            if(isFish1) {
            	dataBundle.putInt("fishType", 2);
    			isFish1 = false;
    		}
    		else {
    			dataBundle.putInt("fishType", 1);
    			isFish1 = true;
    		}

            Message msg = new Message();
            msg.setData(dataBundle);

            fishLocationHandler.sendMessage(msg);
        }
        
        protected void doPause() {
        	isPause = true;
        }
        
        protected void doResume() {
        	isPause = false;
        }
        
        protected void moveTo(int newX, int newY) {
        	curX = newX;
        	curY = newY;
        	
        	Bundle dataBundle = new Bundle();
        	dataBundle.putInt("index", index);
            dataBundle.putInt("nextX", newX);
            dataBundle.putInt("nextY", newY);
            dataBundle.putInt("fishType", 1);
            isFish1 = true;
            
        	Message msg = new Message();
            msg.setData(dataBundle);

            fishLocationHandler.sendMessage(msg);
        }

        protected void moveAhead() {
        	if(curX + (int) (orientation.x * velocity) < FISH_BOUND_LEFT 
        	|| curX + (int) (orientation.x * velocity) > FISH_BOUND_RIGHT
        	|| curY + (int) (orientation.y * velocity) < FISH_BOUND_UP
        	|| curY + (int) (orientation.y * velocity) > FISH_BOUND_DOWN) 
        		return;
        	else {
        		curX += (int) (orientation.x * velocity);
        		curY += (int) (orientation.y * velocity);
        	}
        }

        protected void changeOrientation(int angle) {
        	float theta = deg2rad(angle);
        	
        	float cs = (float) Math.cos(theta);
        	float sn = (float) Math.sin(theta);
        	
        	float px = orientation.x * cs - orientation.y * sn;
        	float py = orientation.x * sn + orientation.y * cs;
        	
        	orientation = new PointF(px, py);
        }

        protected float deg2rad(float deg) {
        	return (float) (deg * Math.PI / 180.0);
        }

        protected float getSpeedUpProb(int curV) {
        	// Linear
        	return 1 - (float)(curV - MIN_SPEED) / MAX_SPEED;
        }

        protected float getSlowDownProb(int curV) {
        	// Linear
        	return 1 - (float)(curV - MIN_SPEED) / MAX_SPEED;
        }

        protected void doRotate(int deg) {
        	if(deg > 180)
        		deg -= 360;
        	fishView.setRotation((float)deg);
        }
        
        protected float getRotateProb() {
        	if(Math.abs(curX - FISH_BOUND_LEFT) < 50 
        	|| Math.abs(curX - FISH_BOUND_RIGHT) < 50
        	|| Math.abs(curY - FISH_BOUND_UP) < 50
        	|| Math.abs(curY - FISH_BOUND_DOWN) < 50)
        		return 1;
        	return 1 - ROTATE_PROB;
        }
    }
	
	class PointPair {
		public Point p1;
		public Point p2;
		
		PointPair(Point p1, Point p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
	}
}