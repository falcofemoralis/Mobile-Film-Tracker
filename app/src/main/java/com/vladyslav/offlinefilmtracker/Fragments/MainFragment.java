package com.vladyslav.offlinefilmtracker.Fragments;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;

public class MainFragment extends Fragment {
    final private double POSTER_SCALE_FACTOR = 2.5; //размер постеров у фильмов
    final private int FILMS_IN_ROW = 7; //кол-во фильмов в строке
    private LinearLayout baseLayout; //базовый лаяут
    private View view;
    private DatabaseManager databaseManager;
    private Handler handlerPopularFilms, handlerFilms;

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
            databaseManager = DatabaseManager.getInstance(view.getContext());
            baseLayout = view.findViewById(R.id.fragment_main_ll_layout);

            //строка с популярными фильмами
            handlerPopularFilms = new Handler() {
                public void handleMessage(Message msg) {
                    Bundle bundle = msg.getData();
                    createFilmRow((Film[]) bundle.getSerializable("film"), "Popular");
                }
            };

            //создаем строки с указанными жанрами
            handlerFilms = new Handler() {
                public void handleMessage(Message msg) {
                    Bundle bundle = msg.getData();
                    createFilmRow((Film[]) bundle.getSerializable("film"), bundle.getString("genre"));
                }
            };

            Thread threadFilms = new Thread(new Runnable() {
                public void run() {
                    Message msg = handlerPopularFilms.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("film", databaseManager.getPopularFilmsLimited(FILMS_IN_ROW));
                    msg.setData(bundle);
                    handlerPopularFilms.sendMessage(msg);

                    String[] genres = new String[]{"Action", "Fantasy", "Comedy", "Animation"};
                    for (String genre : genres) {
                        Message msg2 = handlerFilms.obtainMessage();
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("film", databaseManager.getFilmsByGenreLimited(genre, 2010, FILMS_IN_ROW));
                        bundle2.putString("genre", genre);
                        msg2.setData(bundle2);
                        handlerFilms.sendMessage(msg2);
                    }
                }
            });
            threadFilms.start();
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
                FragmentHelper.openFragment(getFragmentManager(), getActivity(), FilmFragment.newInstance(film));
            }
        });

        layout.addView(filmLayout);
    }

    //добавляем кнопку открытия всех фильмов по категории
    public void addMoreBtn(LinearLayout linearLayout, final String genre) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_more, null);
        ((ImageView) layout.getChildAt(0)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(getFragmentManager(), getActivity(), CategoryFragment.newInstance(genre));
            }
        });
        linearLayout.addView(layout);
    }
}