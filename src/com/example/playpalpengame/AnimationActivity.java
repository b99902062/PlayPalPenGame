package com.example.playpalpengame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.util.Scanner;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
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
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class AnimationActivity extends Activity {
	FramesSequenceAnimation anim = null;
	ImageView monsterView;
	private String mUserName = null;
	private int mBadges = 0;
	private int mHighScore = 0;
	private int mWinCount = 0;
	private int gameIndex;
	private boolean isWin;
	private int[] starResArray = {R.drawable.star_1, R.drawable.star_2, R.drawable.star_3, R.drawable.star_4, R.drawable.star_5, R.drawable.star_6};
	protected AnimationDrawable monsterAnim;
	
	public static Context self;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		self = this;
		setContentView(R.layout.activity_animation);
		
		Bundle bundle = getIntent().getExtras();
		gameIndex = bundle.getInt("GameIndex");
		isWin = bundle.getBoolean("isWin");
		mUserName = bundle.getString("userName");
		mBadges = bundle.getInt("GameBadges");
		mHighScore = bundle.getInt("GameHighScore");
		mWinCount = bundle.getInt("GameWinCount");
		int newScore = bundle.getInt("NewScore");
		monsterView = (ImageView)findViewById(R.id.monsterView);
		ImageView replayBtn = (ImageView)findViewById(R.id.replayBtn);
		
		setHomeListener(findViewById(R.id.homeBtn));
		
		if(isWin)
			mWinCount++;
		
		if(isWin && newScore >= mHighScore * 1.1) {
			if(mHighScore == 0)
				mHighScore = newScore;
			else
				mHighScore = (int) (mHighScore * 1.1);
			for(int j=0; j<6; j++) {
				if(((mBadges >> j) & 0x1) == 0) {
					((ImageView)findViewById(R.id.starView)).setImageResource(starResArray[j]);
					mBadges |= (0x1 << j);
					break;
				}
			}
		
			updateRecordJson();
			
			anim = AnimationsContainer.getInstance().createStarAnim(monsterView);
			anim.start();
			PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_HOORAY, this);
			monsterView.setOnTouchListener(new OnTouchListener() {
				private boolean isSetEnd = false;
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if(!isSetEnd) {
						anim.stop();
						((ImageView)findViewById(R.id.starView)).setVisibility(ImageView.GONE);
						setEndAnim();
						isSetEnd = true;
					}
					return true;
				}
			});
		}
		else if(isWin) {
			mHighScore -= 5;
			updateRecordJson();
			setEndAnim();
		}
		else 
			setEndAnim();
		
		((ImageView)findViewById(R.id.replayBtn)).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(anim != null)
					anim.stop();
				Intent newAct = new Intent();
				if(gameIndex == 1)
					newAct.setClass(AnimationActivity.this, Game1Activity.class);
				else if(gameIndex == 2)
					newAct.setClass(AnimationActivity.this, Game2Activity.class);
				else if(gameIndex == 3)
					newAct.setClass(AnimationActivity.this, Game3Activity.class);
				else if(gameIndex == 4)
					newAct.setClass(AnimationActivity.this, Game4Activity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userName", mUserName);
				bundle.putInt("GameBadges", mBadges);
				bundle.putInt("GameHighScore", mHighScore);
	            newAct.putExtras(bundle);
				startActivityForResult(newAct, 0);
				AnimationActivity.this.finish();
				return true;
			}
		});
			
    	return;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		BackgroundMusicHandler.recyle();
	}
	
	@Override
	protected void onResume() {
		if(mUserName == null)
			return;
		super.onResume();
		if(isWin)
			BackgroundMusicHandler.initMusic(this, BackgroundMusicHandler.MUSIC_WIN);
		else
			BackgroundMusicHandler.initMusic(this, BackgroundMusicHandler.MUSIC_LOSE);
		BackgroundMusicHandler.setMusicSt(true);
	}
	
	private void updateRecordJson() {
		String recordJson = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader("/sdcard/Android/data/com.example.playpalgame/record.json"));
			String line = null;
			while ((line = reader.readLine()) != null)
			    recordJson = recordJson.concat(line);
			JSONArray recordArray = new JSONArray(recordJson);
			for(int i=0; i<recordArray.length(); i++) {
				JSONObject singleRecord = recordArray.getJSONObject(i);
				if(singleRecord.getString("name").equals(mUserName)) {
					String badgeKey = "gameBadge".concat(String.valueOf(gameIndex));
					String scoreKey = "gameHighScore".concat(String.valueOf(gameIndex));
					String winKey = "gameWinCount".concat(String.valueOf(gameIndex));
					singleRecord.put(badgeKey, mBadges);
					singleRecord.put(scoreKey, mHighScore);
					singleRecord.put(winKey, mWinCount);
					break;
				}
			}
			File newTextFile = new File("/sdcard/Android/data/com.example.playpalgame/record.json");
			FileWriter fileWriter = new FileWriter(newTextFile);
            fileWriter.write(recordArray.toString(2));
            fileWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setEndAnim() {
		((ImageView)findViewById(R.id.homeBtn)).setVisibility(ImageView.VISIBLE);
		((ImageView)findViewById(R.id.replayBtn)).setVisibility(ImageView.VISIBLE);
		anim = AnimationsContainer.getInstance()
				.createGameAnim(monsterView, gameIndex, isWin);
		anim.start();
	}
	
	protected void setHomeListener(View targetView) {
		targetView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				Intent newAct = new Intent();
				newAct.setClass(AnimationActivity.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("userName", mUserName);
	            newAct.putExtras(bundle);
				startActivityForResult(newAct, 0);
				AnimationActivity.this.finish();
				return true;
			}
		});
	}
}

class FramesSequenceAnimation {
	private boolean mIsRepeat = false;
	
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

	private Callable<Integer> mStopListener = null;

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

    public FramesSequenceAnimation(ImageView imageView, int[] frames, int fps, boolean isRepeat) {
    	this(imageView, frames, fps);
    	mIsRepeat = isRepeat;
    }
    
    private int getNext() {
        mIndex++;
        if (mIndex >= mFrames.length) {
        	if(mIsRepeat)
        		mIndex = 0;
        	else
        		return -1;
        }
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
                    playSpecificSoundByFrame(imageRes);
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
            if(mStopListener != null)
				try {
					mStopListener.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
        }
        
        public synchronized void replay() {
        	mIndex = 0;
            mShouldRun = true;
        }

		public void setStoppedAnimListener(Callable<Integer> callable) {
			mStopListener = callable;
		}
		
		public void playSpecificSoundByFrame(int resID) {
			if(resID == R.drawable.monster1_ani_18)
				PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_DRINKING, AnimationActivity.self);
			else if(resID == R.drawable.welcome_ani_19)
				PlayPalUtility.playSoundEffect(PlayPalUtility.SOUND_HOORAY, WelcomeActivity.self);
		}
    }

class AnimationsContainer {
    public int FPS = 10;  // animation FPS
    public int LOSE_FPS = 2;  // animation FPS

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
    private int[] mGame1LoseFrames = {R.drawable.lose_1_1, R.drawable.lose_1_2, R.drawable.lose_1_3};
    private int[] mGame2LoseFrames = {R.drawable.lose_2_1, R.drawable.lose_2_2};
    private int[] mGame3LoseFrames = {R.drawable.lose_3_1, R.drawable.lose_3_2};
    private int[] mGame4LoseFrames = {R.drawable.lose_4_1, R.drawable.lose_4_2};
    
    private int[] mStarAnimFrames = {R.drawable.star_ani_01, R.drawable.star_ani_02, R.drawable.star_ani_03, R.drawable.star_ani_04, R.drawable.star_ani_05, R.drawable.star_ani_06, R.drawable.star_ani_07, R.drawable.star_ani_08, R.drawable.star_ani_09, R.drawable.star_ani_10, R.drawable.star_ani_11, R.drawable.star_ani_12, R.drawable.star_ani_13, R.drawable.star_ani_14, R.drawable.star_ani_15, R.drawable.star_ani_16, R.drawable.star_ani_17};
    private int[] mWelcomeAnimFrames = {R.drawable.welcome_ani_01, R.drawable.welcome_ani_02, R.drawable.welcome_ani_03, R.drawable.welcome_ani_04, R.drawable.welcome_ani_05_x5, R.drawable.welcome_ani_06, R.drawable.welcome_ani_06_2, R.drawable.welcome_ani_07, R.drawable.welcome_ani_07_2, R.drawable.welcome_ani_08, R.drawable.welcome_ani_09, R.drawable.welcome_ani_10, R.drawable.welcome_ani_11, R.drawable.welcome_ani_12, R.drawable.welcome_ani_13, R.drawable.welcome_ani_14, R.drawable.welcome_ani_15, R.drawable.welcome_ani_16, R.drawable.welcome_ani_17, R.drawable.welcome_ani_18, R.drawable.welcome_ani_19, R.drawable.welcome_ani_20, R.drawable.welcome_ani_21, R.drawable.welcome_ani_22, R.drawable.welcome_ani_23, R.drawable.welcome_ani_24, R.drawable.welcome_ani_24_2, R.drawable.welcome_ani_25, R.drawable.welcome_ani_25_2, R.drawable.welcome_ani_26, R.drawable.welcome_ani_26_2, R.drawable.welcome_ani_27, R.drawable.welcome_ani_27_2, R.drawable.welcome_ani_28, R.drawable.welcome_ani_28_2, R.drawable.welcome_ani_29, R.drawable.welcome_ani_29_2, R.drawable.welcome_ani_30, R.drawable.welcome_ani_30_2, R.drawable.welcome_ani_31, R.drawable.welcome_ani_31_2, R.drawable.welcome_ani_32 };
    
    private int[] mGame1PotStirAnimFrams = {R.drawable.game1_pot_3, R.drawable.game1_pot_4, R.drawable.game1_pot_5, R.drawable.game1_pot_6};
    private int[] mGame3MixAnimFrames  = {R.drawable.game3_mix2, R.drawable.game3_mix3, R.drawable.game3_mix4, R.drawable.game3_mix5};
    private int[] mGame3MixAnimFrames2 = {R.drawable.game3_mix6, R.drawable.game3_mix7, R.drawable.game3_mix8, R.drawable.game3_mix9};
    
    public FramesSequenceAnimation createStarAnim(ImageView imageView) {
    	return new FramesSequenceAnimation(imageView, mStarAnimFrames, FPS, true);
    }
    
    public FramesSequenceAnimation createWelcomeAnim(ImageView imageView) {
    	return new FramesSequenceAnimation(imageView, mWelcomeAnimFrames, FPS, false);
    } 
    
    public FramesSequenceAnimation createGame3StirAnim(ImageView imageView, int stirIdx){
    	if(stirIdx == 1)
    		return new FramesSequenceAnimation(imageView, mGame3MixAnimFrames, FPS);
    	else if(stirIdx ==2)
    		return new FramesSequenceAnimation(imageView, mGame3MixAnimFrames2, FPS);
    	else{
    		Log.e("AnimationActivity","error idx when newing striAnim");
    		return null;
    	}
    }
    
    public FramesSequenceAnimation createGame1PotStirAnim(ImageView imageView){
    	return new FramesSequenceAnimation(imageView, mGame1PotStirAnimFrams, FPS);
    }
    
    	
    /**
     * @param imageView
     * @return splash screen animation
     */
    public FramesSequenceAnimation createGameAnim(ImageView imageView, int gameIndex, boolean isWin) {
    	if(isWin) {
	    	if(gameIndex == 1)
	    		return new FramesSequenceAnimation(imageView, mGame1AnimFrames, FPS, true);
	    	else if(gameIndex == 2)
	    		return new FramesSequenceAnimation(imageView, mGame2AnimFrames, FPS, true);
	    	else if(gameIndex == 3)
	    		return new FramesSequenceAnimation(imageView, mGame3AnimFrames, FPS, true);
	    	else if(gameIndex == 4)
	    		return new FramesSequenceAnimation(imageView, mGame4AnimFrames, FPS, true);
	    	else
	    		return null;
    	} else {
    		if(gameIndex == 1)
	    		return new FramesSequenceAnimation(imageView, mGame1LoseFrames, LOSE_FPS, true);
	    	else if(gameIndex == 2)
	    		return new FramesSequenceAnimation(imageView, mGame2LoseFrames, LOSE_FPS, true);
	    	else if(gameIndex == 3)
	    		return new FramesSequenceAnimation(imageView, mGame3LoseFrames, LOSE_FPS, true);
	    	else if(gameIndex == 4)
	    		return new FramesSequenceAnimation(imageView, mGame4LoseFrames, LOSE_FPS, true);
	    	else
	    		return null;
    	}
    }
}
