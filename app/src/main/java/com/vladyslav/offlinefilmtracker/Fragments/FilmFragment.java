package com.vladyslav.offlinefilmtracker.Fragments;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private static final String ARG_FILM = "param1";
    private DatabaseManager databaseManager;
    private Film film;
    private View view;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_film, container, false);
            databaseManager = DatabaseManager.getInstance(view.getContext());
            setBaseFilmInfo();
            setAdditionalFilmInfo();
            setCrew();
        }
        return view;
    }


    //устанавливаем основную информацию про фильм
    public void setBaseFilmInfo() {
        //устанавливаем основную информацию
        ((TextView) view.findViewById(R.id.fragment_film_tv_rating)).setText(film.getRating() + "\n(" + film.getVotes() + ")");
        ((TextView) view.findViewById(R.id.fragment_film_tv_title)).setText(film.getTitle());

        ImageView posterView = view.findViewById(R.id.fragment_film_iv_poster);
        BitmapDrawable poster = film.getPoster(getContext());
        posterView.setLayoutParams(new LinearLayout.LayoutParams((ResourcesManager.getDpFromPx(poster.getBitmap().getWidth(), POSTER_SCALE_FACTOR, getContext())),
                (ResourcesManager.getDpFromPx(poster.getBitmap().getHeight(), POSTER_SCALE_FACTOR, getContext()))));
        posterView.setImageDrawable(poster);

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
    public void setAdditionalFilmInfo() {
        ((TextView) view.findViewById(R.id.fragment_film_tv_releaseDate)).setText("Release date" + ": " + film.getPremiered());
        ((TextView) view.findViewById(R.id.fragment_film_tv_runtime)).setText("Runtime" + ": " + film.getRuntime_minutes() + " minutes");
        ((TextView) view.findViewById(R.id.fragment_film_tv_plot)).setText(film.getPlot());


        TextView adult = view.findViewById(R.id.fragment_film_tv_adult);
        if (film.getIsAdult())
            adult.setText("18+");
        else
            adult.setVisibility(View.GONE);
    }

    //устанавливем комманду
    public void setCrew() {
        Actor[] actors = databaseManager.getActorsByTitleId(film.getFilm_id());
        LinearLayout actorsLayout = view.findViewById(R.id.fragment_film_ll_actorsLayout);

        ArrayList<ArrayList<SpannableString>> personsStrings = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            ArrayList<SpannableString> list = new ArrayList<>();
            personsStrings.add(list);
        }

        ArrayList<TextView> personsTextViews = new ArrayList<>();
        personsTextViews.add((TextView) view.findViewById(R.id.fragment_film_tv_directors));
        personsTextViews.get(0).setText("Director: ");

        personsTextViews.add((TextView) view.findViewById(R.id.fragment_film_tv_producers));
        personsTextViews.get(1).setText("Producers: ");

        personsTextViews.add((TextView) view.findViewById(R.id.fragment_film_tv_writers));
        personsTextViews.get(2).setText("Writers: ");

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
            personsStrings.get(n).add(setClickableActorName(personsTextViews.get(n), actor));
        }

        for (int i = 0; i < personsStrings.size(); i++) {
            for (int j = 0; j < personsStrings.get(i).size(); j++) {
                personsTextViews.get(i).append(personsStrings.get(i).get(j));
                if (j != personsStrings.get(i).size() - 1) personsTextViews.get(i).append(", ");
            }
        }
    }

    //установка актера в колонку Crew
    public void setActor(final Actor actor, LinearLayout actorsLayout) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_actor, null);

        //ставим постер
        BitmapDrawable photo = actor.getPhoto(getContext());
        ImageView photoView = (ImageView) layout.getChildAt(0);
        photoView.setLayoutParams(new LinearLayout.LayoutParams((int) (ResourcesManager.getDpFromPx(photo.getBitmap().getWidth(), PHOTO_SCALE_FACTOR, getContext())),
                (int) (ResourcesManager.getDpFromPx(photo.getBitmap().getHeight(), PHOTO_SCALE_FACTOR, getContext()))));
        photoView.setImageDrawable(photo);

        ((TextView) layout.getChildAt(1)).setText(actor.getName());

        TextView charactersTV = (TextView) layout.getChildAt(2);
        charactersTV.setText("");
        for (String character : actor.getCharacters())
            charactersTV.append(character + "\n");

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(ActorFragment.newInstance(actor));
            }
        });
        actorsLayout.addView(layout);
    }

    //установка перехода по нажатию на имя режисера, сценариста, продюсера
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
