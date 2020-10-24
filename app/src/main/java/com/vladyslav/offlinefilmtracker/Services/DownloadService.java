package com.vladyslav.offlinefilmtracker.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
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

import com.vladyslav.offlinefilmtracker.Activities.MainActivity;
import com.vladyslav.offlinefilmtracker.R;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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

        final Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                stopSelf();
                return null;
            }
        };

        final Handler mHandler = new Handler(Looper.getMainLooper());
        final ArrayList<String> links = intent.getStringArrayListExtra("links");
        final ProgressDialog dialog = MainActivity.progressDialog;

        //создаем поток
        (new Thread() {
            public void run() {
                for (int i = 0; i < links.size() - 1; i+=2) {
                    try {
                        URL url = new URL(links.get(i));
                        URLConnection connection = url.openConnection();
                        connection.connect();

                        final int lengthOfFile = connection.getContentLength();
                        mHandler.post(new Runnable() {
                            public void run() {
                                dialog.setMax(lengthOfFile / 1024 / 1024);
                            }
                        });

                        InputStream inputStream = url.openStream();
                        FileOutputStream outputStream = new FileOutputStream(links.get(i + 1));

                        byte data[] = new byte[1024];
                        int count = 0;
                        long total = 0;

                        while ((count = inputStream.read(data)) != -1) {
                            total += count;
                            // publishing the progress....
                            // After this onProgressUpdate will be called

                            final long finalTotal = total;
                            mHandler.post(new Runnable() {
                                public void run() {
                                    dialog.setProgress(Integer.parseInt(String.valueOf((int) (finalTotal / 1024 / 1024))));
                                }
                            });

                            // writing data to file
                            outputStream.write(data, 0, count);
                        }

                        inputStream.close();
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mHandler.post(new Runnable() {
                    public void run() {
                        dialog.hide();

                    }
                });

                try {
                    callable.call();
                    MainActivity.callable.call();
                } catch (Exception e) {
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
