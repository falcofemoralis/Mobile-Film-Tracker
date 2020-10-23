package com.vladyslav.offlinefilmtracker.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Fragments.MainFragment;
import com.vladyslav.offlinefilmtracker.Fragments.SearchFragment;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Services.DownloadService;

import java.io.IOException;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fm; //менеджер фрагментов
    public static Callable callable;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //скачивание файлов
        try {
            ResourcesManager.getInstance(getApplicationContext());
            setBottomBar();
        } catch (IOException e) {
            final Intent intent = new Intent(this, DownloadService.class);

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Downloading 3 files");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setButton(ProgressDialog.BUTTON_NEUTRAL,
                    "Close",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            stopService(intent);
                        }
                    });


            callable = new Callable() {
                @Override
                public Object call() throws Exception {
                   // setBottomBar();
                    return null;
                }
            };

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(intent);
            else startService(intent);
        }
    }

    public void setBottomBar() {
        fm = getSupportFragmentManager(); // получаем менджер фрагментов
        FragmentHelper.init(fm, this);

        final Fragment mainFragment = new MainFragment(); //главный фрагмент
        final Fragment searchFragment = new SearchFragment(); //фрагмент поиска

        //устанавливаем бар навигации
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_nv_bottomBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //   setScrollViewToLastPosition();
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
        //возвращает скролл вью в прошлую позицию
        int count = fm.getBackStackEntryCount();
        if (count > 0) {
            FragmentManager.BackStackEntry backStackEntry = fm.getBackStackEntryAt(count - 1);
            final int scrollViewLastPos = Integer.parseInt(backStackEntry.getName());

            final NestedScrollView mainScroll = findViewById(R.id.nestedScrollView);
            mainScroll.postDelayed(new Runnable() {
                public void run() {
                    mainScroll.scrollTo(0, scrollViewLastPos);
                }
            }, 20);
        }
        super.onBackPressed();
    }
}




