package com.smapley.base.utils;

import android.os.Handler;
import android.os.Message;

/**
 * Created by wuzhixiong on 2017/4/28.
 */

public class ThreadSleep {


    private final int SLEEP = 1;
    private Callback mcallback;
    private int LOOPS = 1;
    private boolean isLoop = false;


    public ThreadSleep() {

    }

    public void sleep(final int loops, final long time, Callback callback) {
        mcallback = callback;
        this.LOOPS = loops;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mhandler.obtainMessage(SLEEP).sendToTarget();
                    LOOPS--;
                    if (LOOPS == 0 && !isLoop)
                        return;
                }
            }
        }).start();
    }

    public void sleep(boolean isLoop, final long time, Callback callback) {
        this.isLoop = isLoop;
        sleep(1, time, callback);
    }

    public void stop() {
        this.isLoop = false;
    }

    public void sleep(final long time, Callback callback) {
        sleep(1, time, callback);
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SLEEP:
                    mcallback.onCallback(LOOPS);
                    break;
            }
        }
    };

    public interface Callback {
        void onCallback(int number);
    }
}
