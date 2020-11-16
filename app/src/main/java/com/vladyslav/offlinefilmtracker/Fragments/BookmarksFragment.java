package com.vladyslav.offlinefilmtracker.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FileManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.Objects.FilmAdapter;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class BookmarksFragment extends Fragment {
    private View view;
    private FilmAdapter adapter; //адаптер фильмов в RecyclerView
    private ArrayList<Film> films;
    private ArrayList<String> filmsIDs;
    private boolean isChanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) bookmarksCheck(); //проверяем изменение закладок
        if (view == null || isChanged) {
            films = new ArrayList<>();
            view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
            loadFilmsFromBookmarks(); //загружаем фильмы
        }
        return view;
    }

    //проверка изменений закладок
    public void bookmarksCheck() {
        //загружаем массив новых данных
        ArrayList<String> filmsIDsTmp = FileManager.read(getContext());

        //проверяем, если размеры разные, то значит фильм был удален\добавлен
        //иначе проверяем содержимое листов на наличие различий
        if (filmsIDsTmp.size() != films.size()) isChanged = true;
        else isChanged = !(filmsIDs.containsAll(filmsIDsTmp));

        //если id фильмов разные, значит закладки изменились
        if (isChanged) filmsIDs = filmsIDsTmp;
    }

    //метод загрузки списка фильмов по id
    public void loadFilmsFromBookmarks() {
        DatabaseManager databaseManager = DatabaseManager.getInstance(getContext());
        if (!isChanged)
            filmsIDs = FileManager.read(getContext()); //если данные были измененеы, то загружать еще раз данные не требуется

        if (filmsIDs.size() != 0) {
            //загружаем из базы фильмы
            databaseManager.getFilmsByTitleIds(filmsIDs, films, new Runnable() {
                @Override
                public void run() {
                    initFilms();
                }
            });
        } else {
            ((ProgressBar) view.findViewById(R.id.fragment_bookmarks_pb_loading)).setVisibility(View.GONE);
            TextView hintTV = view.findViewById(R.id.fragment_bookmarks_tv_hint);
            hintTV.setVisibility(View.VISIBLE);
        }
    }

    //устанавливаем фильмы
    public void initFilms() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ProgressBar) view.findViewById(R.id.fragment_bookmarks_pb_loading)).setVisibility(View.GONE);

                final RecyclerView recyclerView = view.findViewById(R.id.fragment_bookmarks_films_rv_films);

                //создаем адаптер и добавляем его recyclerView
                adapter = new FilmAdapter(getContext(), films, getActivity());
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                recyclerView.setAdapter(adapter);
            }
        });
    }
}