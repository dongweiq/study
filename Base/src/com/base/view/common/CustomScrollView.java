package com.base.view.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {
	 private GestureDetector mGestureDetector;
	    View.OnTouchListener mGestureListener;

	    public CustomScrollView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	        mGestureDetector = new GestureDetector(new YScrollDetector());
	        setFadingEdgeLength(0);
	    }

	    @Override
	    public boolean onInterceptTouchEvent(MotionEvent ev) {
	        return super.onInterceptTouchEvent(ev) && mGestureDetector.onTouchEvent(ev);
	    }

	    // Return false if we're scrolling in the x direction  
	    class YScrollDetector extends SimpleOnGestureListener {
	        @Override
	        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	            if(Math.abs(distanceY) > Math.abs(distanceX)) {
	                return true;
	            }
	            return false;
	        }
	    }
}
