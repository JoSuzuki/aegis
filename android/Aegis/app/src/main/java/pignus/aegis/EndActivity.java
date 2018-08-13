package pignus.aegis;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class EndActivity extends AppCompatActivity {

    CollectorKeyboard mCollectorKeyboard;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        mCollectorKeyboard = new CollectorKeyboard(this, R.id.keyboardview, R.xml.collector_keyboard);

        mCollectorKeyboard.registerEditText(R.id.editText2);

    }

    @Override public void onBackPressed() {
        if( mCollectorKeyboard.isCollectorKeyboardVisible() ) mCollectorKeyboard.hideCollectorKeyboard(); else this.finish();
    }
}
