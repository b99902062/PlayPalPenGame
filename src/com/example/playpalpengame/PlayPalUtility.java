package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class PlayPalUtility {
	protected final static int FROM_OUTLEFT_TO_CUR = 1;
	protected final static int FROM_CUR_TO_OUTRIGHT = 2;
	protected final static int FROM_OUTRIGHT_TO_CUR = 3;
	protected final static int FROM_CUR_TO_OUTLEFT = 4;
	
	protected static boolean isLineGestureOn;
	protected static int lastTriggerSetIndex = -1;
	protected static View targetView;
	protected static Context targetContext;
	protected static boolean isDebugMode = true;
	
	protected static ArrayList<GestureSet> gestureSetList = new ArrayList<GestureSet>();
	
	protected static TranslateAnimation CreateTranslateAnimation(int translateType) {
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
		newAnim.setDuration(2000);
		newAnim.setRepeatCount(0);
		return newAnim;
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
	
	protected static void registerLineGesture(View view, Context context, final Callable<Integer> func) {
		targetView = view;
		targetContext = context;
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(!isLineGestureOn)
					return false;
				for(int setIndex=0; setIndex<gestureSetList.size(); setIndex++) {
					if(!gestureSetList.get(setIndex).isValid)
						continue;
					ArrayList<Boolean> isPointPassedList = gestureSetList.get(setIndex).passedList;
					
					switch(event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							//Log.d("PlayPal", String.format("DOWN: (%d, %d)", (int)event.getX(), (int)event.getY()));
							if(isWithinBox(setIndex, 0, new Point((int)event.getX(), (int)event.getY()))) {
								Log.d("PlayPalTest", String.format("Set: %d, Start 0", setIndex));
								isPointPassedList.set(0, Boolean.valueOf(true));
							}
							else
								isPointPassedList.set(0, Boolean.valueOf(false));
							break;
						case MotionEvent.ACTION_MOVE:
							//Log.d("PlayPal", String.format("MOVE: (%d, %d)", (int)event.getX(), (int)event.getY()));
							if(isPointPassedList.get(0) == Boolean.FALSE)
								break;
							for(int i=1; i<isPointPassedList.size(); i++) {
								if(isPointPassedList.get(i) == Boolean.TRUE)
									continue;
								if(isWithinBox(setIndex, i, new Point((int)event.getX(), (int)event.getY()))) {
									Log.d("PlayPalTest", String.format("Set: %d, Start: %d", setIndex, i));
									isPointPassedList.set(i, Boolean.valueOf(true));
								}
								break;
							}
							break;
						case MotionEvent.ACTION_UP:
							//Log.d("PlayPal", String.format("UP: (%d, %d)", (int)event.getX(), (int)event.getY()));
							if(isPointPassedList.get(0) == Boolean.FALSE)
								break;
							Log.d("PlayPalTest", "Before check whole");
							for(int i=1; i<isPointPassedList.size(); i++)
								if(isPointPassedList.get(i) == Boolean.FALSE)
									isPointPassedList.set(0, Boolean.valueOf(false));
							if(isPointPassedList.get(0) == Boolean.FALSE) // Check again
								break;
							
							lastTriggerSetIndex = setIndex;
							Log.d("PlayPalTest", "Pass check whole");
							
							try {
								SETIDX = lastTriggerSetIndex;
								func.call();
								for(int i=0; i<gestureSetList.size(); i++) {
									isPointPassedList = gestureSetList.get(i).passedList;
									for(int j=0; j<isPointPassedList.size(); j++)
										isPointPassedList.set(j, Boolean.valueOf(false));
								}
								
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
					}
				}
				return true;
			}
		});
	}
	
	protected static int initialLineGestureParams(int size, Point... points) {
		for (Point p : points) 
			Log.d("PlayPalUtility", String.format("Point = (%d, %d)", p.x, p.y));
		
		GestureSet gestureSet = new GestureSet();
		gestureSet.boxSize = size;
		gestureSet.pointList = new ArrayList<Point>();
		gestureSet.passedList = new ArrayList<Boolean>(); 
		gestureSet.hintViewList = new ArrayList<ImageView>(); 
		for (Point p : points) {
			gestureSet.pointList.add(p);
			gestureSet.passedList.add(Boolean.valueOf(false));
			
			if(isDebugMode) {
				ImageView imgView = new ImageView(targetContext);
				imgView.setAlpha(0.5f);
				imgView.setImageDrawable(targetContext.getResources().getDrawable( R.drawable.game1_fire_2));
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
		Log.d("PlayPalUtility", String.format("SetIndex: %d", setIndex));
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
			        params.setMargins(targetPointList.get(counter).x + p.x + gestureSetList.get(setIndex).boxSize * 2, targetPointList.get(counter).y + p.y - gestureSetList.get(setIndex).boxSize, 0, 0);
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
	
	protected static Point getPoint(int setIndex, int index) {
		ArrayList<Point> targetPointList = gestureSetList.get(setIndex).pointList;
		return targetPointList.get(index);
	}
	
	protected static boolean isWithinBox(int setIndex, int boxIndex, Point targetPoint) {
		ArrayList<Point> targetPointList = gestureSetList.get(setIndex).pointList;
		int boxSize = gestureSetList.get(setIndex).boxSize;
		if(Math.abs(targetPoint.x - targetPointList.get(boxIndex).x) < boxSize && Math.abs(targetPoint.y - targetPointList.get(boxIndex).y) < boxSize)
			return true;
		return false;
	}
	
	protected static void setLineGesture(boolean value) {
		isLineGestureOn = value;
	}
	
	protected static void setDebugMode(boolean value) {
		isDebugMode = value;
	}
}

class GestureSet {
	protected ArrayList<Point> pointList;
	protected ArrayList<Boolean> passedList;
	protected ArrayList<ImageView> hintViewList;
	protected int boxSize;
	protected boolean isValid;
};