package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.android.vending.expansion.zipfile.ZipResourceFile;
import com.vladyslav.offlinefilmtracker.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

//SINGLETON
public class ResourcesManager {
    private static ResourcesManager instance;
    public ZipResourceFile photosZip, postersZip;
    private String[] genres;
    private Context context;

    public ResourcesManager(Context context) {
        String obbFilePath = context.getObbDir().getPath();
        genres = context.getResources().getStringArray(R.array.genres);
        this.context = context;

        try {
            photosZip = new ZipResourceFile(obbFilePath + "/photos.zip");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
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

    public static void delete() {
        instance = null;
    }

    //получение постера по id фильма
    public BitmapDrawable getPosterDrawableById(final String fileName) {
        InputStream fileStream = null;
        try {
            fileStream = postersZip.getInputStream(fileName + ".jpeg");
            if (fileStream == null) {
                fileStream = postersZip.getInputStream("noimage_poster.jpeg");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (BitmapDrawable) BitmapDrawable.createFromStream(fileStream, null);
    }

    //получение фотографии по id актера
    public BitmapDrawable getPhotoDrawableById(final String fileName) {
        InputStream fileStream = null;
        try {
            fileStream = photosZip.getInputStream(fileName + ".jpeg");
            if (fileStream == null)
                fileStream = photosZip.getInputStream("noimage_photo.jpeg");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return (BitmapDrawable) BitmapDrawable.createFromStream(fileStream, null);
    }

    public Drawable getGenreDrawableById(int genreId) {
        //устанавливаем иконку
        Drawable drawable = null;
        try {
            int drawableId = context.getResources().getIdentifier("ic_category_" + genres[genreId-1], "drawable", context.getPackageName());
            drawable = context.getDrawable(drawableId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return drawable;
    }

    //перевод пикселей в dp
    public static int getDpFromPx(int px, double size, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((px * scale) * size);
    }

    //перевод dp в пиксели
    public static int getPxFromDp(int dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public static String getRoleById(String name, Context context) {
        try {
            Field idField = R.string.class.getDeclaredField("role_" + name);
            return context.getString(idField.getInt(idField));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
