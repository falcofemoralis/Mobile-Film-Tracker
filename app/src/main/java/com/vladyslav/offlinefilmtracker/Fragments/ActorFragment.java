package com.vladyslav.offlinefilmtracker.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.Objects.FilmAdapter;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorFragment extends Fragment {
    private static final String ARG_ACTOR = "param1"; //параметр объекта актера
    private Actor actor; //объект актера
    private View view; ///вью фрагмента
    private LinearLayout baseLayout; //базовый лаяут для добавления списка фильмов
    private ProgressBar progressBar; //бар загрузки

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_actor, container, false);
            baseLayout = view.findViewById(R.id.fragment_actor_films_ll_main);
            progressBar = view.findViewById(R.id.fragment_actor_pb_loading);

            setActorBaseInfo();
            setFilms();
        }
        return view;
    }

    //метод установки базовой информации актера (имя, фото, даты рождения/смерти)
    public void setActorBaseInfo() {
        ((TextView) view.findViewById(R.id.fragment_actor_films_tv_name)).setText(actor.getName());
        ((ImageView) view.findViewById(R.id.fragment_actor_films_iv_photo)).setImageDrawable(actor.getPhoto(getContext()));

        //установка даты рождения актера
        TextView bornTV = view.findViewById(R.id.fragment_actor_films_tv_born);
        String born = actor.getBorn();
        if (born != null) bornTV.setText(getString(R.string.actor_born, born));
        else bornTV.setText(getString(R.string.unknown));

        //установка даты смерти актера
        TextView diedTV = view.findViewById(R.id.fragment_actor_films_tv_died);
        String died = actor.getDied();
        if (died != null) diedTV.setText(getString(R.string.actor_died, died));
        else diedTV.setVisibility(View.GONE);
    }

    //метод установки фильмов
    public void setFilms() {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        final Map<String, Integer> rolesMap = new HashMap<String, Integer>() {{
            put("actor", 0);
            put("director", 1);
            put("producer", 2);
            put("writer", 3);
        }};

        final ArrayList<ArrayList<Film>> filmByRoles = new ArrayList<>();
        for (int i = 0; i < rolesMap.size(); ++i) {
            ArrayList<Film> list = new ArrayList<>();
            filmByRoles.add(list);
        }

        progressBar.setVisibility(View.VISIBLE);

        (new Thread() {
            public void run() {
                //получаем фильмы по жанру
                Film[] films = DatabaseManager.getInstance(getContext()).getFilmsByPersonId(actor.getPerson_id());

                //распределяем фильмы в зависимости от роли
                for (Film film : films) {
                    String[] roles = DatabaseManager.getInstance(getContext()).getRoleByPersonAndTitleId(actor.getPerson_id(), film.getFilm_id());
                    for (String role : roles) {
                        int roleId = 0;
                        try {
                            roleId = rolesMap.get(role);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        filmByRoles.get(roleId).add(film);
                    }
                }

                //устанавливаем полученные фильмы в строки в UI потоке
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

                        //установка всех фильмов акетера в RecyclerView
                        for (int i = 0; i < filmByRoles.size(); ++i) {
                            if (filmByRoles.get(i).size() != 0) {
                                //создаем вью заголовка
                                TextView textView = new TextView(getContext());
                                LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                textLayoutParams.setMarginStart(ResourcesManager.getPxFromDp(15, getContext()));
                                textView.setLayoutParams(textLayoutParams);
                                textView.setTextAppearance(R.style.Header);

                                //устанавливаем текст заголовка
                                textView.setText(ResourcesManager.getRoleById(getKey(rolesMap, i),getContext()));
                                baseLayout.addView(textView);

                                //создаем RecyclerView (список фильмов)
                                RecyclerView recyclerView = new RecyclerView(getContext());
                                recyclerView.setLayoutParams(layoutParams);
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                                FilmAdapter adapter = new FilmAdapter(getContext(), filmByRoles.get(i));
                                recyclerView.setAdapter(adapter);
                                baseLayout.addView(recyclerView);
                            }
                        }
                        view.findViewById(R.id.fragment_actor_pb_loading).setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    //метод получаем ключа из хешмапы
    public <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}