

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class Game2Activity extends Activity {
	protected final int FISH_NUM = 10;
	protected final int FISH_BOUND_LEFT = 50;
	protected final int FISH_BOUND_RIGHT = 1600;
	protected final int FISH_BOUND_UP = 250;
	protected final int FISH_BOUND_DOWN = 1300;
	protected final int GESTURE_BOX_SIZE = 50;
	protected final int GESTURE_FIRST_OFFSET_X = 0;
	protected final int GESTURE_FIRST_OFFSET_Y = 0;
	protected final int GESTURE_SECOND_OFFSET_X = 100;
	protected final int GESTURE_SECOND_OFFSET_Y = 80;
	protected final int GESTURE_THIRD_OFFSET_X = 200;
	protected final int GESTURE_THIRD_OFFSET_Y = 0;
	
	protected final int step1TotalProgressCount = 10;
	protected final int step2TotalProgressCount = 26;
	
	protected ArrayList<Point> crossPosArray = new ArrayList<Point>();
	protected ArrayList<ImageView> crossViewArray = new ArrayList<ImageView>(); 
	
	protected boolean canPutInBasket = false;
	protected ImageView netView;
	protected ImageView basketView;
	protected ImageView grillView;
	protected ImageView fishView1;
	protected ImageView fishView2;
	protected ImageView fishView3;
	protected ImageView fishView4;
	protected RelativeLayout game2RelativeLayout;
	protected TextView testProgressCountText;
	
	protected int progressCount;
	protected LinkedList<FishHandlerThread> fishThreadList = new LinkedList<FishHandlerThread>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game2);
		
		crossPosArray.add(new Point(470, 600));
		crossPosArray.add(new Point(470, 1000));
		crossPosArray.add(new Point(910, 600));
		crossPosArray.add(new Point(910, 1000));
		crossPosArray.add(new Point(1350, 600));
		crossPosArray.add(new Point(1350, 1000));
		crossPosArray.add(new Point(1790, 600));
		crossPosArray.add(new Point(1790, 1000));
		
		progressCount = 0;
		
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
		
		game2RelativeLayout = (RelativeLayout)findViewById(R.id.Game2RelativeLayout);
		PlayPalUtility.registerLineGesture(game2RelativeLayout, this, new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return catchFish();
			}
		});
		PlayPalUtility.setLineGesture(true);
		
		game2RelativeLayout.setOnHoverListener(new View.OnHoverListener() {
            @Override
            public boolean onHover(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_HOVER_ENTER:
                    	netView.setVisibility(ImageView.VISIBLE);
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX() - 200, (int)event.getY() - 200 , 0, 0);
                    	if(canPutInBasket) {
                    		if(event.getX() - 200 > 1960 && event.getY() - 200 > 380 && event.getY() - 200 < 1380) {
                    			netView.setImageResource(R.drawable.game2_net);
                    			canPutInBasket = false;
                    			PlayPalUtility.setLineGesture(true);
                    			// Play the pu-ton animation
                    			progressCount++;
                    			testProgressCountText.setText(String.format("ProgressCount: %d", progressCount));
                    			if(progressCount == step1TotalProgressCount) {
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
                    	netView.setVisibility(ImageView.INVISIBLE);
                        break;
                }
                return true;
            }
        });
		
		createFish();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    for(int i=0; i<fishThreadList.size(); i++) {
	    	if (fishThreadList.get(i) != null) 
		        if (!fishThreadList.get(i).isInterrupted()) 
		        	fishThreadList.get(i).interrupt();
	    }
	}
	
	protected void setHomeListener(View targetView) {
		targetView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for(int i=0; i<fishThreadList.size(); i++) {
			    	if (fishThreadList.get(i) != null) 
				        fishThreadList.get(i).killThread();
			    }
				
				PlayPalUtility.setLineGesture(false);
	            PlayPalUtility.clearGestureSets();
				PlayPalUtility.unregisterLineGesture(game2RelativeLayout);
				
				Intent newAct = new Intent();
				newAct.setClass( Game2Activity.this, MainActivity.class );
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
		
		canPutInBasket = true;
		PlayPalUtility.setLineGesture(false);
		PlayPalUtility.cancelGestureSet(index);
		fishThreadList.get(index).killThread();
		fishThreadList.get(index).fishView.setVisibility(ImageView.GONE);
		netView.setImageResource(R.drawable.game2_net2);
		
		
		return 0;
	}
	
	protected void LoadStep2() {
		basketView.setVisibility(ImageView.GONE);
		grillView.setVisibility(ImageView.VISIBLE);
		fishView1.setVisibility(ImageView.VISIBLE);
		fishView2.setVisibility(ImageView.VISIBLE);
		fishView3.setVisibility(ImageView.VISIBLE);
		fishView4.setVisibility(ImageView.VISIBLE);
		
		PlayPalUtility.registerLineGesture(game2RelativeLayout, this, new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return performCross();
			}
		});
		PlayPalUtility.setLineGesture(true);
		
		for(int i=0; i<crossPosArray.size(); i++) {
			ImageView crossView = new ImageView(this);
			crossView.setImageResource(R.drawable.dotted_cross);
			
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(crossPosArray.get(i).x, crossPosArray.get(i).y, 0, 0);
			crossView.setLayoutParams(params);

			game2RelativeLayout.addView(crossView);
			crossViewArray.add(crossView);
			
			PlayPalUtility.initialLineGestureParams(GESTURE_BOX_SIZE, new Point(crossPosArray.get(i).x, crossPosArray.get(i).y), new Point(crossPosArray.get(i).x + 300, crossPosArray.get(i).y + 300));
			PlayPalUtility.initialLineGestureParams(GESTURE_BOX_SIZE, new Point(crossPosArray.get(i).x + 300, crossPosArray.get(i).y), new Point(crossPosArray.get(i).x, crossPosArray.get(i).y + 300));
		}
	}
	
	protected Integer performCross() {
		return 0;
	}
	
	private Handler fishLocationHandler = new Handler() {
        public void handleMessage(Message msg) {
        	Log.d("PlayPalTest", "Get msg");
            //super.handleMessage(msg);
            
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
            PlayPalUtility.changeGestureParams(false, msg.getData().getInt("index"), new Point(curX + GESTURE_FIRST_OFFSET_X, curY + GESTURE_FIRST_OFFSET_Y), new Point(curX + GESTURE_SECOND_OFFSET_X, curY + GESTURE_SECOND_OFFSET_Y), new Point(curX + GESTURE_THIRD_OFFSET_X, curY + GESTURE_THIRD_OFFSET_Y));
        }
    };
    
	
	class FishHandlerThread extends Thread {	 
		public static final float ROTATE_PROB = 0.13f;
		public static final float MOVE_PROB = 0.75f;
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
			
			index = PlayPalUtility.initialLineGestureParams(GESTURE_BOX_SIZE, new Point(curX + GESTURE_FIRST_OFFSET_X, curY + GESTURE_FIRST_OFFSET_Y), new Point(curX + GESTURE_SECOND_OFFSET_X, curY + GESTURE_SECOND_OFFSET_Y), new Point(curX + GESTURE_THIRD_OFFSET_X, curY + GESTURE_THIRD_OFFSET_Y));
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
            		doNextAction();
					Thread.sleep(200);
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
        	/*
        	Matrix matrix = new Matrix();
        	fishView.setScaleType(ScaleType.MATRIX);
        	matrix.postRotate((float) deg, fishView.getDrawable().getBounds().width()/2, fishView.getDrawable().getBounds().height()/2);
        	fishView.setImageMatrix(matrix);
        	*/
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
}