package com.vladyslav.offlinefilmtracker.Managers;

import android.app.Activity;
import android.content.Context;
import android.transition.TransitionInflater;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.vladyslav.offlinefilmtracker.R;

public class FragmentHelper {
    public static void openFragment(FragmentManager fragmentManager, Activity activity, Fragment fragment) {
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.main_fragment_container, fragment).addToBackStack(null).commit();
        ((NestedScrollView) activity.findViewById(R.id.nestedScrollView)).scrollTo(0, 0);
    }
}
