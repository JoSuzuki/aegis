package pignus.aegis;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        // Do something in response to button
        EditText editText = (EditText) findViewById(R.id.editText2);
        String test_file = new String("teste");
        String message = editText.getText().toString();
        Log.e("teste","CLIQUEI");
        File myFile = new File("/sdcard/mysdfile.txt");
        try {
            Log.e("teste",message.toString());
            myFile.createNewFile();
        } catch (Exception e) {
            Log.e("ERRR", "Could not create file",e);
        }
        try {
            FileOutputStream fOut = new FileOutputStream(myFile,true);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.write(message.toString());
            myOutWriter.flush();
            myOutWriter.close();
            fOut.close();
        } catch(Exception e) {
            Log.e("ERRR", "Could not write on file");
        }

    }
}
