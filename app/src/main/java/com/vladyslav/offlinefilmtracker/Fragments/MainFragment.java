package com.vladyslav.offlinefilmtracker.Fragments;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;

public class MainFragment extends Fragment {
    final private double POSTER_SCALE_FACTOR = 2.5; //размер постеров у фильмов
    final private int FILMS_IN_ROW = 7; //кол-во фильмов в строке
    ResourcesManager resourcesManager; //доступ к файлам постеров

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        resourcesManager = new ResourcesManager(view.getContext());
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(view.getContext());

        //получаем необходимые данные из базы данных
        Cursor popularFilmsCursor = databaseHelper.runSQLQuery("SELECT titles.title_id, titles.primary_title, ratings.rating " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id" +
                " WHERE ratings.rating > 7.5 AND ratings.votes > 5000 AND titles.premiered > 2019");
        LinearLayout popularFilmsLayout = view.findViewById(R.id.fragment_main_ll_popularFilms);
        for (int i = 0; i < FILMS_IN_ROW; i++)
            if (!addFilm(popularFilmsCursor, popularFilmsLayout)) {
                --i;
                break;
            }

        //получаем необходимые данные из базы данных
        Cursor adventureFilmsCursor = databaseHelper.runSQLQuery("SELECT titles.title_id, titles.primary_title, ratings.rating " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                "WHERE titles.genres like '%Adventure%'");
        LinearLayout adventureFilmsLayout = view.findViewById(R.id.fragment_main_ll_adventurerFilms);
        for (int i = 0; i < FILMS_IN_ROW; i++)
            if (!addFilm(adventureFilmsCursor, adventureFilmsLayout)) {
                --i;
                break;
            }
        return view;
    }

    //добавление нового фильма в указанный лаяут
    private boolean addFilm(Cursor cursor, LinearLayout baseLayout) {
        cursor.moveToNext();

        //получаем основную информацию про фильм
        final String title = cursor.getString(cursor.getColumnIndex("primary_title"));
        final String film_id = cursor.getString(cursor.getColumnIndex("title_id"));
        final String rating = cursor.getString(cursor.getColumnIndex("rating"));

        //получаем постер
        final Drawable posterDrawable = resourcesManager.getPosterByTitleId(film_id);
        if (posterDrawable == null)
            return false;

        //создаем View для постера
        LinearLayout movieLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_movie, null);

        //ставим постер
        ImageView filmPoster = (ImageView) movieLayout.getChildAt(0);
        filmPoster.setLayoutParams(new LinearLayout.LayoutParams((int) (posterDrawable.getIntrinsicWidth() * POSTER_SCALE_FACTOR), (int) (posterDrawable.getIntrinsicHeight() * POSTER_SCALE_FACTOR)));
        filmPoster.setImageDrawable(posterDrawable);

        //ставим основную информацию
        ((TextView) movieLayout.getChildAt(1)).setText(title);
        ((TextView) movieLayout.getChildAt(2)).setText(rating);

        //добавляем нажатие для перехода на фрагмент фильма
        movieLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper fragmentHelper = new FragmentHelper();
                fragmentHelper.openFragment(new FilmFragment(film_id, title, rating, posterDrawable));

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.activity_main_nv_bottomBar);
                bottomNavigationView.setVisibility(View.GONE);
            }
        });

        baseLayout.addView(movieLayout);
        return true;
    }
}