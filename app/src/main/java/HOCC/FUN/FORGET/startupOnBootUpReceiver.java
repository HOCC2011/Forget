package HOCC.FUN.FORGET;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class startupOnBootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Intent activityIntent = new Intent(context, StartUpView.class);

            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(activityIntent);
        }
    }
}
