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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;

import pignus.aegis.Http;

public class LoginActivity extends AppCompatActivity  implements SensorEventListener {
    String ActivityID;

    String BufferAccel = "";
    String BufferGyro = "";
    String BufferMag = "";
    String BufferTouch = "";

    CollectorKeyboard mCollectorKeyboard;

    TextView txtProb;

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

    private final String USER_AGENT = "Mozilla/5.0";

    private String GravarArquivo(String bufferDados, String dados){
        bufferDados = bufferDados + dados;
        return bufferDados;
    }

    private void sendGet() throws Exception {
        Log.i("Diego", "Teste1");
        String url = "https://127.0.0.1:8000/getjson/";

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        Log.i("Diego", "Teste2");

        // optional default is GET
        con.setRequestMethod("GET");
        Log.i("Diego", "Teste11");

        //add request header
        //con.setRequestProperty("User-Agent", USER_AGENT);
        Log.i("Diego", "Teste12");

        int responseCode = con.getResponseCode();
        Log.i("Diego", "Teste4");
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            Log.i("Diego", "Teste5");
            response.append(inputLine);
        }
        in.close();

        //print result
        Log.i("Diego", response.toString());
        Log.i("Diego", "Teste3");
        System.out.println(response.toString());
    }


    public void Autenticate(String result){
        try {
            Log.i("Diego", "Result: " + result);
            JSONObject obj = new JSONObject(result);
            double prob = (double)obj.get("auth");
            Log.i("Diego", "Auth: " + prob );
            txtProb.setText( Double.toString(prob));
            if(prob == 1.0){
                Log.i("Diego", "Login Ok");
            }else{
                Log.i("Diego", "Login Failed");
            }

        }catch(Exception e){

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ActivityID = intent.getStringExtra(MainActivity.USER_NAME);

        setContentView(R.layout.activity_login);

        textBox = findViewById(R.id.Txt);

        txtProb = findViewById(R.id.tviewProb);

        final LoginActivity loginActivity = this;

       mCollectorKeyboard = new CollectorKeyboard(this, R.id.keyboardview, R.xml.collector_keyboard, ActivityID, true);
       mCollectorKeyboard.registerEditText(R.id.Txt);

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
                    BufferTouch = GravarArquivo(BufferTouch,unixTime + ',' + eventTime + ',' + ActivityID + ','
                            + event.getPointerCount() + ',' + '0' + ',' + eventAction + ',' + PosX + ',' + PosY + ','
                            + Press + ',' + Area + ',' + PhoneOrientation + '\n');

                    Log.i("Diego", "Button start press");
                } else if (eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_POINTER_UP ) {
                    PosX = Float.toString(event.getX());
                    PosY = Float.toString(event.getY());
                    Press = Float.toString(event.getPressure());
                    Area = Float.toString(event.getSize());

                    //Escrever no Arquivo
                    BufferTouch = GravarArquivo(BufferTouch, unixTime + ',' + eventTime + ',' + ActivityID + ','
                            + event.getPointerCount() + ',' + '0' + ',' + eventAction + ',' + PosX + ',' + PosY + ','
                            + Press + ',' + Area + ',' + PhoneOrientation + '\n');
                    BufferAccel = GravarArquivo(BufferAccel, "");
                    BufferMag = GravarArquivo(BufferMag, "");
                    BufferGyro = GravarArquivo(BufferGyro, "");
                    //Intent intent = new Intent(getBaseContext(), EndActivity.class);
                    //startActivity(intent);
                    Log.i("Diego", "Button Pressed");

                    //String response;
                    Http http = new Http(loginActivity, BufferAccel, BufferGyro, BufferMag, mCollectorKeyboard.getBufferKeyPress(), mCollectorKeyboard.getBufferKeyboardTouch());
                    BufferAccel = "";
                    BufferGyro = "";
                    BufferMag = "";
                    textBox.setText("");
                    http.execute();

                    //try{
                    //    sendGet();
                    //}catch(Exception e){
                    //}
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
            BufferAccel = GravarArquivo(BufferAccel,unixTime + ',' + eventTime + ',' + ActivityID + ','
                    + AccelX + ',' + AccelY + ',' + AccelZ + ','
                    + PhoneOrientation + '\n');

        }else if(SensorType == Sensor.TYPE_GYROSCOPE){
            GyroX = Float.toString(event.values[0]);
            GyroY = Float.toString(event.values[1]);
            GyroZ = Float.toString(event.values[2]);

            BufferGyro = GravarArquivo(BufferGyro,unixTime + ',' + eventTime + ',' + ActivityID + ','
                    + GyroX + ',' + GyroY + ',' + GyroZ + ','
                    + PhoneOrientation + '\n');


        }else if(SensorType == Sensor.TYPE_MAGNETIC_FIELD){
            MagX = Float.toString(event.values[0]);
            MagY = Float.toString(event.values[1]);
            MagZ = Float.toString(event.values[2]);

            BufferMag = GravarArquivo(BufferMag,unixTime + ',' + eventTime + ',' + ActivityID + ','
                    + MagX + ',' + MagY + ',' + MagZ + ','
                    + PhoneOrientation + '\n');
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
