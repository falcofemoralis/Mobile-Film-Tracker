package com.vladyslav.offlinefilmtracker.Fragments;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResoursesManager;

import java.util.Random;

import static java.lang.Thread.sleep;

public class MainFragment extends Fragment {
    final int SCALE_FACTOR = 2, FILMS_IN_ROW = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Random random = new Random();
        ResoursesManager resoursesManager = new ResoursesManager(view.getContext());
        DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext(), "imdb.db");

        //получаем необходимые компоненты
        final LinearLayout popular_movies_layout = view.findViewById(R.id.fragment_main_ll_popularFilms);

        //получаем необходимые данные из базы данных
        Cursor cursor = databaseHelper.runSQLQuery("SELECT titles.title_id, titles.primary_title, ratings.rating " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id" +
                " WHERE ratings.rating > 7.5 AND ratings.votes > 500 AND titles.premiered > 2015;");

        for (int i = 0; i < FILMS_IN_ROW; i++) {
            //получаем необходимые данные из массива
            cursor.moveToPosition(random.nextInt(cursor.getCount()));
            String title_id = cursor.getString(cursor.getColumnIndex("title_id"));
            String primary_title = cursor.getString(cursor.getColumnIndex("primary_title"));
            String rating = cursor.getString(cursor.getColumnIndex("rating"));

            //получаем постер
            Drawable posterDrawable = resoursesManager.getPosterByTitleId(title_id);
            if (posterDrawable == null) {
                --i;
                break;
            }

            //создаем View для постера
            LinearLayout movieLayout = (LinearLayout) LayoutInflater.from(view.getContext()).inflate(R.layout.inflate_movie, null);

            //ставим постер
            ImageView filmPoster = (ImageView) movieLayout.getChildAt(0);
            filmPoster.setLayoutParams(new LinearLayout.LayoutParams(posterDrawable.getIntrinsicWidth() * SCALE_FACTOR, posterDrawable.getIntrinsicHeight() * SCALE_FACTOR));
            filmPoster.setImageDrawable(posterDrawable);

            //ставим название фильма
            TextView filmTitle = (TextView) movieLayout.getChildAt(1);
            filmTitle.setText(primary_title);

            //ставим рейтинг фильму
            TextView filmRating = (TextView) movieLayout.getChildAt(2);
            filmRating.setText(rating);

            //добавляем view в лаяут
            popular_movies_layout.addView(movieLayout);
        }
        return view;
    }
}