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

    //устанавливем комманду
    private void setCrew(View view) {
        Actor[] actors = databaseManager.getActorsByTitleId(film.getFilm_id());
        LinearLayout actorsLayout = view.findViewById(R.id.fragment_film_ll_actorsLayout);

        TextView directorTV = view.findViewById(R.id.fragment_film_tv_directors);
        directorTV.setText("Director: ");

        TextView producerTV = view.findViewById(R.id.fragment_film_tv_producers);
        producerTV.setText("Producers: ");

        TextView writerTV = view.findViewById(R.id.fragment_film_tv_writers);
        writerTV.setText("Writers: ");
        for (final Actor actor : actors) {
            switch (actor.getCategory()) {
                case "director":
                    directorTV.append(actor.getName() + " ");
                    break;
                case "producer":
                    producerTV.append(actor.getName() + " ");
                    break;
                case "writer":
                    writerTV.append(actor.getName() + " ");
                    break;
                default:
                    LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_actor, null);
                    ((ImageView) layout.getChildAt(0)).setImageDrawable(actor.getPhoto(getContext()));
                    ((TextView) layout.getChildAt(1)).setText(actor.getName());
                    TextView charactersTV = (TextView) layout.getChildAt(2);
                    charactersTV.setText("");
                    for (String character : actor.getCharacters())
                        charactersTV.append(character + "\n");

                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FragmentHelper.openFragment(new ActorFragment(actor));
                            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.activity_main_nv_bottomBar);
                            bottomNavigationView.setVisibility(View.GONE);
                        }
                    });
                    actorsLayout.addView(layout);
            }
        }
    }
}
