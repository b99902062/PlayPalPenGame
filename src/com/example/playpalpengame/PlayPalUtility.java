package com.example.playpalpengame;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import com.samsung.spen.lib.input.SPenEventLibrary;
import com.samsung.spensdk.applistener.SPenHoverListener;

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
import android.graphics.RectF;
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
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PlayPalUtility {
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
	protected static View targetView;
	protected static Context targetContext;
	protected static boolean isDebugMode = true;
	protected static int barMode;
	
	protected static DrawView drawview;
	protected static Timer timer;
	protected static TimeBarTask timerTask;

	private static int totalProgress = 0;
	private static int curProgress = 0;
	private static ProgressBar progressBar;
	private static ImageView progressMark;
	private static ImageView progressBack;
	
	private static boolean isPressing;
	
	protected static ArrayList<GestureSet> gestureSetList = new ArrayList<GestureSet>();
	protected static ArrayList<StrokeSet> strokeSetList = new ArrayList<StrokeSet>();
	
	protected static boolean butterSqueezing = false;
	protected static SPenEventLibrary mSPenEventLibrary = new SPenEventLibrary();
	
	protected static float calcDistance(Point p1, Point p2){
		float dx = p1.x - p2.x;
		float dy = p1.y - p2.y;
		
		return FloatMath.sqrt(dx*dx + dy*dy);
	}
	
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
	
	protected static void setAlphaAnimation(View view, boolean isIn) {
		setAlphaAnimation(view, isIn, null);
	}
	
	protected static void setAlphaAnimation(View view, boolean isIn, final Callable<Integer> func) {
		Animation fade;
		if(isIn) 
			fade = new AlphaAnimation(0, 1);
		else
			fade = new AlphaAnimation(1, 0);
		fade.setInterpolator(new DecelerateInterpolator());
		fade.setDuration(2000);
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
	
	
	protected static void registerSingleHoverPoint(View view, Context context, final Callable<Integer> func) {
		targetView = view;
		targetContext = context;
		
		mSPenEventLibrary.setSPenHoverListener(targetView, new SPenHoverListener(){
			Point startPoint;
			ImageView curButterView;
			
			
			@Override
			public void onHoverButtonUp(View v, MotionEvent event) {
				for(int setIndex=0; setIndex<gestureSetList.size(); setIndex++) {
					GestureSet curSet = gestureSetList.get(setIndex);
					ArrayList<Integer> pointPassedList = curSet.passedList;

					//testing the first point 
					if(isWithinBox(setIndex, 0, new Point((int)event.getX(), (int)event.getY()))){
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
			public boolean onHover(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onHoverButtonDown(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				
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
								if(isWithinBox(setIndex, lastIndex+1, new Point((int)event.getX(), (int)event.getY()))) {
									pointPassedList.add(lastIndex+1);
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
									func.call();
								} catch(Exception ex) {
									ex.printStackTrace();
								}
							}
							pointPassedList.clear();
							break;
					}
				}
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
	
	protected static void registerProgressBar(ProgressBar barView, ImageView markView, ImageView progressBackView) {
		progressBar = barView;
		progressMark = markView;
		progressBack = progressBackView;
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
		}
		if(isReachGoal) {
			progressBack.setImageResource(R.drawable.progress_finish);
			return true;
		}
		return false;
	}
	
	protected static void pauseProgress() {
		timerTask.pause();
	}
	
	protected static void resumeProgress() {
		timerTask.resume();
	}
	
	protected static void killTimeBar() {
		timerTask.cancel();
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
	
	protected static void setDebugMode(boolean value) {
		isDebugMode = value;
	}
	
	/*DrawView utility*/
	protected static void initDrawView(RelativeLayout layout, Context context){
		initDrawView(layout, context, new Point(0,0), 0);
	}
	
	
	protected static void initDrawView(RelativeLayout layout, Context context, Point orig, int r){
		drawview = new DrawView(context, orig, r);
		drawview.setMinimumHeight(2160);
		drawview.setMinimumWidth(1600);
		
		layout.addView(drawview);
	}
	
	protected static void initialStroke(){
		StrokeSet curSet = new StrokeSet();
		strokeSetList.add(curSet);
		curSet.isValid = true;
	}
	
	protected static void setStraightStroke(Point... points){
		initialStroke(); 
		int idx = strokeSetList.size()-1; 
		setStraightStroke(idx, points);
	}
	
	protected static void setStraightStroke(int setIndex, Point... points){
		initialStroke();
		
		for(Point p:points){
			strokeSetList.get(setIndex).pointList.add (p);			
		}
		drawview.invalidate();
	}
	
	protected static void setCircleStroke(Point o, int r){
		drawview.isStraight = false;
		drawview.orig = o;
		drawview.radius = r;
		
		drawview.invalidate();
	}
	
	protected static void clearDrawView(){
		drawview.reset();
		drawview.invalidate();
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
	protected ArrayList<Point> pointList;
	protected boolean isValid;
	
	public StrokeSet(){
		pointList = new ArrayList<Point>();
		isValid = true;
	}
}

class DrawView extends View{
	protected Paint paint;
	protected Point orig;
	protected Point centralPoint = new Point(1280,800);
	protected int radius;
	protected boolean isStraight;
	
	Canvas canvas;
	
	public DrawView(Context context, Point orig, int r) {
		super(context);
		canvas= new Canvas();
		radius = r;
        
		paint = new Paint();
        initPenEffect(paint);
	}
	
	public void setStraight(boolean straight){
		isStraight = straight;
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
		radius = 0;
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
        	
        	for(int i=0; i<curSet.pointList.size()-1; i++){
        		Path path = new Path();
        		path.moveTo(curSet.pointList.get(i).x,   curSet.pointList.get(i).y );
        		path.lineTo(curSet.pointList.get(i+1).x, curSet.pointList.get(i+1).y );
        		canvas.drawPath(path, paint);
        	}
        }	
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
