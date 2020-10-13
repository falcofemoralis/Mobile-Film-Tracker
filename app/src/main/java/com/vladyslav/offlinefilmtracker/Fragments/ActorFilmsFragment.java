package com.vladyslav.offlinefilmtracker.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vladyslav.offlinefilmtracker.Objects.Actor;
import com.vladyslav.offlinefilmtracker.R;

public class ActorFilmsFragment extends Fragment {
    private Actor actor;

    public ActorFilmsFragment(Actor actor) {
        this.actor = actor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actor_films, container, false);

        ((TextView) view.findViewById(R.id.fragment_actor_films_tv_test)).setText(actor.getName());
        return view;
    }
}