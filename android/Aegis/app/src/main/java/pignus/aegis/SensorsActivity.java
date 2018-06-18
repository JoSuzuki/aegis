package pignus.aegis;

import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import android.view.MotionEvent;

import android.util.Log;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener{
    private  SensorManager mSensorManager;
    private  Sensor mAccelerometer;
    private  Sensor mGyroscope;
    private  Sensor mMagnetometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer  = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAccelerometer, 100000000);
        mSensorManager.registerListener(this, mGyroscope, 100000000);
        mSensorManager.registerListener(this, mMagnetometer, 100000000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN){
            String unixTime = "UnixT: " + Long.toString(System.currentTimeMillis());
            String PosX = "PosX: " + Float.toString(event.getX());
            String PosY = "PosY: " + Float.toString(event.getY());
            String Press = "Press: " + Float.toString(event.getPressure());
            String Area = "Area: " + Float.toString(event.getSize());

            TextView mTxtTime = (TextView) findViewById(R.id.TxtTime);
            TextView mTxtPosX = (TextView) findViewById(R.id.TxtPosX);
            TextView mTxtPosY = (TextView) findViewById(R.id.TxtPosY);
            TextView mTxtPress = (TextView) findViewById(R.id.TxtPress);
            TextView mTxtArea = (TextView) findViewById(R.id.TxtArea);

            mTxtTime.setText(unixTime);
            mTxtPosX.setText(PosX);
            mTxtPosY.setText(PosY);
            mTxtPress.setText(Press);
            mTxtArea.setText(Area);
        }
        return true;
    }

    public SensorsActivity() {
    }

    protected void onResume(SensorEvent event) {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, 100000000);
        mSensorManager.registerListener(this, mGyroscope, 100000000);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        int SensorType = event.sensor.getType();

        if (SensorType == Sensor.TYPE_ACCELEROMETER){
            String AccelX = "AccX: " + Float.toString(event.values[0]);
            String AccelY = "AccY: " + Float.toString(event.values[1]);
            String AccelZ = "AccZ: " + Float.toString(event.values[2]);

            TextView mTxtAccelX = (TextView) findViewById(R.id.TxtAccelX);
            TextView mTxtAccelY = (TextView) findViewById(R.id.TxtAccelY);
            TextView mTxtAccelZ = (TextView) findViewById(R.id.TxtAccelZ);

            mTxtAccelX.setText(AccelX);
            mTxtAccelY.setText(AccelY);
            mTxtAccelZ.setText(AccelZ);

        }else if(SensorType == Sensor.TYPE_GYROSCOPE){
            String GyroX = "GyrX: " + Float.toString(event.values[0]);
            String GyroY = "GyrY: " + Float.toString(event.values[1]);
            String GyroZ = "GyrZ: " + Float.toString(event.values[2]);

            TextView mTxtGyroX = (TextView) findViewById(R.id.TxtGyroX);
            TextView mTxtGyroY = (TextView) findViewById(R.id.TxtGyroY);
            TextView mTxtGyroZ = (TextView) findViewById(R.id.TxtGyroZ);

            mTxtGyroX.setText(GyroX);
            mTxtGyroY.setText(GyroY);
            mTxtGyroZ.setText(GyroZ);
        }else if(SensorType == Sensor.TYPE_MAGNETIC_FIELD){
            String MagX = "MagX: " + Float.toString(event.values[0]);
            String MagY = "MagY: " + Float.toString(event.values[1]);
            String MagZ = "MagZ: " + Float.toString(event.values[2]);

            TextView mTxtMagX = (TextView) findViewById(R.id.TxtMagX);
            TextView mTxtMagY = (TextView) findViewById(R.id.TxtMagY);
            TextView mTxtMagZ = (TextView) findViewById(R.id.TxtMagZ);

            mTxtMagX.setText(MagX);
            mTxtMagY.setText(MagY);
            mTxtMagZ.setText(MagZ);
        }
    }
}

