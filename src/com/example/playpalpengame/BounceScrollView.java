package com.example.playpalpengame;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class BounceScrollView extends HorizontalScrollView {

	private static final int MAX_Y_OVERSCROLL_DISTANCE = 200;
    
    private Context mContext;
	private int mMaxYOverscrollDistance;
	
	public BounceScrollView(Context context) {
		super(context);
		mContext = context;
		//this.setOverScrollMode(OVER_SCROLL_ALWAYS);
		initBounceListView();
	}
	
	public BounceScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		//this.setOverScrollMode(OVER_SCROLL_ALWAYS);
		initBounceListView();
	}
	
	public BounceScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		//this.setOverScrollMode(OVER_SCROLL_ALWAYS);
		initBounceListView();
	}
	
	private void initBounceListView() {
		final DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        final float density = metrics.density;
        
		mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) { 
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, mMaxYOverscrollDistance, isTouchEvent);  
	}
}
