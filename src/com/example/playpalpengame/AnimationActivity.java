package com.example.playpalpengame;

import java.lang.ref.SoftReference;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AnimationActivity extends Activity {
	FramesSequenceAnimation anim = null;
	ImageView monsterView;
	int gameIndex;
	int[] monsterAnimArray = {0, R.anim.monster1_animation, R.anim.monster2_animation, R.anim.monster3_animation, R.anim.monster4_animation};
	protected AnimationDrawable monsterAnim;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.activity_animation);
		
		Bundle bundle = getIntent().getExtras();
		gameIndex = bundle.getInt("GameIndex");
				
		monsterView = (ImageView)findViewById(R.id.monsterView);
		ImageView replayBtn = (ImageView)findViewById(R.id.replayBtn);
		
		setHomeListener(findViewById(R.id.homeBtn));
		
		anim = AnimationsContainer.getInstance().createGameAnim(monsterView, gameIndex);
		
		replayBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(anim != null)
					anim.stop();
				anim = AnimationsContainer.getInstance().createGameAnim(monsterView, gameIndex);
				anim.start();
				return true;
			}
		});
		
		anim.start();
    	return;
	}
	
	protected void setHomeListener(View targetView) {
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				Intent newAct = new Intent();
				newAct.setClass(AnimationActivity.this, MainActivity.class);
				startActivityForResult(newAct, 0);
				AnimationActivity.this.finish();
				return true;
			}
		});
	}
}

class FramesSequenceAnimation {
    private int[] mFrames; // animation frames
    private int mIndex; // current frame
    private boolean mShouldRun; // true if the animation should continue running. Used to stop the animation
    private boolean mIsRunning; // true if the animation currently running. prevents starting the animation twice
    private SoftReference<ImageView> mSoftReferenceImageView; // Used to prevent holding ImageView when it should be dead.
    private Handler mHandler;
    private int mDelayMillis;
    //private OnAnimationStoppedListener mOnAnimationStoppedListener;

    private Bitmap mBitmap = null;
    private BitmapFactory.Options mBitmapOptions;

    public FramesSequenceAnimation(ImageView imageView, int[] frames, int fps) {
        mHandler = new Handler();
        mFrames = frames;
        mIndex = -1;
        mSoftReferenceImageView = new SoftReference<ImageView>(imageView);
        mShouldRun = false;
        mIsRunning = false;
        mDelayMillis = 1000 / fps;

        imageView.setImageResource(mFrames[0]);

        // use in place bitmap to save GC work (when animation images are the same size & type)
        if (Build.VERSION.SDK_INT >= 11) {
            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            Bitmap.Config config = bmp.getConfig();
            mBitmap = Bitmap.createBitmap(width, height, config);
            mBitmapOptions = new BitmapFactory.Options();
            // setup bitmap reuse options. 
            mBitmapOptions.inBitmap = mBitmap;
            mBitmapOptions.inMutable = true;
            mBitmapOptions.inSampleSize = 1;
        }
    }

    private int getNext() {
        mIndex++;
        if (mIndex >= mFrames.length)
            return -1;
        
        return mFrames[mIndex];
    }

    /**
     * Starts the animation
     */
    public synchronized void start() {
        mShouldRun = true;
        if (mIsRunning)
            return;

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ImageView imageView = mSoftReferenceImageView.get();
                if (!mShouldRun || imageView == null) {
                    mIsRunning = false;
                    /*
                    if (mOnAnimationStoppedListener != null) {
                        mOnAnimationStoppedListener.AnimationStopped();
                    }
                    */
                    return;
                }

                mIsRunning = true;
                mHandler.postDelayed(this, mDelayMillis);

                if (imageView.isShown()) {
                    int imageRes = getNext();
                    if(imageRes == -1) {
                    	stop();
                    	return;
                    }
                    if (mBitmap != null) { // so Build.VERSION.SDK_INT >= 11
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeResource(imageView.getResources(), imageRes, mBitmapOptions);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap);
                        } else {
                            imageView.setImageResource(imageRes);
                            mBitmap.recycle();
                            mBitmap = null;
                        }
                    } else {
                        imageView.setImageResource(imageRes);
                    }
                }

            }
        };

        mHandler.post(runnable);
    }

        /**
         * Stops the animation
         */
        public synchronized void stop() {
            mShouldRun = false;
        }
        
        public synchronized void replay() {
        	mIndex = 0;
            mShouldRun = true;
        }
    }

class AnimationsContainer {
    public int FPS = 10;  // animation FPS

    // single instance procedures
    private static AnimationsContainer mInstance;

    private AnimationsContainer() {
    };

    public static AnimationsContainer getInstance() {
        if (mInstance == null)
            mInstance = new AnimationsContainer();
        return mInstance;
    }

    // animation splash screen frames
    private int[] mGame1AnimFrames = { R.drawable.monster1_ani_1, R.drawable.monster1_ani_2, R.drawable.monster1_ani_3, R.drawable.monster1_ani_4, R.drawable.monster1_ani_5, R.drawable.monster1_ani_6, R.drawable.monster1_ani_7, R.drawable.monster1_ani_8, R.drawable.monster1_ani_9, R.drawable.monster1_ani_10, 
    		R.drawable.monster1_ani_11, R.drawable.monster1_ani_12, R.drawable.monster1_ani_13, R.drawable.monster1_ani_14, R.drawable.monster1_ani_15, R.drawable.monster1_ani_16, R.drawable.monster1_ani_17, R.drawable.monster1_ani_18, R.drawable.monster1_ani_19, R.drawable.monster1_ani_20, 
    		R.drawable.monster1_ani_21, R.drawable.monster1_ani_22, R.drawable.monster1_ani_23, R.drawable.monster1_ani_24, R.drawable.monster1_ani_25, R.drawable.monster1_ani_26, R.drawable.monster1_ani_27, R.drawable.monster1_ani_28, R.drawable.monster1_ani_29, R.drawable.monster1_ani_30, 
    		R.drawable.monster1_ani_31, R.drawable.monster1_ani_32, R.drawable.monster1_ani_33, R.drawable.monster1_ani_34, R.drawable.monster1_ani_35, R.drawable.monster1_ani_36, R.drawable.monster1_ani_37, R.drawable.monster1_ani_38, R.drawable.monster1_ani_39, R.drawable.monster1_ani_40, 
    		R.drawable.monster1_ani_41, R.drawable.monster1_ani_42, R.drawable.monster1_ani_43, R.drawable.monster1_ani_44, R.drawable.monster1_ani_45, R.drawable.monster1_ani_46, R.drawable.monster1_ani_47, R.drawable.monster1_ani_48, R.drawable.monster1_ani_49, R.drawable.monster1_ani_50, 
    		R.drawable.monster1_ani_51, R.drawable.monster1_ani_52, R.drawable.monster1_ani_53, R.drawable.monster1_ani_54, R.drawable.monster1_ani_55, R.drawable.monster1_ani_56, R.drawable.monster1_ani_57};
    private int[] mGame2AnimFrames = { R.drawable.monster2_ani_1, R.drawable.monster2_ani_2, R.drawable.monster2_ani_3, R.drawable.monster2_ani_4, R.drawable.monster2_ani_5, R.drawable.monster2_ani_6, R.drawable.monster2_ani_7, R.drawable.monster2_ani_8, R.drawable.monster2_ani_9, R.drawable.monster2_ani_10, 
    		R.drawable.monster2_ani_11, R.drawable.monster2_ani_12, R.drawable.monster2_ani_13, R.drawable.monster2_ani_14, R.drawable.monster2_ani_15, R.drawable.monster2_ani_16, R.drawable.monster2_ani_17, R.drawable.monster2_ani_18, R.drawable.monster2_ani_19, R.drawable.monster2_ani_20, 
    		R.drawable.monster2_ani_21, R.drawable.monster2_ani_22, R.drawable.monster2_ani_23, R.drawable.monster2_ani_24, R.drawable.monster2_ani_25, R.drawable.monster2_ani_26, R.drawable.monster2_ani_27, R.drawable.monster2_ani_28, R.drawable.monster2_ani_29, R.drawable.monster2_ani_30, 
    		R.drawable.monster2_ani_31, R.drawable.monster2_ani_32, R.drawable.monster2_ani_33, R.drawable.monster2_ani_34, R.drawable.monster2_ani_35, R.drawable.monster2_ani_36, R.drawable.monster2_ani_37, R.drawable.monster2_ani_38, R.drawable.monster2_ani_39, R.drawable.monster2_ani_40, 
    		R.drawable.monster2_ani_41, R.drawable.monster2_ani_42, R.drawable.monster2_ani_43, R.drawable.monster2_ani_44, R.drawable.monster2_ani_45, R.drawable.monster2_ani_46, R.drawable.monster2_ani_47, R.drawable.monster2_ani_48, R.drawable.monster2_ani_49, R.drawable.monster2_ani_50, 
    		R.drawable.monster2_ani_51, R.drawable.monster2_ani_52, R.drawable.monster2_ani_53, R.drawable.monster2_ani_54, R.drawable.monster2_ani_55, R.drawable.monster2_ani_56, R.drawable.monster2_ani_57, R.drawable.monster2_ani_58, R.drawable.monster2_ani_59, R.drawable.monster2_ani_60, 
    		R.drawable.monster2_ani_61, R.drawable.monster2_ani_62, R.drawable.monster2_ani_63, R.drawable.monster2_ani_64};
    private int[] mGame3AnimFrames = { R.drawable.monster3_ani_1, R.drawable.monster3_ani_2, R.drawable.monster3_ani_3, R.drawable.monster3_ani_4, R.drawable.monster3_ani_5, R.drawable.monster3_ani_6, R.drawable.monster3_ani_7, R.drawable.monster3_ani_8, R.drawable.monster3_ani_9, R.drawable.monster3_ani_10, 
    		R.drawable.monster3_ani_11, R.drawable.monster3_ani_12, R.drawable.monster3_ani_13, R.drawable.monster3_ani_14, R.drawable.monster3_ani_15, R.drawable.monster3_ani_16, R.drawable.monster3_ani_17, R.drawable.monster3_ani_18, R.drawable.monster3_ani_19, R.drawable.monster3_ani_20, 
    		R.drawable.monster3_ani_21, R.drawable.monster3_ani_22, R.drawable.monster3_ani_23, R.drawable.monster3_ani_24, R.drawable.monster3_ani_25, R.drawable.monster3_ani_26, R.drawable.monster3_ani_27, R.drawable.monster3_ani_28, R.drawable.monster3_ani_29, R.drawable.monster3_ani_30, 
    		R.drawable.monster3_ani_31, R.drawable.monster3_ani_32, R.drawable.monster3_ani_33, R.drawable.monster3_ani_34, R.drawable.monster3_ani_35, R.drawable.monster3_ani_36, R.drawable.monster3_ani_37, R.drawable.monster3_ani_38, R.drawable.monster3_ani_39, R.drawable.monster3_ani_40, 
    		R.drawable.monster3_ani_41, R.drawable.monster3_ani_42, R.drawable.monster3_ani_43, R.drawable.monster3_ani_44, R.drawable.monster3_ani_45, R.drawable.monster3_ani_46, R.drawable.monster3_ani_47, R.drawable.monster3_ani_48, R.drawable.monster3_ani_49, R.drawable.monster3_ani_50, 
    		R.drawable.monster3_ani_51, R.drawable.monster3_ani_52, R.drawable.monster3_ani_53, R.drawable.monster3_ani_54, R.drawable.monster3_ani_55, R.drawable.monster3_ani_56, R.drawable.monster3_ani_57, R.drawable.monster3_ani_58, R.drawable.monster3_ani_59, R.drawable.monster3_ani_60, 
    		R.drawable.monster3_ani_61, R.drawable.monster3_ani_62, R.drawable.monster3_ani_63, R.drawable.monster3_ani_64, R.drawable.monster3_ani_65, R.drawable.monster3_ani_66, R.drawable.monster3_ani_67, R.drawable.monster3_ani_68, R.drawable.monster3_ani_69, R.drawable.monster3_ani_70, 
    		R.drawable.monster3_ani_71}; 
    private int[] mGame4AnimFrames = { R.drawable.monster4_ani_1, R.drawable.monster4_ani_2, R.drawable.monster4_ani_3, R.drawable.monster4_ani_4, R.drawable.monster4_ani_5, R.drawable.monster4_ani_6, R.drawable.monster4_ani_7, R.drawable.monster4_ani_8, R.drawable.monster4_ani_9, R.drawable.monster4_ani_10, 
    		R.drawable.monster4_ani_11, R.drawable.monster4_ani_12, R.drawable.monster4_ani_13, R.drawable.monster4_ani_14, R.drawable.monster4_ani_15, R.drawable.monster4_ani_16, R.drawable.monster4_ani_17, R.drawable.monster4_ani_18, R.drawable.monster4_ani_19, R.drawable.monster4_ani_20, 
    		R.drawable.monster4_ani_21, R.drawable.monster4_ani_22, R.drawable.monster4_ani_23, R.drawable.monster4_ani_24, R.drawable.monster4_ani_25, R.drawable.monster4_ani_26, R.drawable.monster4_ani_27, R.drawable.monster4_ani_28, R.drawable.monster4_ani_29, R.drawable.monster4_ani_30, 
    		R.drawable.monster4_ani_31, R.drawable.monster4_ani_32, R.drawable.monster4_ani_33, R.drawable.monster4_ani_34, R.drawable.monster4_ani_35, R.drawable.monster4_ani_36, R.drawable.monster4_ani_37, R.drawable.monster4_ani_38, R.drawable.monster4_ani_39, R.drawable.monster4_ani_40, 
    		R.drawable.monster4_ani_41, R.drawable.monster4_ani_42, R.drawable.monster4_ani_43, R.drawable.monster4_ani_44, R.drawable.monster4_ani_45, R.drawable.monster4_ani_46, R.drawable.monster4_ani_47, R.drawable.monster4_ani_48, R.drawable.monster4_ani_49, R.drawable.monster4_ani_50, 
    		R.drawable.monster4_ani_51};
    /**
     * @param imageView
     * @return splash screen animation
     */
    public FramesSequenceAnimation createGameAnim(ImageView imageView, int gameIndex) {
    	if(gameIndex == 1)
    		return new FramesSequenceAnimation(imageView, mGame1AnimFrames, FPS);
    	else if(gameIndex == 2)
    		return new FramesSequenceAnimation(imageView, mGame2AnimFrames, FPS);
    	else if(gameIndex == 3)
    		return new FramesSequenceAnimation(imageView, mGame3AnimFrames, FPS);
    	else if(gameIndex == 4)
    		return new FramesSequenceAnimation(imageView, mGame4AnimFrames, FPS);
    	else
    		return null;
    }

    /**
     * AnimationPlayer. Plays animation frames sequence in loop
     */


}
