package pignus.aegis;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class LoginActivity extends AppCompatActivity  implements SensorEventListener {
    private final static int STRING_MAX_SIZE = 10000;
    String ActivityID;

    File folder;

    File AccelerometerFile;
    File GyroscopeFile;
    File MagnetometerFile;
    File TouchEventFile;


    String BufferAccel = "";
    String BufferGyro = "";
    String BufferMag = "";
    String BufferTouch = "";

    CollectorKeyboard mCollectorKeyboard;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mGyroscope, mMagnetometer;
    Display display;
    int PhoneOrientation;
    int SensorType;
    int eventAction;
    String eventTime;

    //Valor dos Sensores
    String AccelX, AccelY, AccelZ;
    String GyroX, GyroY, GyroZ;
    String MagX, MagY, MagZ;
    String unixTime, PosX, PosY, Press, Area;

    //Txt da Tela
    EditText textBox;

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
        Intent intent = getIntent();
        setContentView(R.layout.activity_login);

        String[] arraySpinner = new String[] {
                "Diego", "Jonathan", "Thiago"
        };


        final Spinner s = (Spinner) findViewById(R.id.sprLogin);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        s.setAdapter(adapter);

        ActivityID =  s.getSelectedItem().toString();

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                ActivityID = s.getSelectedItem().toString();
                Log.i("Diego", "Item selected");
                Log.i("Diego", ActivityID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        final LoginActivity activity = this;
        textBox = (EditText) findViewById(R.id.Txt);

        textBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Diego", "textBox Pressed");
                //ActivityID = s.getSelectedItem().toString();
                String Folder = ActivityID;
                folder = new File(Environment.getExternalStorageDirectory() + File.separator + "aegis" + File.separator +Folder);

                AccelerometerFile = new File("/sdcard/aegis/" + Folder + "/Accelerometer.csv");
                GyroscopeFile = new File("/sdcard/aegis/" + Folder + "/Gyroscope.csv");
                MagnetometerFile = new File("/sdcard/aegis/" + Folder + "/Magnetometer.csv");
                TouchEventFile = new File("/sdcard/aegis/" + Folder + "/TouchEvent.csv");

                if (!folder.exists()) {
                    folder.mkdirs();
                }

                if(!AccelerometerFile.exists()){
                    try {
                        AccelerometerFile.createNewFile();
                        Log.i("Diego", "AccelerometerFile Created");
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

                mCollectorKeyboard = new CollectorKeyboard(activity, R.id.keyboardview, R.xml.collector_keyboard, ActivityID);
                mCollectorKeyboard.registerEditText(R.id.Txt);

            }
        });

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mMagnetometer  = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, mAccelerometer, 100000000);
        mSensorManager.registerListener(this, mGyroscope, 100000000);
        mSensorManager.registerListener(this, mMagnetometer, 100000000);

        final Button buttonLogin = findViewById(R.id.btnLogin);
        buttonLogin.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                eventAction = event.getAction();
                unixTime = Long.toString(System.currentTimeMillis());
                if (eventAction == MotionEvent.ACTION_DOWN || eventAction == MotionEvent.ACTION_POINTER_DOWN ||
                        eventAction == MotionEvent.ACTION_MOVE){
                    PosX = Float.toString(event.getX());
                    PosY = Float.toString(event.getY());
                    Press = Float.toString(event.getPressure());
                    Area = Float.toString(event.getSize());
                    eventTime = Long.toString(event.getEventTime());

                    //Escrever no Arquivo
                    BufferTouch = GravarArquivo(TouchEventFile, BufferTouch,unixTime + ',' + eventTime + ',' + ActivityID + ','
                            + event.getPointerCount() + ',' + '0' + ',' + eventAction + ',' + PosX + ',' + PosY + ','
                            + Press + ',' + Area + ',' + PhoneOrientation + '\n', false);

                    Log.i("Diego", "Button start press");
                    return true;
                } else if (eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_POINTER_UP ) {
                    PosX = Float.toString(event.getX());
                    PosY = Float.toString(event.getY());
                    Press = Float.toString(event.getPressure());
                    Area = Float.toString(event.getSize());

                    //Escrever no Arquivo
                    BufferTouch = GravarArquivo(TouchEventFile, BufferTouch,unixTime + ',' + eventTime + ',' + ActivityID + ','
                            + event.getPointerCount() + ',' + '0' + ',' + eventAction + ',' + PosX + ',' + PosY + ','
                            + Press + ',' + Area + ',' + PhoneOrientation + '\n', false);
                    BufferAccel = GravarArquivo(AccelerometerFile,BufferAccel,"", false);
                    BufferMag = GravarArquivo(MagnetometerFile,BufferMag,"", false);
                    BufferGyro = GravarArquivo(GyroscopeFile,BufferGyro,"", false);
                    Intent intent = new Intent(getBaseContext(), EndActivity.class);
                    startActivity(intent);
                    Log.i("Diego", "Button Pressed");
                    return true;
                }
                return true;
            }
        });
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        SensorType = event.sensor.getType();
        unixTime = Long.toString(System.currentTimeMillis());
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        PhoneOrientation = display.getRotation();
        eventTime = Long.toString(event.timestamp);

        if (SensorType == Sensor.TYPE_ACCELEROMETER){
            AccelX = Float.toString(event.values[0]);
            AccelY = Float.toString(event.values[1]);
            AccelZ = Float.toString(event.values[2]);

            //Gravacao dos Dados no Arquivo
            BufferAccel = GravarArquivo(AccelerometerFile, BufferAccel,unixTime + ',' + eventTime + ',' + ActivityID + ','
                    + AccelX + ',' + AccelY + ',' + AccelZ + ','
                    + PhoneOrientation + '\n', true);

        }else if(SensorType == Sensor.TYPE_GYROSCOPE){
            GyroX = Float.toString(event.values[0]);
            GyroY = Float.toString(event.values[1]);
            GyroZ = Float.toString(event.values[2]);

            BufferGyro = GravarArquivo(GyroscopeFile, BufferGyro,unixTime + ',' + eventTime + ',' + ActivityID + ','
                    + GyroX + ',' + GyroY + ',' + GyroZ + ','
                    + PhoneOrientation + '\n', true);


        }else if(SensorType == Sensor.TYPE_MAGNETIC_FIELD){
            MagX = Float.toString(event.values[0]);
            MagY = Float.toString(event.values[1]);
            MagZ = Float.toString(event.values[2]);

            BufferMag = GravarArquivo(MagnetometerFile, BufferMag,unixTime + ',' + eventTime + ',' + ActivityID + ','
                    + MagX + ',' + MagY + ',' + MagZ + ','
                    + PhoneOrientation + '\n', true);
        }
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

    @Override public void onBackPressed() {
        if( mCollectorKeyboard.isCollectorKeyboardVisible() ) mCollectorKeyboard.hideCollectorKeyboard(); else this.finish();
    }

    @Override public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("Jon", String.valueOf(newConfig.orientation));
        final View view = findViewById(R.id.scrollView);
        final Configuration finalNewConfig = newConfig;

        ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                mCollectorKeyboard.updateCollectorKeyboardLayout(finalNewConfig.orientation);
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}
