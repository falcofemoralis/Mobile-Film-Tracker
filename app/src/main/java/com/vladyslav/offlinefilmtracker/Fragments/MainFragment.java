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
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFragment extends Fragment {
    private final double POSTER_SCALE_FACTOR = 0.30; //размер постеров у фильмов
    private final int FILMS_IN_ROW = 7; //кол-во фильмов в строке
    private LinearLayout baseLayout; //базовый лаяут для установки рядов фильмов
    private View view; //вью фрагмента
    private DatabaseManager databaseManager; //менджер базы данных
    private int moreBtnHeight; //размер кнопки More

    //хешмап из ключа жанра (в базе) и id строковой константы, где -1 = Popular
    public final LinkedHashMap<String, String> genres = new LinkedHashMap<String, String>() {
        {
            put("-1", "popular");
            put("1", "action");
            put("9", "scifi");
            put("16", "fantasy");
            put("21", "comedy");
            put("18", "animation");
        }
    };

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
        for (final Map.Entry<String, String> genreEntry : genres.entrySet()) {
            final String genreId = genreEntry.getKey(); //id жанра в базе
            final ArrayList<Film> films = new ArrayList<>(); //список фильмов, в него будет загружены фильмы из базы

            //получаем в фильмы из базы и устанавливаем ряд из них
            try {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createFilmRow(films, genreEntry);
                                getActivity().findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                getActivity().findViewById(R.id.main_fragment_container).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                };

                if (genreId.equals("-1"))
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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //создаем ряд с фильмами
    public void createFilmRow(ArrayList<Film> films, final Map.Entry<String, String> genreEntry) {
        final LinearLayout filmsLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film_row, null); //строка фильмов
        ((TextView) filmsLayout.getChildAt(0)).setText(getString(R.string.films, ResourcesManager.getGenreStringById(genreEntry.getValue(), getContext()))); //устанавливаем заголовок строки

        final LinearLayout linearLayout = (LinearLayout) ((HorizontalScrollView) filmsLayout.getChildAt(1)).getChildAt(0);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!genreEntry.getKey().equals("-1")) addMoreBtn(linearLayout, genreEntry);
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

                        if (filmPoster != null) {
                            int posterHeight = ResourcesManager.getDpFromPx(poster[0].getBitmap().getHeight(), POSTER_SCALE_FACTOR, getContext());
                            int posterWidth = ResourcesManager.getDpFromPx(poster[0].getBitmap().getWidth(), POSTER_SCALE_FACTOR, getContext());

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
    public void addMoreBtn(LinearLayout baseLayout, final Map.Entry<String, String> genreEntry) {
        LinearLayout moreBtnLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_more, null);
        ImageView moreBtn = (ImageView) moreBtnLayout.getChildAt(0);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(FilmsListFragment.newInstance(genreEntry));
            }
        });
        ViewGroup.LayoutParams layoutParams = moreBtn.getLayoutParams();
        layoutParams.height = moreBtnHeight;
        moreBtn.setLayoutParams(layoutParams);
        baseLayout.addView(moreBtnLayout);
    }
}