package com.vladyslav.offlinefilmtracker.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.Objects.FilmAdapter;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    private static final String ARG_GENRE = "param1";
    private static final int FILMS_PER_SCROLL = 18;
    private String genre;
    private View view;
    private NestedScrollView scrollView;
    private Cursor filmsCursor;
    private final ArrayList<Film> films = new ArrayList<>();
    FilmAdapter adapter;
    ProgressBar progressBar;

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
            progressBar = view.findViewById(R.id.fragment_category_pb_loading);
            setFilms();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
        super.onDestroy();
    }

    //создаем таблицу фильмов
    public void setFilms() {
        //получаем необходимые лаяуты
        scrollView = getActivity().findViewById(R.id.nestedScrollView);
        final RecyclerView recyclerView = view.findViewById(R.id.fragment_category_films_rv_films);

        //устанаваливаем заголовок
        ((TextView) view.findViewById(R.id.fragment_category_films_tv_header)).setText(genre + " films");

        //получаем фильмы
        addFilms(new Runnable() {
            @Override
            public void run() {
                //создаем адаптер и добавляем его recyclerView
                adapter = new FilmAdapter(getContext(), films);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
        });

        //при прокрутке в низ, будет установленно 9 фильмов
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //вычесляем разницу
                View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                //если мы опустились в самый низ
                if (diff == 0) addFilms(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    //устанавливаем фильмы в количестве n штук
    public void addFilms(final Runnable runnable) {
        final Handler mHandler = new Handler(Looper.getMainLooper());
        if (filmsCursor == null)
            filmsCursor = DatabaseManager.getInstance(getContext()).getFilmsByGenre(genre);

        progressBar.setVisibility(View.VISIBLE);

        (new Thread() {
            public void run() {
                for (int i = 0; i < FILMS_PER_SCROLL; i++) {
                    if (filmsCursor.moveToNext())
                        films.add(DatabaseManager.getInstance(getContext()).getFilmData(filmsCursor));
                    else
                        break;
                }
                mHandler.post(runnable);
            }
        }).start();
    }
}
