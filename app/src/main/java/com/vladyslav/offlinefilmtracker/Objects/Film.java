package com.vladyslav.offlinefilmtracker.Objects;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;

import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;

import java.io.Serializable;
import java.util.Arrays;

public class Film implements Serializable {
    private String film_id, title, rating, votes, runtime_minutes, premiered, plot;
    private boolean isAdult;
    private int[] genresId;

    public Film(String film_id, String title, String rating, String votes, String runtime_minutes, String premiered, String isAdult, String[] genresIdString, String plot) {
        this.film_id = film_id;
        this.title = title;
        this.rating = rating;
        this.votes = votes;
        this.runtime_minutes = runtime_minutes;
        this.premiered = premiered;
        this.isAdult = Boolean.parseBoolean(isAdult);

        this.genresId = new int[genresIdString.length];
        for (int i = 0; i < genresIdString.length; ++i) {
            genresId[i] = Integer.parseInt(genresIdString[i]);
        }

        this.plot = plot;
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

    public void getPoster(final Context context, final BitmapDrawable[] poster, final Runnable runnable) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                poster[0] = ResourcesManager.getInstance(context).getPosterDrawableById(film_id);
                runnable.run();
            }
        })).start();
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

    public int[] getGenresId() {
        return genresId;
    }

    public String getPlot() {
        return plot;
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
                ", plot='" + plot + '\'' +
                ", isAdult=" + isAdult +
                ", genresId=" + Arrays.toString(genresId) +
                '}';
    }
}
