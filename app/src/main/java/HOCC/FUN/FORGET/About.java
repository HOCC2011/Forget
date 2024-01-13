package HOCC.FUN.FORGET;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;

public class About extends AppCompatActivity {

    ImageView button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        button = (ImageView) findViewById(R.id.button);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
                final VibrationEffect vibrationEffect1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }
            }
        });
    }

    public void openNewActivity() {
        Intent intent = new Intent(About.this, MainActivity.class);
        startActivity(intent);
    }
}