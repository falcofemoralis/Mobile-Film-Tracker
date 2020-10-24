package com.vladyslav.offlinefilmtracker.Fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

public class MainFragment extends Fragment {
    private final double POSTER_SCALE_FACTOR = 0.40; //размер постеров у фильмов
    private final int FILMS_IN_ROW = 7; //кол-во фильмов в строке
    private LinearLayout baseLayout; //базовый лаяут
    private View view;
    private DatabaseManager databaseManager;
    private int moreBtnHeight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
            baseLayout = view.findViewById(R.id.fragment_main_ll_layout);

            final Handler mHandler = new Handler(Looper.getMainLooper());
            final String[] genres = new String[]{"Popular", "Action", "Sci-Fi", "Fantasy", "Comedy", "Animation"};
            //создаем поток
            (new Thread() {
                public void run() {
                    databaseManager = DatabaseManager.getInstance(view.getContext());
                    //получаем фильмы по жанру
                    for (int i = 0; i < genres.length; ++i) {
                        final Film[] films;
                        if (genres[i].equals("Popular"))
                            films = databaseManager.getPopularFilms(FILMS_IN_ROW);
                        else
                            films = databaseManager.getFilmsByGenre(genres[i], 2015, FILMS_IN_ROW);

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
                FragmentHelper.openFragment(CategoryFragment.newInstance(genre, true));
            }
        });
        ViewGroup.LayoutParams layoutParams = moreBtn.getLayoutParams();
        layoutParams.height = moreBtnHeight;
        moreBtn.setLayoutParams(layoutParams);
        baseLayout.addView(moreBtnLayout);
    }
}