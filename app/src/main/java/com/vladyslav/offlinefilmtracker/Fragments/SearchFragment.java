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

import androidx.fragment.app.Fragment;

import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Objects.Film;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFragment extends Fragment {
    View view;
    AutoCompleteTextView editText;
    InputMethodManager imm;
    LinearLayout hintLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);
            editText = view.findViewById(R.id.fragment_search_act_suggest);
            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            hintLayout = view.findViewById(R.id.fragment_search_ll_hint);

            final Handler mHandler = new Handler(Looper.getMainLooper());

            //создаем поток
            (new Thread() {
                public void run() {
                    final HashMap<String, String> films = DatabaseManager.getInstance(getContext()).getAllFilms();
                    ArrayList<String> filmsTitles = new ArrayList<>(films.keySet());
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_list_item, R.id.text_view_list_item, filmsTitles);
                    mHandler.post(new Runnable() {
                        public void run() {
                            editText.setAdapter(adapter);
                            editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                                    String titleId = films.get(editText.getText().toString());
                                    Film film = DatabaseManager.getInstance(getContext()).getFilmByTitleId(titleId);
                                    hintLayout.setVisibility(View.GONE);
                                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_search_fragment_container, FilmFragment.newInstance(film)).commit();
                                }
                            });
                            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    if ((actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT)) {
                                        editText.dismissDropDown();
                                        String text = editText.getText().toString();
                                        if (text.equals("")) {
                                            Toast.makeText(getContext(), "Enter film name!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            hintLayout.setVisibility(View.GONE);
                                            getParentFragmentManager().beginTransaction().replace(R.id.fragment_search_fragment_container, CategoryFragment.newInstance(text, false)).commit();
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
        return view;
    }
}