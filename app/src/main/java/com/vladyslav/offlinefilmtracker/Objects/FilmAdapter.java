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

import com.vladyslav.offlinefilmtracker.Fragments.CategoryFragment;
import com.vladyslav.offlinefilmtracker.Fragments.FilmFragment;
import com.vladyslav.offlinefilmtracker.Fragments.MainFragment;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class FilmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Object> films = new ArrayList<>();
    private Context context;

    public FilmAdapter(Context context, ArrayList<Film> filmsArray, MainFragment.MoreBtn ... moreBtn) {
        this.films.addAll(filmsArray);
        if(moreBtn.length != 0) this.films.add(moreBtn[0]);

        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = inflater.inflate(R.layout.inflate_film, parent, false);
            return new FilmViewHolder(view);
        } else {
            View moreBtnLayout = inflater.inflate(R.layout.inflate_more, parent, false);
            return new MoreViewHolder(moreBtnLayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (films.get(position) instanceof Film) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FilmViewHolder) {
            final Film film = (Film) films.get(position);

            //ставим постер
            BitmapDrawable poster = film.getPoster(context);
            ((FilmViewHolder) holder).posterView.setLayoutParams(new LinearLayout.LayoutParams((int) (ResourcesManager.getDpFromPx(poster.getBitmap().getWidth(), context) * 2.3),
                    (int) (ResourcesManager.getDpFromPx(poster.getBitmap().getHeight(), context) * 2.3)));
            ((FilmViewHolder) holder).posterView.setImageDrawable(poster);

            //устанавливаем название и рейтинг фильма
            ((FilmViewHolder) holder).titleView.setText(film.getTitle());
            ((FilmViewHolder) holder).ratingView.setText(film.getRating());

            ((FilmViewHolder) holder).layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.openFragment(FilmFragment.newInstance(film));
                }
            });
        } else {
            //кнопка открывающая меню жанра
            final MainFragment.MoreBtn moreBtn = (MainFragment.MoreBtn) films.get(position);
            ((MoreViewHolder) holder).moreIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.openFragment(CategoryFragment.newInstance(moreBtn.genre));
                }
            });

            ViewGroup.LayoutParams layoutParams = ((MoreViewHolder) holder).moreIcon.getLayoutParams();
            layoutParams.height = moreBtn.height;
            ((MoreViewHolder) holder).moreIcon.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder {
        ImageView posterView;
        TextView titleView, ratingView;
        View layout;

        FilmViewHolder(View view) {
            super(view);
            layout = view;
            posterView = view.findViewById(R.id.film_poster);
            titleView = view.findViewById(R.id.film_title);
            ratingView = view.findViewById(R.id.film_rating);
        }
    }

    public static class MoreViewHolder extends RecyclerView.ViewHolder {
        ImageView moreIcon;

        MoreViewHolder(View view) {
            super(view);
            moreIcon = (ImageView) ((LinearLayout) view).getChildAt(0);
        }
    }
}
