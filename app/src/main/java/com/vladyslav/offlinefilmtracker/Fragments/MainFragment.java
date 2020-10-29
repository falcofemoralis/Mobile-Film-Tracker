package com.vladyslav.offlinefilmtracker.Fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    private final double POSTER_SCALE_FACTOR = 0.30; //размер постеров у фильмов
    private final int FILMS_IN_ROW = 7; //кол-во фильмов в строке
    private LinearLayout baseLayout; //базовый лаяут для установки рядов фильмов
    private View view; //вью фрагмента
    private DatabaseManager databaseManager; //менджер базы данных
    private int moreBtnHeight; //размер кнопки More

    //лист пар из ключа жанра (в базе) и id строковой константы
    public final ArrayList<Pair<String, Integer>> genres = new ArrayList<Pair<String, Integer>>() {
        {
            add(new Pair<>("Popular", R.string.genre_popular));
            add(new Pair<>("Action", R.string.genre_action));
            add(new Pair<>("Sci-Fi", R.string.genre_sciFi));
            add(new Pair<>("Fantasy", R.string.genre_fantasy));
            add(new Pair<>("Comedy", R.string.genre_comedy));
            add(new Pair<>("Animation", R.string.genre_animation));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
            baseLayout = view.findViewById(R.id.fragment_main_ll_layout);

            getFilmsFromDatabase();
        }
        return view;
    }

    //загружаем фильмы из базы по жанрам
    public void getFilmsFromDatabase() {
        final Handler mHandler = new Handler(Looper.getMainLooper());
        (new Thread() {
            public void run() {
                databaseManager = DatabaseManager.getInstance(view.getContext());
                //получаем фильмы по жанру
                for (final Pair<String, Integer> genre : genres) {
                    final String genreString = genre.first;

                    final Film[] films;
                    if (genreString.equals("Popular")) {
                        films = databaseManager.getPopularFilms(FILMS_IN_ROW);
                    } else {
                        films = databaseManager.getFilmsByGenre(genreString, 2015, FILMS_IN_ROW);
                    }

                    //устанавливаем полученные фильмы в строки
                    mHandler.post(new Runnable() {
                        public void run() {
                            createFilmRow(films, genreString, genre.second);

                        }
                    });
                }
                mHandler.post(new Runnable() {
                    public void run() {
                        getActivity().findViewById(R.id.progress_bar).setVisibility(View.GONE);
                        getActivity().findViewById(R.id.main_fragment_container).setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    //создаем ряд с фильмами
    public void createFilmRow(Film[] films, String genre, int genreStringId) {
        LinearLayout filmsLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film_row, null); //строка фильмов
        ((TextView) filmsLayout.getChildAt(0)).setText(getString(R.string.films, getString(genreStringId))); //устанавливаем заголовок строки

        LinearLayout linearLayout = (LinearLayout) ((HorizontalScrollView) filmsLayout.getChildAt(1)).getChildAt(0);
        for (int i = 0; i < FILMS_IN_ROW; i++)
            addFilm(films[i], linearLayout);

        if (!genre.equals("Popular")) addMoreBtn(linearLayout, genre);
        baseLayout.addView(filmsLayout); //добавляем в корень
    }

    //добавление нового фильма в указанный лаяут
    public void addFilm(final Film film, LinearLayout layout) {
        //создаем View для постера
        final LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, null);

        //ставим постер
        BitmapDrawable poster = film.getPoster(getContext());
        ImageView filmPoster = (ImageView) filmLayout.getChildAt(0);

        int posterHeight = ResourcesManager.getDpFromPx(poster.getBitmap().getHeight(), POSTER_SCALE_FACTOR, getContext());
        int posterWidth = ResourcesManager.getDpFromPx(poster.getBitmap().getWidth(), POSTER_SCALE_FACTOR, getContext());

        filmPoster.setLayoutParams(new LinearLayout.LayoutParams(posterWidth, posterHeight));
        filmPoster.setImageDrawable(poster);
        moreBtnHeight = posterHeight;

        //ставим основную информацию
        TextView titleView = ((TextView) filmLayout.getChildAt(1));
        titleView.setText(film.getTitle());
        titleView.setWidth(posterWidth);

        ((TextView) filmLayout.getChildAt(2)).setText(film.getRating());

        //добавляем нажатие для перехода на фрагмент фильма
        filmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(FilmFragment.newInstance(film));
            }
        });

        layout.addView(filmLayout);
    }

    //добавляем кнопку открытия всех фильмов по категории
    public void addMoreBtn(LinearLayout baseLayout, final String genre) {
        LinearLayout moreBtnLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_more, null);
        ImageView moreBtn = (ImageView) moreBtnLayout.getChildAt(0);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(FilmsListFragment.newInstance(genre, true));
            }
        });
        ViewGroup.LayoutParams layoutParams = moreBtn.getLayoutParams();
        layoutParams.height = moreBtnHeight;
        moreBtn.setLayoutParams(layoutParams);
        baseLayout.addView(moreBtnLayout);
    }
}