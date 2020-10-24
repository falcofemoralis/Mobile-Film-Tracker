package com.vladyslav.offlinefilmtracker.Objects;

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

    public FilmAdapter(Context context, ArrayList<Film> films) {
        this.context = context;
        this.films = films;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FilmAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.inflate_film, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Film film = films.get(position);

        //ставим постер
        BitmapDrawable poster = film.getPoster(context);
        holder.posterView.setLayoutParams(new LinearLayout.LayoutParams((ResourcesManager.getDpFromPx(poster.getBitmap().getWidth(), 2.3, context)),
                (ResourcesManager.getDpFromPx(poster.getBitmap().getHeight(), 2.3, context))));
        holder.posterView.setImageDrawable(poster);

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
