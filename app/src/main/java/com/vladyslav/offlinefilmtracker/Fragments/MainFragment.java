package com.vladyslav.offlinefilmtracker.Fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

    //массив id жанров
    public final int[] genres = {-1, 1, 9, 16, 21, 18};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
            baseLayout = view.findViewById(R.id.fragment_main_ll_layout);
            databaseManager = DatabaseManager.getInstance(getContext());

            getFilmsFromDatabase();
        }
        return view;
    }

    //загружаем фильмы из базы по жанрам
    public void getFilmsFromDatabase() {
        ArrayList<Thread> threads = new ArrayList<>();
        for (final Integer genreId : genres) {
            final ArrayList<Film> films = new ArrayList<>(); //список фильмов, в него будет загружены фильмы из базы

            //получаем в фильмы из базы и устанавливаем ряд из них
            try {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createFilmRow(films, genreId);
                            }
                        });
                    }
                };

                if (genreId == -1)
                    threads.add(databaseManager.getPopularFilms(FILMS_IN_ROW, films, runnable));
                else
                    threads.add(databaseManager.getFilmsByGenre(genreId, 2015, FILMS_IN_ROW, films, runnable));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            for (int i = 0; i < threads.size(); i++) {
                Thread thread = threads.get(i);
                thread.start();
                thread.join();
            }
            getActivity().findViewById(R.id.progress_bar).setVisibility(View.GONE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //создаем ряд с фильмами
    public void createFilmRow(ArrayList<Film> films, final Integer genreId) {
        final LinearLayout filmsLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film_row, null); //строка фильмов
        ((TextView) filmsLayout.getChildAt(0)).setText(databaseManager.getGenreById(genreId)); //устанавливаем заголовок строки

        final LinearLayout linearLayout = (LinearLayout) ((HorizontalScrollView) filmsLayout.getChildAt(1)).getChildAt(0);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (genreId != -1) addMoreBtn(linearLayout, genreId);
                baseLayout.addView(filmsLayout); //добавляем в корень
            }
        };
        for (int i = 0; i < FILMS_IN_ROW; i++)
            addFilm(films.get(i), linearLayout, i, runnable);
    }

    //добавление нового фильма в указанный лаяут
    public void addFilm(final Film film, LinearLayout layout, final int i, final Runnable runnable) {
        //создаем View для постера
        final LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, null);

        //ставим постер
        final BitmapDrawable[] poster = new BitmapDrawable[1];
        film.getPoster(getContext(), poster, new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //устанавливаем постер
                        ImageView filmPoster = (ImageView) filmLayout.getChildAt(0);

                        final int posterHeight = ResourcesManager.getDpFromPx(poster[0].getBitmap().getHeight(), POSTER_SCALE_FACTOR, getContext());
                        final int posterWidth = ResourcesManager.getDpFromPx(poster[0].getBitmap().getWidth(), POSTER_SCALE_FACTOR, getContext());

                        if (filmPoster != null) {
                            filmPoster.setLayoutParams(new LinearLayout.LayoutParams(posterWidth, posterHeight));
                            filmPoster.setImageDrawable(poster[0]);

                            moreBtnHeight = posterHeight;

                            //ставим основную информацию
                            TextView titleView = ((TextView) filmLayout.getChildAt(1));
                            titleView.setText(film.getTitle());
                            titleView.setWidth(posterWidth);

                            if (i + 1 == FILMS_IN_ROW) runnable.run();
                        }
                    }
                });
            }
        });

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
    public void addMoreBtn(LinearLayout baseLayout, final int genreId) {
        LinearLayout moreBtnLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_more, null);
        ImageView moreBtn = (ImageView) moreBtnLayout.getChildAt(0);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(FilmsListFragment.newInstance(genreId));
            }
        });
        ViewGroup.LayoutParams layoutParams = moreBtn.getLayoutParams();
        layoutParams.height = moreBtnHeight;
        moreBtn.setLayoutParams(layoutParams);
        baseLayout.addView(moreBtnLayout);
    }
}