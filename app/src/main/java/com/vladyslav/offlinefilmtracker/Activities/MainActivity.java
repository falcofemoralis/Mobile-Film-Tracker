package com.vladyslav.offlinefilmtracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Fragments.MainFragment;
import com.vladyslav.offlinefilmtracker.Fragments.SearchFragment;
import com.vladyslav.offlinefilmtracker.R;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Fragment mainFragment = new MainFragment();
        final Fragment searchFragment = new SearchFragment();

        bottomNavigationView = findViewById(R.id.activity_main_nv_bottomBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_movies:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, mainFragment).commit();
                        break;
                    case R.id.nav_search:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, searchFragment).commit();
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