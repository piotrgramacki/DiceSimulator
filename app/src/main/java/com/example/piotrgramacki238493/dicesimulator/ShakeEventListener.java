package com.example.piotrgramacki238493.dicesimulator;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;


public class ShakeEventListener implements SensorEventListener {



    private static final int MIN_FORCE = 10;
    private static final int MIN_DIRECTION_CHANGE = 3;
    private static final int MAX_PAUSE_BETWEEN_DIRECTION_CHANGE = 200;
    private static final int MAX_TOTAL_DURATION_OF_SHAKE = 400;

    private long firstDirectionChangeTime = 0;
    private long lastDirectionChangeTime;
    private int directionChangeCount = 0;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    private OnShakeListener mShakeListener;

    public interface OnShakeListener {
        void onShake();
    }

    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent se) {
        // get sensor data
        float x = se.values[0];
        float y = se.values[1];
        float z = se.values[2];

        // calculate movement
        float totalMovement = Math.abs(x + y + z - lastX - lastY - lastZ);

        if (totalMovement > MIN_FORCE) {

            // get time
            long now = System.currentTimeMillis();

            // store first movement time
            if (firstDirectionChangeTime == 0) {
                firstDirectionChangeTime = now;
                lastDirectionChangeTime = now;
            }

            // check if the last movement was not long ago
            long lastChangeWasAgo = now - lastDirectionChangeTime;
            if (lastChangeWasAgo < MAX_PAUSE_BETWEEN_DIRECTION_CHANGE) {

                // store movement data
                lastDirectionChangeTime = now;
                directionChangeCount++;

                // store last sensor data
                lastX = x;
                lastY = y;
                lastZ = z;

                // check how many movements are so far
                if (directionChangeCount >= MIN_DIRECTION_CHANGE) {

                    // check total duration
                    long totalDuration = now - firstDirectionChangeTime;
                    if (totalDuration < MAX_TOTAL_DURATION_OF_SHAKE) {
                        mShakeListener.onShake();
                        resetShakeParameters();
                    }
                }

            } else {
                resetShakeParameters();
            }
        }
    }

    private void resetShakeParameters() {
        firstDirectionChangeTime = 0;
        directionChangeCount = 0;
        lastDirectionChangeTime = 0;
        lastX = 0;
        lastY = 0;
        lastZ = 0;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
