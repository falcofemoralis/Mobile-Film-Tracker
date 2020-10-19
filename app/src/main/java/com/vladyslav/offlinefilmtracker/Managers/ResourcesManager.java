package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.vending.expansion.zipfile.ZipResourceFile;

import java.io.IOException;
import java.io.InputStream;

//SINGLETON
public class ResourcesManager {
    private static ResourcesManager instance;
    public ZipResourceFile photosZip, postersZip;

    public ResourcesManager(Context context) {
        //  String obbFilePath = context.getObbDir().getPath() + "/main.1.com.vladyslav.offlinefilmtracker.obb";
        String obbFilePath = context.getObbDir().getPath();
        try {
            photosZip = new ZipResourceFile(obbFilePath + "/photos.zip");
            postersZip = new ZipResourceFile(obbFilePath + "/posters.zip");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ResourcesManager getInstance(Context context) {
        if (instance == null) {
            instance = new ResourcesManager(context);
        }
        return instance;
    }

    //получение постера по id фильма или фотографии по id актера
    public BitmapDrawable getDrawableById(final String fileName, final boolean type) {
        InputStream fileStream = null;

        try {
            if (type) fileStream = postersZip.getInputStream(fileName + ".jpeg");
            else fileStream = photosZip.getInputStream(fileName + ".jpeg");

            if (fileStream == null) {
                if (type) fileStream = postersZip.getInputStream("noimage_poster.jpeg");
                else fileStream = photosZip.getInputStream("noimage_photo.jpeg");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (BitmapDrawable) BitmapDrawable.createFromStream(fileStream, null);
    }

    //перевод пикселей  в dp
    public static int getDpFromPx(int px, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((px - 0.5f) / scale);
    }

    //перевод dp в пиксели
    public static int getPxFromDp(int dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
