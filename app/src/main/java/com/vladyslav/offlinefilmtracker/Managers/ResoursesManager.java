package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.vending.expansion.zipfile.ZipResourceFile;

import java.io.IOException;
import java.io.InputStream;

public class ResoursesManager {

    private String obbFilePath;
    private ZipResourceFile expansionFile;

    public ResoursesManager(Context context) {
        obbFilePath = context.getObbDir().getPath() + "/main.1.com.vladyslav.offlinefilmtracker.zip";
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
