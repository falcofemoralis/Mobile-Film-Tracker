package com.vladyslav.offlinefilmtracker.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.managers.DatabaseOpenHelper;
import com.vladyslav.offlinefilmtracker.managers.ResoursesManagers;

import java.util.Random;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        LinearLayout popular_movies_layout = view.findViewById(R.id.popular_movies_layout);

        DatabaseOpenHelper dataBaseOpener = new DatabaseOpenHelper(view.getContext(), "imdb.db");
        SQLiteDatabase database = dataBaseOpener.getReadableDatabase();
        String selectQuery = "select titles.title_id, titles.original_title, ratings.rating from titles inner join ratings on titles.title_id=ratings.title_id limit 1000;";
        Cursor cursor = database.rawQuery(selectQuery, null);
        Random random = new Random();
        ResoursesManagers resoursesManagers = new ResoursesManagers(view.getContext());
        for (int i = 0; i < 1; i++) {
            cursor.moveToPosition(random.nextInt(cursor.getCount()));
            String title_id = cursor.getString(cursor.getColumnIndex("title_id"));
            ImageView filmPoster = (ImageView) LayoutInflater.from(view.getContext()).inflate(R.layout.inflate_movie, null);

            Drawable drawable = resoursesManagers.getPoster(title_id);
            filmPoster.setImageDrawable(drawable);

            Drawable drawable1 = view.getContext().getDrawable(R.drawable.tt0002844);
            drawable1 = drawable;
            filmPoster.setImageDrawable(drawable1);
          //  popular_movies_layout.addView(filmPoster);
            ImageView testImage = view.findViewById(R.id.test_image);
            testImage.setImageDrawable(drawable1);
        }

        return view;
    }
}