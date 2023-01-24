package com.example.sensorprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class CompassApp extends AppCompatActivity implements SensorEventListener {
    private TextView textView;
    private ImageView imageView;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor,magnetometerSesnsor;

    private float[] lastAccelerometer=new float[3];
    private float[] lastMagnetometer=new float[3];
    private float[] rotationMatrix=new float[9];
    private float[] orientation=new float[3];

    boolean isLastAccelerometerArrayCopied=false;
    boolean isLastMagnetometerArrayCopied=false;

    long lastUpdatedTime=0;
    float currentDegree=0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_app);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textView=findViewById(R.id.textView);
        imageView=findViewById(R.id.imageView);
        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSesnsor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor==accelerometerSensor)
        {
            System.arraycopy(event.values,0,lastAccelerometer,0,event.values.length);  // opying values in lastAccelerometerArray
            isLastAccelerometerArrayCopied=true;
        }
        else if(event.sensor==magnetometerSesnsor)
        {
            System.arraycopy(event.values,0,lastMagnetometer,0,event.values.length);  // opying values in lastAccelerometerArray
            isLastMagnetometerArrayCopied=true;
        }

        if(isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied && System.currentTimeMillis()-lastUpdatedTime>250)
        {
            SensorManager.getRotationMatrix(rotationMatrix,null,lastAccelerometer,lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix,orientation);

            float azimuthInRadians=orientation[0];
            float azimythInDegree= (float) Math.toDegrees(azimuthInRadians);

            RotateAnimation rotateAnimation=new RotateAnimation(currentDegree,-azimythInDegree, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation.setDuration(250);;
            rotateAnimation.setFillAfter(true);
            imageView.startAnimation(rotateAnimation);

            currentDegree=-azimythInDegree;
            lastUpdatedTime=System.currentTimeMillis();

            int x= (int) azimythInDegree;
            textView.setText(x+"°");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // for registering
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,magnetometerSesnsor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    // for unregistering
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this,accelerometerSensor);
        sensorManager.unregisterListener(this,magnetometerSesnsor);

    }
}