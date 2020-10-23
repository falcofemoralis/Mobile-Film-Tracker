package com.vladyslav.offlinefilmtracker.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.vladyslav.offlinefilmtracker.Activities.MainActivity;
import com.vladyslav.offlinefilmtracker.Managers.DownloadHelper;
import com.vladyslav.offlinefilmtracker.R;

import java.util.concurrent.Callable;

public class DownloadService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //запуск сервис в фоне в зависимости от версии Т.е. мы создаем уведомление и назначаем ему ID. Сервис переходит в режим неуязвимости, а в статус-баре появится уведомление.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground();
        else startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.vladyslav.offlinefilmtracker.Download";
        String channelName = "Download Service";

        //создаем канал для уведомлений
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        //создаем уведомление (то что в верхней части экрана)
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_app)
                .build();
        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                stopSelf();
                return null;
            }
        };

        String path = getApplicationContext().getObbDir().getPath() + "/";
        new DownloadHelper(MainActivity.progressDialog, MainActivity.callable, callable)
                .execute("https://dl.dropboxusercontent.com/s/7f0ftnfup1bmtjn/photos.zip?dl=0", path + "photos.zip"
                        , "http://dl.dropboxusercontent.com/s/asm7y6quur0xl7n/imdb.db?dl=0", path + "imdb.db"
                        , "https://dl.dropboxusercontent.com/s/92vwcr52oqdtrrj/posters.zip?dl=0", path + "posters.zip");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
