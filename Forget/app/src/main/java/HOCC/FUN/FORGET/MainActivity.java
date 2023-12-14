package HOCC.FUN.FORGET;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity {
    ImageView button;
    ImageView info;
    EditText task;
    EditText list;

    ImageView Chinese;
    TextView Chinese_text;

    ImageView English;
    TextView English_text;

    //eng=0 chi=1
    int lan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.activity_main);

        //task open+intent
        button = (ImageView) findViewById(R.id.button);
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

        //about open
        info = findViewById(R.id.button_about);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewAbout();
                final VibrationEffect vibrationEffect1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }
            }
        });

        //set edittext
        task = findViewById(R.id.input);
        list = findViewById(R.id.list);

        //language switcher
        English = findViewById(R.id.english);
        English_text = findViewById(R.id.english_text);
        Chinese_text = findViewById(R.id.chinese_text);
        Chinese = findViewById(R.id.chinese) ;

        //switch chinese
        Chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final VibrationEffect vibrationEffect1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }
                //tts language var
                MainActivity.this.lan = 1;
                //ui change
                Chinese_text.setTextColor(getColor(R.color.orange_light));
                English_text.setTextColor(getColor(R.color.white));
                Chinese.setColorFilter(Chinese.getContext().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                English.setColorFilter(English.getContext().getResources().getColor(R.color.orange_light), PorterDuff.Mode.SRC_ATOP);
            }
        });

        //switch english
        English .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final VibrationEffect vibrationEffect1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }
                //tts language var (default = 0 english)
                MainActivity.this.lan = 0;
                English_text.setTextColor(getColor(R.color.orange_light));
                Chinese_text.setTextColor(getColor(R.color.white));
                English.setColorFilter(English.getContext().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                Chinese.setColorFilter(Chinese.getContext().getResources().getColor(R.color.orange_light), PorterDuff.Mode.SRC_ATOP);
            }
        });

        //safe task
        SharedPreferences pref=this.getSharedPreferences("MY", MODE_PRIVATE);
        if (pref.getInt("TASK", 0) == 1){
            task.setText(pref.getString("INPUT_TEXT" , ""));
            list.setText(pref.getString("LIST" , ""));
            lan = pref.getInt("LANGUAGE" , 0);
            openNewActivity();
        }
    }

    //start task
    public void openNewActivity() {
        Intent intent = new Intent(MainActivity.this, Task.class);
        intent.putExtra("input_text", task.getText().toString());
        intent.putExtra("language",lan);
        intent.putExtra("list",list.getText().toString());
        //restore state
        this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("TASK", 1).putString("INPUT_TEXT" , task.getText().toString()).putInt("LANGUAGE" , lan).putString("LIST" , list.getText().toString()).apply();
        startActivity(intent);
    }

    //open about
    public void openNewAbout() {
        Intent intent = new Intent(MainActivity.this, About.class);
        startActivity(intent);
    }

}