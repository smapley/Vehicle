package com.smapley.base.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by wuzhixiong on 2017/5/14.
 */

public class LinearlayoutView extends LinearLayout {

    public static final int KEYBOARD_HIDE = 0;
    public static final int KEYBOARD_SHOW = 1;
    private static final  int SOFTKEYPAD_MIN_HEIGHT = 50;
    private KeyBoardStateListener keyBoardStateListener;
    private Handler uiHandler = new Handler();

    public LinearlayoutView(Context context) {
        super(context);
    }

    public LinearlayoutView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if(oldh - h>SOFTKEYPAD_MIN_HEIGHT){
                    keyBoardStateListener.stateChange(KEYBOARD_SHOW);
                }else{
                    keyBoardStateListener.stateChange(KEYBOARD_HIDE);
                }
            }
        });
    }

    public void setKeyBoardStateListener(KeyBoardStateListener keyBoardStateListener){
        this.keyBoardStateListener = keyBoardStateListener;
    }

    public  interface  KeyBoardStateListener{
        void stateChange(int state);
    }
}
