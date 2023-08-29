package HOCC.FUN.FORGET;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class Task extends AppCompatActivity implements View.OnClickListener {

    TextView task;

    TextView tog_bg;

    TextView tog;
    int time = 0 ;

    int size = 0;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.orange_light));
        setContentView(R.layout.activity_task);
        checkOverlayPermission();
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("input_text", this.getIntent().getStringExtra("input_text"));
        serviceIntent.putExtra("list", this.getIntent().getStringExtra("list"));
        serviceIntent.putExtra("language", this.getIntent().getIntExtra("language", 0));
        ImageView button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
                openNewActivity();
                final VibrationEffect vibrationEffect1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }
            }
        });

        //size
        tog_bg = findViewById(R.id.tog_bg);
        tog_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                final VibrationEffect vibrationEffect1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }
                if (size == 0) {
                    Task.this.size = 1;
                    tog_bg.setBackground(getResources().getDrawable(R.drawable.background));
                    Log.d("value entered", Float.toString(size));
                    stopService();
                    serviceIntent.putExtra("size" ,size);
                    startService();
                }
                else {
                    Task.this.size = 0;
                    tog_bg.setBackground(getResources().getDrawable(R.drawable.tog_unse));
                    Log.d("value entered", Float.toString(size));
                    stopService();
                    serviceIntent.putExtra("size" ,size);
                    startService();
                }
            }
        });

        //time
        //5min
        TextView min5_cou = findViewById(R.id.min5);
        min5_cou.setOnClickListener(this);

        //10min
        TextView min10_cou = findViewById(R.id.min10);
        min10_cou.setOnClickListener(this);
        //15min
        TextView min15_cou = findViewById(R.id.min15);
        min15_cou.setOnClickListener(this);
        //30min
        TextView min30_cou = findViewById(R.id.min30);
        min30_cou.setOnClickListener(this);
        //45min
        TextView min45_cou = findViewById(R.id.min45);
        min45_cou.setOnClickListener(this);
        //1hr
        TextView hr1_cou = findViewById(R.id.hr1);
        hr1_cou.setOnClickListener(this);
    }

    private boolean serviceStarted = false;

    // method for starting the service
    public void startService(){
        if (!this.serviceStarted) {
            stopService(serviceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // check if the user has already granted
                // the Draw over other apps permission
                if (Settings.canDrawOverlays(this)) {
                    // start the service based on the android version
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(serviceIntent);
                    } else {
                        startService(serviceIntent);
                    }
                }
            } else {
                startService(serviceIntent);
            }
            this.serviceStarted = true;
        }
    }

    private void stopService() {
        if (this.serviceStarted) {
            stopService(serviceIntent);
            this.serviceStarted = false;
        }
    }

    // method to ask user to grant the Overlay permission
    public void checkOverlayPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(myIntent);
            }
        }
    }

    TextToSpeech tts;
    boolean canSpeak;

    @Override
    protected void onResume() {
        super.onResume();
        startService();
        canSpeak = true;
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.US);
                    //tts.speak(getIntent().getStringExtra("input_text"), TextToSpeech.QUEUE_ADD, null, "test");
                }
            }
        });
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                if (utteranceId.equals("silence")) {
                    tts.speak(getIntent().getStringExtra("input_text"), TextToSpeech.QUEUE_ADD, null, "test");
                } else {
                    tts.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, "silence");
                }
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        canSpeak = false;
        tts.stop();
    }

    public void openNewActivity(){
        Intent intent = new Intent(Task.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final VibrationEffect vibrationEffect1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
            vibrator.cancel();
            vibrator.vibrate(vibrationEffect1);
        }
        if (v.getId() == R.id.min5) {
            this.time = 5*60*1000;
        } else if (v.getId() == R.id.min10) {
            this.time = 10*60*1000;
        } else if (v.getId() == R.id.min15) {
            this.time = 15*60*1000;
        } else if (v.getId() == R.id.min30) {
            this.time = 30*60*1000;
        } else if (v.getId() == R.id.min45) {
            this.time = 45*60*1000;
        } else if (v.getId() == R.id.hr1) {
            this.time = 60*60*1000;
        }
       // this.time = this.time / 60;
        stopService();
        serviceIntent.putExtra("min" ,time);
        startService();

    }
}