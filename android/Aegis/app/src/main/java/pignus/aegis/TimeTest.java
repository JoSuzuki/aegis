package pignus.aegis;

import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class TimeTest extends AppCompatActivity implements SensorEventListener{
    private  SensorManager mSensorManager;
    private  Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_test);
        TextView mTextView = (TextView) findViewById(R.id.TesteTempo);
        mTextView.setText("Welcome to Dynamic TextView");
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    //@Override
    //public boolean onTouchEvent(MotionEvent event){
    //    String unixTime = Long.toString(System.currentTimeMillis());
    //    int eventAction = event.getAction();

    //    if (eventAction == MotionEvent.ACTION_DOWN){
    //        TextView mTextView = (TextView) findViewById(R.id.TesteTempo);
    //        mTextView.setText(unixTime);
    //    }
    //    return true;
    //}

    public TimeTest() {
        //mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        //mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        String Acele = Float.toString(event.values[0]);
        String Acele2 = Float.toString(event.values[1]);
        String Acele3 = Float.toString(event.values[2]);
        //int eventAction = event.getAction();

        //if (eventAction == MotionEvent.ACTION_DOWN){
            TextView mTextView = (TextView) findViewById(R.id.TesteTempo);
            TextView mTextView2 = (TextView) findViewById(R.id.Teste2);
            TextView mTextView3 = (TextView) findViewById(R.id.Teste3);
            mTextView.setText(Acele);
            mTextView2.setText(Acele2);
            mTextView3.setText(Acele3);
        //}

    }
}

