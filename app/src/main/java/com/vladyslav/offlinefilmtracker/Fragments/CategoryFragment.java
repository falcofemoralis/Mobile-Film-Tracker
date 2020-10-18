package com.vladyslav.offlinefilmtracker.Fragments;

import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    private static final String ARG_GENRE = "param1";
    private static final int FILMS_PER_SCROLL = 9;
    private String genre;
    private View view;
    private NestedScrollView scrollView;
    private Cursor filmsCursor;

    public static CategoryFragment newInstance(String genre) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GENRE, genre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genre = getArguments().getString(ARG_GENRE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_category, container, false);
            scrollView = getActivity().findViewById(R.id.nestedScrollView);
            setFilmsTables();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
        super.onDestroy();
    }

    //создаем таблицу фильмов
    public void setFilmsTables() {
        //получаем необходимые лаяуты
        final LinearLayout filmsLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_films_table, null);
        final LinearLayout baseLayout = view.findViewById(R.id.fragment_category_films_ll_films);
        ((TextView) filmsLayout.getChildAt(0)).setText(genre + " films");

        //добавляем в базовый лаяут
        baseLayout.addView(filmsLayout);

        //получаем фильмы
        filmsCursor = DatabaseManager.getInstance(getContext()).getFilmsByGenre(genre);
        setFilms(filmsLayout);

        //при прокрутке в низ, будет установленно 9 фильмов
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //вычесляем разницу
                View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                //если мы опустились в самый низ
                if (diff == 0) setFilms(filmsLayout);
            }
        });

    }

    //устанавливаем фильмы в количестве 9 штук
    public void setFilms(LinearLayout filmsLayout) {
        final Handler mHandler = new Handler(Looper.getMainLooper());
        final TableLayout tableLayout = (TableLayout) filmsLayout.getChildAt(1);
        final ArrayList<Film> films = new ArrayList<>();
        view.findViewById(R.id.fragment_category_pb_loading).setVisibility(View.VISIBLE);

        //получаем фильмы и устанавливаем первые 9
        (new Thread() {
            public void run() {
                for (int i = 0; i < FILMS_PER_SCROLL; i++) {
                    if (filmsCursor.moveToNext())
                        films.add(DatabaseManager.getInstance(getContext()).getFilmData(filmsCursor));
                    else
                        break;
                }

                TableRow rowFilms = null;
                int size = films.size();

                for (int i = 0; i < size; ++i) {
                    // В одном ряду может быть лишь 3 кнопки, если уже три созданы, создается следующая колонка
                    if (i % 3 == 0) {
                        rowFilms = new TableRow(getContext());
                        ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                        rowFilms.setLayoutParams(params);
                        rowFilms.setOrientation(TableRow.HORIZONTAL);
                        rowFilms.setWeightSum(1f);

                        //устанавливаем полученные фильмы в строки в UI потоке
                        final TableRow finalRowSubject = rowFilms;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tableLayout.addView(finalRowSubject);
                            }
                        });
                    }

                    //создаем лаяут самого фильма
                    final LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowFilms, false);

                    //устанавлиавем постер
                    final ImageView filmPoster = (ImageView) filmLayout.getChildAt(0);
                    final BitmapDrawable poster = films.get(i).getPoster(getContext());
                    filmPoster.setLayoutParams(new LinearLayout.LayoutParams((int) (poster.getBitmap().getWidth() * 0.9f), (int) (poster.getBitmap().getHeight() * 0.9f)));

                    //устанавливаем базовую информацию
                    ((TextView) filmLayout.getChildAt(1)).setText(films.get(i).getTitle());
                    ((TextView) filmLayout.getChildAt(2)).setText(films.get(i).getRating());

                    //добавляем в строку
                    final Film film = films.get(i);
                    filmLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentHelper.openFragment(getFragmentManager(), getActivity(), FilmFragment.newInstance(film));
                        }
                    });
                    final TableRow finalRowFilms = rowFilms;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(view).load(poster).into(filmPoster);
                            finalRowFilms.addView(filmLayout);
                        }
                    });

                }

                //заполняются остаточные блоки
                int n = 0;
                if (size % 3 != 0) n = ((size / 3) * 3 + 3) - size;

                for (int i = 0; i < n; ++i) {
                    final LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowFilms, false);
                    ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(150, 250);
                    filmLayout.getChildAt(0).setLayoutParams(params);
                    filmLayout.setGravity(Gravity.CENTER);
                    filmLayout.setVisibility(View.INVISIBLE);
                    final TableRow finalRowFilms1 = rowFilms;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            finalRowFilms1.addView(filmLayout);
                        }
                    });
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        view.findViewById(R.id.fragment_category_pb_loading).setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }
}