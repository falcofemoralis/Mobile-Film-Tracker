package com.vladyslav.offlinefilmtracker.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.Objects.FilmAdapter;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    private final int FILMS_IN_ROW = 7; //кол-во фильмов в строке
    private LinearLayout baseLayout; //базовый лаяут
    private View view;
    private DatabaseManager databaseManager;

    public class MoreBtn {
        public int height;
        public String genre;

        public MoreBtn(int height, String genre) {
            this.height = height;
            this.genre = genre;
        }
    }

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
                        final ArrayList<Film> films;
                        if (genres[i].equals("Popular"))
                            films = databaseManager.getPopularFilms(FILMS_IN_ROW);
                        else
                            films = databaseManager.getFilmsByGenre(genres[i], 2015, FILMS_IN_ROW);

                        //устанавливаем полученные фильмы в строки в UI потоке
                        final int finalI = i;
                        mHandler.post(new Runnable() {
                            public void run() {
                                setFilms(films, genres[finalI]);
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

    public void setFilms(ArrayList<Film> films, final String genre) {
        TextView textView = new TextView(getContext());
        textView.setTextAppearance(R.style.Header);
        textView.setText(genre);
        baseLayout.addView(textView);

        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));

        FilmAdapter adapter;
        if (genre != "Popular") {
            MoreBtn moreBtn = new MoreBtn(((Film) films.get(0)).getPoster(getContext()).getBitmap().getHeight(), genre);
            adapter = new FilmAdapter(getContext(), films, moreBtn);
        } else {
            adapter = new FilmAdapter(getContext(), films);
        }

        recyclerView.setAdapter(adapter);
        baseLayout.addView(recyclerView);
    }
}
