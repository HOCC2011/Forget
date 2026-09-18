package hocc.fun.forget;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ResumeTaskWorker extends Worker {
    private Intent serviceIntent;
    String started_text;
    private boolean serviceStarted = false;
    private final Context context;

    public ResumeTaskWorker(@NonNull Context contextReceived, @NonNull WorkerParameters workerParams) {
        super(contextReceived, workerParams);
        context = contextReceived;
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences pref = context.getSharedPreferences("Forget", MODE_PRIVATE);
        if (pref.getInt("task_num", 0) > 0) {
            started_text = pref.getString("started_text", "");
            serviceIntent = new Intent(context.getApplicationContext(), ForegroundService.class);
            serviceIntent.putExtra("started_text", started_text);
            startService(context);
            pref.edit().putBoolean("taskPaused", false).apply();
        }
        return Result.success();
    }

    public void startService(Context context) {
        if (!serviceStarted) {
            if (!Settings.canDrawOverlays(context)) {
                Intent activityIntent = new Intent(context, PermissionRequest.class);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);
            } else {
                Intent activityIntent = new Intent(context, MainActivity.class);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);
            }
            serviceStarted = true;
        }
    }
}