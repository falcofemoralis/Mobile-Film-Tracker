package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.vending.expansion.zipfile.ZipResourceFile;

import java.io.IOException;
import java.io.InputStream;

//SINGLETON
public class ResourcesManager {
    private ZipResourceFile expansionFile;
    private static ResourcesManager instance;

    public static ResourcesManager getInstance(Context context) {
        if (instance == null) {
            instance = new ResourcesManager(context);
        }
        return instance;
    }

    public ResourcesManager(Context context) {
        String obbFilePath = context.getObbDir().getPath() + "/main.1.com.vladyslav.offlinefilmtracker.zip";
        try {
            expansionFile = new ZipResourceFile(obbFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Drawable getPosterByTitleId(String fileName) {
        InputStream fileStream;
        try {
            fileStream = expansionFile.getInputStream("images/" + fileName + ".jpeg");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return Drawable.createFromStream(fileStream, null);
    }
}
