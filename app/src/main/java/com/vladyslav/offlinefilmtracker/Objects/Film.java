package com.vladyslav.offlinefilmtracker.Objects;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.R;

import java.io.Serializable;
import java.util.Arrays;

public class Film implements Serializable {
    private String film_id, title, rating, votes, runtime_minutes, premiered;
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

    public BitmapDrawable getPoster(Context context) {
        return ResourcesManager.getInstance(context).getDrawableById(film_id, true);
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
