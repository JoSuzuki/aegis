package pignus.aegis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.btnStart);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Diego", "Button Pressed");
                Intent intent = new Intent(getBaseContext(), SensorsActivity.class);
                startActivity(intent);
            }
        });
    }
}
