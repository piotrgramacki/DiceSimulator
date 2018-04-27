package com.example.piotrgramacki238493.dicesimulator;

import android.content.Context;
import android.content.res.TypedArray;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ImageView[] dices;
    private ImageView[] results;
    private SensorManager sensorManager;
    private int currentDices;
    private static final int MAX_DICES = 5;
    private Random random = new Random();

    private SensorEventListener gravityListener;
    private ShakeEventListener shakeListener;

    private float prev_x = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setListeners();

        currentDices = 1;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(gravityListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(shakeListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(gravityListener);
        sensorManager.unregisterListener(shakeListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(gravityListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(shakeListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }

    private void findViews() {

        dices = new ImageView[MAX_DICES];
        TypedArray dicesID = getResources().obtainTypedArray(R.array.dices);

        for (int i = 0; i < MAX_DICES; i++) {
            int id = dicesID.getResourceId(i, 0);
            dices[i] = findViewById(id);
        }
        dicesID.recycle();

        results = new ImageView[MAX_DICES];
        TypedArray resultsID = getResources().obtainTypedArray(R.array.res);

        for (int i = 0; i < MAX_DICES; i++) {
            int id = resultsID.getResourceId(i, 0);
            results[i] = findViewById(id);
        }

        resultsID.recycle();
    }

    private void setListeners() {
        gravityListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                if (prev_x != 0) {
                    if (x == 0) {
                        prev_x = 0;
                    } else if (prev_x / x < 0) {
                        prev_x = 0;
                    }
                } else if (Math.abs(x) > 2) {
                    if (x < 0 && currentDices < MAX_DICES) {
                        oneMoreDice();
                    } else if (currentDices > 1)
                        oneLessDice();

                    prev_x = x;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        shakeListener = new ShakeEventListener();
        shakeListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            @Override
            public void onShake() {
                throwDice();
            }
        });
    }

    private void throwDice() {
        reset();

        TypedArray numbers = getResources().obtainTypedArray(R.array.numbers);

        for (int i = 0; i < currentDices; i++) {
            results[i].setVisibility(View.VISIBLE);
            int res = random.nextInt(6);
            results[i].setImageResource(numbers.getResourceId(res, 0));
        }

        numbers.recycle();
    }

    private void reset() {
        for (int i = 0; i < MAX_DICES; i++)
            results[i].setVisibility(View.GONE);
    }

    private void oneMoreDice() {
        dices[currentDices++].setImageResource(R.drawable.dice);
    }

    private void oneLessDice() {
        dices[--currentDices].setImageResource(R.drawable.dice_off);
    }
}
