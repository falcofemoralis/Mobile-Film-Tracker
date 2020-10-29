package com.vladyslav.offlinefilmtracker.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFragment extends Fragment {
    private View view;  //вью фрагмента
    private AutoCompleteTextView editText; //вью ввода текста для поиска
    private InputMethodManager imm; //менеджер клавиатуры
    private LinearLayout hintsLayout; //лаяут подсказоки для фрагмента (текст в середине)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);
            editText = view.findViewById(R.id.fragment_search_act_suggest);
            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            hintsLayout = view.findViewById(R.id.fragment_search_ll_hint);

            initSearch();
        }
        return view;
    }

    //метод инициализации поиска
    public void initSearch() {
        final Handler mHandler = new Handler(Looper.getMainLooper());

        (new Thread() {
            public void run() {
                final HashMap<String, String> films = DatabaseManager.getInstance(getContext()).getAllFilms();
                final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_list_item, R.id.text_view_list_item, new ArrayList<>(films.keySet()));

                mHandler.post(new Runnable() {
                    public void run() {
                        editText.setAdapter(adapter);

                        //установка листенера выбора элемента (фильма)
                        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                //скрываем клавиатуру
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                                //получаем объект фильма
                                String titleId = films.get(editText.getText().toString());
                                Film film = DatabaseManager.getInstance(getContext()).getFilmByTitleId(titleId);

                                //скрываем подсказку
                                hintsLayout.setVisibility(View.GONE);

                                //меняем фрагмент на фрагмент фильма
                                getParentFragmentManager().beginTransaction().replace(R.id.fragment_search_fragment_container, FilmFragment.newInstance(film)).commit();
                            }
                        });
                        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                //в случае нажатия кнопки Enter на клавиаутре будет показан список фильмов
                                if ((actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT)) {
                                    editText.dismissDropDown();
                                    String text = editText.getText().toString();

                                    //проверяем ведденный текст
                                    if (text.equals("")) {
                                        Toast.makeText(getContext(), getString(R.string.enter_film_name), Toast.LENGTH_SHORT).show();
                                    } else {
                                        //скрываем подсказку
                                        hintsLayout.setVisibility(View.GONE);

                                        //меняем фрагмент на фрагмент категории
                                        getParentFragmentManager().beginTransaction().replace(R.id.fragment_search_fragment_container, FilmsListFragment.newInstance(text, false)).commit();
                                    }

                                }
                                return false;
                            }
                        });
                    }
                });
            }

        }).start();
    }
}