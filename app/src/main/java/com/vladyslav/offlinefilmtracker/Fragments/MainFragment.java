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

import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;

public class MainFragment extends Fragment {
    final private double POSTER_SCALE_FACTOR = 2.5; //размер постеров у фильмов
    final private int FILMS_IN_ROW = 7; //кол-во фильмов в строке
    private LinearLayout baseLayout; //базовый лаяут
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
            DatabaseManager databaseManager = DatabaseManager.getInstance(view.getContext());
            baseLayout = view.findViewById(R.id.fragment_main_ll_layout);

            //строка с популярными фильмами
            createFilmRow(databaseManager.getPopularFilms(), "Popular films");

            //создаем строки с указанными жанрами
            String[] genres = new String[]{"Action", "Sci-Fi", "Fantasy", "Comedy", "Animation"};
            for (String genre : genres)
                createFilmRow(databaseManager.getFilmsByGenre(genre), genre + " films");
        }
        return view;
    }

    //нужен для сохранения состояния фрагмента
    @Override
    public void onDestroyView() {
        if (view.getParent() != null) ((ViewGroup) view.getParent()).removeView(view);
        super.onDestroyView();
    }

    //создаем строку с фильмами
    private void createFilmRow(Film[] films, String rowName) {
        LinearLayout filmsLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_filmrow, null); //колонка фильмов
        ((TextView) filmsLayout.getChildAt(0)).setText(rowName); //устанавливаем заголовок колонки
        LinearLayout linearLayout = (LinearLayout) ((HorizontalScrollView) filmsLayout.getChildAt(1)).getChildAt(0);
        for (int i = 0; i < FILMS_IN_ROW; i++)
            addFilm(films[i], linearLayout);

        baseLayout.addView(filmsLayout); //добавляем в корень
    }

    //добавление нового фильма в указанный лаяут
    private void addFilm(final Film film, LinearLayout layout) {
        //получаем постер
        Drawable poster = film.getPoster(getContext());

        //создаем View для постера
        LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, null);

        //ставим постер
        ImageView filmPoster = (ImageView) filmLayout.getChildAt(0);
        filmPoster.setLayoutParams(new LinearLayout.LayoutParams((int) (poster.getIntrinsicWidth() * POSTER_SCALE_FACTOR), (int) (poster.getIntrinsicHeight() * POSTER_SCALE_FACTOR)));
        filmPoster.setImageDrawable(poster);

        //ставим основную информацию
        ((TextView) filmLayout.getChildAt(1)).setText(film.getTitle());
        ((TextView) filmLayout.getChildAt(2)).setText(film.getRating());

        //добавляем нажатие для перехода на фрагмент фильма
        filmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, FilmFragment.newInstance(film)).addToBackStack(null).commit();
            }
        });

        layout.addView(filmLayout);
    }
}