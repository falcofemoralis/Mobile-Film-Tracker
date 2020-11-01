package com.vladyslav.offlinefilmtracker.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vladyslav.offlinefilmtracker.Fragments.CategoriesFragment;
import com.vladyslav.offlinefilmtracker.Fragments.MainFragment;
import com.vladyslav.offlinefilmtracker.Fragments.SearchFragment;
import com.vladyslav.offlinefilmtracker.Managers.DatabaseManager;
import com.vladyslav.offlinefilmtracker.Managers.FragmentHelper;
import com.vladyslav.offlinefilmtracker.Managers.ResourcesManager;
import com.vladyslav.offlinefilmtracker.R;
import com.vladyslav.offlinefilmtracker.Services.DownloadService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private FragmentManager fm; //менеджер фрагментов
    private Intent intent; //интент сервиса скачивания файлов
    private ProgressDialog progressDialog; //диалог прогресса скачивания файлов
    public static ArrayList<Pair<String, String>> links = new ArrayList<>(); //ссылки для скачивания необходимых файлов
    public static final int SETPROGRESSMAX = 1, SETPROGRESS = 2, SETBOTTOMBAR = 3, FAILED = 4;
    public static Handler hMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hMain = new MyHandler(this);

        //инциализация помошника по фрагментам
        fm = getSupportFragmentManager();
        FragmentHelper.init(fm, this);

        //производится проверка наличия файлов
        checkFiles(new Runnable() {
            @Override
            public void run() {
                if (links.size() > 0) downloadFiles();
                else setBottomBar();
            }
        });
    }

    //метод проверки наличия файлов
    public void checkFiles(final Runnable runnable) {
        final String path = this.getObbDir().getPath() + "/";

        (new Thread() {
            @Override
            public void run() {
                //проверяем файлы
                ResourcesManager resourcesManager = ResourcesManager.getInstance(getApplicationContext());
                if (resourcesManager.photosZip == null)
                    links.add(new Pair<>("https://dl.dropboxusercontent.com/s/7f0ftnfup1bmtjn/photos.zip?dl=0", path + "photos.zip"));

                if (resourcesManager.postersZip == null)
                    links.add(new Pair<>("https://dl.dropboxusercontent.com/s/92vwcr52oqdtrrj/posters.zip?dl=0", path + "posters.zip"));

                if (DatabaseManager.getInstance(getApplicationContext()) == null)
                    links.add(new Pair<>("https://dl.dropboxusercontent.com/s/kb7e5roc76do03c/imdb.db?dl=0", path + "imdb.db"));

                runOnUiThread(runnable);
                super.run();
            }
        }).start();
    }

    //метод запуска сервиса скачивание файлов по ссылкам
    public void downloadFiles() {
        ResourcesManager.delete();
        DatabaseManager.delete();

        intent = new Intent(this, DownloadService.class);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle(getString(R.string.dialog_title));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setButton(ProgressDialog.BUTTON_NEUTRAL, getString(R.string.close),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopService(intent);
                    }
                });
        progressDialog.show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent);
        else startService(intent);
    }

    //метод установки боттом бара (запускается главный фрагмент)
    public void setBottomBar() {
        final Fragment mainFragment = new MainFragment(); //главный фрагмент
        final Fragment searchFragment = new SearchFragment(); //фрагмент поиска
        final Fragment categoriesFragment = new CategoriesFragment(); //фрагмент поиска

        //устанавливаем бар навигации
        BottomNavigationView bottomNavigationView = findViewById(R.id.activity_main_nv_bottomBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_movies:
                        FragmentHelper.changeFragment(mainFragment);
                        break;
                    case R.id.nav_search:
                        FragmentHelper.changeFragment(searchFragment);
                        break;
                    case R.id.nav_categories:
                        FragmentHelper.changeFragment(categoriesFragment);
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

    @Override
    protected void onDestroy() {
        if (hMain != null) hMain.removeCallbacksAndMessages(null);

        //если пользователь закрыл приложение, сервис останавливается
        if (intent != null) stopService(intent);
        finishAndRemoveTask();
        super.onDestroy();
    }

    static class MyHandler extends Handler {
        WeakReference<MainActivity> wrActivity; //слабая ссылка на активити не препятствует его уничтожению

        public MyHandler(MainActivity activity) {
            wrActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = wrActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case SETPROGRESSMAX:
                        activity.progressDialog.setMax(Integer.parseInt(msg.obj.toString()));
                        break;
                    case SETPROGRESS:
                        activity.progressDialog.setProgress(Integer.parseInt(msg.obj.toString()));
                        break;
                    case SETBOTTOMBAR:
                        activity.progressDialog.hide();
                        ResourcesManager.getInstance(activity.getApplicationContext());
                        activity.setBottomBar();
                        break;
                    case FAILED:
                        activity.progressDialog.setTitle(activity.getString(R.string.downloading_error));
                        break;
                }
            }
        }
    }
}




