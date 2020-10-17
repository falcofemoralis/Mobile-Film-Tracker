package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.android.vending.expansion.zipfile.ZipResourceFile;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

//SINGLETON
public class ResourcesManager {
    public ZipResourceFile photosZip, postersZip;
    private static ResourcesManager instance;

    public static ResourcesManager getInstance(Context context) {
        if (instance == null) {
            instance = new ResourcesManager(context);
        }
        return instance;
    }

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

    //получение постера по id фильма или фотографии по id актера
    public Drawable getDrawableById(final String fileName, final boolean type) {
        InputStream fileStream = null;

        try {
            if (type) fileStream = postersZip.getInputStream(fileName + ".jpeg");
            else fileStream = photosZip.getInputStream(fileName + ".jpeg");
        } catch (IOException e) {
            e.printStackTrace();
            fileStream = null;
        }

        return Drawable.createFromStream(fileStream, null);
    }
}
