package com.example.ai.mapdemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.MainThread;

/**
 * 方向传感器，定位图标随手机转动而转动
 */
public class MyOrientationListener implements SensorEventListener {

    private SensorManager mManager;
    private Context mContext;
    private Sensor mSensor;

    private float lastX;

    public void setOrientationListener(
            onOrientationListener
                    mOrientationListener) {
        this.mOrientationListener = mOrientationListener;
    }

    private onOrientationListener mOrientationListener;

    public MyOrientationListener(Context context){

        this.mContext=context;

    }
    //开始监听
    public void start(){
        mManager=(SensorManager)mContext.
                getSystemService(Context.SENSOR_SERVICE);
        if(mManager!=null){
            //获得方向传感器
            mSensor=mManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        }

        if (mSensor!=null){
            mManager.registerListener(this,mSensor,SensorManager.SENSOR_DELAY_GAME);
        }

    }
    //结束监听
    public void stop(){
        mManager.unregisterListener(this);
    }
    //当传感器检测到的数值发生变化是调用
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){

            float x=event.values[SensorManager.DATA_X];

            if (Math.abs(x-lastX)>1.0){

                if (mOrientationListener!=null){
                    mOrientationListener.onOrientationChanged(x);
                }
            }

            lastX=x;
        }
    }

    //表示精度发生变化的时候调用
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface onOrientationListener{
        void onOrientationChanged(float x);
    }

}
