package com.touchtest;

import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

public class ZoomListener implements View.OnTouchListener {
    private static final String TAG = "Touch";
    private float oldDist;
    private float maxZoom;
    private float minZoom;
    //Zoom state
    static final int NONE = 0;
    static final int ZOOM = 1;
    int mode = NONE;
    private float textSize;
    private TextView text;
    private int touchSlop;

    public ZoomListener(TextView textView) {
        text = textView;
        textSize = text.getTextSize();
        maxZoom = 150;
        minZoom = 15;
        touchSlop = ViewConfiguration.getTouchSlop();
    }

    public boolean onTouch(View v, MotionEvent event) {
        MotionEventWrapper rawEvent = MotionEventWrapper.wrap(event);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG, "ACTION_DOWN");
//
//                break;
            case MotionEvent.ACTION_CANCEL:
//                Log.d(TAG, "ACTION_CANCEL");
//                Log.i(TAG, "mode=NONE");
                mode = NONE;
                return false;
                //break;
            //start
            case MotionEvent.ACTION_POINTER_DOWN:
                //Log.d(TAG, "ACTION_POINTER_DOWN");
                oldDist = spacing(rawEvent);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > touchSlop) {
                    mode = ZOOM;
                    Log.i(TAG, "mode=ZOOM");
                }
                break;
            //end
            case MotionEvent.ACTION_POINTER_UP:
                Log.d(TAG, "ACTION_POINTER_UP");
                mode = NONE;

                textSize = text.getTextSize();

//                Log.i(TAG, "mode=NONE");
//                Log.d(TAG, "TextSize=" + textSize);
                break;
            //move
            case MotionEvent.ACTION_MOVE:
                //Log.d(TAG, "ACTION_POINTER_MOVE");
                if (mode == ZOOM) {
                    float newDist = spacing(rawEvent);
                    //event.getHistoricalPointerCoords();

                    if (newDist > touchSlop) {
                        float scale = newDist / oldDist;
                        float newSize = textSize * scale;
                        //Log.d(TAG, "newSize=" + newSize);
                        if (true || newSize > minZoom && newSize < maxZoom) {
                            //Log.d(TAG, "oldDist=" + oldDist + ", newDist=" + newDist + ", scale=" + scale + ", textSize=" + newSize);
                            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
                        }
                    }
                } else{
                    Log.d(TAG, "Move while not in zoom mode give up focus");
                    return false;
                }
                break;
            default:
                Log.d(TAG, "couldn't handle event in mode " + mode + event.toString() );
                return mode == NONE;
        }
        return true; // indicate event was handled
    }

    private float spacing(MotionEventWrapper event) {
        try {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } catch (ArrayIndexOutOfBoundsException ex) {
            //finger moved off the edge of the screen
            return -1;
        }

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
