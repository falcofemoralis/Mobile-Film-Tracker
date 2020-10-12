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
    private static ArrayList<Fragment> openedFragments = new ArrayList<>(); //список открытых фрагментов

    //фрагменты боттом бара
    public static MainFragment mainFragment = new MainFragment();//главный фрагмент
    public static SearchFragment searchFragment = new SearchFragment();//фрагмент поиска

    //инициализация хелпера
    public static void init(FragmentManager fragmentManager) {
        fm = fragmentManager; // получаем менеджер фрагментов при инициалзиации

        //добавляем в менеджер фрагментов исходные франменты боттом бара
        fm.beginTransaction().add(R.id.main_fragment_container, searchFragment).hide(searchFragment).commit();
        fm.beginTransaction().add(R.id.main_fragment_container, mainFragment).commit(); // будет показываться изначально
        openedFragments.add(mainFragment); //добавляем с список открытых фрагментов
    }

    //смена фрагмента с сохранением состояния в боттом баре
    public void changeFragment(Fragment fragmentToChange) {
        //скрываем предыдущий фрагмент и показывает выбранный фрагмент
        fm.beginTransaction().hide(openedFragments.get(0)).commit();
        fm.beginTransaction().show(fragmentToChange).commit();

        //убираем текущий фрагмент из списка открытых и добавляем новый открытый фрагмент
        openedFragments.remove(0);
        openedFragments.add(fragmentToChange);
    }

    //открываем фрагмент поверх другого фрагмента с сохранением состояния
    public void openFragment(Fragment fragmentToOpen) {
        openedFragments.add(fragmentToOpen); // добавляем новый открытый фрагмент в список
        fm.beginTransaction().hide(openedFragments.get(openedFragments.size() - 2)).commit(); //скрываем текщий фрагмент

        //в случае если фрагмент уже есть в менеджере, то нужно просто показать его
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment == fragmentToOpen) {
                fm.beginTransaction().show(fragmentToOpen).commit();
                return;
            }
        }
        //добавляем фрагмент в менеджер и показываем его
        fm.beginTransaction().add(R.id.main_fragment_container, fragmentToOpen).commit();
    }

    //закрытие фрагмента при нажатии кнопки Back (используется в OnBackPressed)
    public int closeFragment() {
        //если в списке есть более чем один открытый фрагмент, его следует закрыть, иначе просто будет выходит из приложения
        if (openedFragments.size() != 1) {
            //скрываем текущий фрагмент и показываем предыдущий из списка
            fm.beginTransaction().hide(openedFragments.get(openedFragments.size() - 1))
                    .show(openedFragments.get(openedFragments.size() - 2)).commit();

            //удалаяем из списка открытый фрагмент
            openedFragments.remove(openedFragments.size() - 1);
            if (openedFragments.size() == 1) return 0;
            return 1;
        }

        return -1;
    }
}
