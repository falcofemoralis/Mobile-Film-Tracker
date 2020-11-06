package com.vladyslav.offlinefilmtracker.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.Objects.FilmAdapter;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class FilmsListFragment extends Fragment {
    private static final String ARG_SELECTPARAM = "param1", ARG_ISGENRE = "param2"; //параметр жанра и параметр указавыющий на принадлежность к жанру
    private String title; //строка(название фильма) по которой будут выбраны фильмы из базы
    private int genreId; //id жанра
    private boolean isGenre; //логическая переменная принадлежности строки выборки к жанру
    private NestedScrollView scrollView; //вью прокручивателя
    private final int FILMS_PER_SCROLL = 18; //кол-во фильмов за пролистывание
    private ProgressBar progressBar; //бар загрузки
    private Cursor filmsCursor; //курсор загруженных фильмов
    private ArrayList<Film> films = new ArrayList<>(); //список текуших фильмов
    private FilmAdapter adapter; //адаптер фильмов в RecyclerView
    private View view; //вью фрагмента
    private int count; //кол-во FILMS_PER_SCROLL добавленно в адаптер

    //создание объекта FilmsListFragment в случае жанра
    public static FilmsListFragment newInstance(int genreId) {
        FilmsListFragment fragment = new FilmsListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SELECTPARAM, genreId);
        args.putBoolean(ARG_ISGENRE, true);
        fragment.setArguments(args);
        return fragment;
    }

    //создание объекта FilmsListFragment в случае названия фильма
    public static FilmsListFragment newInstance(String selectParam) {
        FilmsListFragment fragment = new FilmsListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SELECTPARAM, selectParam);
        args.putBoolean(ARG_ISGENRE, false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isGenre = getArguments().getBoolean(ARG_ISGENRE);
            if (isGenre) genreId = getArguments().getInt(ARG_SELECTPARAM);
            else title = getArguments().getString(ARG_SELECTPARAM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_filmslist, container, false);
            progressBar = view.findViewById(R.id.fragment_filmslist_pb_loading);
            scrollView = getActivity().findViewById(R.id.nestedScrollView);

            //устанаваливаем заголовок фрагмента (если параметр являетсяжанр)
            TextView genreText = view.findViewById(R.id.fragment_filmslist_films_tv_header);
            if (isGenre)
                genreText.setText(getString(R.string.films, DatabaseManager.getInstance(getContext()).getGenreById(genreId)));
            else
                genreText.setVisibility(View.GONE);

            initFilms();
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
    public void initFilms() {
        final RecyclerView recyclerView = view.findViewById(R.id.fragment_filmslist_films_rv_films);

        //получаем фильмы
        getFilms(new Runnable() {
            @Override
            public void run() {
                //создаем адаптер и добавляем его recyclerView
                adapter = new FilmAdapter(getContext(), films, getActivity());
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
                if (diff == 0) {
                    //включаем бар загрузки
                    progressBar.setVisibility(View.VISIBLE);

                    addFilms(new Runnable() {
                        @Override
                        public void run() {
                            //обновляем адаптер фильмов в RecyclerView
                            adapter.notifyItemRangeInserted(FILMS_PER_SCROLL * count, FILMS_PER_SCROLL);

                            //выключаем бар загрузки
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    //метод получения фильмов в RecyclerView в количестве FILMS_PER_SCROLL штук
    public void getFilms(final Runnable runnable) {
        //проверяем были ли загруженны фильмы из базы данных
        DatabaseManager databaseManager = DatabaseManager.getInstance(getContext());

        //если курсор null (не были загруженны фильмы) получаем их из базы данных
        final ArrayList<Cursor> cursorTmp = new ArrayList<>();
        Runnable getFilmsRunnable = new Runnable() {
            @Override
            public void run() {
                filmsCursor = cursorTmp.get(0);
                addFilms(runnable);
            }
        };
        if (isGenre)
            databaseManager.getFilmsByGenre(genreId, cursorTmp, getFilmsRunnable);
        else databaseManager.getFilmsByTitle(title, cursorTmp, getFilmsRunnable);
    }

    //метод добавления фильмов в RecyclerView в количестве FILMS_PER_SCROLL штук
    public void addFilms(final Runnable runnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < FILMS_PER_SCROLL; i++) {
                    if (filmsCursor.moveToNext())
                        films.add(DatabaseManager.getInstance(getContext()).getFilmData(filmsCursor));
                    else
                        break;
                }
                count++;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                });
            }
        }).start();
    }
}
