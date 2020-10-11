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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResoursesManager;

public class MainFragment extends Fragment {
    final private int SCALE_FACTOR = 2, FILMS_IN_ROW = 7;
    ResoursesManager resoursesManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        resoursesManager = new ResoursesManager(view.getContext());
        DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext(), "imdb.db");

        //получаем необходимые данные из базы данных
        Cursor popularFilmsCursor = databaseHelper.runSQLQuery("SELECT titles.title_id, titles.primary_title, ratings.rating " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id" +
                " WHERE ratings.rating > 7.5 AND ratings.votes > 5000 AND titles.premiered > 2019");
        LinearLayout popularFilmsLayout = view.findViewById(R.id.fragment_main_ll_popularFilms);
        for (int i = 0; i < FILMS_IN_ROW; i++)
            if (!addFilm(popularFilmsCursor, popularFilmsLayout)) {
                --i;
                break;
            }


        //получаем необходимые данные из базы данных
        Cursor adventureFilmsCursor = databaseHelper.runSQLQuery("SELECT titles.title_id, titles.primary_title, ratings.rating " +
                "FROM titles INNER JOIN ratings ON titles.title_id=ratings.title_id " +
                "WHERE titles.genres like '%Adventure%'");
        LinearLayout adventureFilmsLayout = view.findViewById(R.id.fragment_main_ll_adventurerFilms);

        for (int i = 0; i < FILMS_IN_ROW; i++)
            if (!addFilm(adventureFilmsCursor, adventureFilmsLayout)) {
                --i;
                break;
            }
        return view;
    }

    private boolean addFilm(Cursor cursor, LinearLayout baseLayout) {
        cursor.moveToNext();
        final String title = cursor.getString(cursor.getColumnIndex("primary_title"));
        final String film_id = cursor.getString(cursor.getColumnIndex("title_id"));
        final String rating = cursor.getString(cursor.getColumnIndex("rating"));

        //получаем постер
        Drawable posterDrawable = resoursesManager.getPosterByTitleId(film_id);
        if (posterDrawable == null) {
            return false;
        }

        //создаем View для постера
        LinearLayout movieLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_movie, null);

        //ставим постер
        ImageView filmPoster = (ImageView) movieLayout.getChildAt(0);
        filmPoster.setLayoutParams(new LinearLayout.LayoutParams(posterDrawable.getIntrinsicWidth() * SCALE_FACTOR, posterDrawable.getIntrinsicHeight() * SCALE_FACTOR));
        filmPoster.setImageDrawable(posterDrawable);

        //ставим название фильма
        TextView filmTitle = (TextView) movieLayout.getChildAt(1);
        filmTitle.setText(title);

        //ставим рейтинг фильму
        TextView filmRating = (TextView) movieLayout.getChildAt(2);
        filmRating.setText(rating);

        //добавляем нажатие
        movieLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper fragmentHelper = new FragmentHelper();
                fragmentHelper.openFragment(new FilmFragment("hello " + title));

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.activity_main_nv_bottomBar);
                bottomNavigationView.setVisibility(View.GONE);
            }
        });

        baseLayout.addView(movieLayout);
        return true;
    }
}