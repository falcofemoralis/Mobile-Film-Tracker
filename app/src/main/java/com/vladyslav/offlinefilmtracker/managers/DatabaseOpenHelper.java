package com.vladyslav.offlinefilmtracker.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


//должен быть синглтон
public class DatabaseOpenHelper  extends SQLiteOpenHelper {
    private static final  int DATABASE_VERSION = 1;

    public DatabaseOpenHelper (Context context, String databaseName) {
        super(context,  context.getObbDir().getPath() + "/" + databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}