package com.vladyslav.offlinefilmtracker.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.vladyslav.offlinefilmtracker.R;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.vladyslav.offlinefilmtracker.Activities.MainActivity.callableStart;
import static com.vladyslav.offlinefilmtracker.Activities.MainActivity.links;
import static com.vladyslav.offlinefilmtracker.Activities.MainActivity.progressDialog;

public class DownloadService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //запуск сервиса в фоне в зависимости от версии
        // Т.е. мы создаем уведомление и назначаем ему ID. Сервис переходит в режим неуязвимости, а в статус-баре появится уведомление.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground();
        else startForeground(1, new Notification());
    }

    //метод запуска сервиса в фоне для андроида Oreo и выше
    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.vladyslav.offlinefilmtracker.DownloadService";
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
                .setContentTitle(getString(R.string.service_title))
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.ic_app)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final Handler mHandler = new Handler(Looper.getMainLooper());

        (new Thread() {
            public void run() {
                try {
                    for (int i = 0; i < links.size(); ++i) {
                        URL url = new URL(links.get(i).first);
                        URLConnection connection = url.openConnection();
                        connection.connect();

                        InputStream inputStream = url.openStream();
                        FileOutputStream outputStream = new FileOutputStream(links.get(i).second);

                        byte data[] = new byte[1024];
                        int count = 0;
                        long total = 0;

                        //получаем максимальный размер файла
                        final int lengthOfFile = connection.getContentLength();
                        mHandler.post(new Runnable() {
                            public void run() {
                                progressDialog.setMax(lengthOfFile / 1024 / 1024);
                            }
                        });

                        //скачиваем файл
                        while ((count = inputStream.read(data)) != -1) {
                            total += count;
                            final long finalTotal = total;
                            mHandler.post(new Runnable() {
                                public void run() {
                                    progressDialog.setProgress(Integer.parseInt(String.valueOf((int) (finalTotal / 1024 / 1024))));
                                }
                            });
                            outputStream.write(data, 0, count);
                        }

                        inputStream.close();
                        outputStream.close();
                    }

                    try {
                        stopSelf();
                        callableStart.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    stopSelf();
                    mHandler.post(new Runnable() {
                        public void run() {
                            progressDialog.setTitle(getString(R.string.downloading_error));
                        }
                    });
                    e.printStackTrace();
                }
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
