package com.vladyslav.offlinefilmtracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Fragments.MainFragment;
import com.vladyslav.offlinefilmtracker.Fragments.SearchFragment;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.R;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentHelper.init(getSupportFragmentManager());

        final Fragment mainFragment = new MainFragment();
        final Fragment searchFragment = new SearchFragment();

        bottomNavigationView = findViewById(R.id.activity_main_nv_bottomBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_movies:
                        FragmentHelper.replaceFragment(mainFragment);
                        break;
                    case R.id.nav_search:
                        FragmentHelper.replaceFragment(searchFragment);
                        break;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_movies);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}