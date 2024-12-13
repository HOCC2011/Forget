package HOCC.FUN.FORGET;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;

public class StartUpView extends AppCompatActivity {

    int lan = 0;

    //counter of ongoing tasks
    int task_num = 0;

    //text of the window
    String started_text;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_view);
        SharedPreferences pref=this.getSharedPreferences("MY", MODE_PRIVATE);
        if (pref.getInt("task_num", 0) > 0){
            //putting the string -- int back to local
            started_text = (pref.getString("INPUT_TEXT", ""));
            lan = pref.getInt("LANGUAGE", 0);
            task_num = pref.getInt("task_num", 0);
            Log.i("task_num", String.valueOf(task_num));
            this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("isRunning", 1).apply();
            serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
            serviceIntent.putExtra("input_text", started_text);
            serviceIntent.putExtra("language", lan);
            stopService();
            startService();
        } //check if any task is started
        if (pref.getInt("TASK", 0) == 1){
            //putting the string -- int back to local
            started_text = (pref.getString("INPUT_TEXT" , ""));
            lan = pref.getInt("LANGUAGE" , 0);
            task_num = pref.getInt("task_num" , 0);
            Log.i("task_num", String.valueOf(task_num));
            //restore state
            serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
            serviceIntent.putExtra("input_text", started_text);
            serviceIntent.putExtra("language", lan);
            stopService();
            startService();
        } //check if any task is started
        new CountDownTimer(100, 100) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                finishAffinity();
            }
        }.start();
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

}