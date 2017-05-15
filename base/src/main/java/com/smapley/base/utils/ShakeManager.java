package com.smapley.base.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;

import com.smapley.base.R;

import java.lang.ref.WeakReference;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by wuzhixiong on 2017/5/1.
 * <p>
 * 监听手机摇一摇动作
 */

public class ShakeManager implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private SoundPool mSoundPool;
    private boolean isShake = false;
    private Vibrator mVibrator;

    private static final int START_SHAKE = 1;
    private static final int END_SHAKE = 2;
    private static final int AGAIN_SHAKE = 3;

    private MyHandler mHandler;
    private int mWeiChatAudio;

    private Context context;

    private static ShakeCallback shakeCallback;

    public ShakeManager(Context context) {
        this.context = context;

        mHandler = new MyHandler(this);

        //获取 SensorManager 负责管理传感器
        sensorManager = ((SensorManager) context.getSystemService(SENSOR_SERVICE));
        if (sensorManager != null) {
            //获取加速度传感器
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        }
        //初始化SoundPool
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        mWeiChatAudio = mSoundPool.load(context, R.raw.weichat_audio, 1);

        //获取Vibrator震动服务
        mVibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
    }

    public void start(){
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void setShakeCallback(ShakeCallback shakeCallback){
        this.shakeCallback = shakeCallback;
    }

    public void close() {
        // 务必要在pause中注销 mSensorManager
        // 否则会造成界面退出后摇一摇依旧生效的bug
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();

        if (type == Sensor.TYPE_ACCELEROMETER) {
            //获取三个方向值
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            if ((Math.abs(x) > 30 || Math.abs(y) > 30 || Math
                    .abs(z) > 30) && !isShake) {
                isShake = true;
                startShake();
            }
        }
    }

    public void startShake() {
        // TODO: 2016/10/19 实现摇动逻辑, 摇动后进行震动
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    //开始震动 发出提示音 展示动画效果
                    mHandler.obtainMessage(START_SHAKE).sendToTarget();
                    Thread.sleep(500);
                    //再来一次震动提示
                    mHandler.obtainMessage(AGAIN_SHAKE).sendToTarget();
                    Thread.sleep(500);
                    mHandler.obtainMessage(END_SHAKE).sendToTarget();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    private static class MyHandler extends Handler {

        private WeakReference<ShakeManager> mReference;
        private ShakeManager shakeManager;

        public MyHandler(ShakeManager activity) {
            mReference = new WeakReference<ShakeManager>(activity);
            if (mReference != null) {
                shakeManager = mReference.get();
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case START_SHAKE:
                    shakeCallback.onShake();
                    //This method requires the caller to hold the permission VIBRATE.
                    shakeManager.mVibrator.vibrate(300);
                    //发出提示音
                    shakeManager.mSoundPool.play(shakeManager.mWeiChatAudio, 1, 1, 0, 0, 1);
                    break;
                case AGAIN_SHAKE:
                    shakeManager.mVibrator.vibrate(300);
                    break;
                case END_SHAKE:
                    shakeCallback.endShake();

                    //整体效果结束, 将震动设置为false
                    shakeManager.isShake = false;
                    break;
            }
        }
    }

    public abstract static class ShakeCallback {

        public abstract void onShake();

        public abstract void endShake();
    }
}
