package com.hendyghsta.mvp.utils.mvp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hendyghsta.mvp.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by hendyghsta on 04/22/2022.
 */
public class BaseViewFragment extends Fragment implements IBaseView {

    private static final String TAG = BaseViewFragment.class.getSimpleName();

    @Nullable
    @BindView(R.id.progressBar)
    protected ProgressBar progressBar;

    @Nullable
    @BindView(R.id.loading)
    protected FrameLayout loading;

    private Dialog pDialog;

    @Override
    public void showToastMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLoading(int visibility) {
        float[] faded = new float[2];
        faded[1] = visibility == View.GONE ? 1f : 0f;
        faded[2] = visibility == View.GONE ? 0f : 1f;

        ObjectAnimator fade = ObjectAnimator.ofFloat(loading, "alpha", faded);
        fade.setDuration(500);

        if (visibility == View.GONE) {
            fade.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loading.setVisibility(visibility);
                }
            });
        } else {
            fade.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    loading.setVisibility(visibility);
                    loading.setAlpha(0);
                }
            });
        }

        fade.start();
    }

    @Override
    public void showProgressDialog(boolean show) {
        if (show) {
            if (pDialog == null) {
                pDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                pDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                pDialog.setCancelable(false);
                pDialog.setContentView(R.layout.dialog_progress);
            }
            pDialog.show();
        } else {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void showDialog(String title, String content, @Nullable DialogInterface.OnCancelListener listener) {
        new MaterialDialog.Builder(getActivity())
                .title(title)
                .content(content)
                .cancelListener(listener)
                .show();
    }

    @Override
    public void showTooltip(View view, int gravity, String title, String desc) {
        TourGuide tourGuide = new TourGuide(getActivity());

        TranslateAnimation anim = new TranslateAnimation(0f, 0f, 200f, 0f);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        anim.setInterpolator(new BounceInterpolator());

        ToolTip toolTip = new ToolTip()
                .setTitle(title)
                .setDescription(desc)
                .setShadow(true)
                .setTextColor(Color.parseColor("#bdc3c7"))
                .setBackgroundColor(Color.parseColor("#e74c3c"))
                .setGravity(gravity)
                .setEnterAnimation(anim);

        Overlay overlay = new Overlay();
        overlay.disableClickThroughHole(true);
        overlay.setOnClickListener(view1 -> tourGuide.cleanUp());
        overlay.setBackgroundColor(R.color.overlay);

        tourGuide.setToolTip(toolTip);
        tourGuide.setOverlay(overlay);
        tourGuide.playOn(view);
    }

}
