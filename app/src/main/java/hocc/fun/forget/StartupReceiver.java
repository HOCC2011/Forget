package  hocc.fun.forget;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;

public class StartupReceiver extends BroadcastReceiver {

    private Intent serviceIntent;
    private boolean serviceStarted = false;
    String started_text;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences pref = context.getSharedPreferences("Forget", MODE_PRIVATE);
            if (pref.getInt("task_num", 0) > 0) {
                started_text = pref.getString("started_text", "");
                serviceIntent = new Intent(context.getApplicationContext(), ForegroundService.class);
                serviceIntent.putExtra("started_text", started_text);
                startService(context);
                pref.edit().putBoolean("taskPaused", false).apply();
            }
        }
    }

    public void startService(Context context) {
        if (!serviceStarted) {
            if (Settings.canDrawOverlays(context)) {
                Intent activityIntent = new Intent(context, PermissionRequest.class);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);
            }
            serviceStarted = true;
        }
    }
}
