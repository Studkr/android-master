package com.INT.apps.GpsspecialDevelopment.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.ListingPaginationFragment;
import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;

import java.util.HashMap;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by shrey on 7/8/15.
 */
abstract public class AbstractListingIndexActivity extends BaseActivity {
    private ListingPaginationQueryBuilder mBuilder;
    protected static String SEARCH_QUERY = "search_query";
    public static int MENU_ID_VIEW_TYPE = 1;
    private boolean isNextPreviousCalled = false;
    private String isSearchByCurrLoc;
    private Double lattitude;


    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLattitude() {
        return lattitude;
    }

    public void setLattitude(Double lattitude) {
        this.lattitude = lattitude;
    }

    private Double longitude;

    public void attachListingIndexFragment(ListingPaginationQueryBuilder builder, boolean override) {
        //findViewById(R.id.loader_amin12).setVisibility(View.GONE);
        //findViewById(R.id.paginate_fragment).setVisibility(View.VISIBLE);
        getToolbar().setOnMenuItemClickListener(null);
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("listing_pagination_tag");
        if (fragment != null && override) {
            fm.beginTransaction().remove(fragment).commit();
            fragment = null;
        }
        if (fragment == null) {
            fragment = ListingPaginationFragment.newInstance(builder.build());
            fm.beginTransaction().add(R.id.paginate_fragment, fragment, "listing_pagination_tag").commit();
        } else {
            Timber.tag("found already").d("found already");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_index);
        ButterKnife.inject(this);
        HashMap<String, String> searchQuery = null;
        if (savedInstanceState != null) {
            searchQuery = (HashMap<String, String>) savedInstanceState.getSerializable(SEARCH_QUERY);
        } else {
            searchQuery = (HashMap<String, String>) getIntent().getSerializableExtra(SEARCH_QUERY);
        }
        ListingPaginationQueryBuilder builder = ListingPaginationQueryBuilder.newInstance(searchQuery);
        setQueryBuilder(builder);
        Toolbar toolbar = setToolbar();
        setToolbarMenuItems(toolbar);
    }

    public void addTypeMenuItem() {
        if (getToolbar().getMenu().size() == 0) {
            getToolbar().getMenu().add(1, MENU_ID_VIEW_TYPE, 1, "Map Icon").
                    setIcon(R.drawable.icon_more).
                    setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
    }

    protected void setQueryBuilder(ListingPaginationQueryBuilder builder) {
        mBuilder = builder;
    }

    protected ListingPaginationQueryBuilder getQueryBuilder() {
        return mBuilder;
    }

    abstract void setToolbarMenuItems(Toolbar toolbar);

    public void setIsNextPreviousCalled(boolean isNextPreviousCalled) {
        this.isNextPreviousCalled = isNextPreviousCalled;
    }

    public boolean getIsNextPreviousCalled() {
        return isNextPreviousCalled;
    }

    public String getIsSearchByCurrLoc() {
        return isSearchByCurrLoc;
    }

    public void setIsSearchByCurrLoc(String isSearchByCurrLoc) {
        this.isSearchByCurrLoc = isSearchByCurrLoc;
    }
}
