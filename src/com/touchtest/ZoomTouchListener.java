package com.touchtest;

import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

public class ZoomTouchListener implements View.OnTouchListener {
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

    public ZoomTouchListener(TextView textView) {
        text = textView;
        textSize = text.getTextSize();
        maxZoom = 150;
        minZoom = 15;
        touchSlop = ViewConfiguration.getTouchSlop();
    }
    public boolean onTouch(View view, MotionEvent ev) {
        boolean result = true;
        MotionEventWrapper event = MotionEventWrapper.wrap(ev);

        //ACTION_POINTER_DOWN doesn't seem to get called reliably within
        //the scrollview so we have to catch the start of a zoom gesture manually
        if(event.getPointerCount() > 1 && mode != ZOOM)
            doPointerDown(event);


        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                result = view.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                view.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_CANCEL:
                mode = NONE;
                view.onTouchEvent(ev);
                break;
            //Start Zoom
            case MotionEvent.ACTION_POINTER_DOWN:
                doPointerDown(event);
                break;
            //End Zoom
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                textSize = text.getTextSize();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {
                    float newDist = spacing(event);

                    float scale = newDist / oldDist;
                    float newSize = textSize * scale;

                    if (newSize > minZoom && newSize < maxZoom) {
                        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
                    }

                } else {
                    mode = SCROLL;
                    try {
                        result = view.onTouchEvent(ev);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        //This gets thrown sometimes by the scrollview, just ignore it
                        //Log.e(TAG, "Error in scrollview.OnTouch", ex);
                    }
                }
                break;
//            default:
//                result = super.onTouchEvent(ev);
        }
        return result; // indicates whether event was handled
    }
    private void doPointerDown(MotionEventWrapper event){
        oldDist = spacing(event);
        if (oldDist > touchSlop) {
            mode = ZOOM;
        }
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
