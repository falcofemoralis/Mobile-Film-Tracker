package com.vladyslav.offlinefilmtracker.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

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

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class ActorFragment extends Fragment {
    private static final String ARG_ACTOR = "param1";
    private Actor actor;
    private View view;
    private LinearLayout filmsLayout;

    public static ActorFragment newInstance(Actor actor) {
        ActorFragment fragment = new ActorFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACTOR, actor);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            actor = (Actor) getArguments().getSerializable(ARG_ACTOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_actor, container, false);
        filmsLayout = view.findViewById(R.id.fragment_actor_films_ll_films);
        setActorBaseInfo();
        setFilmsTables();
        return view;
    }

    //устанавливаем базовую информацию актеру
    public void setActorBaseInfo() {
        ((TextView) view.findViewById(R.id.fragment_actor_films_tv_name)).setText(actor.getName());
        ((ImageView) view.findViewById(R.id.fragment_actor_films_iv_photo)).setImageDrawable(actor.getPhoto(getContext()));
        ((TextView) view.findViewById(R.id.fragment_actor_films_tv_born)).setText("Born: " + actor.getBorn());
        ((TextView) view.findViewById(R.id.fragment_actor_films_tv_died)).setText("Died: " + actor.getDied());
    }

    //сортируем фильмы по роли
    public void setFilmsTables() {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        final ArrayList<ArrayList<Film>> filmByRoles = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            ArrayList<Film> list = new ArrayList<>();
            filmByRoles.add(list);
        }
        view.findViewById(R.id.fragment_actor_pb_loading).setVisibility(View.VISIBLE);

        //создаем поток
        (new Thread() {
            public void run() {
                //получаем фильмы по жанру
                Film[] films = DatabaseManager.getInstance(getContext()).getFilmsByPersonId(actor.getPerson_id());

                //распределяем фильмы в зависимости от роли
                for (Film film : films) {
                    String[] roles = DatabaseManager.getInstance(getContext()).getRoleByPersonAndTitleId(actor.getPerson_id(), film.getFilm_id());
                    int n;
                    for (String role : roles) {
                        switch (role) {
                            case "director":
                                n = 1;
                                break;
                            case "producer":
                                n = 2;
                                break;
                            case "writer":
                                n = 3;
                                break;
                            default:
                                n = 0;
                        }
                        filmByRoles.get(n).add(film);
                    }
                }
                //устанавливаем полученные фильмы в строки в UI потоке
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //получение всех фильмов акетера
                        String[] roles = new String[]{"Actor", "Director", "Producer", "Writer"};
                        for (int i = 0; i < filmByRoles.size(); ++i) {
                            if (filmByRoles.get(i).size() != 0) {
                                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_films_table, null);
                                ((TextView) linearLayout.getChildAt(0)).setText(roles[i]);
                                setFilms(filmByRoles.get(i), (TableLayout) linearLayout.getChildAt(1));
                                filmsLayout.addView(linearLayout);
                            }
                        }
                        view.findViewById(R.id.fragment_actor_pb_loading).setVisibility(View.GONE);
                        filmsLayout.setVisibility(View.VISIBLE);

                    }
                });

            }
        }).start();
    }

    //устанавливаем фильмы актеру
    public void setFilms(ArrayList<Film> films, TableLayout tableLayout) {
        //необходимые переменные
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
                tableLayout.addView(rowFilms);
            }

            //создаем лаяут самого фильма
            LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowFilms, false);

            //устанавлиавем постер
            ImageView filmPoster = (ImageView) filmLayout.getChildAt(0);
            Drawable poster = films.get(i).getPoster(getContext());
            filmPoster.setLayoutParams(new LinearLayout.LayoutParams((int) (poster.getIntrinsicWidth() * 2.5f), (int) (poster.getIntrinsicHeight() * 2.5f)));
            filmPoster.setImageDrawable(poster);

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
            rowFilms.addView(filmLayout);
        }

        //заполняются остаточные блоки
        int n = 0;
        if (size % 3 != 0) n = ((size / 3) * 3 + 3) - size;

        for (int i = 0; i < n; ++i) {
            LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowFilms, false);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(150, 250);
            filmLayout.getChildAt(0).setLayoutParams(params);
            filmLayout.setGravity(Gravity.CENTER);
            filmLayout.setVisibility(View.INVISIBLE);
            rowFilms.addView(filmLayout);
        }
    }
}