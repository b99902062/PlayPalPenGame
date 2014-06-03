

package com.example.playpalpengame;

import java.util.LinkedList;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class Game2Activity extends Activity {
	protected final int FISH_NUM = 10;
	protected final int FISH_BOUND_LEFT = 50;
	protected final int FISH_BOUND_RIGHT = 1600;
	protected final int FISH_BOUND_UP = 250;
	protected final int FISH_BOUND_DOWN = 1300;
	protected final int GESTURE_BOX_SIZE = 100;
	protected final int GESTURE_FIRST_OFFSET_X = 0;
	protected final int GESTURE_FIRST_OFFSET_Y = 0;
	protected final int GESTURE_SECOND_OFFSET_X = 100;
	protected final int GESTURE_SECOND_OFFSET_Y = 80;
	protected final int GESTURE_THIRD_OFFSET_X = 200;
	protected final int GESTURE_THIRD_OFFSET_Y = 0;
	
	protected final int step1TotalProgressCount = 10;
	protected final int step2TotalProgressCount = 26;
	
	protected boolean canPutInBasket = false;
	protected ImageView netView;
	protected ImageView basketView;
	protected ImageView grillView;
	protected ImageView fishView1;
	protected ImageView fishView2;
	protected ImageView fishView3;
	protected ImageView fishView4;
	
	protected int progressCount;
	protected LinkedList<FishHandlerThread> fishThreadList = new LinkedList<FishHandlerThread>();
	
	protected RelativeLayout game2RelativeLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game2);
		
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
		
		game2RelativeLayout = (RelativeLayout)findViewById(R.id.Game2RelativeLayout);
		PlayPalUtility.registerLineGesture(game2RelativeLayout, new Callable<Integer>() {
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
                        Log.d("PlayPal", "Enter");
                        break;
                    case MotionEvent.ACTION_HOVER_MOVE:
                    	RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    	params.setMargins((int)event.getX() - 200, (int)event.getY() - 200 , 0, 0);
                    	if(canPutInBasket) {
                    		if(event.getX() - 200 > 1960 && event.getY() - 200 > 380 && event.getY() - 200 < 1380) {
                    			netView.setImageResource(R.drawable.game2_net);
                    			canPutInBasket = false;
                    			// Play the pu-ton animation
                    			progressCount++;
                    			if(progressCount == step1TotalProgressCount) {
                    				PlayPalUtility.clearGestureSets();
                    				PlayPalUtility.setLineGesture(false);
                    				basketView.setVisibility(ImageView.GONE);
                    				grillView.setVisibility(ImageView.VISIBLE);
                    				fishView1.setVisibility(ImageView.VISIBLE);
                    				fishView2.setVisibility(ImageView.VISIBLE);
                    				fishView3.setVisibility(ImageView.VISIBLE);
                    				fishView4.setVisibility(ImageView.VISIBLE);
                    			}
                    				
                    		}
                    			
                    	}
                    		
                    	netView.setLayoutParams(params);
                    	Log.d("PlayPal", "Move");
                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                    	netView.setVisibility(ImageView.INVISIBLE);
                    	Log.d("PlayPal", "Exit");
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
				Intent newAct = new Intent();
				newAct.setClass( Game2Activity.this, MainActivity.class );
	            startActivityForResult(newAct ,0);
	            Game2Activity.this.finish();
			}
		});		
	}
	
	protected void createFish() {
		for(int i=0; i<FISH_NUM; i++) {
			FishHandlerThread fht = new FishHandlerThread(i, this);
			fishThreadList.add(fht);
			
			fht.start();
		}
	}
	
	protected Integer catchFish() {
		int index = PlayPalUtility.getLastTriggerSetIndex();
		
		/** Do catch fish */
		PlayPalUtility.cancelGestureSet(index);
		fishThreadList.get(index).fishView.setVisibility(ImageView.GONE);
		fishThreadList.get(index).killThread();
		netView.setImageResource(R.drawable.game2_net2);
		canPutInBasket = true;
		
		return 0;
	}
	
	private Handler fishLocationHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            
            int curX = msg.getData().getInt("nextX");
            int curY = msg.getData().getInt("nextY");
            
            fishThreadList.get(msg.getData().getInt("index")).setMargins(curX, curY, 0, 0);
            PlayPalUtility.changeGestureParams(false, msg.getData().getInt("setIndex"), new Point(curX + GESTURE_FIRST_OFFSET_X, curY + GESTURE_FIRST_OFFSET_Y), new Point(curX + GESTURE_SECOND_OFFSET_X, curY + GESTURE_SECOND_OFFSET_Y), new Point(curX + GESTURE_THIRD_OFFSET_X, curY + GESTURE_THIRD_OFFSET_Y));
        }
    };
    
	
	class FishHandlerThread extends Thread {	 
        protected ImageView fishView;
        protected boolean isFish1 = false;
		protected int index;
		protected int curX;
		protected int curY;
		protected int gestureSetIndex;
		protected float slope;
		
		protected boolean isDead = false;
		
        FishHandlerThread(int index, Game2Activity context) {
        	fishView = new ImageView(context);
			fishView.setImageResource(R.drawable.game2_fish_1);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			curX = (int)(FISH_BOUND_LEFT + Math.random()*(FISH_BOUND_RIGHT - FISH_BOUND_LEFT + 1));
			curY = (int)(FISH_BOUND_UP + Math.random()*(FISH_BOUND_DOWN - FISH_BOUND_UP + 1));
			params.setMargins(curX, curY, 0, 0);
			fishView.setLayoutParams(params);

			RelativeLayout layout = (RelativeLayout)findViewById(R.id.Game2RelativeLayout);
			layout.addView(fishView);
			
			index = PlayPalUtility.initialLineGestureParams(GESTURE_BOX_SIZE, new Point(curX + GESTURE_FIRST_OFFSET_X, curY + GESTURE_FIRST_OFFSET_Y), new Point(curX + GESTURE_SECOND_OFFSET_X, curY + GESTURE_SECOND_OFFSET_Y), new Point(curX + GESTURE_THIRD_OFFSET_X, curY + GESTURE_THIRD_OFFSET_Y));
        }
        
        public void setMargins(int left, int top, int right, int down) {
        	LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.setMargins(left, top, right, down);
			fishView.setLayoutParams(params);
        }
        
        public void killThread() {
        	isDead = true;
        }
        
        @Override
        public void run() {
            super.run();
            
            while(isDead) {
            	try {
            		if(isFish1) {
            			fishView.setImageResource(R.drawable.game2_fish_2);
            			isFish1 = false;
            		}
            		else {
            			fishView.setImageResource(R.drawable.game2_fish_1);
            			isFish1 = true;
            		}
            		
            		curX += (int)(Math.random() * 3 - 1);
            		curY += (int)(Math.random() * 3 - 1);
            		Bundle dataBundle = new Bundle();
                    dataBundle.putInt("index", index);
                    dataBundle.putInt("nextX", curX);
                    dataBundle.putInt("nextY", curY);

                    Message msg = new Message();
                    msg.setData(dataBundle);

                    fishLocationHandler.sendMessage(msg);
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        }
    }	
}