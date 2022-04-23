package com.hendyghsta.mvp.utils.mvp;

/**
 * Created by hendyghsta on 04/22/2022.
 */
public class BasePresenter<ViewT> implements IBasePresenter<ViewT> {

    protected ViewT view;

    @Override
    public void onViewActive(ViewT view) {
        this.view = view;
    }

    @Override
    public void onViewInactive() {
        view = null;
    }
}
