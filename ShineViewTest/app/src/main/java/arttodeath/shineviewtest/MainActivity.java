package arttodeath.shineviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ShineView shineView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        shineView = (ShineView) findViewById(R.id.shine_view);
        shineView.setOnValueChangeListener(new ShineView.OnValueChangeListener() {
            @Override
            public void onValueChanged(int value) {
                Log.d(TAG, "value:"+value);
            }
        });
    }
}
