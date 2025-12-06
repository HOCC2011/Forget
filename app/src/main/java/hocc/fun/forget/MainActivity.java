package hocc.fun.forget;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView start;
    TextView stop;
    EditText task;
    int task_num = 0;
    int ending_task = 0;
    boolean taskPaused = false;
    String started_text;
    private Intent serviceIntent;
    RecyclerView recyclerView;
    LinearLayout tasklist;
    List<TaskItem> TaskList = new ArrayList<>();
    TaskViewAdapter adapter;


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
        TaskList.clear();
        tasklist = findViewById(R.id.tasklist);
        recyclerView = findViewById(R.id.TaskView);
        adapter = new TaskViewAdapter(TaskList, this::onItemLongClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
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
        restoreTasks();
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
            TextView ok = dialogView.findViewById(R.id.ok);
            TextView cancel = dialogView.findViewById(R.id.cancel);
            builder.setView(dialogView);
            builder.setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            time.setOnKeyListener((View keyView, int keycode, KeyEvent keyEvent) -> {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keycode == KeyEvent.KEYCODE_ENTER){
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
                }
                return false;
            });
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
    public void endDialog(View view){
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
    public void restoreTasks() {
        //restore task
        SharedPreferences pref=this.getSharedPreferences("Forget", MODE_PRIVATE);
        if (pref.getInt("task_num", 0) > 0){ //check if any task is started
            //putting the string -- int back to local
            started_text = (pref.getString("started_text", ""));
            task_num = pref.getInt("task_num", 0);
            tasklist.setVisibility(View.VISIBLE);
            //check how much task is started and set visibility by the number of tasks
            for (int i = 0; i < task_num; i++) {
                TaskList.add(new TaskItem(pref.getString("task" + (i + 1), "")));
            }
            recyclerView = findViewById(R.id.TaskView);
            adapter = new TaskViewAdapter(TaskList, this::onItemLongClick);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            if (pref.getBoolean("taskPaused", false) == false) {
                serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
                serviceIntent.putExtra("started_text", started_text);
                stopService();
                startService();
                Log.d("Restore Task", "Task Started!");
            }
        }
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
            String taskString;
            if (task.getText().toString().endsWith(" ")) {
                taskString = task.getText().toString().substring(0, task.getText().toString().length() - 1);
                Log.d("tag", taskString);
            } else {
                taskString = task.getText().toString();
            }
            TaskList.add(new TaskItem(taskString));
            recyclerView = findViewById(R.id.TaskView);
            adapter = new TaskViewAdapter(TaskList, this::onItemLongClick);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            if (task_num == 0) {
                started_text = taskString;
            } else {
                started_text = started_text + ", " + taskString;
            }
            task_num = task_num + 1;
            this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putString("task" + (task_num), taskString).apply();
        }
        this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putString("started_text", started_text).putInt("task_num", task_num).putBoolean("taskPaused", taskPaused).apply();
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("started_text", started_text);
        stopService();
        startService();
        task.setText("");
    }
    public void endTask() {
        if (task_num == 1 && ending_task == 1) {
            tasklist.setVisibility(View.GONE);
            TaskList.clear();
            task_num = 0;
        } else {
            TaskList.remove(ending_task - 1);
            recyclerView = findViewById(R.id.TaskView);
            adapter = new TaskViewAdapter(TaskList, this::onItemLongClick);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            for (int i = ending_task; i < task_num; i++) {
                SharedPreferences pref=this.getSharedPreferences("Forget", MODE_PRIVATE);
                this.getSharedPreferences("Forget", MODE_PRIVATE).edit().putString("task" + (i), pref.getString("task" + (i+1), "")).apply();
            }
            task_num = task_num - 1;
            // Update the started_text based on the remaining visible tasks
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < task_num; i++) {
                if (i > 0) sb.append(", ");
                SharedPreferences pref=this.getSharedPreferences("Forget", MODE_PRIVATE);
                sb.append(pref.getString("task" + (i+1), ""));
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
        this.getSharedPreferences("Forget", MODE_PRIVATE).edit()
                .putInt("task_num", task_num)
                .putString("started_text", started_text)
                .putBoolean("taskPaused", taskPaused)
                .apply();
    }
    public boolean onItemLongClick(View view, TaskItem item, int position) {
        //Toast.makeText(this, "Clicked: " + item.getTaskString() + ", item Number: " + position , Toast.LENGTH_SHORT).show();
        ending_task = position + 1;
        endDialog(view);
        return true;
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