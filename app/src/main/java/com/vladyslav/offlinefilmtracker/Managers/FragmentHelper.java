package com.vladyslav.offlinefilmtracker.Managers;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.vladyslav.offlinefilmtracker.Fragments.MainFragment;
import com.vladyslav.offlinefilmtracker.Fragments.SearchFragment;
import com.vladyslav.offlinefilmtracker.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentHelper {
    private static FragmentManager fm; //менеджер фрагментов

    //инициализация фрагментов боттом бара
    public static void init(FragmentManager fragmentManager) {
        fm = fragmentManager; // получаем менеджер фрагментов при инициалзиации
    }

    //открываем фрагмент
    public static void replaceFragment(Fragment fragmentToOpen) {
        fm.beginTransaction().replace(R.id.main_fragment_container, fragmentToOpen).commit();
    }

    //открываем фрагмент
    public static void openFragment(Fragment fragmentToOpen) {
        fm.beginTransaction().replace(R.id.main_fragment_container, fragmentToOpen).addToBackStack(null).commit();
    }
}
