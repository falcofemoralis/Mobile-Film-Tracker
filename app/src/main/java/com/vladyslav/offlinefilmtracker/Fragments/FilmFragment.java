package com.vladyslav.offlinefilmtracker.Fragments;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_film, container, false);

        TextView ratingTV = view.findViewById(R.id.fragment_film_tv_rating);
        TextView titleTV = view.findViewById(R.id.fragment_film_tv_title);
        ImageView posterIV = view.findViewById(R.id.fragment_film_iv_poster);

        ratingTV.setText(rating);
        titleTV.setText(title);
        posterIV.setImageDrawable(poster);

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
