package pignus.aegis;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import android.view.MotionEvent;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class SensorsActivity extends AppCompatActivity implements SensorEventListener{
    private final static int STRING_MAX_SIZE = 10000;
    String Folder = "aegis";
    File folder = new File(Environment.getExternalStorageDirectory() + File.separator + Folder);

    String BufferAccel = "";
    String BufferGyro = "";
    String BufferMag = "";
    String BufferTouch = "";


    File AccelerometerFile = new File("/sdcard/" + Folder + "/Accelerometer.csv");
    File GyroscopeFile = new File("/sdcard/" + Folder + "/Gyroscope.csv");
    File MagnetometerFile = new File("/sdcard/" + Folder + "/Magnetometer.csv");
    File TouchEventFile = new File("/sdcard/" + Folder + "/TouchEvent.csv");

    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mMagnetometer;
    Display display;
    int PhoneOrientation;
    int SensorType;
    int eventAction;

    //Valor dos Sensores
    String AccelX, AccelY, AccelZ;
    String GyroX, GyroY, GyroZ;
    String MagX, MagY, MagZ;
    String unixTime, PosX, PosY, Press, Area;

    //Txt da Tela
    EditText textBox;
    TextView mTxtAccelX, mTxtAccelY, mTxtAccelZ;
    TextView mTxtGyroX, mTxtGyroY, mTxtGyroZ;
    TextView mTxtMagX, mTxtMagY, mTxtMagZ;
    TextView mTxtTime, mTxtPosX, mTxtPosY, mTxtPress, mTxtArea;

    private String GravarArquivo(File Arquivo, String bufferDados, String dados, boolean Buffer){
        if(bufferDados.length() + dados.length() > STRING_MAX_SIZE || Buffer == false) {
            try {
                FileOutputStream fOut = new FileOutputStream(Arquivo, true);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write(bufferDados + dados);
                myOutWriter.flush();
                myOutWriter.close();
                fOut.close();
                bufferDados = "";
                Log.i("Diego", "File Writen");
            } catch (Exception e) {
                Log.e("Diego", "Could not write on file");
            }
        }else {
            bufferDados = bufferDados + dados;
            Log.i("Diego", Integer.toString(bufferDados.length() + dados.length()));
        }
        return bufferDados;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        //Txtbox editavel da tela
        textBox = (EditText) findViewById(R.id.editText);


        //Txts da Tela
        mTxtAccelX = (TextView) findViewById(R.id.TxtAccelX);
        mTxtAccelY = (TextView) findViewById(R.id.TxtAccelY);
        mTxtAccelZ = (TextView) findViewById(R.id.TxtAccelZ);

        mTxtGyroX = (TextView) findViewById(R.id.TxtGyroX);
        mTxtGyroY = (TextView) findViewById(R.id.TxtGyroY);
        mTxtGyroZ = (TextView) findViewById(R.id.TxtGyroZ);

        mTxtMagX = (TextView) findViewById(R.id.TxtMagX);
        mTxtMagY = (TextView) findViewById(R.id.TxtMagY);
        mTxtMagZ = (TextView) findViewById(R.id.TxtMagZ);

        mTxtTime = (TextView) findViewById(R.id.TxtTime);
        mTxtPosX = (TextView) findViewById(R.id.TxtPosX);
        mTxtPosY = (TextView) findViewById(R.id.TxtPosY);
        mTxtPress = (TextView) findViewById(R.id.TxtPress);
        mTxtArea = (TextView) findViewById(R.id.TxtArea);


        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer  = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAccelerometer, 100000000);
        mSensorManager.registerListener(this, mGyroscope, 100000000);
        mSensorManager.registerListener(this, mMagnetometer, 100000000);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        if(!AccelerometerFile.exists()){
           try {
               AccelerometerFile.createNewFile();
               Log.i("Diego", "AccelerometerFilee Created");
           } catch (Exception e) {
               Log.e("Diego", "Could not create AccelerometerFile",e);
           }
        }

        if(!GyroscopeFile.exists()){
            try {
                GyroscopeFile.createNewFile();
                Log.i("Diego", "GyroscopeFile Created");
            } catch (Exception e) {
                Log.e("Diego", "Could not create GyroscopeFile",e);
            }
        }

        if(!MagnetometerFile.exists()){
            try {
                MagnetometerFile.createNewFile();
                Log.i("Diego", "MagnetometerFile Created");
            } catch (Exception e) {
                Log.e("Diego", "Could not create MagnetometerFile",e);
            }

        }if(!TouchEventFile.exists()){
            try {
                TouchEventFile.createNewFile();
                Log.i("Diego", "TouchEventFile Created");
            } catch (Exception e) {
                Log.e("Diego", "Could not create TouchEventFile",e);
            }
        }

        final Button button = findViewById(R.id.btnEnd);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Diego", "Button Pressed");
                BufferAccel = GravarArquivo(AccelerometerFile,BufferAccel,"", false);
                BufferMag = GravarArquivo(MagnetometerFile,BufferMag,"", false);
                BufferGyro = GravarArquivo(GyroscopeFile,BufferGyro,"", false);
                BufferTouch = GravarArquivo(TouchEventFile, BufferTouch, "", false);
                Intent intent = new Intent(getBaseContext(), EndActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN || eventAction == MotionEvent.ACTION_UP ||
            eventAction == MotionEvent.ACTION_POINTER_DOWN || eventAction == MotionEvent.ACTION_POINTER_UP ||
            eventAction == MotionEvent.ACTION_MOVE){
            unixTime = Long.toString(System.currentTimeMillis());
            PosX = Float.toString(event.getX());
            PosY = Float.toString(event.getY());
            Press = Float.toString(event.getPressure());
            Area = Float.toString(event.getSize());

            mTxtTime.setText("UnixT: " + unixTime);
            mTxtPosX.setText("PosX: " + PosX);
            mTxtPosY.setText("PosY: " + PosY);
            mTxtPress.setText("Press: " + Press);
            mTxtArea.setText("Area: " + Area);

            //Escrever no Arquivo
            BufferTouch = GravarArquivo(TouchEventFile, BufferTouch,unixTime + ',' + "123" + ',' + "123" + ','
                    + event.getPointerCount() + ',' + '0' + ',' + eventAction + ',' + PosX + ',' + PosY + ','
                    + Press + ',' + Area + ',' + PhoneOrientation + '\n', false);

            Log.i("Diego", "Tocado");
        }
        return true;
    }

    public SensorsActivity() {
    }

    protected void onResume(SensorEvent event) {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, 100000000);
        mSensorManager.registerListener(this, mGyroscope, 100000000);
        mSensorManager.registerListener(this, mMagnetometer, 100000000);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        SensorType = event.sensor.getType();
        unixTime = Long.toString(System.currentTimeMillis());
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        PhoneOrientation = display.getRotation();

        if (SensorType == Sensor.TYPE_ACCELEROMETER){
            AccelX = Float.toString(event.values[0]);
            AccelY = Float.toString(event.values[1]);
            AccelZ = Float.toString(event.values[2]);

            mTxtAccelX.setText("AccX: "  + AccelX);
            mTxtAccelY.setText("AccY: " + AccelY);
            mTxtAccelZ.setText("AccZ: " + AccelZ);

            //Gravacao dos Dados no Arquivo
            BufferAccel = GravarArquivo(AccelerometerFile, BufferAccel,unixTime + ',' + "123" + ',' + "123" + ','
                    + AccelX + ',' + AccelY + ',' + AccelZ + ','
                    + PhoneOrientation + '\n', true);

        }else if(SensorType == Sensor.TYPE_GYROSCOPE){
            GyroX = Float.toString(event.values[0]);
            GyroY = Float.toString(event.values[1]);
            GyroZ = Float.toString(event.values[2]);

            mTxtGyroX.setText("GyrX: " + GyroX);
            mTxtGyroY.setText("GyrY: " + GyroY);
            mTxtGyroZ.setText("GyrZ: " + GyroZ);

            BufferGyro = GravarArquivo(GyroscopeFile, BufferGyro,unixTime + ',' + "123" + ',' + "123" + ','
                    + GyroX + ',' + GyroY + ',' + GyroZ + ','
                    + PhoneOrientation + '\n', true);


        }else if(SensorType == Sensor.TYPE_MAGNETIC_FIELD){
            MagX = Float.toString(event.values[0]);
            MagY = Float.toString(event.values[1]);
            MagZ = Float.toString(event.values[2]);

            mTxtMagX.setText("MagX: " + MagX);
            mTxtMagY.setText("MagY: " + MagY);
            mTxtMagZ.setText("MagZ: " + MagZ);

            BufferMag = GravarArquivo(MagnetometerFile, BufferMag,unixTime + ',' + "123" + ',' + "123" + ','
                    + MagX + ',' + MagY + ',' + MagZ + ','
                    + PhoneOrientation + '\n', true);
        }
    }



}

