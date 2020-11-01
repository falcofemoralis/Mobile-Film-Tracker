package com.vladyslav.offlinefilmtracker.Fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class FilmFragment extends Fragment {
    private final double POSTER_SCALE_FACTOR = 0.55; //размер постеров у фильмов
    private final double PHOTO_SCALE_FACTOR = 1.5; //размер фото у актеров
    private static final String ARG_FILM = "param1"; //параметр объект фильма
    private Film film; //объект фильма
    private View view; ///вью фрагмента

    public static FilmFragment newInstance(Film film) {
        FilmFragment fragment = new FilmFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILM, film);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            film = (Film) getArguments().getSerializable(ARG_FILM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_film, container, false);

            setBaseFilmInfo();
            setAdditionalFilmInfo();
            getCrew();
        }
        return view;
    }

    //метод установки основонй информации про фильм (рейтинг, название, постер, жанры)
    public void setBaseFilmInfo() {
        //устанавливаем основную информацию
        ((TextView) view.findViewById(R.id.fragment_film_tv_rating)).setText(getString(R.string.rating, film.getRating(), film.getVotes()));
        ((TextView) view.findViewById(R.id.fragment_film_tv_title)).setText(film.getTitle());

        final BitmapDrawable[] poster = new BitmapDrawable[1];
        film.getPoster(getContext(), poster, new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //устанавливаем постер
                        ImageView posterView = view.findViewById(R.id.fragment_film_iv_poster);

                        posterView.setLayoutParams(new LinearLayout.LayoutParams((ResourcesManager.getDpFromPx(poster[0].getBitmap().getWidth(), POSTER_SCALE_FACTOR, getContext())),
                                (ResourcesManager.getDpFromPx(poster[0].getBitmap().getHeight(), POSTER_SCALE_FACTOR, getContext()))));
                        posterView.setImageDrawable(poster[0]);
                    }
                });
            }
        });

        //устанавливаем жанры фильма
        String[] filmGenres = film.getGenres();
        LinearLayout genresLayout = view.findViewById(R.id.fragment_film_ll_genres);

        for (String genre : filmGenres) {
            TextView genresTV = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.inflate_tag, null);
            genresTV.setText(ResourcesManager.getGenreStringById(DatabaseManager.getInstance(getContext()).getGenreById(genre), getContext()));
            genresLayout.addView(genresTV);
        }
    }

    //метод установки допольнительной информации про фильм (дата, время, сюжет и пр)
    public void setAdditionalFilmInfo() {
        ((TextView) view.findViewById(R.id.fragment_film_tv_releaseDate)).setText(getString(R.string.release_date, film.getPremiered()));
        ((TextView) view.findViewById(R.id.fragment_film_tv_runtime)).setText(getString(R.string.runtime, film.getRuntime_minutes()));
        ((TextView) view.findViewById(R.id.fragment_film_tv_plot)).setText(film.getPlot());

        TextView adult = view.findViewById(R.id.fragment_film_tv_adult);
        if (film.getIsAdult())
            adult.setText(R.string.adult);
        else
            adult.setVisibility(View.GONE);
    }

    public void getCrew() {
        final ArrayList<Actor> actors = new ArrayList<>();
        DatabaseManager.getInstance(view.getContext()).getActorsByTitleId(film.getFilm_id(), actors, new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCrew(actors);
                    }
                });
            }
        });
    }

    //метод установки кооманды фильма
    public void setCrew(ArrayList<Actor> actors) {
        LinearLayout actorsLayout = view.findViewById(R.id.fragment_film_ll_actorsLayout);

        //список людей по ролям т.е список режисеров, продюсеров и писателей
        ArrayList<ArrayList<SpannableString>> spannablePersonNamesList = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            ArrayList<SpannableString> list = new ArrayList<>();
            spannablePersonNamesList.add(list);
        }

        //лист пар из ключа жанра (в базе) и id строковой константы
        ArrayList<Pair<Integer, Integer>> personRoleTextViews = new ArrayList<Pair<Integer, Integer>>() {
            {
                add(new Pair<>(R.id.fragment_film_tv_directors, R.string.directors));
                add(new Pair<>(R.id.fragment_film_tv_producers, R.string.producers));
                add(new Pair<>(R.id.fragment_film_tv_writers, R.string.writers));
            }
        };

        //распределяем людей по типу роли в фильме в списки
        int n;
        for (Actor actor : actors) {
            switch (actor.getCategory()) {
                case "director":
                    n = 0;
                    break;
                case "producer":
                    n = 1;
                    break;
                case "writer":
                    n = 2;
                    break;
                default:
                    setActor(actor, actorsLayout);
                    continue;
            }
            spannablePersonNamesList.get(n).add(setClickableActorName((TextView) view.findViewById(personRoleTextViews.get(n).first), actor));
        }

        //устанавливаем актеров во вьюшки
        for (int i = 0; i < spannablePersonNamesList.size(); i++) {
            int size = spannablePersonNamesList.get(i).size();
            TextView textView = view.findViewById(personRoleTextViews.get(i).first);

            if (size > 0) textView.setText(getString(personRoleTextViews.get(i).second));
            else textView.setVisibility(View.GONE);

            for (int j = 0; j < size; j++) {
                textView.append(spannablePersonNamesList.get(i).get(j));
                if (j != size - 1) textView.append(", ");
            }
        }
    }

    //метод установки актера в строку актеров
    public void setActor(final Actor actor, LinearLayout actorsLayout) {
        final LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_actor, null);

        //ставим имя актера
        ((TextView) layout.getChildAt(1)).setText(actor.getName());

        //ставим фото актера
        final BitmapDrawable[] photo = new BitmapDrawable[1];
        actor.getPhoto(getContext(), photo, new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView photoView = (ImageView) layout.getChildAt(0);
                        photoView.setLayoutParams(new LinearLayout.LayoutParams((ResourcesManager.getDpFromPx(photo[0].getBitmap().getWidth(), PHOTO_SCALE_FACTOR, getContext())),
                                (ResourcesManager.getDpFromPx(photo[0].getBitmap().getHeight(), PHOTO_SCALE_FACTOR, getContext()))));
                        photoView.setImageDrawable(photo[0]);

                    }
                });
            }
        });

        //устанавливаем персонажей актера в фильме
        TextView charactersTV = (TextView) layout.getChildAt(2);
        for (String character : actor.getCharacters())
            charactersTV.append(character + "\n");

        //добавляем переход на фрагмент актера
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(ActorFragment.newInstance(actor));
            }
        });

        actorsLayout.addView(layout);
    }

    //метод установки перехода по нажатию на имя режисера, сценариста, продюсера при помощи SpannableString
    public SpannableString setClickableActorName(TextView textView, final Actor actor) {
        SpannableString ss = new SpannableString(actor.getName());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                FragmentHelper.openFragment(ActorFragment.newInstance(actor));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        return ss;
    }


}
