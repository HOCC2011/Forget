package hocc.fun.forget;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ImportView extends AppCompatActivity {
    TextView text;
    String callApp;
    String import_text;
    String import_text_end;
    TextView import_button;
    int task_num = 0;
    int isRunning = 0;
    String started_text;
    String task;
    private Intent serviceIntent;
    Intent intent;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_import_view);
        intent = getIntent();
        callApp = intent.getStringExtra("callApp");
        task = intent.getStringExtra("task");
        import_text = getString(R.string.import_text);
        import_text_end = getString(R.string.import_text_end);
        text = findViewById(R.id.text);
        text.setText(import_text + callApp + import_text_end);
        import_button = findViewById(R.id.import_button);
        import_button.setOnClickListener(v -> {
            startTask();
            Intent redirectToHome = new Intent(ImportView.this, MainActivity.class);
            startActivity(redirectToHome);
        });
    }
    public void startTask() { //void to detect inputted words and start it
        SharedPreferences pref=this.getSharedPreferences("Forget", MODE_PRIVATE);
        task_num = pref.getInt("task_num" , 0);
        isRunning = 1;
        started_text = (pref.getString("started_text", ""));
        //check how many task are ongoing on the same time
        if (task_num == 4) {
            CharSequence text = "There are already 4 tasks running.";
            Toast.makeText(ImportView.this, text, Toast.LENGTH_SHORT).show();
        } else {
            if (task_num == 0) {
                started_text = task;
            } else {
                started_text = started_text + ", " + task;
            }
            task_num = task_num + 1;
            this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putString("task" + (task_num), task).apply();
        }
        //restore state
        this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putString("started_text", started_text).putInt("task_num", task_num).putInt("isRunning", isRunning).apply();
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("started_text", started_text);
        stopService();
        startService();
    }
    private boolean serviceStarted = false;
    public void startService() { // method for starting the service
        if (!this.serviceStarted) {
            stopService(serviceIntent);
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
                // start the service based on the android version
                startForegroundService(serviceIntent);
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