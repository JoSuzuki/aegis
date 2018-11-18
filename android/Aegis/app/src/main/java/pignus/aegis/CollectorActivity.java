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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;

public class CollectorActivity extends AppCompatActivity implements SensorEventListener{
    private final static int STRING_MAX_SIZE = 10000;

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
    int[] PerguntasSelecionadas = new int[3];
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
    TextView mTxtQ1;
    TextView mTxtQ2;
    TextView mTxtQ3;

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
        setContentView(R.layout.activity_collector);
        String userName = intent.getStringExtra(NameCollectorActivity.USER_NAME);
        ActivityID = userName;

        String Folder = userName;
        folder = new File(Environment.getExternalStorageDirectory() + File.separator + "aegis" + File.separator +Folder);

        AccelerometerFile = new File("/sdcard/aegis/" + Folder + "/Accelerometer.csv");
        GyroscopeFile = new File("/sdcard/aegis/" + Folder + "/Gyroscope.csv");
        MagnetometerFile = new File("/sdcard/aegis/" + Folder + "/Magnetometer.csv");
        TouchEventFile = new File("/sdcard/aegis/" + Folder + "/TouchEvent.csv");



        //Txtbox editavel da tela
        textBox = (EditText) findViewById(R.id.TxtA1);

        // Collector keyboard
        mCollectorKeyboard = new CollectorKeyboard(this, R.id.keyboardview, R.xml.collector_keyboard, userName, false);
        mCollectorKeyboard.registerEditText(R.id.TxtA1);
        mCollectorKeyboard.registerEditText(R.id.TxtA2);
        mCollectorKeyboard.registerEditText(R.id.TxtA3);

        //Txts da Tela

        //mTxtUserName = (TextView) findViewById(R.id.TxtUserName);
        //mTxtUserName.setText(userName);
        Random r = new Random();
        int NumSorteado;
        boolean repetido = false;
        int contadorPerguntasSelecionadas = 0;

        while(contadorPerguntasSelecionadas < PerguntasSelecionadas.length){
            NumSorteado = r.nextInt(Perguntas.length);
            for(int j = 0; j < contadorPerguntasSelecionadas; j++){
                if(PerguntasSelecionadas[j] == NumSorteado) {
                    repetido = true;
                }
            }
            if(!repetido){
                PerguntasSelecionadas[contadorPerguntasSelecionadas] = NumSorteado;
                contadorPerguntasSelecionadas++;
            }
            repetido = false;
        }

        mTxtQ1 = (TextView) findViewById(R.id.TxtQ1);
        mTxtQ1.setText(Perguntas[PerguntasSelecionadas[0]]);

        mTxtQ2 = (TextView) findViewById(R.id.TxtQ2);
        mTxtQ2.setText(Perguntas[PerguntasSelecionadas[1]]);

        mTxtQ3 = (TextView) findViewById(R.id.TxtQ3);
        mTxtQ3.setText(Perguntas[PerguntasSelecionadas[2]]);


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

        final Button button = findViewById(R.id.btnEnd);
        button.setOnTouchListener(new View.OnTouchListener(){
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

            //Escrever no Arquivo
            BufferTouch = GravarArquivo(TouchEventFile, BufferTouch,unixTime + ',' + event.getEventTime() + ',' + ActivityID + ','
                    + event.getPointerCount() + ',' + '0' + ',' + eventAction + ',' + PosX + ',' + PosY + ','
                    + Press + ',' + Area + ',' + PhoneOrientation + '\n', false);

            Log.i("Diego", "Tocado");
        }
        return true;
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

