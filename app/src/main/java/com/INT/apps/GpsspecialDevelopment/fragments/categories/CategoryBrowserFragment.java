package com.INT.apps.GpsspecialDevelopment.fragments.categories;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.ListingViewActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.Category;
import com.INT.apps.GpsspecialDevelopment.data.models.CategoryBrowser;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.ListingRowViewHolderDealsMarker;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.HotListingLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.views.MapViewInsideScrollView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnCategoryFragmentInteractionListener}
 * interface.
 */
public class CategoryBrowserFragment extends Fragment implements OnMapReadyCallback {

    @InjectView(R.id.listing_map_view)
    protected MapViewInsideScrollView mapView;
    private GoogleMap googleMap;
    private HashMap<Marker, Listing_> markersMap = new HashMap<>();

    @InjectView(R.id.hot_listings_container)
    protected LinearLayout listingsContainer;

    private ArrayList<Category> mCategories;
    private OnCategoryFragmentInteractionListener mListener;
    public static final int PERMISSION_LOCATION = 1;

    public static CategoryBrowserFragment newInstance() {
        return new CategoryBrowserFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot_listings_map_with_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated");
        ButterKnife.inject(this, view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        fillCategories(false);
    }

    private boolean requestedLocationPermissions() {
        if (Build.VERSION.SDK_INT < 23) return true;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    public void fillCategories(boolean isLocalized) {
        if (isLocalized) {
            CategoryBrowser categoryBrowser = CategoryBrowser.getInstance(getContext());
            mCategories = categoryBrowser.getChildrenCategories(null);
            String s = getResources().getString(R.string.all_categories);
            String iconName = getResources().getString(R.string.icon_more);
            mCategories.add(new Category(s, -1, iconName));
        }

        listingsContainer.removeAllViews();
        CategoryRowView rowView = new CategoryRowView();
        int i = 0;
        for (Category category : getCategories()) {
            Timber.i("fill categories, category name: %s", category.getTitle());
            View convertView = getActivity().getLayoutInflater().inflate(R.layout.list_category, null, false);
            View categoryView = rowView.getView(getActivity(), category, convertView, listingsContainer);
            attachCategoryListener(categoryView, i);
            listingsContainer.addView(categoryView);
            i++;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    private void attachCategoryListener(View categoryView, final Integer position) {
        categoryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCategoryFragmentInteraction(getCategories().get(position));
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        IOBus.getInstance().register(this);
        super.onAttach(activity);
        try {
            mListener = (OnCategoryFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        IOBus.getInstance().unregister(this);
        super.onDetach();
        mListener = null;
    }

    protected ArrayList<Category> getCategories() {
        if (mCategories == null && getActivity() instanceof CategoryListDataSource) {
            mCategories = ((CategoryListDataSource) getActivity()).getCategories();
        }
        return mCategories;
    }

    public void setCategories(ArrayList<Category> categories) {
        mCategories = categories;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        prepareMap();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length == 0) return;
        if (requestCode == PERMISSION_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            prepareMap();
        }
    }

    @SuppressLint("MissingPermission")
    private void prepareMap() {
        if (googleMap != null && requestedLocationPermissions()) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            setMarkerInfoWindow();
        }
    }

    @Subscribe
    public void hotListingsLoaded(HotListingLoadedEvent listingLoaded) {
        if (googleMap != null) {
            googleMap.clear();
            if (listingLoaded.getListings().size() > 0) {
                LatLngBounds.Builder lngBuilder = new LatLngBounds.Builder();
                for (Listing_ listing : listingLoaded.getListings()) {
                    MarkerOptions marker = getMarker(listing);
                    Marker markerOb = googleMap.addMarker(marker);
                    markersMap.put(markerOb, listing);
                    lngBuilder.include(marker.getPosition());
                }
                LatLngBounds bounds = lngBuilder.build();
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));
            }
        }
    }

    private MarkerOptions getMarker(Listing_ listing) {
        MarkerOptions markerOptions = new MarkerOptions();
        BitmapDescriptor iconDeals = BitmapDescriptorFactory.fromResource(R.drawable.ic_deals_map);
        BitmapDescriptor iconNoDeals = BitmapDescriptorFactory.fromResource(R.drawable.ic_deals_no_map);
        if (listing.getDeals().size() == 0) {
            markerOptions.icon(iconNoDeals);
        } else {
            markerOptions.icon(iconDeals);
        }
        markerOptions.position(new LatLng(listing.getLatitude(), listing.getLongitude())).draggable(false);
        return markerOptions;
    }

    private void setMarkerInfoWindow() {
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.listing_marker_info_window, null);
                Listing_ listing = markersMap.get(marker);
                if (listing == null) {
                    return null;
                }
                ListingRowViewHolderDealsMarker viewHolder = new ListingRowViewHolderDealsMarker();
                viewHolder.setViewContent(getActivity(), listing, view, "TYPE_MAP");
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Listing_ listing = markersMap.get(marker);
                if (listing == null) {
                    return;
                }
                openListingView(listing);
            }
        });
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
    }

    protected void openListingView(Listing_ listing) {
        Intent intent = new Intent(getActivity(), ListingViewActivity.class);
        intent.putExtra(ListingViewActivity.ARG_LISTING_ID, listing.getId());
        intent.putExtra(ListingViewActivity.ARG_LISTING_TITLE, listing.getTitle());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_left_to_right);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCategoryFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onCategoryFragmentInteraction(Category category);
    }

    public interface CategoryListDataSource {
        public ArrayList<Category> getCategories();
    }
}
