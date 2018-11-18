package pignus.aegis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

public class SelectLoginActivity extends AppCompatActivity implements HttpCallbackInterface {

    public static final String USER_NAME = "USER_NAME";
    public static final String SESSION_NUMBER = "SESSION_NUMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_login);

        final Spinner s = (Spinner) findViewById(R.id.sprLogin);
        final EditText txtSessionNumber = (EditText) findViewById(R.id.editSessionNumber);

        SelectLoginActivity selectLoginActivity = this;

        Http http = new Http(selectLoginActivity, 1);
        http.execute();

        CollectorKeyboard mCollectorKeyboard = new CollectorKeyboard(this, R.id.keyboardview2, R.xml.collector_keyboard, "selectLoginActivity", false);
        mCollectorKeyboard.registerEditText(R.id.editSessionNumber);

        final Button buttonLogin = findViewById(R.id.btnLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                intent.putExtra(USER_NAME, s.getSelectedItem().toString());
                intent.putExtra(SESSION_NUMBER,txtSessionNumber.getText().toString());

                startActivity(intent);
            }
        });
    }

    @Override
    public void Callback(String response) {
        String[] arraySpinner = (response)
                .replace("\"", "")
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "")
                .split(",");
        final Spinner s = (Spinner) findViewById(R.id.sprLogin);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        s.setAdapter(adapter);
    }

    public void errorCallback(){
    }
}
