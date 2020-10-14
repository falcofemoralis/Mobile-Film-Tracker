package com.vladyslav.offlinefilmtracker.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

public class ActorFragment extends Fragment {
    private Actor actor;

    public ActorFragment(Actor actor) {
        this.actor = actor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actor, container, false);


        setFilms(view);
        ((TextView) view.findViewById(R.id.fragment_actor_films_tv_name)).setText(actor.getName());
        return view;
    }

    private void setFilms(View view) {
        TableLayout tableLayout = view.findViewById(R.id.fragment_actor_films_tl_films);
        Film[] films = DatabaseManager.getInstance(getContext()).getFilmsByPersonId(actor.getPerson_id());

        TableRow rowSubject = null;
        int size = films.length;
        for (int i = 0; i < size; ++i) {
            // В одном ряду может быть лишь 3 кнопки, если уже три созданы, создается следующая колонка
            if (i % 3 == 0) {
                ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                rowSubject = new TableRow(getContext());
                rowSubject.setLayoutParams(params);
                rowSubject.setOrientation(TableRow.HORIZONTAL);
                rowSubject.setWeightSum(3f);
                tableLayout.addView(rowSubject);
            }

            LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowSubject, false);

            //ставим постер
            ImageView filmPoster = (ImageView) filmLayout.getChildAt(0);
            Drawable poster = films[i].getPoster(getContext());
            filmPoster.setLayoutParams(new LinearLayout.LayoutParams((int) (poster.getIntrinsicWidth() * 2.5f), (int) (poster.getIntrinsicHeight() * 2.5f)));
            filmPoster.setImageDrawable(poster);

            ((TextView) filmLayout.getChildAt(1)).setText(films[i].getTitle());
            ((TextView) filmLayout.getChildAt(2)).setText(films[i].getRating());

            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(150,250);
            ((ImageView) filmLayout.getChildAt(0)).setLayoutParams(params);

            filmLayout.setGravity(Gravity.CENTER);
            rowSubject.addView(filmLayout);
        }

        int n =  ((size / 3) * 3 + 3) - size;
        for (int i = 0; i < n; ++i) {
            LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowSubject, false);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(150,250);
            ((ImageView) filmLayout.getChildAt(0)).setLayoutParams(params);
            filmLayout.setGravity(Gravity.CENTER);
            filmLayout.setVisibility(View.INVISIBLE);
            rowSubject.addView(filmLayout);
        }
    }
}