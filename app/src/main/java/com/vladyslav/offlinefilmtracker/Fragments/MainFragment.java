package com.vladyslav.offlinefilmtracker.Fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFragment extends Fragment {
    final private double POSTER_SCALE_FACTOR = 2.5; //размер постеров у фильмов
    final private int FILMS_IN_ROW = 7; //кол-во фильмов в строке
    private LinearLayout baseLayout; //базовый лаяут
    private View view;
    private DatabaseManager databaseManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
            baseLayout = view.findViewById(R.id.fragment_main_ll_layout);
            databaseManager = DatabaseManager.getInstance(view.getContext());

            final Handler mHandler = new Handler(Looper.getMainLooper());
            final String[] genres = new String[]{"Popular", "Action", "Sci-Fi", "Fantasy", "Comedy", "Animation"};
            //создаем поток
            (new Thread() {
                public void run() {
                    //получаем фильмы по жанру
                    for (int i = 0; i < genres.length; ++i) {
                        final Film[] films;
                        if (genres[i].equals("Popular"))
                            films = databaseManager.getPopularFilmsLimited(FILMS_IN_ROW);
                        else
                            films = databaseManager.getFilmsByGenreLimited(genres[i], 2015, FILMS_IN_ROW);

                        //устанавливаем полученные фильмы в строки в UI потоке
                        final int finalI = i;
                        mHandler.post(new Runnable() {
                            public void run() {
                                createFilmRow(films, genres[finalI]);
                                if (finalI == genres.length - 1) {
                                    getActivity().findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                    getActivity().findViewById(R.id.main_fragment_container).setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }
            }).start();
        }
        return view;
    }

    //создаем строку с фильмами
    public void createFilmRow(Film[] films, String genre) {
        LinearLayout filmsLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film_row, null); //колонка фильмов
        ((TextView) filmsLayout.getChildAt(0)).setText(genre + " films"); //устанавливаем заголовок колонки

        LinearLayout linearLayout = (LinearLayout) ((HorizontalScrollView) filmsLayout.getChildAt(1)).getChildAt(0);
        for (int i = 0; i < FILMS_IN_ROW; i++)
            addFilm(films[i], linearLayout);
        if (genre != "Popular") addMoreBtn(linearLayout, genre);
        baseLayout.addView(filmsLayout); //добавляем в корень
    }

    //добавление нового фильма в указанный лаяут
    public void addFilm(final Film film, LinearLayout layout) {
        //получаем постер
        final Drawable poster = film.getPoster(getContext());

        //создаем View для постера
        final LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, null);

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
                FragmentHelper.openFragment(getFragmentManager(), getActivity(), FilmFragment.newInstance(film));
            }
        });

        layout.addView(filmLayout);
    }

    //добавляем кнопку открытия всех фильмов по категории
    public void addMoreBtn(LinearLayout linearLayout, final String genre) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_more, null);
        layout.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(getFragmentManager(), getActivity(), CategoryFragment.newInstance(genre));
            }
        });
        linearLayout.addView(layout);
    }
}