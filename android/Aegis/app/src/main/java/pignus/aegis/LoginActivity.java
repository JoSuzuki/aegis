package pignus.aegis;

import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
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
}
