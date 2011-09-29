package com.touchtest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.lang.annotation.Retention;

public class CustomScrollView extends ScrollView {
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private int mode;
    private static final int NONE = 0;
    private static final int ZOOM = 1;
    private static final String TAG = "Tag.ScrollView";
    private static final int SCROLL = 2;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gestureDetector = new GestureDetector(new YScrollDetector());
        setFadingEdgeLength(0);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    private void logMode(){
        if(mode==NONE)
            Log.i(TAG, "mode=NONE");
        else if(mode==SCROLL)
            Log.i(TAG, "mode=SCROLL");
        else if(mode==ZOOM)
            Log.i(TAG, "mode=ZOOM");
        else
            Log.i(TAG, "mode=UNKNOWN");
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "ACTION_DOWN");
                logMode();
                if(mode == SCROLL){
                    mode = NONE;
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "ACTION_CANCEL");
                if(mode == SCROLL){
                    logMode();
                    mode = NONE;
                    return true;
                }
                break;
            //start
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "ACTION_POINTER_DOWN");
                logMode();
                mode = ZOOM;

                break;
            //end
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "ACTION_POINTER_UP");
                logMode();
                mode = NONE;

                break;
            case MotionEvent.ACTION_MOVE:
                //Log.d(TAG, "ACTION_MOVE");
                if(mode == ZOOM)

                    Log.v(TAG, "mode=ZOOM");
                else
                    mode = SCROLL;
                    //Log.i(TAG, "mode=SCROLL");
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "ACTION_UP");
                logMode();
                mode = NONE;

                break;

        }
        if (mode == ZOOM) return false;
        //return false;
        //Call super first because it does some hidden motion event handling
        boolean result = super.onInterceptTouchEvent(ev);
        //Now see if we are scrolling vertically with the custom gesture detector
        if (gestureDetector.onTouchEvent(ev)) {
            Log.d("Touch", "Intercepting event in scrollview");
            return result;
        }
        //If not scrolling vertically (more y than x), don't hijack the event.
        else {
            return false;
        }
    }

    // Return false if we're scrolling in the x direction
    class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            try {
                if (Math.abs(distanceY) > Math.abs(distanceX)) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    }
}
