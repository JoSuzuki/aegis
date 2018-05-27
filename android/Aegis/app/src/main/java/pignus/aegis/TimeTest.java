package pignus.aegis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

public class TimeTest extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_test);
        TextView mTextView = (TextView) findViewById(R.id.TesteTempo);
        mTextView.setText("Welcome to Dynamic TextView");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        String unixTime = Long.toString(System.currentTimeMillis());
        int eventAction = event.getAction();

        if (eventAction == MotionEvent.ACTION_DOWN){
            TextView mTextView = (TextView) findViewById(R.id.TesteTempo);
            mTextView.setText(unixTime);
        }
        return true;
    }
}

