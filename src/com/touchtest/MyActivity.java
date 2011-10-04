package com.touchtest;

import android.app.Activity;
import android.os.Bundle;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class MyActivity extends Activity {
    private static final String TAG = "Touch";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        TextView textView = (TextView) findViewById(R.id.text);
        String text = getString(R.string.text);
        textView.setText(Html.fromHtml(text));
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView textView2 = (TextView) findViewById(R.id.text2);
        textView2.setText(Html.fromHtml(text));
        textView2.setMovementMethod(LinkMovementMethod.getInstance());

//        textView.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                //view.onTouchEvent(motionEvent);
//                return false;
//            }
//        });

        ScrollView scroll = (ScrollView) findViewById(R.id.scroll);
        scroll.setOnTouchListener(new ZoomTouchListener(textView));
        //scroll.setTextView(textView);
    }

}