package pignus.aegis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String USER_NAME = "USER_NAME";
    EditText editText;
    CollectorKeyboard mCollectorKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.editTextName);
        mCollectorKeyboard = new CollectorKeyboard(this, R.id.keyboardview2, R.xml.collector_keyboard, "mainActivity", false);
        mCollectorKeyboard.registerEditText(R.id.editTextName);
        final Button button = findViewById(R.id.btnStart);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Diego", "Button Pressed");

                Intent intent = new Intent(getBaseContext(), CollectorActivity.class);
                intent.putExtra(USER_NAME, editText.getText().toString());

                startActivity(intent);
            }
        });

        final Button buttonLogin = findViewById(R.id.btnLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Diego", "Button Pressed");

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.putExtra(USER_NAME, editText.getText().toString());

                startActivity(intent);
            }
        });
    }
}
