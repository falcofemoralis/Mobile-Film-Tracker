package com.vladyslav.offlinefilmtracker.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.R;

import java.util.HashMap;
import java.util.Map;

public class CategoriesFragment extends Fragment {
    private View view;  //вью фрагмента
    private GridLayout baseLayout; //лаяут с кнопками

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_categories, container, false);
            baseLayout = view.findViewById(R.id.fragment_categories_ll_main);

            setCategories();
        }
        return view;
    }

    //метод получения и установки жанров
    public void setCategories() {
        //получаем размеры кнопок
        int btnMargin = ResourcesManager.getPxFromDp(10, getContext());
        int btnSize = ((getContext().getResources().getDisplayMetrics().widthPixels) / 3) - btnMargin * 2;

        //получаем все жанры
        HashMap<String, String> genres = DatabaseManager.getInstance(getContext()).getGenresMap();
        for (final Map.Entry<String, String> genreEntry : genres.entrySet()) {
            //создаем кнопку
            Button btn = (Button) LayoutInflater.from(getContext()).inflate(R.layout.inflate_category_button, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(btnSize, btnSize);
            lp.setMargins(btnMargin, btnMargin, btnMargin, btnMargin);
            btn.setLayoutParams(lp);
            btn.setText(ResourcesManager.getGenreStringById(genreEntry.getValue(), getContext()));       //устанавливаем текст

            //устанавливаем иконку
            try {
                int drawableId = getContext().getResources().getIdentifier("ic_category_" + ResourcesManager.normalizeGenreString(genreEntry.getValue()), "drawable", getContext().getPackageName());
                btn.setCompoundDrawablesWithIntrinsicBounds(null, getContext().getDrawable(drawableId), null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //добавляем нажатие кнопки
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentHelper.openFragment(FilmsListFragment.newInstance(genreEntry));
                }
            });
            baseLayout.addView(btn);
        }
    }
}