package com.INT.apps.GpsspecialDevelopment.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.reviews.AbstractReviewListFragment;

/**
 * Created by shrey on 20/8/15.
 */
abstract public class AbstractReviewPaginationActivity extends BaseActivity
{
    abstract AbstractReviewListFragment getReviewFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_pagination);
        FragmentManager fm = getSupportFragmentManager();
        if(fm.findFragmentByTag("review_pagination") == null)
        {
            Fragment fragment = getReviewFragment();
            fm.beginTransaction().
                    add(R.id.review_pagination, fragment, "review_pagination")
                    .commit();
        }

    }

    @Override
    protected boolean useNavigationDrawer() {
        return false;
    }
}
