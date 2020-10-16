package com.vladyslav.offlinefilmtracker.Fragments;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.vladyslav.offlinefilmtracker.Activities.MainActivity;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    private static final String ARG_GENRE = "param1";
    private String genre;
    private View view;
    private LinearLayout baseLayout, filmsLayout;
    private NestedScrollView scrollView;
    private Cursor filmsCursor;

    public static CategoryFragment newInstance(String genre) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GENRE, genre);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            genre = getArguments().getString(ARG_GENRE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_category, container, false);

        //получаем необходиміе лаяуты
        scrollView = getActivity().findViewById(R.id.nestedScrollView);
        baseLayout = view.findViewById(R.id.fragment_category_films_ll_films);
        filmsLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_films_table, null);

        //получаем фильмы и устанавливаем первые 9
        filmsCursor = DatabaseManager.getInstance(getContext()).getFilmsByGenre(genre);
        ((TextView) filmsLayout.getChildAt(0)).setText(genre + " films");
        baseLayout.addView(filmsLayout);
        setFilms();
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                //вычесляем разницу
                View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

                //если мы опустились в самый конец
                if (diff == 0) setFilms();
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
        super.onDestroy();
    }

    //устанавливаем фильмы в количестве 9 штук
    private void setFilms() {
        TableLayout tableLayout = (TableLayout) filmsLayout.getChildAt(1);

        Film[] films = new Film[9];

        for (int i = 0; i < 9; i++) {
            if (filmsCursor.moveToNext())
                films[i] = DatabaseManager.getInstance(getContext()).getFilmData(filmsCursor);
            else
                break;
        }

        //необходимые переменные
        TableRow rowSubject = null;
        int size = films.length;

        for (int i = 0; i < size; ++i) {
            // В одном ряду может быть лишь 3 кнопки, если уже три созданы, создается следующая колонка
            if (i % 3 == 0) {
                rowSubject = new TableRow(getContext());
                ViewGroup.LayoutParams params = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                rowSubject.setLayoutParams(params);
                rowSubject.setOrientation(TableRow.HORIZONTAL);
                rowSubject.setWeightSum(1f);
                tableLayout.addView(rowSubject);
            }

            //создаем лаяут самого фильма
            LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowSubject, false);

            //устанавлиавем постер
            ImageView filmPoster = (ImageView) filmLayout.getChildAt(0);
            Drawable poster = films[i].getPoster(getContext());
            filmPoster.setLayoutParams(new LinearLayout.LayoutParams((int) (poster.getIntrinsicWidth() * 2.5f), (int) (poster.getIntrinsicHeight() * 2.5f)));
            filmPoster.setImageDrawable(poster);

            //устанавливаем базовую информацию
            ((TextView) filmLayout.getChildAt(1)).setText(films[i].getTitle());
            ((TextView) filmLayout.getChildAt(2)).setText(films[i].getRating());

            //добавляем в строку
            final Film film = films[i];
            filmLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getFragmentManager().beginTransaction().replace(R.id.main_fragment_container, FilmFragment.newInstance(film)).addToBackStack(null).commit();
                }
            });
            rowSubject.addView(filmLayout);
        }

        //заполняются остаточные блоки
        int n = 0;
        if (size % 3 != 0) n = ((size / 3) * 3 + 3) - size;

        for (int i = 0; i < n; ++i) {
            LinearLayout filmLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.inflate_film, rowSubject, false);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(150, 250);
            filmLayout.getChildAt(0).setLayoutParams(params);
            filmLayout.setGravity(Gravity.CENTER);
            filmLayout.setVisibility(View.INVISIBLE);
            rowSubject.addView(filmLayout);
        }
    }
}