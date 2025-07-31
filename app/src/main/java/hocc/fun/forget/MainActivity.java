package hocc.fun.forget;

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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView start;
    TextView stop;
    TextView task1;
    TextView task2;
    TextView task3;
    TextView task4;
    EditText task;
    LinearLayout tasklist;
    int task_num = 0;
    int ending_task = 0;
    boolean taskPaused = false;
    String started_text;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //check and request for permission
        if (!Settings.canDrawOverlays(this)) {
            // send user to the device settings
            Intent myIntent = new Intent(MainActivity.this, PermissionRequest.class);
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
        start.setOnClickListener(v -> {
            startTask();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(task.getWindowToken(), 0);
            task.clearFocus();
        });
        task = findViewById(R.id.input);
        //set edittext
        task.setOnKeyListener((view, keycode, keyEvent) -> {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keycode == KeyEvent.KEYCODE_ENTER){
                startTask();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(task.getWindowToken(), 0);
                task.clearFocus();
            }
            return false;
        });
        //restore task
        SharedPreferences pref=this.getSharedPreferences("Forget", MODE_PRIVATE);
        if (pref.getInt("task_num", 0) > 0){ //check if any task is started
            //putting the string -- int back to local
            started_text = (pref.getString("started_text", ""));
            task_num = pref.getInt("task_num", 0);
            tasklist.setVisibility(View.VISIBLE);
            //check how much task is started and set visibility by the number of tasks
            TextView[] tasks = {task1, task2, task3, task4};
            for (int i = 0; i < task_num; i++) {
                tasks[i].setVisibility(View.VISIBLE);
                tasks[i].setText(pref.getString("task" + (i + 1), ""));
            }
            if (pref.getBoolean("taskPaused", false) == false) {
                serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
                serviceIntent.putExtra("started_text", started_text);
                stopService();
                startService();
                Log.d("Restore Task", "Task Started!");
            }
        }
        //long press to end task
        task1.setOnLongClickListener(view -> {
            ending_task = 1;
            EndDialog(view);
            return false;
        });
        task2.setOnLongClickListener(view -> {
            ending_task = 2;
            EndDialog(view);
            return false;
        });
        task3.setOnLongClickListener(view -> {
            ending_task = 3;
            EndDialog(view);
            return false;
        });
        task4.setOnLongClickListener(view -> {
            ending_task = 4;
            EndDialog(view);
            return false;
        });
        //stop for 5min
        stop = findViewById(R.id.stop);
        stop.setOnClickListener(v -> {
            stopService();
            serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
            serviceIntent.putExtra("started_text", started_text);
            serviceIntent.putExtra("min", 5 * 60 * 1000);
            startService();
            taskPaused = true;
            this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putBoolean("taskPaused", taskPaused).apply();
        });
        stop.setOnLongClickListener(view -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.stop_task_dia, viewGroup, false);
            EditText time = dialogView.findViewById(R.id.time);
            TextView ok =dialogView.findViewById(R.id.ok);
            TextView cancel =dialogView.findViewById(R.id.cancel);
            builder.setView(dialogView);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            ok.setOnClickListener(v -> {
                try {
                    int stop_time = Integer.parseInt(time.getText().toString());
                    alertDialog.dismiss();
                    stopService();
                    serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
                    serviceIntent.putExtra("started_text", started_text);
                    serviceIntent.putExtra("min", stop_time * 60 * 1000);
                    startService();
                    taskPaused = true;
                    this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putBoolean("taskPaused", taskPaused).apply();
                } catch(NumberFormatException nfe) {
                    Log.d("Cannot turn string to int (stop_time)", nfe.toString());
                    CharSequence text = "Please enter an integer.";
                    Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();

                }
            });
            cancel.setOnClickListener(v -> alertDialog.dismiss());
            alertDialog.show();
            return false;
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
        end.setOnClickListener(v -> {
            endTask();
            alertDialog.dismiss();
        });
        cancel.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

    public void startTask() { //void to detect inputted words and start it
        tasklist.setVisibility(View.VISIBLE);
        SharedPreferences pref=this.getSharedPreferences("Forget", MODE_PRIVATE);
        taskPaused = false;
        task_num = pref.getInt("task_num" , 0);
        //check how many task are ongoing on the same time
        if (task_num == 4) {
            CharSequence text = "Forget only supports 4 tasks at the same time.";
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        } else {
            tasklist.setVisibility(View.VISIBLE);
            TextView[] tasks = {task1, task2, task3, task4};
            tasks[task_num].setVisibility(View.VISIBLE);
            String taskString;
            if (task.getText().toString().endsWith(" ")) {
                taskString = task.getText().toString().substring(0, task.getText().toString().length() - 1);
                Log.d("tag", taskString);
            } else {
                taskString = task.getText().toString();
            }
            tasks[task_num].setText(taskString);
            if (task_num == 0) {
                started_text = taskString;
            } else {
                started_text = started_text + ", " + taskString;
            }
            task_num = task_num + 1;
            this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putString("task" + (task_num), taskString).apply();
        }
        //restore state
        this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putString("started_text", started_text).putInt("task_num", task_num).putBoolean("taskPaused", taskPaused).apply();
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("started_text", started_text);
        stopService();
        startService();
        task.setText("");
    }
    public void  endTask(){
        TextView[] tasks = {task1, task2, task3, task4};
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
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("started_text", started_text);
        if(task_num == 0){
            stopService();
        }
        else {
            stopService();
            startService();
        }
        taskPaused = false;
        this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putInt("task_num", task_num).putString("task1", task1.getText().toString()).putString("task2", task2.getText().toString()).putString("task3", task3.getText().toString()).putString("task4", task4.getText().toString()).putString("started_text", started_text).putBoolean("taskPaused", taskPaused).apply();
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
    @Override
    protected void onResume() {
        super.onResume();
        if (!Settings.canDrawOverlays(this)) {
            // send user to the device settings
            Intent myIntent = new Intent(MainActivity.this, PermissionRequest.class);
            startActivity(myIntent);
        }
    }
}