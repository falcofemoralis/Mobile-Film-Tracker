package com.vladyslav.offlinefilmtracker.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Objects.Film;

public class FilmFragment extends Fragment {
    DatabaseManager databaseManager;
    Film film;

    public FilmFragment(Film film) {
        this.film = film;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_film, container, false);
        databaseManager = DatabaseManager.getInstance(view.getContext());

        setBaseFilmInfo(view);
        setAdditionalFilmInfo(view);
        setCrew(view);

        //устанавлиаем обработчик нажатия для актера
        //TODO убрать FORMAT !!!
        LinearLayout linearLayout = view.findViewById(R.id.actor_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper fragmentHelper = new FragmentHelper();
                fragmentHelper.openFragment(new ActorFilmsFragment());

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.activity_main_nv_bottomBar);
                bottomNavigationView.setVisibility(View.GONE);
            }
        });

        return view;
    }

    //устанавливаем основную информацию про фильм
    private void setBaseFilmInfo(View view) {
        //устанавливаем основную информацию
        ((TextView) view.findViewById(R.id.fragment_film_tv_rating)).setText(film.getRating() + "\n(" + film.getVotes() + ")");
        ((TextView) view.findViewById(R.id.fragment_film_tv_title)).setText(film.getTitle());
        ((ImageView) view.findViewById(R.id.fragment_film_iv_poster)).setImageDrawable(film.getPoster(getContext()));

        //устанавливаем жанры фильма
        String[] genres = film.getGenres();
        LinearLayout genresLayout = view.findViewById(R.id.fragment_film_ll_genres);
        for (int i = 0; i < genres.length; ++i) {
            TextView genresTV = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.inflate_tag, null);
            genresTV.setText(genres[i]);
            genresLayout.addView(genresTV);
        }
    }

    //устанавливаем допольнительную информацию про фильм
    private void setAdditionalFilmInfo(View view) {
        ((TextView) view.findViewById(R.id.fragment_film_tv_releaseDate)).setText("Release date" + ": " + film.getPremiered());
        ((TextView) view.findViewById(R.id.fragment_film_tv_runtime)).setText("Runtime" + ": " + film.getRuntime_minutes() + " minutes");

        TextView adult = view.findViewById(R.id.fragment_film_tv_adult);
        if (film.getIsAdult())
            adult.setText("18+");
        else
            adult.setVisibility(View.GONE);
    }

    private void setCrew(View view) {
        Actor[] actors = databaseManager.getActorsByTitleId(film.getFilm_id());

        TextView directorTV = view.findViewById(R.id.fragment_film_tv_directors);
        directorTV.setText("Director: ");

        for (Actor actor : actors) {
            switch (actor.getCategory()) {
                case "director":
                    directorTV.append(actor.getName() + " ");
            }
        }
    }
}
