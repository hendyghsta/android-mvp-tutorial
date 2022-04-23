package com.hendyghsta.mvp.utils.mvp;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by hendyghsta on 04/22/2022.
 */
public interface IBaseView {
    void showToastMessage(String msg);

    void setProgressBar(boolean show);

    void setLoading(int visibility);

    void showProgressDialog(boolean show);

    void showDialog(String title, String content, @Nullable DialogInterface.OnCancelListener listener);

    void showTooltip(View view, int gravity, String title, String desc);

    Activity getActivity();
}
