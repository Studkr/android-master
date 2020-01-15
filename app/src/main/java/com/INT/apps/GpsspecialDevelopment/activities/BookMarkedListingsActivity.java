package com.INT.apps.GpsspecialDevelopment.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.session.UserSession;
import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;

public class BookMarkedListingsActivity extends AbstractListingIndexActivity
{
    public static String ARG_USER_ID="user_id";
    public static String ARG_USER_NAME="username";
    public static String mUsername;
    public String mUserId;
    public boolean mIsMyFavorite;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mUserId = getIntent().getStringExtra(ARG_USER_ID);
        mUsername = getIntent().getStringExtra(ARG_USER_NAME);
        ListingPaginationQueryBuilder builder = ListingPaginationQueryBuilder.newInstance();
        if(UserSession.getUserSession().isLoggedIn() && mUserId.equals(UserSession.getUserSession().getSessionUser().getId()))
        {
            mIsMyFavorite = true;
            builder.setMethod("myfavorite");
        }else
        {
            mIsMyFavorite = false;
            builder.setMethod("favorite");
            builder.setNamedParameter("user",mUserId);
        }
        super.onCreate(savedInstanceState);
        setQueryBuilder(builder);
        attachListingIndexFragment(getQueryBuilder(),true);
    }

    @Override
    void setToolbarMenuItems(Toolbar toolbar)
    {
        if (mIsMyFavorite == true)
        {
            toolbar.setTitle(R.string.bookmarked_business);
        }else
        {
            toolbar.setTitle(getString(R.string.users_bookmarks,mUsername));
        }
    }
}
