package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Intent;
import android.os.Bundle;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.SearchBoxFragment;
import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;
import com.INT.apps.GpsspecialDevelopment.utils.LocationDetector;

public class ListingSearchBoxActivity extends BaseActivity implements SearchBoxFragment.QuerySelectListener {

    private boolean finishOnResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_search_box);
        if (finishOnResume == false) {
            LocationDetector.getInstance(getApplicationContext()).getLocation();
        }
        SearchBoxFragment searchBoxFragment = SearchBoxFragment.newInstance(null, false);
        getSupportFragmentManager().beginTransaction().add(R.id.search_box_wrapper, searchBoxFragment, "tag").commit();
    }

    @Override
    public void onQueryResult(ListingPaginationQueryBuilder query) {
        String location = query.getLocationSearchKeyword();
        if ((location == null || location.length() == 0)) {
            query.enableSearchByCoordinates();
        } else {
            query.setSearchLocationKeyword(location);
        }
        Intent intent = new Intent(this, ListingIndexActivity.class);
        intent.putExtra(ListingIndexActivity.SEARCH_QUERY, query.build());
        finishOnResume = true;
        startActivity(intent);
    }

    @Override
    public void onSearchCancel(ListingPaginationQueryBuilder query) {
        finish();
    }

    @Override
    protected void onStart() {
        if (finishOnResume == true) {
            finish();
        }
        super.onStart();
    }

    @Override
    protected boolean useNavigationDrawer() {
        return false;
    }
}
