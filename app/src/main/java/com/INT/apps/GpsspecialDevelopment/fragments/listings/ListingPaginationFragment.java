package com.INT.apps.GpsspecialDevelopment.fragments.listings;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.AbstractListingIndexActivity;
import com.INT.apps.GpsspecialDevelopment.activities.BaseActivity;
import com.INT.apps.GpsspecialDevelopment.activities.DealInformationActivity;
import com.INT.apps.GpsspecialDevelopment.activities.ListDealsPaginationActivity;
import com.INT.apps.GpsspecialDevelopment.activities.ListingViewActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingPaging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.ListingDealsRowVH;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.ListingRowViewHolderDealsMarker;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.map.ClusterListingItem;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.ListingPaginationDataEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.ListingPaginationFailedEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.listings.RequestListingPaginationEvent;
import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;
import com.INT.apps.GpsspecialDevelopment.utils.LocationDetector;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ListingPaginationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingPaginationFragment extends ListFragment {
    private static final String ARG_QUERY = "query";
    @InjectView(R.id.loading_problem_layout)
    public View loadingProblemView;

    @InjectView(R.id.loader_amin12)
    public View loaderAmination;

    @InjectView(android.R.id.list)
    public View listingListView;

    @InjectView(R.id.listing_map_wrapper)
    public View mapViewWrapper;

    @InjectView(R.id.listing_map_view)
    public MapView mapView;

    @InjectView(R.id.no_listing_found)
    public LinearLayout mNoListingFound;

    private FooterViewWrapper mFooterViewWrapper;

    private ListingPaginationQueryBuilder mQueryBuilder;

    private boolean mListingLoadingStarted = false;
    private boolean mListingLoaded = false;
    private boolean loadNewPageCalled = false;
    private boolean mapViewOn = false;
    private String mRequestKey;

    private MapRender mMapRender;
    private ListingPaging mlistingPaging;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListingPaginationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingPaginationFragment newInstance(HashMap<String, String> mQueryBuilder) {
        ListingPaginationFragment fragment = new ListingPaginationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUERY, mQueryBuilder);
        fragment.setArguments(args);
        return fragment;
    }

    public ListingPaginationFragment() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_QUERY)) {
            mQueryBuilder = ListingPaginationQueryBuilder.newInstance((HashMap<String, String>) getArguments().getSerializable(ARG_QUERY));
        } else {
            mQueryBuilder = ListingPaginationQueryBuilder.newInstance();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listing_pagination, container, false);
        ButterKnife.inject(this, view);
        mapView.onCreate(savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        IOBus.getInstance().register(this);
        listenToBackEvent();
        mapView.onResume();
        if (!mListingLoadingStarted) {
            mListingLoadingStarted = true;
            startListingLoading();
        }
        super.onResume();
    }

    private void startListingLoading() {
        mListingLoaded = false;
        hideViewsIfNotArg(loaderAmination);
        IOBus.getInstance().post(new RequestListingPaginationEvent(mQueryBuilder, mRequestKey, LocationDetector.getInstance(getActivity()).getLastLocation()));
    }

    @Subscribe
    public void listingsLoaded(ListingPaginationDataEvent event) {
        mListingLoaded = true;
        ListingPaging listingPaging = event.getListingPaging();
        mlistingPaging = listingPaging;
        mRequestKey = event.getRequestKey();

        attachToolbarActions((BaseActivity) getActivity());
        displayListingListView();
    }

    @Subscribe
    public void onUserLocation(LocationDetector.OnLocationEvent event) {
    }

    @Subscribe
    public void onUserLocationLoadError(LocationDetector.OnLocationFetchErrorEvent errorEvent) {
        Toast.makeText(getActivity(), "----**No Location", Toast.LENGTH_SHORT).show();
    }

    private void filterAndShowClusterListings(Collection<ClusterListingItem> clusterListingItems) {
        if (clusterListingItems.size() == 0) return;

        final List<Listing> listings = new ArrayList<>();
        for (ClusterListingItem listingItem : clusterListingItems) {
            final Listing listing = new Listing();
            listing.setListing(listingItem.getListing());
            listings.add(listing);
        }
        ListingIndexPresenter presenter = new ListingIndexPresenter(listings);
        clearPaginationRow();
        setListAdapter(presenter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listing_ listing = listings.get(position)
                        .getListing();
                openListingView(listing);
            }
        });
        switchToListView(false);
    }

    private void setMainListOfListings() {
        ListingIndexPresenter presenter = new ListingIndexPresenter(mlistingPaging.getListings());
        setPaginationView();
        setListAdapter(presenter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listing_ listing = mlistingPaging.getListings().get(position)
                        .getListing();
                openListingView(listing);

            }
        });
    }

    private void displayListingListView() {
        if (mlistingPaging.getPaging().getCount() == 0) {
            switchToListView(true);
            hideViewsIfNotArg(mNoListingFound);
            return;
        }
        hideViewsIfNotArg(listingListView);
        setMainListOfListings();
//        System.out.println("----**isNextPrev: " + ((ListingIndexActivity) getActivity()).getIsNextPreviousCalled());
        if (((AbstractListingIndexActivity) getActivity()).getIsNextPreviousCalled()) {
            // switchToMapView();
            //"MAPSAQIB"
            switchToListView(false);
            return;
        }
        //switchToListView();
        //"MAPSAQIB"
        switchToMapView();

    }

    @Subscribe
    public void listingLoadError(ListingPaginationFailedEvent event) {
        mListingLoaded = true;
        Toast.makeText(getActivity(), event.getError().getMessage(), Toast.LENGTH_SHORT).show();
        hideViewsIfNotArg(loadingProblemView);
    }

    @OnClick(R.id.retry_connection)
    public void reloadContent() {
        startListingLoading();
    }

    private void setPaginationView() {
        ListView listView = getListView();
        mFooterViewWrapper = new FooterViewWrapper(mlistingPaging);
        listView.setFooterDividersEnabled(true);
        listView.addFooterView(mFooterViewWrapper.getView(true));
    }

    private void clearPaginationRow() {
        ListView listView = getListView();
        listView.removeFooterView(mFooterViewWrapper.getView(false));
        listView.setFooterDividersEnabled(false);
    }

    @Override
    public void onPause() {
        if (mListingLoaded == false && mListingLoadingStarted == true) {
            mListingLoadingStarted = false;
        }
        IOBus.getInstance().unregister(this);
        getArguments().putString(ARG_QUERY, mQueryBuilder.toString());
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mFooterViewWrapper = null;
        mMapRender = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }


    public void loadPage(Integer page) {
        if (loadNewPageCalled)
            return;
        loadNewPageCalled = true;
        ListingPaginationQueryBuilder builder = ListingPaginationQueryBuilder.newInstance(mQueryBuilder.build());
        builder.setPageNumber(page);
        ((AbstractListingIndexActivity) getActivity()).attachListingIndexFragment(builder, true);
    }

    private void switchToMapView() {
        if (mMapRender == null) {
            mMapRender = new MapRender(mlistingPaging);
        }
        mapViewOn = true;
        hideViewsIfNotArg(mapViewWrapper);
        if (getActivity() != null) {
            ((AbstractListingIndexActivity) getActivity()).getToolbar().
                    getMenu().findItem(AbstractListingIndexActivity.MENU_ID_VIEW_TYPE).setIcon(R.drawable.ic_action_action_view_list_light);
        }
    }

    private void hideViewsIfNotArg(View view) {
        View[] views = {loaderAmination, loadingProblemView, listingListView, mapViewWrapper, mNoListingFound};
        for (View viewI : views) {
            if (viewI != view) {
                if (viewI == mapViewWrapper && mapViewWrapper.getVisibility() == View.VISIBLE) {
                    Animation slide_down = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                            R.anim.slide_down);
                    mapViewWrapper.startAnimation(slide_down);
                    slide_down.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mapViewWrapper.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });


                } else {
                    viewI.setVisibility(View.GONE);
                }
            }
        }
        if (view == mapViewWrapper) {
            Animation slide_up = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                    R.anim.slide_up);
            mapViewWrapper.setVisibility(View.VISIBLE);
            slide_up.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mMapRender.showWithAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mapViewWrapper.startAnimation(slide_up);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void switchToListView(boolean needFixClusterAdapter) {
        if (needFixClusterAdapter && getListView().getFooterViewsCount() == 0)
            setMainListOfListings();
        mapViewOn = false;
        hideViewsIfNotArg(listingListView);
        if (getActivity() != null) {
            ((AbstractListingIndexActivity) getActivity()).getToolbar().
                    getMenu().findItem(AbstractListingIndexActivity.MENU_ID_VIEW_TYPE).setIcon(R.drawable.nav_action_maps);
        }
    }

    private void attachToolbarActions(BaseActivity activity) {
        ((AbstractListingIndexActivity) activity).addTypeMenuItem();
        if (activity == null) {
            return;
        }
        activity.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == AbstractListingIndexActivity.MENU_ID_VIEW_TYPE) {
                    if (mapViewOn == false) {
                        switchToMapView();
                    } else {
                        switchToListView(true);
                        if (mListingLoaded && mlistingPaging.getPaging().getCount() == 0)
                            hideViewsIfNotArg(mNoListingFound);
                    }
                }
                return false;
            }
        });
    }

    private void listenToBackEvent() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK && mapViewOn) {
                    switchToListView(true);
                    return true;
                }
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

    class ListingIndexPresenter extends BaseAdapter {

        private final static int TYPE_LISTING = 0;
        private final static int TYPE_LISTING_DEALS = 1;

        final List<Listing> listings = new ArrayList<>();

        ListingIndexPresenter(List<Listing> listings) {
            this.listings.addAll(listings);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return getItem(position).getDealsPaging().getCount() == 0 ? TYPE_LISTING : TYPE_LISTING_DEALS;
        }

        @Override
        public int getCount() {
            return listings.size();
        }

        @Override
        public Listing_ getItem(int position) {
            return listings.get(position).getListing();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Listing_ listing = getItem(position);

            switch (getItemViewType(position)) {
                case TYPE_LISTING:
                    ListingRowViewHolderDealsMarker listingViewHolder;
                    if (convertView == null || !(convertView.getTag() instanceof ListingRowViewHolderDealsMarker)) {
                        convertView = getActivity().getLayoutInflater().inflate(R.layout.listing_pagination_row, parent, false);
                        listingViewHolder = new ListingRowViewHolderDealsMarker(80, 80);
                        convertView.setTag(listingViewHolder);
                    } else {
                        listingViewHolder = (ListingRowViewHolderDealsMarker) convertView.getTag();
                    }

                    listingViewHolder.setViewContent(getActivity(), listing, convertView, "TYPE_LIST");
                    if (listing.isFeatured()) {
                        convertView.setBackground(getResources().getDrawable(R.drawable.paginate_featured_listing));
                    } else {
                        convertView.setBackground(getResources().getDrawable(android.R.color.transparent));
                    }
                    View textView = convertView.findViewById(R.id.is_featured);
                    if (textView != null) {
                        textView.setVisibility(View.GONE);
                        if (listing.isFeatured()) {
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
                case TYPE_LISTING_DEALS:
                    ListingDealsRowVH listingDealsRowVH;
                    if (convertView == null || !(convertView.getTag() instanceof ListingDealsRowVH)) {
                        convertView = getActivity().getLayoutInflater().inflate(R.layout.listing_deals_pagintaion_row, parent, false);
                        listingDealsRowVH = new ListingDealsRowVH(convertView);
                        convertView.setTag(listingDealsRowVH);
                    } else {
                        listingDealsRowVH = (ListingDealsRowVH) convertView.getTag();
                    }
                    listingDealsRowVH.setListing(listing, dealsGroupClickListener);
                    break;
            }
            return convertView;
        }

        private final ListingDealsRowVH.DealsGroupClickListener dealsGroupClickListener = new ListingDealsRowVH.DealsGroupClickListener() {
            @Override
            public void chooseDeal(DealInfo dealsInfo) {
                Intent intent = new Intent(getActivity(), DealInformationActivity.class);
                intent.putExtra(DealInformationActivity.ARG_DEAL_ID, dealsInfo.getId());
                intent.putExtra(DealInformationActivity.ARG_DEAL_TITLE, dealsInfo.getTitle());
                intent.putExtra(DealInformationActivity.ARG_DEAL_IMAGE, dealsInfo.getImage());
                startActivity(intent);
            }

            @Override
            public void showMore(Listing_ listing) {
                Intent intent = new Intent(getActivity(), ListDealsPaginationActivity.class);
                intent.putExtra(ListDealsPaginationActivity.ARG_LISTING, listing);
                startActivity(intent);
            }
        };
    }

    class FooterViewWrapper {
        ListingPaging mListingPaging;

        private View view;

        @InjectView(R.id.load_next_page)
        Button nextButton;

        @InjectView(R.id.load_prev_page)
        Button previousButton;


        FooterViewWrapper(ListingPaging listingPaging) {
            mListingPaging = listingPaging;
        }

        View getView(boolean forceInflate) {
            if (view != null && !forceInflate)
                return view;
            else {
                view = getActivity().getLayoutInflater().inflate(R.layout.listing_pagination_footer, null);
                ButterKnife.inject(this, view);
                int nextVisibility = View.GONE;
                int prevVisiblity = View.GONE;
                int buttonCount = 0;
                Button onlyVisibleButton = null;
                if (mListingPaging.getPaging().hasNextPage()) {
                    buttonCount++;
                    nextVisibility = View.VISIBLE;
                    onlyVisibleButton = nextButton;
                }
                if (mListingPaging.getPaging().hasPreviousPage()) {
                    buttonCount++;
                    prevVisiblity = View.VISIBLE;
                    onlyVisibleButton = previousButton;
                }
                nextButton.setVisibility(nextVisibility);
                String limit = String.valueOf(mListingPaging.getPaging().getLimit());
                nextButton.setText(getResources().getString(R.string.load_next_listing_page, limit));

                previousButton.setVisibility(prevVisiblity);
                previousButton.setText(getResources().getString(R.string.load_prev_listing_page, limit));
                if (onlyVisibleButton != null && buttonCount == 1) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) onlyVisibleButton.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                }

                return view;
            }
        }

        @OnClick(R.id.load_next_page)
        void onNextClick() {
            ((AbstractListingIndexActivity) getActivity()).setIsNextPreviousCalled(true);
            loadPage(mListingPaging.getPaging().getPage() + 1);
        }

        @OnClick(R.id.load_prev_page)

        public void onPrevClick() {
            ((AbstractListingIndexActivity) getActivity()).setIsNextPreviousCalled(true);
            loadPage(mListingPaging.getPaging().getPage() - 1);
        }
    }

    class MapRender implements OnMapReadyCallback {
        private GoogleMap mGoogleMap;
        private ClusterManager mClusterManager;
        private ListingPaging mListingPaging;
        private boolean mapReady = false;
        private boolean showOnLoad = true;
        private boolean mapRendered = false;
        private Listing_ chosenListing;

        MapRender(ListingPaging listingPaging) {
            mListingPaging = listingPaging;
            mapView.getMapAsync(this);
        }

        void showWithAnimation() {
            if (mapReady == false) {
                showOnLoad = true;
                return;
            }

            if (mapRendered) {
                return;
            }
            mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    onMapRenderingReady();
                }
            });
        }

        @SuppressWarnings("unchecked")
        private void onMapRenderingReady() {
            MapsInitializer.initialize(getActivity());

            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.setMyLocationEnabled(true);

            LatLngBounds latLngBounds = null;
            mClusterManager.clearItems();
            for (Listing listing : mListingPaging.getListings()) {
                final ClusterListingItem clusterListingItem = new ClusterListingItem(listing.getListing());

                if (clusterListingItem.getPosition() != null) {
                    mClusterManager.addItem(clusterListingItem);
                    if (latLngBounds == null)
                        latLngBounds = new LatLngBounds(clusterListingItem.getPosition(), clusterListingItem.getPosition());
                    else latLngBounds = latLngBounds.including(clusterListingItem.getPosition());
                }
            }

            mClusterManager.cluster();
            mapRendered = true;
            setBounds(latLngBounds);
        }

        private void setBounds(LatLngBounds bounds) {
            CameraUpdate cameraUpdate;
            if (bounds == null) {
                Double lat = ((AbstractListingIndexActivity) getActivity()).getLattitude();
                Double lang = ((AbstractListingIndexActivity) getActivity()).getLongitude();
                if (lat == null || lang == null) {
                    return;
                }
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lang), 16);
            } else {
                cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 70);
            }

            try {
                mGoogleMap.animateCamera(cameraUpdate);
            } catch (IllegalStateException e) {
                final MapView mapView1 = mapView;
                final CameraUpdate finalCameraUpdate = cameraUpdate;
                mapView.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            @SuppressWarnings("deprecation")
                            public void onGlobalLayout() {
                                mapView1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                mGoogleMap.animateCamera(finalCameraUpdate);
                            }
                        }
                );
            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            initClusterManager();
            mapReady = true;
            if (showOnLoad) {
                //showWithAnimation();
            }
        }

        @SuppressWarnings("unchecked")
        private void initClusterManager() {
            mClusterManager = new ClusterManager<ClusterListingItem>(getActivity(), mGoogleMap);
            mClusterManager.setRenderer(new ListingRenderer());
            mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    Timber.i("info window");
                    View view = getActivity().getLayoutInflater().inflate(R.layout.listing_marker_info_window, null);
                    Listing_ listing = chosenListing;
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
            mGoogleMap.setOnCameraChangeListener(mClusterManager);
            mGoogleMap.setOnMarkerClickListener(mClusterManager);
            mGoogleMap.setOnInfoWindowClickListener(mClusterManager);
            mGoogleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

            mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<ClusterListingItem>() {
                @Override
                public boolean onClusterItemClick(ClusterListingItem clusterItem) {
                    chosenListing = clusterItem.getListing();
                    return false;
                }
            });
            mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterListingItem>() {
                @Override
                public void onClusterItemInfoWindowClick(ClusterListingItem clusterItem) {
                    openListingView(clusterItem.getListing());
                }
            });
            mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener() {
                @Override
                public boolean onClusterClick(Cluster cluster) {
                    float maxZoom = mGoogleMap.getMaxZoomLevel();
                    if (mGoogleMap.getCameraPosition().zoom == maxZoom) {
                        filterAndShowClusterListings(cluster.getItems());
                    } else {
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), Math.min(mGoogleMap.getCameraPosition().zoom + 1f, maxZoom)));
                    }
                    return true;
                }
            });

        }

        class ListingRenderer extends DefaultClusterRenderer<ClusterListingItem> {

            private ViewGroup iconClusterView;
            private TextView clusterCountView;

            private final int MIN_CLUSTER_SIZE = 2; //By default
            private Bitmap currentBitmap;

            ListingRenderer() {
                super(getActivity(), mGoogleMap, mClusterManager);
                iconClusterView = (ViewGroup) getLayoutInflater().inflate(R.layout.view_icon_cluster, null);
                clusterCountView = iconClusterView.findViewById(R.id.cluster_count);
            }

            @Override
            protected void onBeforeClusterItemRendered(ClusterListingItem item, MarkerOptions markerOptions) {
                //Draw usual marker
                BitmapDescriptor iconDeals = BitmapDescriptorFactory.fromResource(R.drawable.ic_deals_map);
                BitmapDescriptor iconNoDeals = BitmapDescriptorFactory.fromResource(R.drawable.ic_deals_no_map);
                if (item.getListing().getDealsPaging().getCount() == 0) {
                    markerOptions.icon(iconNoDeals);
                } else {
                    markerOptions.icon(iconDeals);
                }
            }

            @Override
            protected void onBeforeClusterRendered(Cluster<ClusterListingItem> cluster, MarkerOptions markerOptions) {
                //Draw cluster marker
                final String currentCount = clusterCountView.getText().toString();
                if (!TextUtils.isEmpty(currentCount) && Integer.parseInt(currentCount) == cluster.getSize() && currentBitmap != null) {
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(currentBitmap));
                    return;
                }

                int size = (int) getSizeClusterMarker(cluster.getSize());
                clusterCountView.setText(String.valueOf(cluster.getSize()));
                iconClusterView.measure(size, size);
                iconClusterView.layout(0, 0, size, size);
                iconClusterView.buildDrawingCache();

                currentBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(currentBitmap);
                iconClusterView.draw(canvas);

                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(currentBitmap));
            }

            @Override
            protected boolean shouldRenderAsCluster(Cluster cluster) {
                // Always render clusters IF:
                return cluster.getSize() >= MIN_CLUSTER_SIZE;
            }

            private float getSizeClusterMarker(final int countMarkers) {
                if (countMarkers >= 0 && countMarkers < 10)
                    return getResources().getDimension(R.dimen.cluster_size_10);
                else if (countMarkers >= 10 && countMarkers <= 20)
                    return getResources().getDimension(R.dimen.cluster_size_20);
                else return getResources().getDimension(R.dimen.cluster_size_more);
            }
        }
    }
}