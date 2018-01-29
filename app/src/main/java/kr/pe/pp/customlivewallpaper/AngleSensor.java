package kr.pe.pp.customlivewallpaper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Administrator on 2018-01-23.
 */

public class AngleSensor {
    private AngleSensorEventListener angleSensorEventListener = null;

    SensorManager sensorManager = null;
    SensorEventListener sensorEventListener = null;
    Sensor sensorGyro = null;
    Sensor sensorAcc = null;
    float[] gyroValues = new float[3];
    float[] accValues = new float[3];
    double accPitch = 0, accRoll = 0;
    double timestamp;
    double dt;
    double temp;
    float a = 0.2f;
    double RAD2DGR = 180 / Math.PI;
    static final float NS2S = 1.0f/1000000000.0f;
    boolean running;
    boolean gyroRunning;
    boolean accRunning;

    public double angleX = 0, angleY = 0, angleZ = 0;

    public AngleSensor(Context context) {
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorEventListener = new AngleSensor.GyroscopeListener();
    }

    public void register() {
        sensorManager.registerListener(sensorEventListener, sensorGyro, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventListener, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
    }
    public void unregister() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    public void setAngleSensorEventListener(AngleSensorEventListener listener) {
        angleSensorEventListener = listener;
    }

    private void complementaty(double new_ts){
        /* 자이로랑 가속 해제 */
        gyroRunning = false;
        accRunning = false;

        /*센서 값 첫 출력시 dt(=timestamp - event.timestamp)에 오차가 생기므로 처음엔 break */
        if(timestamp == 0){
            timestamp = new_ts;
            return;
        }
        dt = (new_ts - timestamp) * NS2S; // ns->s 변환
        timestamp = new_ts;

        /* degree measure for accelerometer */
        accPitch = -Math.atan2(accValues[0], accValues[2]) * 180.0 / Math.PI; // Y 축 기준
        accRoll= Math.atan2(accValues[1], accValues[2]) * 180.0 / Math.PI; // X 축 기준

        /**
         * 1st complementary filter.
         *  mGyroValuess : 각속도 성분.
         *  mAccPitch : 가속도계를 통해 얻어낸 회전각.
         */
        temp = (1/a) * (accPitch - angleX) + gyroValues[1];
        angleX = angleX + (temp*dt);

        temp = (1/a) * (accRoll - angleY) + gyroValues[0];
        angleY = angleY + (temp*dt);

        if(angleSensorEventListener != null) {
            angleSensorEventListener.onReceiveAngle(angleX, angleY, 0);
        }
    }

    private class GyroscopeListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch(event.sensor.getType()) {
                case Sensor.TYPE_GYROSCOPE:
                    gyroValues = event.values;
                    if(!gyroRunning) gyroRunning = true;
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = event.values;
                    if(!accRunning) accRunning = true;
                    break;
            }

            if(accRunning && gyroRunning) {
                complementaty(event.timestamp);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    public interface AngleSensorEventListener {
        void onReceiveAngle(double angleX, double angleY, double angleZ);
    }

}

/*
public class AngleSensor {
    private AngleSensorEventListener angleSensorEventListener = null;

    SensorManager sensorManager = null;
    SensorEventListener sensorEventListener = null;
    Sensor sensorMagnetic = null;
    Sensor sensorAcc = null;
    float[] magneticValues = null;
    float[] accValues = null;
    float[] orientationValues = new float[3];
    boolean running;
    boolean magneticRunning;
    boolean accRunning;

    public double angleX = 0, angleY = 0, angleZ;

    public AngleSensor(Context context) {
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorAcc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorEventListener = new AngleSensor.GyroscopeListener();
    }

    public void register() {
        sensorManager.registerListener(sensorEventListener, sensorMagnetic, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(sensorEventListener, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
    }
    public void unregister() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    public void setAngleSensorEventListener(AngleSensorEventListener listener) {
        angleSensorEventListener = listener;
    }

    private class GyroscopeListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            switch(event.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    magneticValues = event.values;
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    accValues = event.values;
                    break;
            }

            if(magneticValues != null && accValues != null) {
                float[] R = new float[9];
                if(SensorManager.getRotationMatrix(R, null, accValues, magneticValues)) {
                    SensorManager.getOrientation(R, orientationValues);
                    angleZ = Math.toDegrees(orientationValues[0]);
                    angleY = Math.toDegrees(orientationValues[1]);
                    angleX = Math.toDegrees(orientationValues[2]);
                    if(angleSensorEventListener != null) {
                        angleSensorEventListener.onReceiveAngle(angleX, angleY, angleZ);
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }

    public interface AngleSensorEventListener {
        void onReceiveAngle(double angleX, double angleY, double angleZ);
    }

}
*/