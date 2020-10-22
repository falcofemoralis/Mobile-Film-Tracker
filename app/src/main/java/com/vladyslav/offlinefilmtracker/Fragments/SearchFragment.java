package com.vladyslav.offlinefilmtracker.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        editText = view.findViewById(R.id.fragment_search_act_suggest);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        final HashMap<String, String> films = DatabaseManager.getInstance(getContext()).getAllFilms();
        ArrayList<String> filmsTitles = new ArrayList<>(films.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                R.layout.custom_list_item, R.id.text_view_list_item, filmsTitles);

        editText.setAdapter(adapter);
        editText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                String titleId = films.get(editText.getText().toString());
                Film film = DatabaseManager.getInstance(getContext()).getFilmByTitleId(titleId);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_search_fragment_container, FilmFragment.newInstance(film)).commit();
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE)) {
                    editText.dismissDropDown();
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment_search_fragment_container, CategoryFragment.newInstance(editText.getText().toString(), false)).commit();
                }
                return false;
            }
        });
        return view;
    }
}