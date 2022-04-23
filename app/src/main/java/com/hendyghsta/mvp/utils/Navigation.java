package com.hendyghsta.mvp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Looper;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

import com.hendyghsta.mvp.ui.activity.main.MainActivity;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;

/**
 * Created by hendyghsta on 04/22/2022.
 */
public class Navigation {

    public static final String FRAGMENT_RESOURCE_ID = "fragment_resource_id";
    public static final String DETAIL_FRAGMENT = "detail_fragment";
    public static final String BITMAP_ID = "bitmap_id";
    public static String BASE_FRAGMENT = "base_fragment";
    public static final String ITEM_TEXT = "item_text";
    public static int ANIM_DURATION = 350;

    public static void launchFragment(Activity fromActivity, View backgroundView, @Nullable String shareText, int layout) {
        ActivityOptionsCompat optionsCompat = TransitionHelper.makeOptionsCompat(fromActivity);
        Intent intent = new Intent(fromActivity, MainActivity.class);
        intent.putExtra(FRAGMENT_RESOURCE_ID, layout);
        intent.putExtra(ITEM_TEXT, shareText);
        if (backgroundView != null)
            BitmapUtil.storeBitmapInIntent(BitmapUtil.createBitmap(backgroundView), intent);
        ActivityCompat.startActivity(fromActivity, intent, optionsCompat.toBundle());

    }

    public static void animateRevealShow(Context context, View formView, View viewRoot) {
        int cx = formView.getLeft() + (formView.getWidth() / 2);
        int cy = formView.getTop() + (formView.getHeight() / 2);
        int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2));

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, radius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }

    public static void animateRevealHide(Context context, View formView, View viewRoot) {
        int cx = formView.getLeft() + (formView.getWidth() / 2);
        int cy = formView.getTop() + (formView.getHeight() / 2);
        int radius = (int) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2));

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, radius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(ANIM_DURATION);
        anim.start();

        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), Color.WHITE, Color.argb(0, 106, 0, 203));
        colorAnim.addUpdateListener(valueAnimator -> viewRoot.setBackgroundColor((Integer) valueAnimator.getAnimatedValue()));
        colorAnim.setInterpolator(new AccelerateInterpolator(2));
        colorAnim.setDuration(ANIM_DURATION);
        colorAnim.start();
    }

    public static void fadeThenFinish(Context context, View view, final Activity activity) {
        if (view != null) {
            view.animate()
                    .alpha(0)
                    .setDuration(ANIM_DURATION)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ActivityCompat.finishAfterTransition(activity);
                        }
                    }).start();
        }
    }

    public static void startAlphaAnimation(Context context, View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE) ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(ANIM_DURATION);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public static void animateBeforeEnter(Context context, View v) {
        v.setAlpha(0);
        v.setTranslationY(100);
        new Handler(Looper.getMainLooper()).postDelayed(() -> v.animate()
                .alpha(1)
                .setStartDelay(ANIM_DURATION / 3)
                .setDuration(ANIM_DURATION * 5)
                .setInterpolator(new DecelerateInterpolator(9))
                .translationY(0)
                .start(), 200);
    }

    public static void animateBeforeBack(Context context, View v) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> v.animate()
                .alpha(0)
                .setStartDelay(ANIM_DURATION / 3)
                .setDuration(ANIM_DURATION * 5)
                .setInterpolator(new DecelerateInterpolator(9))
                .translationY(100)
                .start(), 200);
    }

    public static void excludeEnterTarget(Activity activity, int targetId, boolean exclude) {
        activity.getWindow().getEnterTransition().excludeTarget(targetId, exclude);
    }
}
