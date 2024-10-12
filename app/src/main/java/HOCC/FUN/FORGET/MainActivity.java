package HOCC.FUN.FORGET;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    //buttons
    TextView start;
    TextView stop;
    TextView task1;
    TextView task2;
    TextView task3;
    TextView task4;
    EditText task;
    LinearLayout tasklist; //listing the ongoing task
    int lan = 0; // tts spoken language eng=0 chi=1
    //counter of ongoing tasks
    int task_num = 0;
    int ending_task = 0;
    int isRunning = 0;
    //text of the window
    String started_text;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check and request for permission
        if (!Settings.canDrawOverlays(this)) {
            // send user to the device settings
            Intent myIntent = new Intent(MainActivity.this, Permission_request.class);
            startActivity(myIntent);
        }
        //finding the view of the list for the ongoing task
        task1 = findViewById(R.id.task1);
        task2 = findViewById(R.id.task2);
        task3 = findViewById(R.id.task3);
        task4 = findViewById(R.id.task4);
        tasklist = findViewById(R.id.tasklist);
        //start the task activity
        start = findViewById(R.id.start);
        task = findViewById(R.id.input);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTask();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(task.getWindowToken(), 0);
                task.clearFocus();
            }
        });
        //set edittext
        task.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keycode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keycode == KeyEvent.KEYCODE_ENTER){
                    startTask();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(task.getWindowToken(), 0);
                    task.clearFocus();
                }
                return false;
            }
        });
        //restore task
        SharedPreferences pref=this.getSharedPreferences("MY", MODE_PRIVATE);
        if (pref.getInt("task_num", 0) > 0){
            //putting the string -- int back to local
            started_text = (pref.getString("INPUT_TEXT", ""));
            lan = pref.getInt("LANGUAGE", 0);
            task_num = pref.getInt("task_num", 0);
            Log.i("task_num", String.valueOf(task_num));
            tasklist.setVisibility(View.VISIBLE);
            //check how much task is started and set visibility by the number of tasks
            TextView[] tasks = {task1, task2, task3, task4};
            for (int i = 0; i < task_num; i++) {
                tasks[i].setVisibility(View.VISIBLE);
                tasks[i].setText(pref.getString("task" + (i + 1), ""));
            }
            if (pref.getInt("isRunning", 0) == 1) {
                //restore state (run)
                serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
                serviceIntent.putExtra("input_text", started_text);
                serviceIntent.putExtra("language", lan);
                stopService();
                startService();
            }
        } //check if any task is started
        task1.setOnLongClickListener(new View.OnLongClickListener() { //long press to end task
            @Override
            public boolean onLongClick(View view) {
                ending_task = 1;
                EndDialog(view);
                return false;
            }
        });
        task2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ending_task = 2;
                EndDialog(view);
                return false;
            }
        });
        task3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ending_task = 3;
                EndDialog(view);
                return false;
            }
        });
        task4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ending_task = 4;
                EndDialog(view);
                return false;
            }
        });
        //stop for 5min
        stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTask();
            }
        });
    }

    public void EndDialog(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.end_task_dia, viewGroup, false);
        TextView end =dialogView.findViewById(R.id.end);
        TextView cancel =dialogView.findViewById(R.id.cancel);
        builder.setView(dialogView);
        builder.setView(dialogView);
        final AlertDialog alertDialog = builder.create();
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskend();
                alertDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    public void stopTask() {
        stopService();
        serviceIntent.putExtra("min", 5 * 60 * 1000);
        isRunning = 0;
        this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("isRunning", isRunning).apply();
        startService();
    }
    public void startTask() { //void to detect inputted words and start it
        task = findViewById(R.id.input);
        task1 = findViewById(R.id.task1);
        task2 = findViewById(R.id.task2);
        task3 = findViewById(R.id.task3);
        task4 = findViewById(R.id.task4);
        tasklist = findViewById(R.id.tasklist);
        tasklist.setVisibility(View.VISIBLE);
        SharedPreferences pref=this.getSharedPreferences("MY", MODE_PRIVATE);
        task_num = pref.getInt("task_num" , 0);
        isRunning = 1;
        //check how many task are ongoing on the same time
        if (task_num == 4) {
            CharSequence text = "Forget only supports 4 tasks at the same time.";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        } else {
            tasklist.setVisibility(View.VISIBLE);
            TextView[] tasks = {task1, task2, task3, task4};
            tasks[task_num].setVisibility(View.VISIBLE);
            tasks[task_num].setText(task.getText().toString());
            if (task_num == 0) {
                started_text = task.getText().toString();
            } else {
                started_text = started_text + ", " + task.getText().toString();
            }
            task_num = task_num + 1;
            this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task" + (task_num), task.getText().toString()).apply();
        }
        //restore state
        this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("INPUT_TEXT", started_text).putInt("LANGUAGE", lan).putInt("task_num", task_num).putInt("isRunning", isRunning).apply();
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("input_text", started_text);
        serviceIntent.putExtra("language", lan);
        stopService();
        startService();
        task.setText("");
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
    public void  taskend(){
        TextView[] tasks = {task1, task2, task3, task4};
        //if (ending_task >= 1 && ending_task <= 4) {
            if (task_num == 1 && ending_task == 1) {
                tasklist.setVisibility(View.GONE);
                task_num = 0;
            } else {
                for (int i = ending_task - 1; i < task_num - 1; i++) {
                    tasks[i].setText(tasks[i + 1].getText().toString());
                }
                tasks[task_num - 1].setVisibility(View.GONE);
                task_num = task_num - 1;
                // Update the started_text based on the remaining visible tasks
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < task_num; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(tasks[i].getText().toString());
                }
                started_text = sb.toString();
            }
        //}
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("input_text", started_text);
        serviceIntent.putExtra("language", lan);
        if(task_num == 0){
            stopService();
            isRunning = 0;
        }
        else {
            stopService();
            startService();
        }
        this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("task_num", task_num).putInt("isRunning", isRunning) .putString("task1", task1.getText().toString()).putString("task2", task2.getText().toString()).putString("task3", task3.getText().toString()).putString("task4", task4.getText().toString()).putString("INPUT_TEXT", started_text).apply();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (!Settings.canDrawOverlays(this)) {
            // send user to the device settings
            Intent myIntent = new Intent(MainActivity.this, Permission_request.class);
            startActivity(myIntent);
        }
    }
}