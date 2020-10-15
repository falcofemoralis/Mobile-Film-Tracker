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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
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
        setActorBaseInfo(view);
        return view;
    }

    //устанавливаем базовую информацию актеру
    private void setActorBaseInfo(View view) {
        ((TextView) view.findViewById(R.id.fragment_actor_films_tv_name)).setText(actor.getName());
        ((ImageView) view.findViewById(R.id.fragment_actor_films_iv_photo)).setImageDrawable(actor.getPhoto(getContext()));
        ((TextView) view.findViewById(R.id.fragment_actor_films_tv_born)).setText("Born: " + actor.getBorn());
        ((TextView) view.findViewById(R.id.fragment_actor_films_tv_died)).setText("Died: " + actor.getDied());
    }

    //устанавливаем фильмы актеру
    private void setFilms(View view) {
        //разметка
        TableLayout tableLayout = view.findViewById(R.id.fragment_actor_films_tl_films);

        //получаем фильмы актера
        final Film[] films = DatabaseManager.getInstance(getContext()).getFilmsByPersonId(actor.getPerson_id());

        //необходимые переменные
        TableRow rowSubject = null;
        int size = films.length;

        for (int i = 0; i < size; ++i) {
            // В одном ряду может быть лишь 3 кнопки, если уже три созданы, создается следующая колонка
            if (i % 3 == 0) {
                rowSubject = new TableRow(getContext());
                ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                rowSubject.setLayoutParams(params);
                rowSubject.setOrientation(TableRow.HORIZONTAL);
                rowSubject.setWeightSum(1f);
                tableLayout.addView(rowSubject);
            }

            //создаем лаяут самого фильма
            LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowSubject, false);

            //устанавлиавем постер
            ImageView filmPoster = (ImageView) filmLayout.getChildAt(0);
            Drawable poster = films[i].getPoster(getContext());
            filmPoster.setLayoutParams(new LinearLayout.LayoutParams((int) (poster.getIntrinsicWidth() * 2.5f), (int) (poster.getIntrinsicHeight() * 2.5f)));
            filmPoster.setImageDrawable(poster);

            //устанавливаем базовую информацию
            ((TextView) filmLayout.getChildAt(1)).setText(films[i].getTitle());
            ((TextView) filmLayout.getChildAt(2)).setText(films[i].getRating());

            //добавляем в строку
            final int finalI = i;
            filmLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.openFragment(new FilmFragment(films[finalI]));
                }
            });
            rowSubject.addView(filmLayout);
        }

        //заполняются остаточные блоки
        int n = 0;
        if (size % 3 != 0) n = ((size / 3) * 3 + 3) - size;

        for (int i = 0; i < n; ++i) {
            LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowSubject, false);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(150, 250);
            filmLayout.getChildAt(0).setLayoutParams(params);
            filmLayout.setGravity(Gravity.CENTER);
            filmLayout.setVisibility(View.INVISIBLE);
            rowSubject.addView(filmLayout);
        }
    }
}