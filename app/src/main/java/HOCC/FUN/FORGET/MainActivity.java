package HOCC.FUN.FORGET;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
    int click;

    //listing the ongoing task
    LinearLayout tasklist;

    // tts spoken language
    // eng=0 chi=1
    int lan = 0;

    //counter of ongoing tasks
    int task_num = 0;
    int ending_task = 0;

    //text of the window
    String started_text;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        setContentView(R.layout.activity_main);

        //check and request for permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                Intent myIntent = new Intent(MainActivity.this, Permission_request.class);
                startActivity(myIntent);
            }
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
                openNewActivity();
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
                    openNewActivity();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(task.getWindowToken(), 0);
                    task.clearFocus();
                }
                return false;
            }
        });

        //restore task
        SharedPreferences pref=this.getSharedPreferences("MY", MODE_PRIVATE);
        if (pref.getInt("TASK", 0) == 1){
            //putting the string -- int back to local
            started_text = (pref.getString("INPUT_TEXT" , ""));
            lan = pref.getInt("LANGUAGE" , 0);
            task_num = pref.getInt("task_num" , 0);
            Log.i("task_num", String.valueOf(task_num));
            tasklist.setVisibility(View.VISIBLE);
            //check how much task is started and set visibility by the number of tasks
            if (task_num == 1){
                task1.setVisibility(View.VISIBLE);
                task1.setText(pref.getString("task1" , ""));
            }
            else if (task_num == 2){
                task1.setVisibility(View.VISIBLE);
                task2.setVisibility(View.VISIBLE);
                task1.setText(pref.getString("task1" , ""));
                task2.setText(pref.getString("task2" , ""));
            }
            else if (task_num == 3){
                task1.setVisibility(View.VISIBLE);
                task2.setVisibility(View.VISIBLE);
                task3.setVisibility(View.VISIBLE);
                task1.setText(pref.getString("task1" , ""));
                task2.setText(pref.getString("task2" , ""));
                task3.setText(pref.getString("task3" , ""));
            }
            else if (task_num == 4){
                task1.setVisibility(View.VISIBLE);
                task2.setVisibility(View.VISIBLE);
                task3.setVisibility(View.VISIBLE);
                task4.setVisibility(View.VISIBLE);
                task1.setText(pref.getString("task1" , ""));
                task2.setText(pref.getString("task2" , ""));
                task3.setText(pref.getString("task3" , ""));
                task4.setText(pref.getString("task4" , ""));
            }
            //restore state (run)
            serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
            serviceIntent.putExtra("input_text", started_text);
            serviceIntent.putExtra("language", lan);
            stopService();
            startService();
        } //check if any task is started

        //long press to end task
        task1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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
                        ending_task = 1;
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
                return false;
            }
        });

        task2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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
                        ending_task = 2;
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
                return false;
            }
        });

        task3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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
                        ending_task = 3;
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
                return false;
            }
        });

        task4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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
                        ending_task = 4;
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
                return false;
            }
        });


        //stop for 5min
        stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
                serviceIntent.putExtra("min", 5 * 60 * 1000);
                startService();
            }
        });
    }

    //void to detect inputted words and start it
    public void openNewActivity() {
        task = findViewById(R.id.input);
        task1 = findViewById(R.id.task1);
        task2 = findViewById(R.id.task2);
        task3 = findViewById(R.id.task3);
        task4 = findViewById(R.id.task4);
        tasklist = findViewById(R.id.tasklist);
        SharedPreferences pref=this.getSharedPreferences("MY", MODE_PRIVATE);
        task_num = pref.getInt("task_num" , 0);
            //check how many task are ongoing on the same time
            if (task_num == 4) {
                CharSequence text = "Forget only supports 4 tasks at the same time.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(MainActivity.this, text, duration);
                toast.show();
            } else if (task_num == 0) {  //no task had started (one task)
                tasklist.setVisibility(View.VISIBLE);
                task1.setVisibility(View.VISIBLE);
                task1.setText(task.getText().toString()); //set the textview on the main activity
                started_text = task.getText().toString(); //set the text of the window
                task_num = task_num + 1; //add one task to the ongoing once
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task.getText().toString()).apply(); //save the text of task1 for reopening the app
            } else if(task_num == 1){  // two tasks
                task2.setVisibility(View.VISIBLE);
                task2.setText(task.getText().toString());
                task_num = task_num + 1;  //add one task to the ongoing once
                started_text = started_text + ", " + task.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task2", task.getText().toString()).apply();
            } else if(task_num == 2){  // three tasks
                task3.setVisibility(View.VISIBLE);
                task3.setText(task.getText().toString());
                //add one task to the ongoing once
                task_num = task_num + 1;
                started_text = started_text + ", " + task.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task3", task.getText().toString()).apply();
            }else if(task_num == 3){  // four tasks
                tasklist.setVisibility(View.VISIBLE);
                task4.setVisibility(View.VISIBLE);
                task4.setText(task.getText().toString());
                //add one task to the ongoing once
                task_num = task_num + 1;
                started_text = started_text + ", " + task.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task4", task.getText().toString()).apply();
            }
            //restore state
            this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("TASK", 1).putString("INPUT_TEXT", started_text).putInt("LANGUAGE", lan).putInt("task_num", task_num).apply();
            serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
            serviceIntent.putExtra("input_text", started_text);
            serviceIntent.putExtra("language", lan);
            stopService();
            startService();
            task.setText("");
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

    public void  sharedend(){
        SharedPreferences pref=this.getSharedPreferences("MY", MODE_PRIVATE);
        this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("TASK", 0).putInt("task_num", 0).apply();
    }

    public void  taskend(){
        SharedPreferences pref=this.getSharedPreferences("MY", MODE_PRIVATE);
        if (ending_task == 1){
            if (task_num == 1){
                tasklist.setVisibility(View.GONE);
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("TASK", 0).apply();
            } else if(task_num == 2){
                task1.setText(task2.getText().toString());
                task2.setVisibility(View.GONE);
                started_text = task1.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task1.getText().toString()).putString("INPUT_TEXT", started_text).apply();
            } else if(task_num == 3){
                task1.setText(task2.getText().toString());
                task2.setText(task3.getText().toString());
                task3.setVisibility(View.GONE);
                started_text = task1.getText().toString() + "," + task2.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task1.getText().toString()).putString("task2", task2.getText().toString()).putString("INPUT_TEXT", started_text).apply();
            } else if(task_num == 4){
                task1.setText(task2.getText().toString());
                task2.setText(task3.getText().toString());
                task3.setText(task4.getText().toString());
                task4.setVisibility(View.GONE);
                started_text = task1.getText().toString() + "," + task2.getText().toString() + "," + task3.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task1.getText().toString()).putString("task2", task2.getText().toString()).putString("task3", task3.getText().toString()).putString("INPUT_TEXT", started_text).apply();
            }
        } //end task 1
        if (ending_task == 2){
            if(task_num == 2){
                task2.setVisibility(View.GONE);
                started_text = task1.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task1.getText().toString()).putString("INPUT_TEXT", started_text).apply();
            } else if(task_num == 3){
                task2.setText(task3.getText().toString());
                task3.setVisibility(View.GONE);
                started_text = task1.getText().toString() + "," + task2.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task1.getText().toString()).putString("task2", task2.getText().toString()).putString("INPUT_TEXT", started_text).apply();
            } else if(task_num == 4){
                task2.setText(task3.getText().toString());
                task3.setText(task4.getText().toString());
                task4.setVisibility(View.GONE);
                started_text = task1.getText().toString() + "," + task2.getText().toString() + "," + task3.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task1.getText().toString()).putString("task2", task2.getText().toString()).putString("task3", task3.getText().toString()).putString("INPUT_TEXT", started_text).apply();
            }
        } //end task 2
        if (ending_task == 3){
           if(task_num == 3){
                task3.setVisibility(View.GONE);
                started_text = task1.getText().toString() + "," + task2.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task1.getText().toString()).putString("task2", task2.getText().toString()).putString("INPUT_TEXT", started_text).apply();
            } else if(task_num == 4){
                task3.setText(task4.getText().toString());
                task4.setVisibility(View.GONE);
                started_text = task1.getText().toString() + "," + task2.getText().toString() + "," + task3.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task1.getText().toString()).putString("task2", task2.getText().toString()).putString("task3", task3.getText().toString()).putString("INPUT_TEXT", started_text).apply();
            }
        } //end task 3
        if (ending_task == 4){
                task4.setVisibility(View.GONE);
                started_text = task1.getText().toString() + "," + task2.getText().toString() + "," + task3.getText().toString();
                this.getSharedPreferences("MY", MODE_PRIVATE).edit().putString("task1", task1.getText().toString()).putString("task2", task2.getText().toString()).putString("task3", task3.getText().toString()).putString("INPUT_TEXT", started_text).apply();
        } //end task 4
        serviceIntent = new Intent(this.getApplicationContext(), ForegroundService.class);
        serviceIntent.putExtra("input_text", started_text);
        serviceIntent.putExtra("language", lan);
        task_num = task_num - 1;
        if(task_num == 0){
            stopService();
        }
        else {
            stopService();
            startService();
        }
        this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("task_num", task_num).apply();
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