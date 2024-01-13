package HOCC.FUN.FORGET;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import java.util.Locale;

public class Task extends AppCompatActivity {

    TextView tog_bg;

    TextView tog;

    TextView number;

    TextView add;

    TextView minus;

    ImageView start;

    int time = 0;

    int counter = 15;
    int size = 0;

    int listtf = 0;

    private Intent serviceIntent;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.activity_task);
        number = findViewById(R.id.incad);
        checkOverlayPermission();
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("input_text", this.getIntent().getStringExtra("input_text"));
        serviceIntent.putExtra("list", this.getIntent().getStringExtra("list"));
        serviceIntent.putExtra("language", this.getIntent().getIntExtra("language", 0));
        serviceIntent.putExtra("listtf", this.getIntent().getIntExtra("listtf", 0));
        listtf = this.getIntent().getIntExtra("listtf", 1);
        if (listtf == 0) {
            Task.this.size = 1;
            Task.this.getSharedPreferences("NE", MODE_PRIVATE).edit().putInt("Size", size).apply();
            serviceIntent.putExtra("size", size);
        } else {
            Task.this.size = 0;
            Task.this.getSharedPreferences("NE", MODE_PRIVATE).edit().putInt("Size", size).apply();
            serviceIntent.putExtra("size", size);
        }
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

        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final VibrationEffect vibrationEffect1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }
                counter = Integer.parseInt(number.getText().toString());
                if (counter == 99) {
                    CharSequence text = "Forget only support timer between 1 to 99 minutes";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(Task.this /* MyActivity */, text, duration);
                    toast.show();
                } else {
                    number.setText(String.valueOf(counter + 1));
                }
            }
        });

        minus = findViewById(R.id.minus);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final VibrationEffect vibrationEffect1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }
                counter = Integer.parseInt(number.getText().toString());
                if (counter == 1) {
                    CharSequence text = "Forget only support timer between 1 to 99 minutes";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(Task.this /* MyActivity */, text, duration);
                    toast.show();
                } else {
                    number.setText(String.valueOf(counter - 1));
                }
            }
        });

        start = findViewById(R.id.startbtn);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                final VibrationEffect vibrationEffect1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrationEffect1 = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK);
                    vibrator.cancel();
                    vibrator.vibrate(vibrationEffect1);
                }
                time = counter * 60 * 1000;
                //time = time / 60;
                stopService();
                serviceIntent.putExtra("min", time);
                startService();
            }
        });
    }

    private boolean serviceStarted = false;

    // method for starting the service
    public void startService() {
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
    public void checkOverlayPermission() {

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

    public void openNewActivity() {
        Intent intent = new Intent(Task.this, MainActivity.class);
        this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("TASK", 0).apply();
        Task.this.size = 0;
        Task.this.getSharedPreferences("NE", MODE_PRIVATE).edit().putInt("Size", size).apply();
        startActivity(intent);
    }

}