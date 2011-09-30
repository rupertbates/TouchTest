package com.touchtest;

import android.content.Context;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;
import android.widget.TextView;

public class CustomScrollView extends ScrollView {
    private int mode;
    private static final int NONE = 0;
    private static final int ZOOM = 1;
    private static final String TAG = "Tag.ScrollView";
    private static final int SCROLL = 2;
    private TextView text;
    private float textSize;
    private int maxZoom;
    private int minZoom;
    private int touchSlop;
    private float oldDist;
    private URLSpan[] urls;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTextView(TextView textView) {
        text = textView;
        textSize = text.getTextSize();
        maxZoom = 150;
        minZoom = 15;
        touchSlop = ViewConfiguration.getTouchSlop();
    }

    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        String modes[] = {"NONE", "ZOOM", "SCROLL"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append(" mode=").append(modes[mode]).append(" ");

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        Log.d(TAG, sb.toString());
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = true;
        MotionEventWrapper event = MotionEventWrapper.wrap(ev);
        dumpEvent(ev);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                result = super.onTouchEvent(ev);
            case MotionEvent.ACTION_UP:
                super.onTouchEvent(ev);
                if (mode != ZOOM && mode != SCROLL)
                {
                    updateEventCoordinates(ev);
                    text.onTouchEvent(ev);
                }
                mode = NONE;
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mode != ZOOM)
                    super.onTouchEvent(ev);
                break;
            //Start Zoom
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > touchSlop) {
                    mode = ZOOM;
                }
                break;
            //End Zoom
            case MotionEvent.ACTION_POINTER_UP:
                textSize = text.getTextSize();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    float newDist = spacing(event);
                    //float distanceChange = Math.abs(newDist - oldDist);
                    //Log.d(TAG, "distance moved = " + distanceChange);

                    float scale = newDist / oldDist;
                    float newSize = textSize * scale;

                    if (newSize > minZoom && newSize < maxZoom) {
                        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
                    }

                } else {
                    mode = SCROLL;
                    try {
                        result = super.onTouchEvent(ev);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        //This gets thrown sometimes by the scrollview, just ignore it
                        //Log.e(TAG, "Error in scrollview.OnTouch", ex);
                    }
                }
                break;
            default:
                result = super.onTouchEvent(ev);
        }
        return result; // indicates whether event was handled
    }

    /**
     * Change the y coordinates of this motion event to take account of  scrolling
     * @param ev
     */
    private void updateEventCoordinates(MotionEvent ev) {
        float offset= 0;
        for(int i = 0;i< getChildCount();i++){
            View v = getChildAt(i);
            if(v != text){
                offset += v.getHeight();
            }
            else
                break;

        }
        ev.setLocation(ev.getX(0), ev.getY(0) + this.getScrollY() - offset);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = (ev.getAction() & MotionEvent.ACTION_MASK);
        boolean result;
        if (action == MotionEvent.ACTION_POINTER_DOWN ||
                action == MotionEvent.ACTION_POINTER_UP ||
                action == MotionEvent.ACTION_DOWN ||
                action == MotionEvent.ACTION_UP) {
            log("intercepting event");
            result = true;
        } else
            result = super.onInterceptTouchEvent(ev);
        log("onIntercept Result = " + result);
        return result;
    }

    private void log(String message) {
        if (mode == NONE)
            Log.i(TAG, message + " mode=NONE");
        else if (mode == SCROLL)
            Log.i(TAG, message + " mode=SCROLL");
        else if (mode == ZOOM)
            Log.i(TAG, message + " mode=ZOOM");
        else
            Log.i(TAG, message + " mode=UNKNOWN");
    }


    private float spacing(MotionEventWrapper event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    public static class MotionEventWrapper {
        protected MotionEvent event;

        MotionEventWrapper(MotionEvent event) {
            this.event = event;
        }

        static public MotionEventWrapper wrap(MotionEvent event) {
            try {
                return new NewMotionEvent(event);
            } catch (VerifyError e) {
                return new MotionEventWrapper(event);
            }
        }


        public int getAction() {
            return event.getAction();
        }

        public float getX() {
            return event.getX();
        }

        public float getX(int pointerIndex) {
            verifyPointerIndex(pointerIndex);
            return getX();
        }

        public float getY() {
            return event.getY();
        }

        public float getY(int pointerIndex) {
            verifyPointerIndex(pointerIndex);
            return getY();
        }

        public int getPointerCount() {
            return 1;
        }

        public int getPointerId(int pointerIndex) {
            verifyPointerIndex(pointerIndex);
            return 0;
        }

        private void verifyPointerIndex(int pointerIndex) {
            if (pointerIndex > 0) {
                throw new IndexOutOfBoundsException("Index not supported on OS versions < 5");
            }
        }

        public static class NewMotionEvent extends MotionEventWrapper {

            protected NewMotionEvent(MotionEvent event) {
                super(event);
            }

            public float getX(int pointerIndex) {
                return event.getX(pointerIndex);
            }

            public float getY(int pointerIndex) {
                return event.getY(pointerIndex);
            }

            public int getPointerCount() {
                return event.getPointerCount();
            }

            public int getPointerId(int pointerIndex) {
                return event.getPointerId(pointerIndex);
            }
        }
    }

}
