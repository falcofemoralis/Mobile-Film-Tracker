package com.vladyslav.offlinefilmtracker.Managers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.Objects.Film;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

//SINGLETON
public class DatabaseManager extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = "imdb.db";
    private static DatabaseManager instance;
    public SQLiteDatabase database;
    private HashMap<String, String> genresMap = new HashMap<>();

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
    public Thread getPopularFilms(final int limit, final ArrayList<Film> films, final Runnable runnable) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = database.rawQuery("SELECT * " +
                        "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                        "WHERE ratings.rating > 7 AND ratings.votes > 5000 AND titles.premiered = 2020 " +
                        "ORDER BY ratings.votes DESC LIMIT ?", new String[]{String.valueOf(limit)});
                while (cursor.moveToNext())
                    films.add(getFilmData(cursor));

                cursor.close();
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //получаем фильмов по жанру и году с ограничением
    public Thread getFilmsByGenre(final String genreId, final int premiered, final int limit, final ArrayList<Film> films, final Runnable runnable) {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                String genreParam = "%" + genreId + "%";
                Cursor cursor = database.rawQuery("SELECT * " +
                        "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                        "WHERE titles.genres like ? AND titles.premiered > ? LIMIT ?", new String[]{genreParam, String.valueOf(premiered), String.valueOf(limit)});
                while (cursor.moveToNext())
                    films.add(getFilmData(cursor));

                cursor.close();

                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //получение фильмов по жанру
    public void getFilmsByGenre(final String genreId, final ArrayList<Cursor> cursor, final Runnable runnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String genreParam = "%" + genreId + "%";
                cursor.add(database.rawQuery("SELECT * " +
                        "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                        "WHERE titles.genres like ? " +
                        "ORDER BY ratings.votes DESC, ratings.rating DESC", new String[]{genreParam}));
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //получение возможных фильмов по названию
    public void getFilmsByTitle(final String title, final ArrayList<Cursor> cursor, final Runnable runnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String titleParam = "%" + title + "%";
                cursor.add(database.rawQuery("SELECT * " +
                        "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                        "WHERE titles.primary_title like ?", new String[]{titleParam}));
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //получение актеров в фильме
    public void getActorsByTitleId(final String titleId, final ArrayList<Actor> actors, final Runnable runnable) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = database.rawQuery("SELECT people.person_id, people.name, people.born, people.died, crew.characters, crew.category " +
                        "FROM crew INNER JOIN people ON people.person_id = crew.person_id " +
                        "WHERE crew.title_id = ?", new String[]{titleId});

                while (cursor.moveToNext()) {
                    actors.add(new Actor(cursor.getString(cursor.getColumnIndex("person_id")),
                            cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getString(cursor.getColumnIndex("born")),
                            cursor.getString(cursor.getColumnIndex("died")),
                            cursor.getString(cursor.getColumnIndex("characters")),
                            cursor.getString(cursor.getColumnIndex("category"))));
                }
                cursor.close();
                runnable.run();
            }
        })).start();
    }

    //получением фильмов актера
    public void getFilmsByPersonId(final String personId, final ArrayList<Film> films, final Runnable runnable) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = database.rawQuery("SELECT DISTINCT crew.title_id, ratings.rating " +
                        "FROM crew INNER JOIN ratings on ratings.title_id = crew.title_id " +
                        "WHERE crew.person_id = ?" +
                        "ORDER BY ratings.rating DESC", new String[]{personId});
                while (cursor.moveToNext()) {
                    Cursor cursor_films = database.rawQuery("SELECT * " +
                            "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id WHERE titles.title_id = ?", new String[]{cursor.getString(cursor.getColumnIndex("title_id"))});
                    cursor_films.moveToFirst();
                    films.add(getFilmData(cursor_films));
                }
                cursor.close();

                runnable.run();
            }
        })).start();
    }

    //получение роли в фильме по актеру и фильму
    //находится в отдельном потоке
    public String[] getRoleByPersonAndTitleId(String personId, String titleId) {
        Cursor cursor = database.rawQuery(" SELECT crew.category FROM crew WHERE crew.person_id = ? and crew.title_id = ?;", new String[]{personId, titleId});

        String[] roles = new String[cursor.getCount()];
        int n = 0;
        while (cursor.moveToNext()) {
            roles[n++] = cursor.getString(cursor.getColumnIndex("category"));
        }

        cursor.close();
        return roles;
    }

    //получение фильмов всех фильмов в кач-ве
    public void getAllFilms(final HashMap<String, String> films, final Runnable runnable) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = database.rawQuery("SELECT titles.primary_title, titles.title_id " +
                        "FROM titles", null);
                while (cursor.moveToNext())
                    films.put(cursor.getString(cursor.getColumnIndex("primary_title")), cursor.getString(cursor.getColumnIndex("title_id")));

                cursor.close();
                runnable.run();
            }
        })).start();
    }

    //получение фильма по id
    public void getFilmByTitleId(final String titleId, final Film[] film, final Runnable runnable) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = database.rawQuery("SELECT * " +
                        "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                        "WHERE titles.title_id = ?", new String[]{titleId});
                cursor.moveToFirst();
                film[0] = getFilmData(cursor);

                runnable.run();
            }
        })).start();
    }

    public String getGenreById(String id) {
        if (genresMap.size() == 0) loadGenres();
        return genresMap.get(id);
    }

    public HashMap<String, String> getGenresMap() {
        if (genresMap.size() == 0) loadGenres();
        return genresMap;
    }

    //конвертирование курсора в объект фильма
    public Film getFilmData(Cursor cursor) {
        //создаем объект фильма
        return new Film(cursor.getString(cursor.getColumnIndex("title_id")),
                cursor.getString(cursor.getColumnIndex("primary_title")),
                cursor.getString(cursor.getColumnIndex("rating")),
                cursor.getString(cursor.getColumnIndex("votes")),
                cursor.getString(cursor.getColumnIndex("runtime_minutes")),
                cursor.getString(cursor.getColumnIndex("premiered")),
                cursor.getString(cursor.getColumnIndex("is_adult")),
                cursor.getString(cursor.getColumnIndex("genres")).split(","),
                cursor.getString(cursor.getColumnIndex("plot")));
    }

    public void loadGenres() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor genresCursor;
                genresCursor = database.rawQuery("SELECT genres.genre, genres.genre_id " +
                        "FROM genres", null);

                while (genresCursor.moveToNext())
                    genresMap.put(genresCursor.getString(1), genresCursor.getString(0));

                genresCursor.close();
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}