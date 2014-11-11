package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenHoverListener;

public class PlayPalUtility {
	protected final static int SOUND_ID_TEST = 0;
	protected final static int SOUND_CUT_FOOD = 1;
	protected final static int SOUND_SPLIT_POT = 2;
	protected final static int SOUND_STIR_POT = 3;
	protected final static int SOUND_HOORAY = 4;
	protected final static int SOUND_UHOH = 5;
	protected final static int SOUND_DRINKING = 6;
	protected final static int SOUND_ROAST = 7;
	protected final static int SOUND_CARTOON = 8;
	
	protected final static int SOUND_MIX = 9;
	protected final static int SOUND_OVEN = 10;
	protected final static int SOUND_POP = 11;
	protected final static int SOUND_CREAM = 12;
	
	protected final static int SOUND_ROLLING = 13;
	protected final static int SOUND_CUT_DOUGH = 14;
	
	protected final static int SOUND_TIME_REMINDER = 15;
	protected final static int SOUND_TIMER_TICK = 16;
	protected final static int SOUND_TIMER_DING = 17;
	protected final static int SOUND_COOKIE_POP = 18;
	
	protected final static int FROM_OUTLEFT_TO_CUR = 1;
	protected final static int FROM_CUR_TO_OUTRIGHT = 2;
	protected final static int FROM_OUTRIGHT_TO_CUR = 3;
	protected final static int FROM_CUR_TO_OUTLEFT = 4;
	
	protected final static int TIME_MODE = 1;
	protected final static int PROGRESS_MODE = 2;
	
	private final static int beginProgressMarkX = 746;
	private final static int endProgressMarkX = 1566;
	private final static int progressMarkY = 122;
	
	private static boolean isNeedHover = false;
	private static ImageView hoverTarget = null;
	
	protected static boolean isLineGestureOn;
	protected static int lastTriggerSetIndex = -1;
	protected static int lastTriggerPointIndex = -1;
	protected static View targetView;
	protected static Context targetContext;
	protected static boolean isDebugMode = true;
	protected static int barMode;
	
	protected static DrawView drawview;
	protected static Timer timer;
	protected static TimeBarTask timerTask;
	
	private static ArrayList<MediaPlayer> voiceList = new ArrayList<MediaPlayer>();
	
	private static boolean isPlayFeedback;
	protected static ImageView feedbackView;

	private static Callable<Integer> failFunc;
	private static Callable<Integer> reminderFunc;
	private static int totalProgress = 0;
	private static int curProgress = 0;
	private static ProgressBar progressBar;
	private static ImageView progressMark;
	private static ImageView progressBack;
	
	private static boolean isPressing;
	
	protected static ArrayList<GestureSet> gestureSetList = new ArrayList<GestureSet>();
	protected static ArrayList<StrokeSet> strokeSetList = new ArrayList<StrokeSet>();
	
	private static int[] soundRes = {R.raw.test_sound, 
									 R.raw.game1_cutting_onion, 
									 R.raw.game1_water_drop, 
									 R.raw.game1_boiling_short, 
									 R.raw.unsorted_yayyy, 
									 R.raw.unsorted_uhoh, 
									 R.raw.game1_drinking, 
									 R.raw.game2_roast, 
									 R.raw.unsorted_cartoon, 
									 R.raw.game3_mixing,
									 R.raw.game3_oven_trimmed, 
									 R.raw.game3_cream_pop,
									 R.raw.game3_squeezing_cream,
									 R.raw.game4_cartoon_rolling,
									 R.raw.game4_cutting_dough,
									 R.raw.time_reminder,
									 R.raw.timer_tick,
									 R.raw.timer_ding,
									 R.raw.pop};
	
	private static int[][] voiceRes = {{R.raw.voice_1_1, R.raw.voice_1_2, R.raw.voice_1_3, R.raw.voice_1_4, R.raw.voice_1_5, R.raw.voice_1_6, R.raw.voice_1_7, R.raw.voice_1_8, R.raw.voice_1_9, R.raw.voice_1_10},
		{R.raw.voice_2_1, R.raw.voice_2_2, R.raw.voice_2_3, R.raw.voice_2_4, R.raw.voice_2_5, R.raw.voice_2_6, R.raw.voice_2_7},
		{R.raw.voice_3_1, R.raw.voice_3_2, R.raw.voice_3_3, R.raw.voice_3_4, R.raw.voice_3_5, R.raw.voice_3_6, R.raw.voice_3_7, R.raw.voice_3_8, R.raw.voice_3_9, R.raw.voice_3_10, R.raw.voice_3_11}, 
		{R.raw.voice_4_1, R.raw.voice_4_2, R.raw.voice_4_3, R.raw.voice_4_4, R.raw.voice_4_5, R.raw.voice_4_6, R.raw.voice_4_7}}; 
	
	protected static boolean butterSqueezing = false;
	protected static SPenEventLibrary mSPenEventLibrary = new SPenEventLibrary();
	public static RecordEntry curEntry;
	
	protected static float calcDistance(Point p1, Point p2){
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		
		return FloatMath.sqrt(dx*dx + dy*dy);
	}
	
	protected static TranslateAnimation CreateTranslateAnimation(int translateType) {
		return CreateTranslateAnimation(translateType, 2000);
	}
	
	protected static TranslateAnimation CreateTranslateAnimation(int translateType, int duration) {
		TranslateAnimation newAnim;

		if (translateType == FROM_OUTLEFT_TO_CUR)
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
					(float) -1.0, Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		else if (translateType == FROM_CUR_TO_OUTRIGHT)
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					(float) 0.0, Animation.RELATIVE_TO_PARENT, (float) 1.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		else if (translateType == FROM_OUTRIGHT_TO_CUR)
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
					(float) 1.0, Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		else if (translateType == FROM_CUR_TO_OUTLEFT)
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					(float) 0.0, Animation.RELATIVE_TO_PARENT, (float) -1.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		else
			newAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
					(float) 0.0, Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0,
					Animation.RELATIVE_TO_SELF, (float) 0.0);
		newAnim.setDuration(duration);
		newAnim.setRepeatCount(0);
		return newAnim;
	}
	
	protected static void setAlphaAnimation(View view, boolean isIn, int duration) {
		setAlphaAnimation(view, isIn, null, duration);
	}
	
	protected static void setAlphaAnimation(View view, boolean isIn) {
		setAlphaAnimation(view, isIn, null, 2000);
	}
	
	protected static void setAlphaAnimation(View view, boolean isIn, final Callable<Integer> func) {
		setAlphaAnimation(view, isIn, func, 2000);
	}
	
	protected static void setAlphaAnimation(View view, boolean isIn, final Callable<Integer> func, int duration) {
		Animation fade;
		if(isIn) 
			fade = new AlphaAnimation(0, 1);
		else
			fade = new AlphaAnimation(1, 0);
		fade.setInterpolator(new DecelerateInterpolator());
		fade.setDuration(duration);
		if(func != null) {
			fade.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation arg0) {
					try {
						func.call();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
		}
		view.setAnimation(fade);
	}
	
	protected static void clearGestureSets() {
		for(int i=0; i<gestureSetList.size(); i++)
			cancelGestureSet(i);
		gestureSetList.clear();
	}
	
	protected static void cancelGestureSet(int setIndex) {
		gestureSetList.get(setIndex).isValid = false;
		for(int i=0; i<gestureSetList.get(setIndex).hintViewList.size(); i++) {
			gestureSetList.get(setIndex).hintViewList.get(i).setVisibility(ImageView.GONE);
		};
	}
	
	protected static void unregisterLineGesture(View view) {
		view.setOnTouchListener(null);
		targetView = null;
	}
	
	protected static void unregisterHoverLineGesture(View view){
		mSPenEventLibrary.setSPenHoverListener(null, null);
		targetView = null;
	}
		
	protected static void registerSingleHoverPoint(final boolean isHovering, View view, Context context, final Callable<Integer> func) {
		targetView = view;
		targetContext = context;
		
		mSPenEventLibrary.setSPenHoverListener(targetView, new SPenHoverListener(){
			Point startPoint;
			ImageView curButterView;
			
			@Override
			public void onHoverButtonUp(View v, MotionEvent event) {
				isPressing = false;
				for(int setIndex=0; setIndex<gestureSetList.size(); setIndex++) {
					GestureSet curSet = gestureSetList.get(setIndex);
					ArrayList<Integer> pointPassedList = curSet.passedList;
					if(!curSet.isValid)
						continue;
					
					//testing the first point 
					if(isWithinBox(setIndex, 0, new Point((int)event.getX(), (int)event.getY()))){
						curSet.isValid = false;
						lastTriggerSetIndex = setIndex;
						try {
							func.call();
							pointPassedList.clear();
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			@Override
			public boolean onHover(View arg0, MotionEvent event) {
				if(isHovering && isPressing){
					for(int setIndex=0; setIndex<gestureSetList.size(); setIndex++) {
						GestureSet curSet = gestureSetList.get(setIndex);
						ArrayList<Integer> pointPassedList = curSet.passedList;
						if(!curSet.isValid)
							continue;
						
						//testing the first point 
						if(isWithinBox(setIndex, 0, new Point((int)event.getX(), (int)event.getY()))){
							curSet.isValid = false;
							lastTriggerSetIndex = setIndex;
							try {
								func.call();
								pointPassedList.clear();
							} catch(Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
				
				if(event.getAction()  == MotionEvent.ACTION_HOVER_ENTER) {
					hoverTarget.setVisibility(ImageView.VISIBLE);
					curEntry = new RecordEntry(
							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_START);
				} 
				else if(event.getAction()  == MotionEvent.ACTION_HOVER_MOVE) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                	params.setMargins((int)event.getX(), (int)event.getY(), 0, 0);
                	hoverTarget.setLayoutParams(params);
					curEntry = new RecordEntry(
							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_MOVE);
				}
				else {
					hoverTarget.setVisibility(ImageView.INVISIBLE);
					curEntry = new RecordEntry(
							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_END);
				}
				return true;
			}

			@Override
			public void onHoverButtonDown(View arg0, MotionEvent arg1) {
				isPressing = true;
			}
		});
	}
	
	
	protected static void registerHoverLineGesture(View view, Context context, final Callable<Integer> func) {
		targetView = view;
		targetContext = context;
		
		mSPenEventLibrary.setSPenHoverListener(targetView, new SPenHoverListener(){
			Point startPoint;
			ImageView curButterView;
			
			GestureSet curSet;
			ArrayList<Integer> pointPassedList;
			
			@Override
			public boolean onHover(View arg0, MotionEvent event) {
				if(event.getAction()  == MotionEvent.ACTION_HOVER_ENTER) {
					hoverTarget.setVisibility(ImageView.VISIBLE);
					curEntry = new RecordEntry(
							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_START);
				} 
				else if(event.getAction()  == MotionEvent.ACTION_HOVER_MOVE) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                	params.setMargins((int)event.getX(), (int)event.getY(), 0, 0);
                	hoverTarget.setLayoutParams(params);
					curEntry = new RecordEntry(
							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_MOVE);
				}
				else {
					hoverTarget.setVisibility(ImageView.INVISIBLE);
					curEntry = new RecordEntry(
							new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_HOVER_END);
				}
				
				
				
				if(!isLineGestureOn || !isPressing)
					return false;
				
				for(int setIndex=0; setIndex<gestureSetList.size(); setIndex++) {
					curSet = gestureSetList.get(setIndex);
					pointPassedList = curSet.passedList;
					
					if(!curSet.isValid)
						continue;
					
					if (!curSet.isContinuous && pointPassedList.size() == 0)
						continue;
					
					if(curSet.isInOrder) {
						if (pointPassedList.get(0) != 0) 
							continue;
						int lastIndex = pointPassedList.get(pointPassedList.size() - 1);
						if(isWithinBox(setIndex, lastIndex+1, new Point((int)event.getX(), (int)event.getY()))) {
							pointPassedList.add(lastIndex+1);
							if(curSet.isContinuous && pointPassedList.size() >= curSet.pointList.size()) {
								try {
									func.call();
									pointPassedList.clear();
								} catch(Exception ex) {
									ex.printStackTrace();
								}
							}
						}
					}
					else {
						for(int pointIndex = 0; pointIndex < curSet.pointList.size(); pointIndex++) {
							if(!pointPassedList.contains(pointIndex) && isWithinBox(setIndex, pointIndex, new Point((int)event.getX(), (int)event.getY()))) {
								Log.d("Utility","Passed"+setIndex+","+(pointIndex+1));
								pointPassedList.add(pointIndex);
								
								if(curSet.isContinuous && pointPassedList.size() >= curSet.pointList.size()) {
									try {
										func.call();
										pointPassedList.clear();
									} catch(Exception ex) {
										ex.printStackTrace();
									}
								}
								continue;
							}
						}
					}
				}
				return false;
			}
			@Override
			public void onHoverButtonDown(View v, MotionEvent event) {
				PenRecorder.forceRecord();
				//PenRecorder.startRecorder();
				
				if(!isLineGestureOn)
					return;
				
				for(int setIndex=0; setIndex<gestureSetList.size(); setIndex++) {
					if(!gestureSetList.get(setIndex).isValid)
						continue;
					GestureSet curSet = gestureSetList.get(setIndex);
					ArrayList<Integer> pointPassedList = curSet.passedList;
					if(curSet.isInOrder) {
						if(isWithinBox(setIndex, 0, new Point((int)event.getX(), (int)event.getY())))
							pointPassedList.add(0);
					}
					else {
						for(int pointIndex = 0; pointIndex < curSet.pointList.size(); pointIndex++) {
							if(isWithinBox(setIndex, pointIndex, new Point((int)event.getX(), (int)event.getY()))) {
								pointPassedList.add(pointIndex);
								break;
							}
						}
					}
				}
				isPressing = true;
			}
				
			@Override
			public void onHoverButtonUp(View v, MotionEvent event) {
				PenRecorder.forceRecord();
				//PenRecorder.stopRecorder();
				
				if(!isLineGestureOn)
					return;
				
				for(int setIndex=0; setIndex<gestureSetList.size(); setIndex++) {
					if(!gestureSetList.get(setIndex).isValid)
						continue;
					GestureSet curSet = gestureSetList.get(setIndex);
					ArrayList<Integer> pointPassedList = curSet.passedList;
					
					if(curSet.isContinuous) {
						pointPassedList.clear();
						continue;
					}
					if(pointPassedList.size() >= curSet.pointList.size()) {
						lastTriggerSetIndex = setIndex;
						try {
							func.call();
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
					pointPassedList.clear();
					break;	
				}
				isPressing = false;
			}
		});
		
	}
	
	/* func  : called after finish line gesture finished
	 * func2 : always called when touched (default null)
	 * */
	protected static void registerLineGesture(View view, Context context, final Callable<Integer> func){
		registerLineGesture(view, context, func, null);
	}
	
	protected static void registerFailFeedback(ImageView view) {
		feedbackView = view;
	}
	
	protected static void unregisterFailFeedback() {
		feedbackView = null;
	}
	
	protected static void doFailFeedback(Context context) {
		Log.d("EndTest", "doFailFeedback()");
		if(feedbackView == null)
			return;
		playSoundEffect(PlayPalUtility.SOUND_UHOH, context);
		isPlayFeedback = true;
		feedbackView.setVisibility(View.VISIBLE);
		setAlphaAnimation(feedbackView, true, new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				setAlphaAnimation(feedbackView, false, new Callable<Integer>() {
					@Override
					public Integer call() throws Exception {
						feedbackView.setVisibility(View.INVISIBLE);
						isPlayFeedback = false;
						return 0;
					}
				}, 300);
				return 0;
			}
		}, 300);
	}

	protected static void registerLineGesture(View view, Context context, final Callable<Integer> func, final Callable<Integer> func2 ){
		targetView = view;
		targetContext = context;
		
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					curEntry = new RecordEntry(
						new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_TOUCH_START);
					PenRecorder.forceRecord();
				}
				else if(event.getAction() == MotionEvent.ACTION_MOVE)
					curEntry = new RecordEntry(
						new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_TOUCH_MOVE);
				else {
					curEntry = new RecordEntry(
						new Point((int)event.getX(), (int)event.getY()), RecordEntry.STATE_TOUCH_END);
					PenRecorder.forceRecord();
				}
				
				if(!isLineGestureOn)
					return false;
				boolean isTrigger = false;
				for(int setIndex=0; setIndex<gestureSetList.size(); setIndex++) {
					if(!gestureSetList.get(setIndex).isValid)
						continue;
					GestureSet curSet = gestureSetList.get(setIndex);
					ArrayList<Integer> pointPassedList = curSet.passedList;
					
					
					switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							if(curSet.isInOrder) {
								if(isWithinBox(setIndex, 0, new Point((int)event.getX(), (int)event.getY())))
									pointPassedList.add(0);
							}
							else {
								for(int pointIndex = 0; pointIndex < curSet.pointList.size(); pointIndex++) {
									if(isWithinBox(setIndex, pointIndex, new Point((int)event.getX(), (int)event.getY()))) {
										pointPassedList.add(pointIndex);
										//trigger func2
										if(func2 != null){
											try {
												func2.call();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
										break;
									}
								}
							}							
							break;
						case MotionEvent.ACTION_MOVE:
							if(isNeedHover) {
								RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		                    	params.setMargins((int)event.getX(), (int)event.getY() , 0, 0);
		                    	hoverTarget.setLayoutParams(params);
		                    	hoverTarget.setVisibility(ImageView.VISIBLE);
							}
							if (!curSet.isContinuous
								&& pointPassedList.size() == 0)
								break;
							if(curSet.isInOrder) {
								if (pointPassedList.get(0) != 0) 
									break;
								int lastIndex = pointPassedList.get(pointPassedList.size() - 1);
								lastTriggerPointIndex = lastIndex;
								if(isWithinBox(setIndex, lastIndex+1, new Point((int)event.getX(), (int)event.getY()))) {
									pointPassedList.add(lastIndex+1);
									//trigger func2
									if(func2 != null){
										try {
											func2.call();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
									if(curSet.isContinuous
									&& pointPassedList.size() >= curSet.pointList.size()) {
										try {
											func.call();
											pointPassedList.clear();
										} catch(Exception ex) {
											ex.printStackTrace();
										}
									}
								}
							}
							else {
								for(int pointIndex = 0; pointIndex < curSet.pointList.size(); pointIndex++) {
									if(!pointPassedList.contains(pointIndex)
									&& isWithinBox(setIndex, pointIndex, new Point((int)event.getX(), (int)event.getY()))) {
										pointPassedList.add(pointIndex);
										lastTriggerPointIndex = pointIndex;
										
										//trigger func2
										if(func2 != null){
											try {
												func2.call();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
										if(curSet.isContinuous
										&& pointPassedList.size() >= curSet.pointList.size()) {
											try {
												func.call();
												pointPassedList.clear();
											} catch(Exception ex) {
												ex.printStackTrace();
											}
										}
										break;
									}
								}
							}
							break;
						case MotionEvent.ACTION_UP:
							if(curSet.isContinuous) {
								pointPassedList.clear();
								break;
							}
							if(pointPassedList.size() >= curSet.pointList.size()) {
								lastTriggerSetIndex = setIndex;
								try {
									isTrigger = true;
									func.call();
								} catch(Exception ex) {
									ex.printStackTrace();
								}
							}
							pointPassedList.clear();
							break;
					}
				}
				if(!isTrigger && event.getAction() == MotionEvent.ACTION_UP && !isPlayFeedback)
					doFailFeedback(targetContext);
				return true;
			}
		});
	}

	protected static void setHoverTarget(boolean value, ImageView view) {
		isNeedHover = value;
		hoverTarget = view;
	}
	
	protected static int initialLineGestureParams(boolean isContinuous, boolean isInOrder, int size, Point... points) {
		for (Point p : points) 
			Log.d("PlayPalUtility", String.format("Point = (%d, %d)", p.x, p.y));
		
		GestureSet gestureSet = new GestureSet();
		gestureSet.boxSize = size;
		gestureSet.isContinuous = isContinuous;
		gestureSet.isInOrder = isInOrder;
		gestureSet.pointList = new ArrayList<Point>();
		gestureSet.passedList = new ArrayList<Integer>(); 
		gestureSet.hintViewList = new ArrayList<ImageView>(); 
		for (Point p : points) {
			gestureSet.pointList.add(p);
			
			if(isDebugMode) {
				ImageView imgView = new ImageView(targetContext);
				imgView.setAlpha(0.5f);
				imgView.setImageDrawable(targetContext.getResources().getDrawable(R.drawable.utility_hint));
				imgView.setScaleType(ImageView.ScaleType.FIT_XY);
				LayoutParams params = new LayoutParams(size * 2, size * 2);
				params.setMargins(p.x - size, p.y - size, 0, 0);
				params.height = size * 2;
				params.width = size * 2;
				imgView.setLayoutParams(params);
				imgView.setVisibility(ImageView.VISIBLE);
				((ViewGroup) targetView).addView(imgView);
				gestureSet.hintViewList.add(imgView);
			}
		}
		gestureSet.isValid = true;
		gestureSetList.add(gestureSet);
		return gestureSetList.size() - 1;
	}
	
	protected static void changeGestureParams(boolean basedOnPrev, int setIndex, Point... points) {
		//Log.d("PlayPalUtility", String.format("SetIndex: %d", setIndex));
		for (Point p : points) 
			Log.d("PlayPalUtility", String.format("Point = (%d, %d)", p.x, p.y));
		
		int counter = 0;
		ArrayList<Point> targetPointList = gestureSetList.get(setIndex).pointList;
		if(basedOnPrev) {
			for (Point p : points) {
		        targetPointList.set(counter, new Point(targetPointList.get(counter).x + p.x, targetPointList.get(counter).y + p.y));
		        
		        if(isDebugMode) {
			        ImageView imgView = gestureSetList.get(setIndex).hintViewList.get(counter);
			        LayoutParams params = new LayoutParams(gestureSetList.get(setIndex).boxSize * 2, gestureSetList.get(setIndex).boxSize * 2);
			        params.setMargins(targetPointList.get(counter).x + p.x - 7 * gestureSetList.get(setIndex).boxSize / 2, targetPointList.get(counter).y + p.y - gestureSetList.get(setIndex).boxSize, 0, 0);
			        imgView.setLayoutParams(params);
		        }
		        
		        Log.d("PlayPalUtility", String.format("Update to Point = (%d, %d)", targetPointList.get(counter).x, targetPointList.get(counter).y));
		        counter++;
			}
		}
		else {
			for (Point p : points) {
		        targetPointList.set(counter, new Point(p.x, p.y));
		        
		        if(isDebugMode) {
			        ImageView imgView = gestureSetList.get(setIndex).hintViewList.get(counter);
			        LayoutParams params = new LayoutParams(gestureSetList.get(setIndex).boxSize * 2, gestureSetList.get(setIndex).boxSize * 2);
			        params.setMargins(p.x - gestureSetList.get(setIndex).boxSize, p.y - gestureSetList.get(setIndex).boxSize, 0, 0);
			        imgView.setLayoutParams(params);
		        }
		        
		        counter++;
			}
		}
	}
	
	protected static void updatePoint(boolean basedOnPrev, int setIndex, int pointIndex, Point p) {
		ArrayList<Point> targetPointList = gestureSetList.get(setIndex).pointList;
		if(basedOnPrev)
			targetPointList.set(pointIndex, new Point(targetPointList.get(pointIndex).x + p.x, targetPointList.get(pointIndex).y + p.y));
		else
			targetPointList.set(pointIndex, new Point(p.x, p.y));
	}
	
	protected static int getLastTriggerSetIndex() {
		int value = lastTriggerSetIndex;
		lastTriggerSetIndex = -1;
		return value;
	}
	
	protected static int getLastTriggerPointIndex() {
		int value = lastTriggerPointIndex;
		lastTriggerPointIndex = -1;
		return value;
	}
	
	
	protected static Point getPoint(int setIndex, int index) {
		ArrayList<Point> targetPointList = gestureSetList.get(setIndex).pointList;
		return targetPointList.get(index);
	}
	
	protected static boolean isWithinBox(int setIndex, int boxIndex, Point targetPoint) {
		ArrayList<Point> targetPointList = gestureSetList.get(setIndex).pointList;
		if(boxIndex >= targetPointList.size())
			return false;
		int boxSize = gestureSetList.get(setIndex).boxSize;
		if(Math.abs(targetPoint.x - targetPointList.get(boxIndex).x) < boxSize && Math.abs(targetPoint.y - targetPointList.get(boxIndex).y) < boxSize)
			return true;
		return false;
	}
	
	protected static void setLineGesture(boolean value) {
		isLineGestureOn = value;
	}
	
	protected static MediaPlayer playSoundEffect(int soundID, Context context, boolean isLoop) {
		MediaPlayer mp = MediaPlayer.create(context, soundRes[soundID]);
		if(isLoop)
			mp.setLooping(true);
		mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });   
        mp.start();
        return mp;
	}
	
	protected static void playSoundEffect(int soundID, Context context) {
		playSoundEffect(soundID, context, false);
	}
	
	protected static void clearAllVoice() {
		for(int i=0; i<voiceList.size(); i++) {
        	voiceList.get(i).stop();
        	voiceList.get(i).release();
        }
        voiceList.clear();
	}
	
	protected static void playTeachVoice(final Context context, int... voiceIndexs) {
		int id = voiceIndexs[0];
		  
		int gameIndex = id / 100 - 1;
		int stageIndex = id % 100 - 1;
		
		final int[] remainIndexs = Arrays.copyOfRange(voiceIndexs, 1, voiceIndexs.length);
		
		MediaPlayer mp = MediaPlayer.create(context, voiceRes[gameIndex][stageIndex]);
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                voiceList.remove(mp);
                if(remainIndexs.length > 0)
                	playTeachVoice(context, remainIndexs);
            }
        });
        clearAllVoice();
        voiceList.add(mp);
        mp.start();
	}
	
	protected static void registerProgressBar(ProgressBar barView, ImageView markView, ImageView progressBackView) {
		registerProgressBar(barView, markView, progressBackView, null);
	}
	
	protected static void registerProgressBar(ProgressBar barView, ImageView markView, ImageView progressBackView, final Callable<Integer> func) {
		progressBar = barView;
		progressMark = markView;
		progressBack = progressBackView;
		failFunc = func;
	}
	
	protected static void registerProgressBar(ProgressBar barView, ImageView markView, ImageView progressBackView, final Callable<Integer> func, final Callable<Integer> func2) {
		progressBar = barView;
		progressMark = markView;
		progressBack = progressBackView;
		failFunc = func;
		reminderFunc = func2;
	}
	
	protected static void initialProgressBar(int total, int mode) {
		totalProgress = total;
		progressBar.setMax(total);
		progressBack.setImageResource(R.drawable.progress);
		barMode = mode;
		if(barMode == PROGRESS_MODE) {
			curProgress = 0;
			progressBar.setProgress(0);
			
		}
		else if(barMode == TIME_MODE){
			curProgress = total;
			progressBar.setProgress(total);
			if(timer != null)
				timer.cancel();
			timer = new Timer(true);
			timerTask = new TimeBarTask();
			timer.schedule(timerTask, 0, 33);
		}
		updateProgressBar();
	}
	
	public static Handler timeBarHandler = new Handler() {
        public void handleMessage(Message msg) {
        	doProgress();
        }
    };
    
	protected static boolean doProgress() {
		boolean isReachGoal = false;
		if(barMode == PROGRESS_MODE) {
			curProgress++;
			if(curProgress >= totalProgress) {
				isReachGoal = true;
				curProgress = totalProgress;
			}
			updateProgressBar();
		}
		else if(barMode == TIME_MODE) {
			curProgress--;
			if(curProgress <= 0) {
				isReachGoal = true;
				curProgress = 0;
			}
			updateProgressBar();
			
			if(curProgress <= totalProgress/2 && curProgress % 10 == 0) {
				try {
					reminderFunc.call();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			if(isReachGoal) {
				progressBack.setImageResource(R.drawable.progress_finish);
				try {
					playSoundEffect(SOUND_TIMER_DING, targetContext, false);
					failFunc.call();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		}
		
		return false;
	}
	
	protected static void pauseProgress() {
		if(timerTask != null)
			timerTask.pause();
	}
	
	protected static void resumeProgress() {
		if(timerTask != null)
			timerTask.resume();
	}
	
	protected static int killTimeBar() {
		if(timerTask != null)
			timerTask.cancel();
		return curProgress;
	}
	
	protected static void updateProgressBar() {
		progressBar.setProgress(curProgress);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		float leftMargin = (float) curProgress / totalProgress * (endProgressMarkX - beginProgressMarkX) + beginProgressMarkX;
    	params.setMargins((int) leftMargin, progressMarkY, 0, 0);
    	progressMark.setLayoutParams(params);
    	if(curProgress == 0)
    		progressMark.setVisibility(ImageView.INVISIBLE);
    	else
    		progressMark.setVisibility(ImageView.VISIBLE);
	}
	
	protected static int getProgressBarMaxVal() {
		return totalProgress;
	}
	
	protected static int getProgressBarCurVal() {
		return curProgress;
	}
	
	protected static void setProgressBarCurVal(int newVal) {
		curProgress = newVal;
		updateProgressBar();
	}
	
	protected static void setDebugMode(boolean value) {
		isDebugMode = value;
	}
	
	
	
	/*DrawView utility*/
	protected static void initDrawView(RelativeLayout layout, Context context){

		drawview = new DrawView(context);
		drawview.setMinimumHeight(2160);
		drawview.setMinimumWidth(1600);
		
		layout.addView(drawview);
		drawview.bringToFront();
		drawview.invalidate();
	}
	
	protected static void setStraightStroke(Point... points){
		setStraightStroke(strokeSetList.size(), points);
	}
	
	protected static void setStraightStroke(int setIndex, Point... points){
		//initial strokeset
		StrokeSet curSet = new StrokeSet();
		curSet.isStraight = true;
		for(Point p:points){
			curSet.pointList.add (p);			
		}
		strokeSetList.add(curSet);
		drawview.invalidate();
	}
	
	protected static void setCircleStroke(Point o, int r){
		StrokeSet curSet = new StrokeSet();
		curSet.isStraight = false;
		curSet.pointList.add (o);
		
		strokeSetList.add(curSet);
		drawview.invalidate();
	}
	
	protected static void eraseStroke(int setIndex){
		StrokeSet curSet = strokeSetList.get(setIndex); 
		curSet.isValid = false;
		
		drawview.invalidate();
	}
	
	protected static void clearDrawView(){
		if(drawview != null){
			drawview.reset();
			drawview.invalidate();
		}
	}
	
}

class GestureSet {
	protected ArrayList<Point> pointList;
	protected ArrayList<Integer> passedList;
	protected ArrayList<ImageView> hintViewList;
	protected int boxSize;
	protected boolean isContinuous;
	protected boolean isInOrder;
	protected boolean isValid;
};


class StrokeSet{
	protected int radius;
	protected ArrayList<Point> pointList;
	protected boolean isValid;
	protected boolean isStraight;
	public StrokeSet(){
		pointList = new ArrayList<Point>();
		isValid = true;
		isStraight = true;
		radius = 175 ;
	}
}

class DrawView extends View{
	protected Paint paint;
	protected Point orig;
	protected Point centralPoint = new Point(1280,800);
	protected int radius;
	
	Canvas canvas;
	
	public DrawView(Context context) {
		super(context);
		canvas= new Canvas();
		paint = new Paint();
        initPenEffect(paint);
        radius = 175;
	}
	
	private void initPenEffect(Paint paint){
        paint.setAntiAlias(true);    
        paint.setStyle(Style.STROKE);  
        paint.setStrokeWidth(10);        
        paint.setColor(Color.BLACK);//DARK GRAY
        
        PathEffect effects = new DashPathEffect(new float[]{15,15,15,15},1);  
        paint.setPathEffect(effects); 
	}
	
	public void reset(){
		orig = new Point(0,0);
		PlayPalUtility.strokeSetList = new ArrayList<StrokeSet>();			
	}
	
	@Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);
        for(int setIndex=0; setIndex<PlayPalUtility.strokeSetList.size(); setIndex++)
        {
        	StrokeSet curSet = PlayPalUtility.strokeSetList.get(setIndex);
        	if(!curSet.isValid)
        		continue;
        	
        	if(curSet.isStraight){
	        	for(int i=0; i<curSet.pointList.size()-1; i++){
	        		Path path = new Path();
	        		path.moveTo(curSet.pointList.get(i).x,   curSet.pointList.get(i).y );
	        		path.lineTo(curSet.pointList.get(i+1).x, curSet.pointList.get(i+1).y );
	        		canvas.drawPath(path, paint);
	        	}
        	}
        	else{
        		canvas.drawCircle(curSet.pointList.get(0).x, curSet.pointList.get(0).y, radius, paint);
        	}
        }
        this.bringToFront();
	}
};

class TimeBarTask extends TimerTask {
	private boolean isPause = false;
	
    public void run() {
    	if(!isPause) {
    		Message msg = new Message();
            PlayPalUtility.timeBarHandler.sendMessage(msg);
    	}
    }
    
    public void pause() {
    	isPause = true;
    }
    
    public void resume() {
    	isPause = false;
    }
};

class CircularTranslateAnimation extends Animation {

    private View view;
    private float cx, cy;           // center x,y position of circular path
    private float prevX, prevY;     // previous x,y position of image during animation
    private float r;                // radius of circle


    /**
     * @param view - View that will be animated
     * @param r - radius of circular path
     */
    public CircularTranslateAnimation(View view, float r){
        this.view = view;
        this.r = r;
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        // calculate position of image center
        int cxImage = width / 2;
        int cyImage = height / 2;
        cx = view.getLeft() + cxImage;
        cy = view.getTop() + cyImage;

        // set previous position to center
        prevX = cx;
        prevY = cy;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if(interpolatedTime == 0){
            // I ran into some issue where interpolated would be
            return;
        }

        float angleDeg = (interpolatedTime * 360f + 90) % 360;
        float angleRad = (float) Math.toRadians(angleDeg);

        // r = radius, cx and cy = center point, a = angle (radians)
        float x = (float) (cx + r * Math.cos(angleRad));
        float y = (float) (cy + r * Math.sin(angleRad));


        float dx = prevX - x;
        float dy = prevY - y;

        prevX = x;
        prevY = y;

        t.getMatrix().setTranslate(dx, dy);
    }
}

class WaitTimerTask extends TimerTask {
	private Activity activity;
	private Intent intent;
	
	public WaitTimerTask(Activity act, Intent inte) {
		activity = act;
		intent = inte;
	}
	
    public void run() {
    	activity.startActivityForResult(intent, 0);
		activity.finish();
    }
  };