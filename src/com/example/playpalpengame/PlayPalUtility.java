package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class PlayPalUtility {
	protected final static int FROM_OUTLEFT_TO_CUR = 1;
	protected final static int FROM_CUR_TO_OUTRIGHT = 2;
	protected final static int FROM_OUTRIGHT_TO_CUR = 3;
	protected final static int FROM_CUR_TO_OUTLEFT = 4;
	
	protected static boolean isLineGestureOn;
	protected static int lastTriggerSetIndex = -1;
	
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
		gestureSetList.clear();
	}
	
	protected static void cancelGestureSet(int setIndex) {
		gestureSetList.get(setIndex).isValid = false;
	}
	
	protected static void registerLineGesture(View view, final Callable<Integer> func) {
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
							if(isWithinBox(setIndex, 0, new Point((int)event.getX(), (int)event.getY()))) {
								isPointPassedList.set(0, Boolean.valueOf(true));
							}
							else
								isPointPassedList.set(0, Boolean.valueOf(false));
							break;
						case MotionEvent.ACTION_MOVE:
							if(isPointPassedList.get(0) == Boolean.FALSE)
								break;
							for(int i=1; i<isPointPassedList.size(); i++) {
								if(isPointPassedList.get(i) == Boolean.TRUE)
									continue;
								if(isWithinBox(setIndex, i, new Point((int)event.getX(), (int)event.getY()))) 
									isPointPassedList.set(i, Boolean.valueOf(true));
								break;
							}
							break;
						case MotionEvent.ACTION_UP:
							if(isPointPassedList.get(0) == Boolean.FALSE)
								break;
							for(int i=1; i<isPointPassedList.size(); i++)
								if(isPointPassedList.get(i) == Boolean.FALSE)
									isPointPassedList.set(0, Boolean.valueOf(false));
							if(isPointPassedList.get(0) == Boolean.FALSE) // Check again
								break;
							
							try {
								func.call();
								for(int i=0; i<isPointPassedList.size(); i++)
									isPointPassedList.set(i, Boolean.valueOf(false));
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
		GestureSet gestureSet = new GestureSet();
		gestureSet.boxSize = size;
		gestureSet.pointList = new ArrayList<Point>();
		gestureSet.passedList = new ArrayList<Boolean>(); 
		for (Point p : points) {
			gestureSet.pointList.add(p);
			gestureSet.passedList.add(Boolean.valueOf(false));
		}
		gestureSet.isValid = true;
		gestureSetList.add(gestureSet);
		return gestureSetList.size() - 1;
	}
	
	protected static void changeGestureParams(boolean basedOnPrev, int setIndex, Point... points) {
		int counter = 0;
		ArrayList<Point> targetPointList = gestureSetList.get(setIndex).pointList;
		if(basedOnPrev) {
			for (Point p : points)
		        targetPointList.set(counter, new Point(targetPointList.get(counter).x + p.x, targetPointList.get(counter).y + p.y));
		}
		else {
			for (Point p : points)
		        targetPointList.set(counter, new Point(p.x, p.y));
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
}

class GestureSet {
	protected ArrayList<Point> pointList;
	protected ArrayList<Boolean> passedList;
	protected int boxSize;
	protected boolean isValid;
};