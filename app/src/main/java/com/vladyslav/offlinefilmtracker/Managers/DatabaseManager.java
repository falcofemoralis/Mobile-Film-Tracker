package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.Objects.Film;

import java.io.File;
import java.util.HashMap;

//SINGLETON
public class DatabaseManager extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "imdb.db";
    private static DatabaseManager instance;
    public SQLiteDatabase database;

    public DatabaseManager(Context context, String path) {
        super(context, path, null, DATABASE_VERSION);
        database = getReadableDatabase();
    }

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            String path = context.getObbDir().getPath() + "/" + DATABASE_NAME;
            File file = new File(path);
            if (!(file.exists() && !file.isDirectory()))
                return null;

            instance = new DatabaseManager(context, path);
        }
        return instance;
    }

    public static void delete() {
        instance = null;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //получение популярных фильмов
    public Film[] getPopularFilms(int limit) {
        Cursor cursor = database.rawQuery("SELECT * " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                "WHERE ratings.rating > 7 AND ratings.votes > 5000 AND titles.premiered = 2020 " +
                "ORDER BY ratings.votes DESC LIMIT ?", new String[]{String.valueOf(limit)});
        Film[] films = new Film[cursor.getCount()];
        int n = 0;
        while (cursor.moveToNext())
            films[n++] = getFilmData(cursor);

        cursor.close();
        return films;
    }

    //получаем фильмов по жанру и году с ограничением
    public Film[] getFilmsByGenre(String genre, int premiered, int limit) {
        String genreParam = "%" + genre + "%";
        Cursor cursor = database.rawQuery("SELECT * " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                "WHERE titles.genres like ? AND titles.premiered > ? LIMIT ?", new String[]{genreParam, String.valueOf(premiered), String.valueOf(limit)});
        Film[] films = new Film[cursor.getCount()];
        int n = 0;
        while (cursor.moveToNext())
            films[n++] = getFilmData(cursor);

        cursor.close();
        return films;
    }

    //получение фильмов по жанру
    public Cursor getFilmsByGenre(String genre) {
        String genreParam = "%" + genre + "%";
        Cursor cursor = database.rawQuery("SELECT * " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                "WHERE titles.genres like ? " +
                "ORDER BY ratings.votes DESC, ratings.rating DESC", new String[]{genreParam});
        return cursor;
    }

    //получение актеров в фильме
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

    //получением фильмов актера
    public Film[] getFilmsByPersonId(String personId) {
        Cursor cursor = database.rawQuery("SELECT DISTINCT crew.title_id, ratings.rating " +
                "FROM crew INNER JOIN ratings on ratings.title_id = crew.title_id " +
                "WHERE crew.person_id = ?" +
                "ORDER BY ratings.rating DESC", new String[]{personId});
        Film[] films = new Film[cursor.getCount()];
        int n = 0;
        while (cursor.moveToNext()) {
            Cursor cursor_films = database.rawQuery("SELECT * " +
                    "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id WHERE titles.title_id = ?", new String[]{cursor.getString(cursor.getColumnIndex("title_id"))});
            cursor_films.moveToFirst();
            films[n++] = getFilmData(cursor_films);
        }
        cursor.close();
        return films;
    }

    //получение роли в фильме по актеру и фильму
    public String[] getRoleByPersonAndTitleId(String personId, String titleId) {
        Cursor cursor = database.rawQuery(" SELECT crew.category FROM crew WHERE crew.person_id = ? and crew.title_id = ?;", new String[]{personId, titleId});

        String[] roles = new String[cursor.getCount()];
        int n = 0;
        while (cursor.moveToNext()) {
            roles[n++] = cursor.getString(cursor.getColumnIndex("category"));
        }

        return roles;
    }

    //конвертирование курсора в объект фильма
    public Film getFilmData(Cursor cursor) {
        return new Film(cursor.getString(cursor.getColumnIndex("title_id")),
                cursor.getString(cursor.getColumnIndex("primary_title")),
                cursor.getString(cursor.getColumnIndex("rating")),
                cursor.getString(cursor.getColumnIndex("votes")),
                cursor.getString(cursor.getColumnIndex("runtime_minutes")),
                cursor.getString(cursor.getColumnIndex("premiered")),
                cursor.getString(cursor.getColumnIndex("is_adult")),
                cursor.getString(cursor.getColumnIndex("genres")),
                cursor.getString(cursor.getColumnIndex("plot")));
    }

    //получение фильмов по жанру
    public HashMap<String, String> getAllFilms() {
        Cursor cursor = database.rawQuery("SELECT titles.primary_title, titles.title_id " +
                "FROM titles", null);
        HashMap<String, String> films = new HashMap<>();
        while (cursor.moveToNext())
            films.put(cursor.getString(cursor.getColumnIndex("primary_title")), cursor.getString(cursor.getColumnIndex("title_id")));

        cursor.close();
        return films;
    }

    public Film getFilmByTitleId(String titleId) {
        Cursor cursor = database.rawQuery("SELECT * " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                "WHERE titles.title_id = ?", new String[]{titleId});
        cursor.moveToFirst();
        return getFilmData(cursor);
    }

    public Cursor getFilmByTitle(String title) {
        String titleParam = "%" + title + "%";
        Cursor cursor = database.rawQuery("SELECT * " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                "WHERE titles.primary_title like ?", new String[]{titleParam});
        return cursor;
    }
}