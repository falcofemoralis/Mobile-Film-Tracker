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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentHelper.init(getSupportFragmentManager());

        bottomNavigationView = findViewById(R.id.activity_main_nv_bottomBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_movies:
                        FragmentHelper.changeFragment(FragmentHelper.mainFragment);
                        break;
                    case R.id.nav_search:
                        FragmentHelper.changeFragment(FragmentHelper.searchFragment);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        switch (FragmentHelper.closeFragment()) {
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