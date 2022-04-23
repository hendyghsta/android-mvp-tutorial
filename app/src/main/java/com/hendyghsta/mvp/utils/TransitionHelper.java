package com.hendyghsta.mvp.utils;

import android.app.Activity;
import android.os.Bundle;
import android.transition.Transition;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

import com.hendyghsta.mvp.R;
import com.hendyghsta.mvp.utils.mvp.BaseViewActivity;
import com.hendyghsta.mvp.utils.mvp.BaseViewFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * Created by hendyghsta on 04/22/2022.
 */
public class TransitionHelper {

    private final Activity activity;
    private boolean isAfterEnter;
    private boolean isPostponeEnterTransition = false;
    private boolean isViewCreatedAlreadyCalled = false;
    private final List<Listener> listeners = new ArrayList<>();

    private TransitionHelper(Activity activity, Bundle savedInstanceState) {
        this.activity = activity;
        isAfterEnter = savedInstanceState != null;
        postponeEnterTransition();
    }

    private void postponeEnterTransition() {
        if (isAfterEnter)
            return;
        ActivityCompat.postponeEnterTransition(activity);
        isPostponeEnterTransition = true;
    }

    private void startPostponedEnterTransition() {
        final View decor = activity.getWindow().getDecorView();
        decor.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                decor.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(activity);
                return true;
            }
        });
    }

    private void onViewCreated() {
        if (isViewCreatedAlreadyCalled)
            return;
        isViewCreatedAlreadyCalled = true;

        View contentView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        for (Listener listener : listeners)
            listener.onBeforeViewShow(contentView);
        if (!isAfterEnter())
            for (Listener listener : listeners)
                listener.onBeforeEnter(contentView);
        if (isPostponeEnterTransition)
            startPostponedEnterTransition();
    }

    private void onAfterEnter() {
        for (Listener listener : listeners)
            listener.onAfterEnter();
        isAfterEnter = true;
    }

    private boolean isAfterEnter() {
        return isAfterEnter;
    }

    private void onResume() {
        if (isAfterEnter)
            return;

        if (!isViewCreatedAlreadyCalled)
            onViewCreated();

        activity.getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                if (isAfterEnter())
                    for (Listener listener : listeners)
                        listener.onBeforeReturn();
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                if (!isAfterEnter())
                    onAfterEnter();
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                if (!isAfterEnter())
                    onAfterEnter();
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        });
    }

    private void onBackPressed() {
        boolean isConsumed = false;
        for (Listener listener : listeners)
            isConsumed = listener.onBeforeBack() || isConsumed;
        if (!isConsumed)
            ActivityCompat.finishAfterTransition(activity);
    }

    private void onSaveInstanceState(Bundle outstate) {
        outstate.putBoolean("isAfterEnter", isAfterEnter);
    }

    private void addListener(Listener listener) {
        listeners.add(listener);
    }

    public interface Listener {
        void onBeforeViewShow(View contentView);

        void onBeforeEnter(View contentView);

        void onAfterEnter();

        boolean onBeforeBack();

        void onBeforeReturn();
    }

    public interface Source {
        TransitionHelper getTransitionHelper();

        void setTransitionHelper(TransitionHelper transitionHelper);
    }

    // STATIC
    public static TransitionHelper of(Activity activity) {
        return ((Source) activity).getTransitionHelper();
    }

    private static void init(Source source, Bundle savedInstanceState) {
        source.setTransitionHelper(new TransitionHelper((Activity) source, savedInstanceState));
    }

    static ActivityOptionsCompat makeOptionsCompat(Activity fromActivity, Pair<View, String>... sharedElements) {
        ArrayList<Pair<View, String>> list = new ArrayList<>(Arrays.asList(sharedElements));

        list.add(Pair.create(fromActivity.findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
        list.add(Pair.create(fromActivity.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));

        list.removeIf(pair -> pair.first == null);

        sharedElements = list.toArray(new Pair[list.size()]);
        ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(fromActivity, sharedElements);
        return compat;

    }

    public static class BaseActivity extends BaseViewActivity implements Source, Listener, FragmentManager.OnBackStackChangedListener {

        TransitionHelper transitionHelper;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            TransitionHelper.init(this, savedInstanceState);
            TransitionHelper.of(this).addListener(this);
            super.onCreate(savedInstanceState);
            getSupportFragmentManager().addOnBackStackChangedListener(this);
        }

        public <T extends Fragment> void showFragment(Class<T> fragmentClass, Bundle bundle, boolean addToBackStack) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentClass.getSimpleName());
            if (fragment == null) {
                try {
                    fragment = fragmentClass.newInstance();
                    fragment.setArguments(bundle);
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("New Fragment should have been created", e);
                }
            }

            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.base_fragment, fragment, fragmentClass.getSimpleName());

            if (addToBackStack)
                fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
        }

        public void popFragmentBackStack() {
            if (getFragmentManager().getBackStackEntryCount() > 0)
                getFragmentManager().popBackStack();
        }

        private void shouldShowActionBarUpButton() {
            if (getFragmentManager().getBackStackEntryCount() == 0)
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            else
                getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        @Override
        protected void onSaveInstanceState(@NonNull Bundle outState) {
            TransitionHelper.of(this).onSaveInstanceState(outState);
            super.onSaveInstanceState(outState);
        }

        @Override
        protected void onResume() {
            TransitionHelper.of(this).onResume();
            super.onResume();
        }

        @Override
        public void onBackPressed() {
            TransitionHelper.of(this).onBackPressed();
        }

        @Override
        public void onBackStackChanged() {
            shouldShowActionBarUpButton();
        }

        @Override
        public void onBeforeViewShow(View contentView) {

        }

        @Override
        public void onBeforeEnter(View contentView) {

        }

        @Override
        public void onAfterEnter() {

        }

        @Override
        public boolean onBeforeBack() {
            return false;
        }

        @Override
        public void onBeforeReturn() {

        }

        @Override
        public TransitionHelper getTransitionHelper() {
            return transitionHelper;
        }

        @Override
        public void setTransitionHelper(TransitionHelper transitionHelper) {
            this.transitionHelper = transitionHelper;
        }

        @Override
        public Activity getActivity() {
            return this;
        }
    }

    public static class BaseFragment extends BaseViewFragment implements Listener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            TransitionHelper.of(getActivity()).addListener(this);
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            TransitionHelper.of(getActivity()).onViewCreated();
            super.onViewCreated(view, savedInstanceState);
        }

        @Override
        public void onBeforeViewShow(View contentView) {

        }

        @Override
        public void onBeforeEnter(View contentView) {

        }

        @Override
        public void onAfterEnter() {

        }

        @Override
        public boolean onBeforeBack() {
            return false;
        }

        @Override
        public void onBeforeReturn() {

        }
    }
}
