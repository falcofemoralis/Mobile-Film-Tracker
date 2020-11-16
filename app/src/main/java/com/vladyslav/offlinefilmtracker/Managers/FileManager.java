package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {
    public static ArrayList<String> read(Context context) {
        String FILE_PATH = context.getFilesDir() + "/bookmarks";
        ArrayList<String> IDs = new ArrayList<>();
        try {
            Scanner fIn = new Scanner(new BufferedInputStream(new FileInputStream(FILE_PATH)));
            while (fIn.hasNextLine()) {
                IDs.add(fIn.nextLine());
            }
            fIn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return IDs;
    }

    public static void write(Context context, ArrayList<String> filmId) {
        String FILE_PATH = context.getFilesDir() + "/bookmarks";
        if (filmId != null) {
            try {
                PrintWriter fOut = new PrintWriter(new BufferedOutputStream(new FileOutputStream(FILE_PATH)));
                for (String id : filmId) {
                    fOut.write(id + "\n");
                }

                fOut.flush();
                fOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}

