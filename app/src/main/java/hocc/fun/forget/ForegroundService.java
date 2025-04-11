package hocc.fun.forget;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class ForegroundService extends Service {

    private Window window;
    private String task;
    private int time;
    CountDownTimer countDownTimer;

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task= intent.getStringExtra("started_text");
        time = intent.getIntExtra("min" , 0);
        startMyOwnForeground();
        window=new Window(this, task);
        window.open();
        window.close();
        window.open();
        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
            this.countDownTimer = null;
        }
        if (time > 0) {
            this.countDownTimer = new CountDownTimer(time, 1000) {

                public void onTick(long millisUntilFinished) {
                    Log.i("test", "countDownTimer.onTick");
                    window.close();
                }

                public void onFinish() {
                    Log.i("test", "countDownTimer.onFinish");
                    window.close();
                    window.open();
                    ForegroundService.this.time = 0;
                    stopFinish();
                }
            }.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void stopFinish(){
        this.getSharedPreferences("MY", MODE_PRIVATE).edit().putInt("isRunning", 1).apply();
    }

    @Override
    public void onDestroy() {
        window.close();
        if (this.countDownTimer != null) {
            this.countDownTimer.cancel();
            this.countDownTimer = null;
        }
        super.onDestroy();
    }
    
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Task";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .build();
        startForeground(2, notification);
    }
}
