package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.DrawerMenuFragment;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user.CheckUserSessionEvent;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;

import co.lokalise.android.sdk.core.LokaliseContextWrapper;

/**
 * Created by shrey on 29/4/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    protected DrawerLayout mFullLayout;
    protected FrameLayout mActContent;
    private boolean checkSession = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && UserSession.getUserSession().isLoggedIn() == false) {
            checkSession = savedInstanceState.getBoolean("baseReLogin", false);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    protected Toolbar setToolbar(boolean withupNav) {
        if (withupNav) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.screen_toolbar);
            toolbar.setNavigationIcon(R.drawable.nav_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
            setToolbarNav(toolbar);
            return setToolbar(toolbar, R.menu.menu_default);
        } else {
            return setToolbar();
        }
    }

    protected Toolbar setToolbar() {
        return setToolbar(R.menu.menu_default);
    }

    protected Toolbar setToolbar(int menuId) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.screen_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu_light);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });
        setToolbarNav(toolbar);
        if (toolbar != null) {
            return setToolbar(toolbar, menuId);
        }
        return null;
    }

    protected Toolbar setToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
        mToolbar.setTitleTextAppearance(this, R.style.toolbar_title);
        return mToolbar;
    }

    protected Toolbar setToolbar(Toolbar toolbar, int menuId) {
        setToolbar(toolbar);
        toolbar.inflateMenu(menuId);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
        return toolbar;
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected void setToolbarNav(Toolbar toolbar) {

    }

    protected boolean useNavigationDrawer() {
        return true;
    }

    @Override
    public void setContentView(int layoutResID) {
        if (useNavigationDrawer()) {
            mFullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.drawer_layout, null);
            mActContent = (FrameLayout) mFullLayout.findViewById(R.id.content_frame);
            getLayoutInflater().inflate(layoutResID, mActContent, true);
            super.setContentView(mFullLayout);

            Fragment drawerFragment = getSupportFragmentManager().findFragmentById(R.id.left_drawer);
            if (drawerFragment == null) {
                drawerFragment = DrawerMenuFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.left_drawer, drawerFragment).commit();
            }
            return;
        }
        super.setContentView(layoutResID);
    }

    public void openDrawer() {
        if (useNavigationDrawer() && mFullLayout != null) {
            mFullLayout.openDrawer(Gravity.START);
        }
    }

    public void closeDrawer() {
        if (useNavigationDrawer() && mFullLayout != null) {
            mFullLayout.closeDrawer(Gravity.START);
        }
    }

    public void openLoginPage() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (UserSession.getUserSession().isLoggedIn()) {
            outState.putBoolean("baseReLogin", true);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSession) {
            checkSession = false;
            IOBus.getInstance().post(new CheckUserSessionEvent());
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        //Lokalise integration
        super.attachBaseContext(LokaliseContextWrapper.wrap(newBase));
    }
}