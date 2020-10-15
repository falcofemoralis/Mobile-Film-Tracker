package com.vladyslav.offlinefilmtracker.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Objects.Film;

import java.util.ArrayList;

public class FilmFragment extends Fragment {
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
        view = inflater.inflate(R.layout.fragment_film, container, false);
        databaseManager = DatabaseManager.getInstance(view.getContext());
        setBaseFilmInfo();
        setAdditionalFilmInfo();
        setCrew();
        return view;
    }

    //устанавливаем основную информацию про фильм
    private void setBaseFilmInfo() {
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
    private void setAdditionalFilmInfo() {
        ((TextView) view.findViewById(R.id.fragment_film_tv_releaseDate)).setText("Release date" + ": " + film.getPremiered());
        ((TextView) view.findViewById(R.id.fragment_film_tv_runtime)).setText("Runtime" + ": " + film.getRuntime_minutes() + " minutes");

        TextView adult = view.findViewById(R.id.fragment_film_tv_adult);
        if (film.getIsAdult())
            adult.setText("18+");
        else
            adult.setVisibility(View.GONE);
    }

    //устанавливем комманду
    private void setCrew() {
        Actor[] actors = databaseManager.getActorsByTitleId(film.getFilm_id());
        LinearLayout actorsLayout = view.findViewById(R.id.fragment_film_ll_actorsLayout);

        //TODO упростить код
        ArrayList<SpannableString> directorsClickable = new ArrayList<>();
        TextView directorTV = view.findViewById(R.id.fragment_film_tv_directors);
        directorTV.setText("Director: ");

        ArrayList<SpannableString> producerClickable = new ArrayList<>();
        TextView producerTV = view.findViewById(R.id.fragment_film_tv_producers);
        producerTV.setText("Producers: ");

        ArrayList<SpannableString> writersClickable = new ArrayList<>();
        TextView writerTV = view.findViewById(R.id.fragment_film_tv_writers);
        writerTV.setText("Writers: ");

        for (final Actor actor : actors) {
            switch (actor.getCategory()) {
                case "director":
                    directorsClickable.add(setClickableActorName(directorTV, actor));
                    break;
                case "producer":
                    producerClickable.add(setClickableActorName(producerTV, actor));
                    break;
                case "writer":
                    writersClickable.add(setClickableActorName(writerTV, actor));
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
                            getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, ActorFragment.newInstance(actor)).addToBackStack(null).commit();
                        }
                    });
                    actorsLayout.addView(layout);
            }
        }

        for(int i=0;i<directorsClickable.size();i++){
            directorTV.append(directorsClickable.get(i));
            if(i!=directorsClickable.size()-1) directorTV.append(", ");
        }

        for(int i=0;i<producerClickable.size();i++){
            producerTV.append(producerClickable.get(i));
            if(i!=producerClickable.size()-1) producerTV.append(", ");
        }

        for(int i=0;i<writersClickable.size();i++){
            writerTV.append(writersClickable.get(i));
            if(i!=writersClickable.size()-1) writerTV.append(", ");
        }
    }


    private SpannableString setClickableActorName(TextView textView, final Actor actor){
        SpannableString ss = new SpannableString(actor.getName());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, ActorFragment.newInstance(actor)).addToBackStack(null).commit();
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
