package com.vladyslav.offlinefilmtracker.Objects;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vladyslav.offlinefilmtracker.Fragments.FilmFragment;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final ArrayList<Film> films;
    private final Context context;
    private final double POSTER_SCALE_FACTOR = 0.35; //размер постеров у фильмов
    private final Activity activity;

    public FilmAdapter(Context context, ArrayList<Film> films, Activity activity) {
        this.context = context;
        this.films = films;
        this.inflater = LayoutInflater.from(context);
        this.activity = activity;
    }

    @NonNull
    @Override
    public FilmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.inflate_film, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Film film = films.get(position);

        holder.layout.setVisibility(View.INVISIBLE)
        ;
        //ставим постер
        final BitmapDrawable[] poster = new BitmapDrawable[1];
        film.getPoster(context, poster, new Runnable() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //устанавливаем постер
                        holder.posterView.setLayoutParams(new LinearLayout.LayoutParams((ResourcesManager.getDpFromPx(poster[0].getBitmap().getWidth(), POSTER_SCALE_FACTOR, context)),
                                (ResourcesManager.getDpFromPx(poster[0].getBitmap().getHeight(), POSTER_SCALE_FACTOR, context))));
                        holder.posterView.setImageDrawable(poster[0]);
                        holder.layout.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        //устанавливаем название и рейтинг фильма
        holder.titleView.setText(film.getTitle());
        holder.ratingView.setText(film.getRating());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper.openFragment(FilmFragment.newInstance(film));
            }
        });
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView posterView;
        TextView titleView, ratingView;
        View layout;

        ViewHolder(View view) {
            super(view);
            layout = view;
            posterView = view.findViewById(R.id.film_poster);
            titleView = view.findViewById(R.id.film_title);
            ratingView = view.findViewById(R.id.film_rating);
        }
    }
}
