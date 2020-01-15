package com.INT.apps.GpsspecialDevelopment.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.ListingGalleryFragment;

public class ListingAssetViewActivity extends BaseActivity {
    public static String ARG_LISTING_ID = "listing_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_asset_view);
        Toolbar toolbar = setToolbar();
        toolbar.setTitle(R.string.business_photos);
        String listingID = getIntent().getStringExtra(ARG_LISTING_ID);
        Fragment f = getSupportFragmentManager().findFragmentByTag("gallery_fragment");
        if (f == null) {
            ListingGalleryFragment galleryFragment = ListingGalleryFragment.newInstance(listingID, 50, false);
            getSupportFragmentManager().beginTransaction().replace(R.id.gallery_grid, galleryFragment, "gallery_fragment")
                    .commit();
        }
    }
}
