package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;;

import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.Objects.Film;

import java.io.File;

//SINGLETON
public class DatabaseManager extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "imdb.db";
    public SQLiteDatabase database;
    private static DatabaseManager instance;

    public DatabaseManager(Context context) {
        super(context, context.getObbDir().getPath() + "/" + DATABASE_NAME, null, DATABASE_VERSION);
        database = getReadableDatabase();
    }

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Film[] getFilmsByQuery(String query) {
        Cursor cursor = database.rawQuery(query, null);
        Film[] film = new Film[cursor.getCount()];
        int n = 0;
        while (cursor.moveToNext())
            film[n++] = getFilmData(cursor);

        cursor.close();
        return film;
    }

    public Actor[] getActorsByTitleId(String titleId) {
        Cursor cursor = database.rawQuery("SELECT people.person_id, people.name, people.born, people.died, crew.characters, crew.category " +
                "FROM crew INNER JOIN people ON people.person_id = crew.person_id " +
                "WHERE crew.title_id = ?", new String[]{titleId});

        Actor[] actors = new Actor[cursor.getCount()];
        int n = 0;
        while (cursor.moveToNext()) {
            actors[n++] = new Actor(cursor.getString(cursor.getColumnIndex("person_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("born")),
                    cursor.getString(cursor.getColumnIndex("died")),
                    cursor.getString(cursor.getColumnIndex("characters")),
                    cursor.getString(cursor.getColumnIndex("category")));
        }
        cursor.close();
        return actors;
    }

    public Film[] getFilmsByPersonId(String personId) {
        Cursor cursor = database.rawQuery("SELECT DISTINCT crew.title_id FROM crew WHERE crew.person_id = ?", new String[]{personId});
        Film[] films = new Film[cursor.getCount()];
        int n = 0;
        while (cursor.moveToNext()) {
            Cursor cursor_films = database.rawQuery("SELECT titles.title_id, titles.genres, titles.premiered, titles.runtime_minutes, titles.is_adult, titles.primary_title," +
                    " ratings.rating, ratings.votes " +
                    "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id WHERE titles.title_id = ?", new String[]{cursor.getString(cursor.getColumnIndex("title_id"))});
            cursor_films.moveToFirst();
            films[n++] = getFilmData(cursor_films);
        }
        cursor.close();
        return films;
    }

    private Film getFilmData(Cursor cursor) {
        return new Film(cursor.getString(cursor.getColumnIndex("title_id")),
                cursor.getString(cursor.getColumnIndex("primary_title")),
                cursor.getString(cursor.getColumnIndex("rating")),
                cursor.getString(cursor.getColumnIndex("votes")),
                cursor.getString(cursor.getColumnIndex("runtime_minutes")),
                cursor.getString(cursor.getColumnIndex("premiered")),
                cursor.getString(cursor.getColumnIndex("is_adult")),
                cursor.getString(cursor.getColumnIndex("genres")));
    }
}