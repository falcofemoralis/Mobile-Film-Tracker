package com.vladyslav.offlinefilmtracker.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.R;

import java.util.HashMap;
import java.util.Map;

public class CategoriesFragment extends Fragment {
    private View view;  //вью фрагмента
    private LinearLayout baseLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_categories, container, false);
            baseLayout = view.findViewById(R.id.fragment_categories_ll_main);

            HashMap<String, String> genres = DatabaseManager.getInstance(getContext()).getGenres();
            for (Map.Entry<String, String> genre : genres.entrySet()) {
                Button btn = new Button(getContext());
                btn.setText(genre.getValue());
                baseLayout.addView(btn);
            }

        }
        return view;
    }
}