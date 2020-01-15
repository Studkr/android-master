package com.INT.apps.GpsspecialDevelopment.fragments.listings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.INT.apps.GpsspecialDevelopment.CvSettings;
import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.ListingViewActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.HotListingLoadEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.HotListingLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.HotListingsLoadingFailedEvent;
import com.INT.apps.GpsspecialDevelopment.utils.LocationDetector;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link HotListingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotListingsFragment extends Fragment {

    private boolean mLocationChecked = false;


    @InjectView(R.id.location_checker_layout)
    public View connectionPrompt;
    @InjectView(R.id.no_listing_found)
    public View emptyPrompt;
    @InjectView(R.id.loader_amin)
    public View loaderAnimation;
    @InjectView(R.id.host_listings_list)
    public LinearLayout hotListingsViewLayout;
    @InjectView(R.id.host_listings_list_container)
    public View hotListingsViewContainer;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HotListingsFragment.
     */
    public static HotListingsFragment newInstance() {
        HotListingsFragment fragment = new HotListingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HotListingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.d("----**hot listing 1:");
        if (savedInstanceState != null) {
            Timber.d("----**hot listing 2:");
            mLocationChecked = savedInstanceState.getBoolean("locationChecked", false);
            Timber.d("----**hot listing 3:");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("----**hot listing 4:");
        View view = inflater.inflate(R.layout.fragment_hot_listings, container, false);
        ButterKnife.inject(this, view);
        Timber.d("----**hot listing 5:");
        LocationDetector locationDetector = getLocationDetector();
        Timber.d("----**hot listing 6:");
        if (locationDetector.isLocationEnabled() == false) {
            Timber.d("----**hot listing 7:");
            Timber.d("----**Location is enable.");
        } else {
            Timber.d("----**hot listing 8:");
            view.findViewById(R.id.location_checker_layout).setVisibility(View.GONE);
            Timber.d("----**hot listing 9:");
            checkAndDisplayListings();
            Timber.d("----**hot listing 10:");
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Timber.i("----**hot listing 11:");
        outState.putBoolean("locationChecked", mLocationChecked);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.check_location_retry)
    public void checkAndDisplayListings() {
        //@todo add loader and show failure with anmination.
        LocationDetector locationDetector = getLocationDetector();
        if (locationDetector.isLocationEnabled() == false) {
            connectionPrompt.setVisibility(View.VISIBLE);
            hotListingsViewContainer.setVisibility(View.GONE);
            loaderAnimation.setVisibility(View.GONE);
            emptyPrompt.setVisibility(View.GONE);
            return;
        }
        connectionPrompt.setVisibility(View.GONE);
        hotListingsViewContainer.setVisibility(View.GONE);
        loaderAnimation.setVisibility(View.VISIBLE);
        locationDetector.getLocation();

    }

    @Subscribe
    public void onUserLocation(LocationDetector.OnLocationEvent event) {
        Timber.i("----**hot listing onUser Location:");
        loadListingsFromLocation(event.getLatitude(), event.getLongitude());
    }

    @Subscribe
    public void onUserLocationError(LocationDetector.OnLocationFetchErrorEvent event) {
        Timber.i("----**hot listing onUserLocationError:");
        connectionPrompt.setVisibility(View.VISIBLE);
        hotListingsViewContainer.setVisibility(View.GONE);
        loaderAnimation.setVisibility(View.GONE);
        emptyPrompt.setVisibility(View.GONE);
    }

    private void loadListingsFromLocation(double latitude, double longitude) {
        Timber.i("----**hot listing loadListingsFromLocation:");
        Timber.i("----**lat-lang:" + latitude + "-" + longitude);
        IOBus.getInstance().post(new HotListingLoadEvent(latitude, longitude));
    }

    @Subscribe
    public void hotListingsLoaded(HotListingLoadedEvent listingLoaded) {
        hotListingsViewContainer.setVisibility(View.VISIBLE);
        connectionPrompt.setVisibility(View.GONE);
        loaderAnimation.setVisibility(View.GONE);
        emptyPrompt.setVisibility((listingLoaded.getListings() != null && listingLoaded.getListings().size() > 0) ? View.GONE : View.VISIBLE);
        View convertView = null;
        hotListingsViewLayout.removeAllViews();
        for (Listing_ listing : listingLoaded.getListings()) {
            convertView = renderListingToLayout(hotListingsViewLayout, listing, null);
            hotListingsViewLayout.addView(convertView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        LocationDetector locationDetector = getLocationDetector();
        if (locationDetector.isLocationEnabled() == false && mLocationChecked == false) {
            mLocationChecked = true;
            locationDetector.showSettingsDialog(getActivity());
        }
    }

    public void onLocationSettings() {
        checkAndDisplayListings();
    }

    private LocationDetector getLocationDetector() {
        LocationDetector locationDetector = LocationDetector.getInstance(getActivity().getApplication());
        return locationDetector;
    }

    private View renderListingToLayout(View parentView, final Listing_ listing, View convertView) {
        int screenWidth = CvSettings.getScreenWidthInDp();
        ListingRowViewHolder listingViewHolder = new ListingRowViewHolder(190, screenWidth);
        convertView = getActivity().getLayoutInflater().inflate(R.layout.listings_featured_row, null);
        listingViewHolder.setViewContent(getActivity(), listing, convertView);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openListingView(listing);
            }
        });
        return convertView;
    }

    protected void openListingView(Listing_ listing) {
        Intent intent = new Intent(getActivity(), ListingViewActivity.class);
        intent.putExtra(ListingViewActivity.ARG_LISTING_ID, listing.getId());
        intent.putExtra(ListingViewActivity.ARG_LISTING_TITLE, listing.getTitle());
        startActivity(intent);
    }

    @Override
    public void onAttach(Activity activity) {
        IOBus.getInstance().register(this);
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        IOBus.getInstance().unregister(this);
        super.onDetach();
    }

    @Subscribe
    public void onNetworkError(HotListingsLoadingFailedEvent event) {
        connectionPrompt.setVisibility(View.VISIBLE);
        hotListingsViewContainer.setVisibility(View.GONE);
        loaderAnimation.setVisibility(View.GONE);
        emptyPrompt.setVisibility(View.GONE);
    }
}


