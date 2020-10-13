package com.vladyslav.offlinefilmtracker.Objects;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.R;

import java.util.Arrays;

public class Film {
    private String film_id;
    private String title;
    private String rating;
    private String votes;
    private String runtime_minutes;
    private String premiered;
    private boolean isAdult;
    private String[] genres;

    public Film(String film_id, String title, String rating, String votes, String runtime_minutes, String premiered, String isAdult, String genres) {
        this.film_id = film_id;
        this.title = title;
        this.rating = rating;
        this.votes = votes;
        this.runtime_minutes = runtime_minutes;
        this.premiered = premiered;
        this.isAdult = Boolean.parseBoolean(isAdult);
        this.genres = genres.split(","); //т.к данны приходят в формате String_1, String_2, то нужно разделить;
    }

    public String getFilm_id() {
        return film_id;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public String getVotes() {
        return votes;
    }

    public Drawable getPoster(Context context) {
        return ResourcesManager.getInstance(context).getPosterByTitleId(film_id);
    }

    public String getRuntime_minutes() {
        return runtime_minutes;
    }

    public String getPremiered() {
        return premiered;
    }

    public boolean getIsAdult() {
        return isAdult;
    }

    public String[] getGenres() {
        return genres;
    }

    @Override
    public String toString() {
        return "Film{" +
                "film_id='" + film_id + '\'' +
                ", title='" + title + '\'' +
                ", rating='" + rating + '\'' +
                ", votes='" + votes + '\'' +
                ", runtime_minutes='" + runtime_minutes + '\'' +
                ", premiered='" + premiered + '\'' +
                ", isAdult='" + isAdult + '\'' +
                ", genres=" + Arrays.toString(genres) +
                '}';
    }
}
