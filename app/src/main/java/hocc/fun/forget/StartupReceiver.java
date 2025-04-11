package  hocc.fun.forget;

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
            SharedPreferences pref = context.getSharedPreferences("Forget", Context.MODE_PRIVATE);
            if (pref.getInt("task_num", 0) > 0) {
                started_text = pref.getString("started_text", "");
                if (pref.getInt("isRunning", 0) == 1) {
                    serviceIntent = new Intent(context.getApplicationContext(), ForegroundService.class);
                    serviceIntent.putExtra("started_text", started_text);
                    startService(context);
                }
            }
        }
    }

    public void startService(Context context) {
        if (!serviceStarted) {
            if (Settings.canDrawOverlays(context)) {
                context.startForegroundService(serviceIntent);
            }
            serviceStarted = true;
        }
    }
}
