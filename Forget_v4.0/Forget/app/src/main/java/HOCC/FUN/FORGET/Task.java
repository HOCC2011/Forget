package HOCC.FUN.FORGET;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class Task extends AppCompatActivity {

    TextView task;

    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.orange_light));
        setContentView(R.layout.activity_task);
        checkOverlayPermission();
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("input_text", this.getIntent().getStringExtra("input_text"));
        serviceIntent.putExtra("language", this.getIntent().getIntExtra("language", 0));
        ImageView button = (ImageView) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(serviceIntent);
                openNewActivity();
            }
        });
        task=(TextView) findViewById(R.id.task);
        task.setText(getIntent().getStringExtra("input_text"));
    }

    // method for starting the service
    public void startService(){
        stopService(serviceIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if(Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            }
        }else{
            startService(serviceIntent);
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
}