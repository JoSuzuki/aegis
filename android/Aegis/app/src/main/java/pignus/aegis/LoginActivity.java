package pignus.aegis;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaCas;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.Random;

public class LoginActivity extends AppCompatActivity  implements SensorEventListener, HttpCallbackInterface {
    String[] Perguntas = {"Você acredita que provas convencionais medem de maneira assertiva as capacidades dos alunos?",
            "Explique para um amigo mais jovem como estudar para o Vestibular.",
            "Explique como chegar na sua casa sem utilizar nenhum nome de rua.",
            "Você tem a oportunidade de aprender a tocar um novo instrumento? Qual você escolheria e por quê?",
            "Você acredita que o Facebook melhora a qualidade de vida das pessoas? Desenvolva.",
            "Você acredita que vale a pena investir em exploração espacial?",
            "Desenvolva um argumento a favor ou contra o uso de celulares enquanto dirige.",
            "O que você gosta e não gosta em relação ao transporte público?",
            "Explique para uma criança como realizar uma tarefa (e.g. atravessar a rua).",
            "Desenvolva um argumento a favor ou contra a seguinte declaração: violência televisiva é apropriada para públicos de todas as idades.",
            "Você acredita que a liberação do porte de armas aumentaria o número de crimes violentos?",
            "Você acredita que restrições de idade para o consumo de álcool são efetivas?",
            "As faculdades públicas deveriam continuar sendo gratuitas para todos? Explique sua resposta.",
            "Você acredita que é aceitável o governo ler o seus emails por motivos anti-terrorismo?",
            "Descreva o que você fez no último fim de semana.",
            "Como você convidaria seus amigos para uma festa se telefones e a Internet não existissem?",
            "Escreva um resumo do enredo do seu filme favorito.",
            "Os restaurantes deveriam ser obrigados a colocar as informações nutricionais no cardápio?",
            "Decida uma festa ou evento que você gostaria de organizar e escreva detalhes de como você gostaria de organizar o evento (música, local, convidados, etc.).",
            "Você é chamado para uma entrevista de emprego no Google. Como você se prepararia?",
            "Explique para sua avó como usar o smartphone para achar um novo restaurante no bairro."};


    String ActivityID;
    String SessionNumber;

    File folder;

    File AccelerometerFile;
    File GyroscopeFile;
    File MagnetometerFile;
    File TouchEventFile;
    File KeyPressEventFile;
    File KeyboardTouchFile;

    String BufferAccel = "";
    String BufferGyro = "";
    String BufferMag = "";
    String BufferTouch = "";
    String BufferKeyPress = "";
    String BufferKeyBoardTouch = "";

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
    TextView txtQuestion;

    private final String USER_AGENT = "Mozilla/5.0";

    private String GravarArquivo(String bufferDados, String dados){
        bufferDados = bufferDados + dados;
        return bufferDados;
    }

    private void salvarArquivo(File Arquivo, String bufferDados){
        try {
            Log.i("Diego", "TesteEscrita1");
            FileOutputStream fOut = new FileOutputStream(Arquivo, true);
            Log.i("Diego", "TesteEscrita2");
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            Log.i("Diego", "TesteEscrita3");
            myOutWriter.write(bufferDados);
            Log.i("Diego", "TesteEscrita4");
            myOutWriter.flush();
            Log.i("Diego", "TesteEscrita5");
            myOutWriter.close();
            Log.i("Diego", "TesteEscrita6");
            fOut.close();
        } catch (Exception e) {
            Log.e("Diego", "Could not write on file");
        }
    }


    public void Callback(String result){
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

    public void errorCallback(){
        txtProb.setText("Aconteceu um erro, tente novamente");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ActivityID = intent.getStringExtra(SelectLoginActivity.USER_NAME);
        SessionNumber = intent.getStringExtra(SelectLoginActivity.SESSION_NUMBER);

        Log.i("Diego", "UsuarioLogin: " + ActivityID+SessionNumber);

        String Folder = ActivityID+SessionNumber;
        folder = new File(Environment.getExternalStorageDirectory() + File.separator + "aegis" + File.separator + "login" + File.separator +Folder);

        AccelerometerFile = new File("/sdcard/aegis/login/" + Folder + "/Accelerometer.csv");
        GyroscopeFile = new File("/sdcard/aegis/login/" + Folder + "/Gyroscope.csv");
        MagnetometerFile = new File("/sdcard/aegis/login/" + Folder + "/Magnetometer.csv");
        TouchEventFile = new File("/sdcard/aegis/login/" + Folder + "/TouchEvent.csv");
        KeyPressEventFile = new File("/sdcard/aegis/login/" + Folder + "/KeyPressEvent.csv");
        KeyboardTouchFile = new File ("/sdcard/aegis/login/" + Folder + "/KeyboardTouchEvent.csv");

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

        setContentView(R.layout.activity_login);

        Random r = new Random();
        int NumSorteado = r.nextInt(Perguntas.length);

        txtQuestion = findViewById(R.id.tviewLogin);
        txtQuestion.setText(Perguntas[NumSorteado]);

        textBox = findViewById(R.id.Txt);

        txtProb = findViewById(R.id.tviewProb);

        final LoginActivity loginActivity = this;

       mCollectorKeyboard = new CollectorKeyboard(this, R.id.keyboardview, R.xml.collector_keyboard, ActivityID+SessionNumber, true);
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
                    BufferTouch = GravarArquivo(BufferTouch,unixTime + ',' + eventTime + ',' + ActivityID+SessionNumber + ','
                            + event.getPointerCount() + ',' + '0' + ',' + eventAction + ',' + PosX + ',' + PosY + ','
                            + Press + ',' + Area + ',' + PhoneOrientation + '\n');

                    Log.i("Diego", "Button start press");
                } else if (eventAction == MotionEvent.ACTION_UP || eventAction == MotionEvent.ACTION_POINTER_UP ) {
                    PosX = Float.toString(event.getX());
                    PosY = Float.toString(event.getY());
                    Press = Float.toString(event.getPressure());
                    Area = Float.toString(event.getSize());

                    //Escrever no Arquivo
                    BufferTouch = GravarArquivo(BufferTouch, unixTime + ',' + eventTime + ',' + ActivityID+SessionNumber + ','
                            + event.getPointerCount() + ',' + '0' + ',' + eventAction + ',' + PosX + ',' + PosY + ','
                            + Press + ',' + Area + ',' + PhoneOrientation + '\n');
                    BufferAccel = GravarArquivo(BufferAccel, "");
                    BufferMag = GravarArquivo(BufferMag, "");
                    BufferGyro = GravarArquivo(BufferGyro, "");
                    //Intent intent = new Intent(getBaseContext(), EndActivity.class);
                    //startActivity(intent);
                    Log.i("Diego", "Button Pressed");

                    //String response;
                    BufferKeyPress = mCollectorKeyboard.getBufferKeyPress();
                    BufferKeyBoardTouch = mCollectorKeyboard.getBufferKeyboardTouch();
                    Http http = new Http(loginActivity, ActivityID,BufferAccel, BufferGyro, BufferMag, BufferKeyPress, BufferKeyBoardTouch,0);

                    salvarArquivo(AccelerometerFile, BufferAccel);
                    salvarArquivo(GyroscopeFile, BufferGyro);
                    salvarArquivo(MagnetometerFile, BufferMag);
                    salvarArquivo(KeyPressEventFile,BufferKeyPress);
                    salvarArquivo(KeyboardTouchFile,BufferKeyBoardTouch);

                    BufferAccel = "";
                    BufferGyro = "";
                    BufferMag = "";
                    BufferKeyPress = "";
                    BufferKeyBoardTouch = "";
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
            BufferAccel = GravarArquivo(BufferAccel,unixTime + ',' + eventTime + ',' + ActivityID+SessionNumber + ','
                    + AccelX + ',' + AccelY + ',' + AccelZ + ','
                    + PhoneOrientation + '\n');

        }else if(SensorType == Sensor.TYPE_GYROSCOPE){
            GyroX = Float.toString(event.values[0]);
            GyroY = Float.toString(event.values[1]);
            GyroZ = Float.toString(event.values[2]);

            BufferGyro = GravarArquivo(BufferGyro,unixTime + ',' + eventTime + ',' + ActivityID+SessionNumber + ','
                    + GyroX + ',' + GyroY + ',' + GyroZ + ','
                    + PhoneOrientation + '\n');


        }else if(SensorType == Sensor.TYPE_MAGNETIC_FIELD){
            MagX = Float.toString(event.values[0]);
            MagY = Float.toString(event.values[1]);
            MagZ = Float.toString(event.values[2]);

            BufferMag = GravarArquivo(BufferMag,unixTime + ',' + eventTime + ',' + ActivityID+SessionNumber + ','
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
