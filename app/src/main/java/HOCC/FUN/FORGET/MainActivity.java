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
import android.provider.Settings;
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

    //eng=0 chi=1
    int lan = 0;

    //list text check
    //no text=0    some text=1
    int listtf = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(MainActivity.this, Permission_request.class);
                startActivity(myIntent);
            }
        }

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

        //save task
        SharedPreferences pref=this.getSharedPreferences("MY", MODE_PRIVATE);
        if (pref.getInt("TASK", 0) == 1){
            task.setText(pref.getString("INPUT_TEXT" , ""));
            list.setText(pref.getString("LIST" , ""));
            lan = pref.getInt("LANGUAGE" , 0);
            listtf = pref.getInt("LISTTF" , 0);
            openNewActivity();
        }
    }

    //start task
    public void openNewActivity() {
        String pattern = "^[A-Za-z0-9. ]+$" ;
        String inputed = task.getText().toString();
        String listmore = list.getText().toString();
        if(inputed.matches(pattern)){
            MainActivity.this.lan = 0;
        }else {
            MainActivity.this.lan = 1;
        }
        if (listmore.matches("")) {
            MainActivity.this.listtf = 0;
        }else {
            MainActivity.this.listtf = 1;
        }
        Intent intent = new Intent(MainActivity.this, Task.class);
        intent.putExtra("input_text", task.getText().toString());
        intent.putExtra("language",lan);
        intent.putExtra("list",list.getText().toString());
        intent.putExtra("listtf", listtf);
        //restore state
        this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("TASK", 1).putString("INPUT_TEXT" , task.getText().toString()).putInt("LANGUAGE" , lan).putString("LIST" , list.getText().toString()).putInt("LISTTF" , listtf).apply();
        startActivity(intent);
    }

    //open about
    public void openNewAbout() {
        Intent intent = new Intent(MainActivity.this, About.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(MainActivity.this, Permission_request.class);
                startActivity(myIntent);
            }
        }
    }

}