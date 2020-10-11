package com.vladyslav.offlinefilmtracker.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.R;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FragmentHelper fragmentHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentHelper = new FragmentHelper();
        fragmentHelper.init(getSupportFragmentManager());

        bottomNavigationView = findViewById(R.id.activity_main_nv_bottomBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_movies:
                        fragmentHelper.changeFragment(FragmentHelper.mainFragment);
                        break;
                    case R.id.nav_search:
                        fragmentHelper.changeFragment(FragmentHelper.searchFragment);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (fragmentHelper.closeFragment()) {
            case 0:
                bottomNavigationView.setVisibility(View.VISIBLE);
                return;
            case 1:
                return;
            case -1:
                super.onBackPressed();
        }


    }
}