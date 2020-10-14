package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;

import com.google.android.vending.expansion.zipfile.ZipResourceFile;

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

    public Drawable getPosterByTitleId(String fileName) {
        InputStream fileStream;
        try {
            fileStream = postersZip.getInputStream(fileName + ".jpeg");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return Drawable.createFromStream(fileStream, null);
    }

    public Drawable getPhotoByPersonId(String fileName) {
        InputStream fileStream;
        try {
            fileStream = photosZip.getInputStream(fileName + ".jpeg");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return Drawable.createFromStream(fileStream, null);
    }
}
