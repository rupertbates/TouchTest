package com.touchtest;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MyActivity extends Activity {
    private static final String TAG = "Touch";


    private LinearLayout frame;
    private TextView text;

    private ScrollView scroll;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //view = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);

        //view.setOnTouchListener(this);
        scroll = (ScrollView) findViewById(R.id.scroll);
//        scroll.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                return true;
//            }
//        });

        frame = (LinearLayout) findViewById(R.id.frame);
        frame.setOnTouchListener(new ZoomListener(text));


    }

}