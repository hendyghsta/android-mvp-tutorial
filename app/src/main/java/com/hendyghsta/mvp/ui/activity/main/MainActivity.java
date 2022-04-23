package com.hendyghsta.mvp.ui.activity.main;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hendyghsta.mvp.R;
import com.hendyghsta.mvp.ui.fragment.main.MainFragment;
import com.hendyghsta.mvp.utils.BitmapUtil;
import com.hendyghsta.mvp.utils.Navigation;
import com.hendyghsta.mvp.utils.TransitionHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hendyghsta on 04/22/2022.
 */
public class MainActivity extends TransitionHelper.BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    public @BindView(R.id.toolbar)
    Toolbar toolbar;
    public @BindView(R.id.base_fragment_background)
    View fragmentBackground;

    private Window window;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initBaseFragment(savedInstanceState);
    }

    private void initToolbar() {
        window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    private void initBaseFragment(Bundle savedInstanceState) {
        if (getIntent().hasExtra(Navigation.BITMAP_ID)) {
            fragmentBackground.setBackground(new BitmapDrawable(getResources(), BitmapUtil.fetchBitmapFromIntent(getIntent())));
        }

        Fragment fragment = null;
        if (savedInstanceState != null) {
            fragment = getSupportFragmentManager().findFragmentByTag(Navigation.BASE_FRAGMENT);
        }
        if (fragment == null)
            fragment = getBaseFragment();
        setBaseFragment(fragment);
    }

    protected Fragment getBaseFragment() {
        int fragmentResourceId = getIntent().getIntExtra(Navigation.FRAGMENT_RESOURCE_ID, R.layout.fragment_main);
        switch (fragmentResourceId) {
            case R.layout.fragment_main:
            default:
                return new MainFragment();
        }
    }

    public void setBaseFragment(Fragment fragment) {
        if (fragment == null)
            return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.base_fragment, fragment, Navigation.BASE_FRAGMENT);
        transaction.commit();
    }

    @Override
    public boolean onBeforeBack() {
        ActivityCompat.finishAfterTransition(this);
        return super.onBeforeBack();
    }

    public static MainActivity of(Activity activity) {
        return (MainActivity) activity;
    }
}
