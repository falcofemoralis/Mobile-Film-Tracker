package com.vladyslav.offlinefilmtracker.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;

public class MainFragment extends Fragment {
    final private double POSTER_SCALE_FACTOR = 2.5; //размер постеров у фильмов
    final private int FILMS_IN_ROW = 7; //кол-во фильмов в строке
    private LinearLayout baseLayout; //базовый лаяут

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        DatabaseManager databaseManager = DatabaseManager.getInstance(view.getContext());
        baseLayout = view.findViewById(R.id.fragment_main_ll_layout);

        //строка с популярными фильмами
        Film[] popularFilms = databaseManager.getFilms("SELECT titles.title_id, titles.genres, titles.premiered, titles.runtime_minutes, titles.is_adult, titles.primary_title, " +
                "ratings.rating, ratings.votes " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                "WHERE ratings.rating > 7 AND ratings.votes > 5000 AND titles.premiered = 2020 " +
                "ORDER BY ratings.votes DESC LIMIT 7");
        createFilmRow(popularFilms, "Popular films");


        //строка с приключенчискими фильмами
        Film[] adventureFilms = databaseManager.getFilms("SELECT titles.title_id, titles.genres, titles.premiered, titles.runtime_minutes, titles.is_adult, titles.primary_title, " +
                "ratings.rating, ratings.votes " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                "WHERE titles.genres like '%Adventure%' LIMIT 7");
        createFilmRow(adventureFilms, "Adventure films");
        return view;
    }

    //создаем строку с фильмами
    private void createFilmRow(Film[] films, String rowName) {
        LinearLayout filmsLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_filmrow, null); //колонка фильмов
        ((TextView) filmsLayout.getChildAt(0)).setText(rowName); //устанавливаем заголовок колонки
        LinearLayout linearLayout = (LinearLayout) ((HorizontalScrollView) filmsLayout.getChildAt(1)).getChildAt(0);
        for (int i = 0; i < FILMS_IN_ROW; i++)
            if (!addFilm(films[i], linearLayout)) {
                --i;
                break;
            }
        baseLayout.addView(filmsLayout); //добавляем в корень
    }

    //добавление нового фильма в указанный лаяут
    private boolean addFilm(final Film film, LinearLayout layout) {
        //получаем постер
        final Drawable poster = film.getPoster(getContext());
        if (poster == null)
            return false;

        //создаем View для постера
        LinearLayout movieLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, null);

        //ставим постер
        ImageView filmPoster = (ImageView) movieLayout.getChildAt(0);
        filmPoster.setLayoutParams(new LinearLayout.LayoutParams((int) (poster.getIntrinsicWidth() * POSTER_SCALE_FACTOR), (int) (poster.getIntrinsicHeight() * POSTER_SCALE_FACTOR)));
        filmPoster.setImageDrawable(poster);

        //ставим основную информацию
        ((TextView) movieLayout.getChildAt(1)).setText(film.getTitle());
        ((TextView) movieLayout.getChildAt(2)).setText(film.getRating());

        //добавляем нажатие для перехода на фрагмент фильма
        movieLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(new FilmFragment(film));
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.activity_main_nv_bottomBar);
                bottomNavigationView.setVisibility(View.GONE);
            }
        });

        layout.addView(movieLayout);
        return true;
    }
}