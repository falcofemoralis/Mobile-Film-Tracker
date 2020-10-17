package com.vladyslav.offlinefilmtracker.Managers;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.vladyslav.offlinefilmtracker.R;

public class FragmentHelper {
    public static void openFragment(FragmentManager fragmentManager, Activity activity, Fragment fragment) {
        fragmentManager.beginTransaction()
                //   .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                //    .setCustomAnimations(R.anim.slide_in, R.anim.slide_out, R.anim.slide_in, R.anim.slide_out)
                .replace(R.id.main_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        //   Fade fade = new Fade();
        //  fade.setDuration(300);
        // fade.setInterpolator(new AccelerateDecelerateInterpolator());
        //  activity.getWindow().setExitTransition(fade);
        // activity.getWindow().setEnterTransition(fade);
        // activity.findViewById(R.id.nestedScrollView).scrollTo(0, 0);
    }
}
