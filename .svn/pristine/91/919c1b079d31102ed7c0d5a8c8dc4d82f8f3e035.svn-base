package i2r.astar.edu.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import i2r.astar.edu.database.R;
import i2r.astar.edu.ui.MainMenuUI;

public class ForegroundService extends Service {

    public static final int ONGOING_NOTIFICATION_ID = 100;
    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        // Set Foreground
        Intent notificationIntent = new Intent(this, MainMenuUI.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification.Builder objBuilder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.img_astar).setContentTitle("On Device Analytics")
                .setContentText("Data Collection in Progress").setTicker("Collection Started!");

        objBuilder.setContentIntent(pendingIntent);
        startForeground(ONGOING_NOTIFICATION_ID, objBuilder.build());
    }
}
