package pignus.aegis;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        double prob = 0;
        int access = 0;
        Intent intent = getIntent();
        prob = intent.getDoubleExtra("PROB", 0);
        access = intent.getIntExtra("ACCESS",0);
        setContentView(R.layout.activity_access);
        View someView = findViewById(R.id.screen);
        View root = someView.getRootView();

        ImageView img = findViewById(R.id.imgView);

        if(access == 1){
            root.setBackgroundColor(0xFF00FF00);
            img.setImageResource(R.drawable.transparentright);
        }else{
            root.setBackgroundColor(0xFFFF0000);
            img.setImageResource(R.drawable.transparentwrong);
        }

        TextView txtProb = findViewById(R.id.txtProb);
        txtProb.setText(String.format("%.2f", prob * 100) + "%");

        final Button buttonLogin = findViewById(R.id.btnReturn);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentReturn = new Intent(getBaseContext(), SelectLoginActivity.class);
                intentReturn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentReturn);
            }
        });
    }
}
