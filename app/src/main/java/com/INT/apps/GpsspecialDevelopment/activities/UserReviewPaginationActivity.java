package com.INT.apps.GpsspecialDevelopment.activities;

import android.os.Bundle;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.reviews.AbstractReviewListFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.reviews.UserReviewListFragment;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;

/**
 * Created by shrey on 20/8/15.
 */
public class UserReviewPaginationActivity extends AbstractReviewPaginationActivity
{
    public static String ARG_USER_ID="user_id";
    public static String ARG_USERNAME="username";

    private String mUsername;
    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mUsername = getIntent().getStringExtra(ARG_USERNAME);
        mUserId = getIntent().getStringExtra(ARG_USER_ID);
        super.onCreate(savedInstanceState);
        setToolbar(true);
        String title = getResources().getString(R.string.entity_reviews,mUsername);
        if(UserSession.getUserSession().isLoggedIn())
        {
            UserSession session = UserSession.getUserSession();
            if(session.getSessionUser().getId().equals(mUserId))
            {
                title = getResources().getString(R.string.my_reviews);
            }
        }
        getToolbar().setTitle(title);
    }

    @Override
    AbstractReviewListFragment getReviewFragment()
    {
        String title = getResources().getString(R.string.entity_reviews,mUsername);
        if(UserSession.getUserSession().isLoggedIn())
        {
            UserSession session = UserSession.getUserSession();
            if(session.getSessionUser().getId().equals(mUserId))
            {
                title = getResources().getString(R.string.my_reviews);
            }
        }
        return UserReviewListFragment.newInstance(mUserId,title);
    }
}
