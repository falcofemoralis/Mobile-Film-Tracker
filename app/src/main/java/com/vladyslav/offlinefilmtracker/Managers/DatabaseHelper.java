package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    public SQLiteDatabase database;

    public DatabaseHelper(Context context, String databaseName) {
        super(context, context.getObbDir().getPath() + "/" + databaseName, null, DATABASE_VERSION);
        database = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor runSQLQuery(String query){
        return database.rawQuery(query, null);
    }
}