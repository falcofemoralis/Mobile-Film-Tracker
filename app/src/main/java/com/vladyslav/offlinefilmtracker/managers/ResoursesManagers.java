package com.vladyslav.offlinefilmtracker.managers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.android.vending.expansion.zipfile.ZipResourceFile;

import java.io.IOException;
import java.io.InputStream;

public class ResoursesManagers {

    private String obbFilePath;

    public ResoursesManagers(Context context) {
        obbFilePath = context.getObbDir().getPath() + "/main.1.edu.vladyslav.sqlitetest.zip";
    }

    public Drawable getPoster(String fileName) {
        InputStream fileStream;
        try {
            fileStream = getFileFromZip("images/subfolder1/" + fileName + ".jpeg");
        } catch (IOException e1) {
            e1.printStackTrace();
            try {
                fileStream = getFileFromZip("images/subfolder2/" + fileName + ".jpeg");
            } catch (IOException e2) {
                e2.printStackTrace();
                return null;
            }
        }
        return Drawable.createFromStream(fileStream, null);
    }

    private InputStream getFileFromZip(String fileName) throws IOException {
        // Get a ZipResourceFile representing a specific expansion file
        ZipResourceFile expansionFile = new ZipResourceFile(obbFilePath);

        // Get an input stream for a known file inside the expansion file ZIPs
        InputStream fileStream = expansionFile.getInputStream(fileName);
        return fileStream;
    }
}
