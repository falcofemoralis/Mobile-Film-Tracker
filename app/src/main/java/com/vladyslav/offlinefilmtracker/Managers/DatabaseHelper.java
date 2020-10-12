package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "imdb.db";
    private SQLiteDatabase database;
    private static DatabaseHelper instance;

    public DatabaseHelper(Context context) {
        super(context, context.getObbDir().getPath() + "/" + DATABASE_NAME, null, DATABASE_VERSION);
        database = getReadableDatabase();
    }

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor runSQLQuery(String query) {
        return database.rawQuery(query, null);
    }

    public Cursor getPersonByID(String personId){
        return database.rawQuery(String.format("SELECT people.name FROM people WHERE people.person_id = \"%s\"", personId), null);
    }
}