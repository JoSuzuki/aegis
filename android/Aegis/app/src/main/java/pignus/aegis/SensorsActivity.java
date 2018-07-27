package pignus.aegis;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.TextView;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import android.view.MotionEvent;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.Time;

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
            Log.i("Diego", "Teste");
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
        String unixTime = Long.toString(System.currentTimeMillis());

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int PhoneOrientation = display.getRotation();

        if (SensorType == Sensor.TYPE_ACCELEROMETER){

            String AccelX = Float.toString(event.values[0]);
            String AccelY = Float.toString(event.values[1]);
            String AccelZ = Float.toString(event.values[2]);

            String txtAccelX = "AccX: " + AccelX;
            String txtAccelY = "AccY: " + AccelY;
            String txtAccelZ = "AccZ: " + AccelZ;

            TextView mTxtAccelX = (TextView) findViewById(R.id.TxtAccelX);
            TextView mTxtAccelY = (TextView) findViewById(R.id.TxtAccelY);
            TextView mTxtAccelZ = (TextView) findViewById(R.id.TxtAccelZ);

            mTxtAccelX.setText(txtAccelX);
            mTxtAccelY.setText(txtAccelY);
            mTxtAccelZ.setText(txtAccelZ);

            File myFile = new File("/sdcard/mysdfile.txt");
            if(!myFile.exists()){
               try {
                   myFile.createNewFile();
                   Log.i("Diego", "File Created");
               } catch (Exception e) {
                   Log.e("ERR", "Could not create file",e);
               }
            }
            try {
                FileOutputStream fOut = new FileOutputStream(myFile,true);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write(unixTime + ',' + "123" + ',' + "123" + ','
                        + AccelX + ',' + AccelY + ',' + AccelZ + ','
                        + PhoneOrientation + '\n');
                myOutWriter.flush();
                myOutWriter.close();
                fOut.close();
                Log.i("Diego", "File Writen");
            } catch(Exception e) {
                Log.e("ERRR", "Could not write on file");
            }

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

