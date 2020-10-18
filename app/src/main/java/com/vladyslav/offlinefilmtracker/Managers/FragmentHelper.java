package com.vladyslav.offlinefilmtracker.Managers;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.vladyslav.offlinefilmtracker.R;

public class FragmentHelper {
    public static void openFragment(FragmentManager fragmentManager, Activity activity, Fragment fragment) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(String.valueOf(activity.findViewById(R.id.nestedScrollView).getScrollY()))
                .commit();
        activity.findViewById(R.id.nestedScrollView).scrollTo(0, 0);
    }
}
