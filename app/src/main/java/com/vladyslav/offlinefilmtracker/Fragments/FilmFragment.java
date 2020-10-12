package com.vladyslav.offlinefilmtracker.Fragments;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseHelper;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.R;

public class FilmFragment extends Fragment {
    private String film_id, title, rating;
    private Drawable poster;

    public FilmFragment(String film_id, String title, String rating, Drawable poster) {
        this.film_id = film_id;
        this.title = title;
        this.rating = rating;
        this.poster = poster;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_film, container, false);
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(view.getContext());

        //устанавливаем основную информацию про фильм
        ((TextView) view.findViewById(R.id.fragment_film_tv_rating)).setText(rating);
        ((TextView) view.findViewById(R.id.fragment_film_tv_title)).setText(title);
        ((ImageView) view.findViewById(R.id.fragment_film_iv_poster)).setImageDrawable(poster);

        //получаем жанры фильма
        Cursor tagCursor = databaseHelper.runSQLQuery(String.format("SELECT titles.genres FROM titles WHERE titles.title_id = \"%s\";", film_id));
        tagCursor.moveToFirst();
        String tags = tagCursor.getString(tagCursor.getColumnIndex("genres"));
        String[] arrSplit = tags.split(","); //т.к данны приходят в формате String_1, String_2, то нужно разделить

        LinearLayout tagsLayout = view.findViewById(R.id.fragment_film_ll_tags);
        for (int i = 0; i < arrSplit.length; ++i) {
            TextView tag = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.inflate_tag, null);
            tag.setText(arrSplit[i]);
            tagsLayout.addView(tag);
        }

        //устанавливаем допольнительную информацию про фильм
        Cursor additionalInfoCursor = databaseHelper.runSQLQuery(String.format("SELECT titles.premiered, titles.runtime_minutes, titles.is_adult FROM titles WHERE titles.title_id = \"%s\";", film_id));
        additionalInfoCursor.moveToFirst();
        ((TextView) view.findViewById(R.id.fragment_film_tv_releaseDate)).setText("Release date" + ": " + additionalInfoCursor.getString(additionalInfoCursor.getColumnIndex("premiered")));
        ((TextView) view.findViewById(R.id.fragment_film_tv_runtime)).setText("Runtime" + ": " + additionalInfoCursor.getString(additionalInfoCursor.getColumnIndex("runtime_minutes")) + " minutes");

        TextView adult = view.findViewById(R.id.fragment_film_tv_adult);
        if (Boolean.parseBoolean(additionalInfoCursor.getString(additionalInfoCursor.getColumnIndex("is_adult"))))
            adult.setText("18+");
        else
            adult.setVisibility(View.GONE);


        Cursor directorCursor = databaseHelper.runSQLQuery(String.format("SELECT crew.person_id FROM crew WHERE crew.title_id = \"%s\" and crew.category = \"director\";", film_id));
        directorCursor.moveToFirst();
        ((TextView) view.findViewById(R.id.fragment_film_tv_directors)).setText(directorCursor.getString(directorCursor.getColumnIndex("person_id")));

        //SELECT people.name FROM people WHERE people.person_id = (SELECT crew.person_id FROM crew WHERE crew.title_id = "%s" and crew.category = "director");
        //устанавлиаем обработчик нажатия для актера
        LinearLayout linearLayout = view.findViewById(R.id.actor_layout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHelper fragmentHelper = new FragmentHelper();
                fragmentHelper.openFragment(new ActorFilmsFragment());

                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.activity_main_nv_bottomBar);
                bottomNavigationView.setVisibility(View.GONE);
            }
        });
        return view;
    }
}
