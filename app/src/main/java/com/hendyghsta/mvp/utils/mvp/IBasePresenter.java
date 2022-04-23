package com.hendyghsta.mvp.utils.mvp;

/**
 * Created by hendyghsta on 04/22/2022.
 */
public interface IBasePresenter<ViewT> {

    void onViewActive(ViewT view);

    void onViewInactive();
}
