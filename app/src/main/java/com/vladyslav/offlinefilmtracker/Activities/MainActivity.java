package com.vladyslav.offlinefilmtracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Fragments.MainFragment;
import com.vladyslav.offlinefilmtracker.Fragments.MovieFragment;
import com.vladyslav.offlinefilmtracker.Fragments.SearchFragment;
import com.vladyslav.offlinefilmtracker.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static FragmentManager fm;
    public static MainFragment mainFragment = new MainFragment();
    public static SearchFragment searchFragment = new SearchFragment();
    private Fragment active = mainFragment;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.main_fragment_container, searchFragment).hide(searchFragment).commit();
        fm.beginTransaction().add(R.id.main_fragment_container, mainFragment).commit();

        bottomNavigationView = findViewById(R.id.activity_main_nv_bottomBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_movies:
                        fm.beginTransaction().hide(active).show(mainFragment).commit();
                        active = mainFragment;
                        break;
                    case R.id.nav_search:
                        fm.beginTransaction().hide(active).show(searchFragment).commit();
                        active = searchFragment;
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = fm.findFragmentByTag("currentFragment");
        if (currentFragment != null && currentFragment.isVisible()) {
            fm.beginTransaction().hide(currentFragment).show(active).commit();
            bottomNavigationView.setVisibility(View.VISIBLE);
            return;
        }

        super.onBackPressed();
    }
}