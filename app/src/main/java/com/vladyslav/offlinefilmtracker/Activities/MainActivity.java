package com.vladyslav.offlinefilmtracker.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Fragments.MainFragment;
import com.vladyslav.offlinefilmtracker.Fragments.SearchFragment;
import com.vladyslav.offlinefilmtracker.R;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fm; //менеджер фрагментов

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager(); // получаем менджер фрагментов
        final Fragment mainFragment = new MainFragment(); //главный фрагмент
        final Fragment searchFragment = new SearchFragment(); //фрагмент поиска

        //устанавливаем бар навигации
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_nv_bottomBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                switch (item.getItemId()) {
                    case R.id.nav_movies:
                        fm.beginTransaction().replace(R.id.main_fragment_container, mainFragment).commit();
                        break;
                    case R.id.nav_search:
                        fm.beginTransaction().replace(R.id.main_fragment_container, searchFragment).commit();
                        break;
                }
                return true;
            }
        });
        //устанавливаем изначальный фрагмент
        bottomNavigationView.setSelectedItemId(R.id.nav_movies);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}