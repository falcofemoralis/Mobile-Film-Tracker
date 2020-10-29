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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.Objects.FilmAdapter;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class FilmsListFragment extends Fragment {
    private static final String ARG_SELECTPARAM = "param1", ARG_ISGENRE = "param2"; //параметр жанра и параметр указавыющий на принадлежность к жанру
    private String selectParam; //строка по которой будут выбраны фильмы из базы
    private boolean isGenre; //логическая переменная принадлежности строки выборки к жанру
    private NestedScrollView scrollView; //вью прокручивателя
    private final int FILMS_PER_SCROLL = 18; //кол-во фильмов за пролистывание
    private ProgressBar progressBar; //бар загрузки
    private Cursor filmsCursor; //курсор загруженных фильмов
    private ArrayList<Film> films = new ArrayList<>(); //список текуших фильмов
    private FilmAdapter adapter; //адаптер фильмов в RecyclerView
    private View view; //вью фрагмента

    public static FilmsListFragment newInstance(String selectParam, boolean isGenre) {
        FilmsListFragment fragment = new FilmsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTPARAM, selectParam);
        args.putBoolean(ARG_ISGENRE, isGenre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectParam = getArguments().getString(ARG_SELECTPARAM);
            isGenre = getArguments().getBoolean(ARG_ISGENRE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_filmslist, container, false);
            progressBar = view.findViewById(R.id.fragment_filmslist_pb_loading);
            scrollView = getActivity().findViewById(R.id.nestedScrollView);

            //устанаваливаем заголовок фрагмента (если параметр выборки жанр)
            TextView genreText = view.findViewById(R.id.fragment_filmslist_films_tv_header);
            if (isGenre) genreText.setText(getString(R.string.films, selectParam));
            else genreText.setVisibility(View.GONE);

            setFilms();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        //убираем листенер с ScrollView при выходите из этого фрагмента
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
        super.onDestroy();
    }

    //метод инциализация списка фильмов
    public void setFilms() {
        final RecyclerView recyclerView = view.findViewById(R.id.fragment_filmslist_films_rv_films);

        //получаем фильмы
        addFilms(new Runnable() {
            @Override
            public void run() {
                //создаем адаптер и добавляем его recyclerView
                adapter = new FilmAdapter(getContext(), films);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView.setAdapter(adapter);

                //убираем бар загрузки
                progressBar.setVisibility(View.GONE);
            }
        });

        //при прокрутке в низ, будет установленно FILMS_PER_SCROLL фильмов
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
                        //обновляем адаптер фильмов в RecyclerView
                        adapter.notifyDataSetChanged();

                        //выключаем бар загрузки
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    //метод добавления фильмов в RecyclerView в количестве FILMS_PER_SCROLL штук
    public void addFilms(final Runnable runnable) {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        //проверяем были ли загруженны фильмы из базы данных
        if (filmsCursor == null) {
            if (isGenre)
                filmsCursor = DatabaseManager.getInstance(getContext()).getFilmsByGenre(selectParam);
            else
                filmsCursor = DatabaseManager.getInstance(getContext()).getFilmsByTitle(selectParam);
        }

        //включаем бар загрузки
        progressBar.setVisibility(View.VISIBLE);

        (new Thread() {
            public void run() {
                //достаем FILMS_PER_SCROLL фильмов из курсора и добавляем в список
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
