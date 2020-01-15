package com.INT.apps.GpsspecialDevelopment.fragments.deals;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.CouponCodesActivity;
import com.INT.apps.GpsspecialDevelopment.activities.DealInformationActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PurchasedDeal;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PurchasedDeals;
import com.INT.apps.GpsspecialDevelopment.fragments.BaseFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.DealRowVH;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadMyPurchasedDealsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OnMyPurchasedDealsErrorEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.OnMyPurchasedDealsLoadedEvent;
import com.INT.apps.GpsspecialDevelopment.utils.LocationDetector;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author Michael Soyma (Created on 9/25/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class MyPurchasesFragment extends BaseFragment implements IPurchasesRender {

    public static final String ARG_USED = "arg_used";

    private boolean used;
    private int currentPage;
    private PurchasedDeals loadedPurchases;

    @InjectView(R.id.loading_problem_layout)
    protected View problemLoadView;
    @InjectView(R.id.emptyView)
    protected View emptyView;
    @InjectView(R.id.progressView)
    protected View progressLoadView;
    @InjectView(R.id.listDeals)
    protected ListView listPurchasesView;

    protected MapView mapView;
    @InjectView(R.id.map_container)
    protected FrameLayout mapViewContainer;

    private PurchasesListAdapter purchasesListAdapter = new PurchasesListAdapter();
    private View footerPaginationView;
    private GoogleMap googleMap;

    //For separating deal's with the same location(listing)
    private SparseArray<Pair<Marker, PurchasedDeal>> dealMarkers = new SparseArray<>(); // Key - listing id.

    public static MyPurchasesFragment newInstance(final boolean used) {
        final MyPurchasesFragment myPurchasesFragment = new MyPurchasesFragment();
        final Bundle args = new Bundle();
        args.putBoolean(ARG_USED, used);
        myPurchasesFragment.setArguments(args);
        return myPurchasesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            used = getArguments().getBoolean(ARG_USED, false);
        }
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        if (mapView != null)
            mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onPause() {
        if (mapView != null)
            mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (mapView != null)
            mapView.onResume();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mapView = new MapView(getActivity(), new GoogleMapOptions().zoomControlsEnabled(true));
        mapView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return inflater.inflate(R.layout.fragment_my_purchases, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mapView.onCreate(savedInstanceState);

        listPurchasesView.setAdapter(purchasesListAdapter);
        listPurchasesView.setFooterDividersEnabled(false);

        loadPurchases(1);
    }

    @Override
    public void onStart() {
        super.onStart();
        IOBus.getInstance().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        IOBus.getInstance().unregister(this);
    }

    @OnClick(R.id.retry_connection)
    protected void retry() {
        loadPurchases(currentPage);
    }

    @OnItemClick(R.id.listDeals)
    protected void onDealClick(int position) {
        viewDealDetail(loadedPurchases.getPurchasedDeals().get(position).getDealOrder());
    }

    private void viewDealDetail(final PurchasedDeal.DealOrder dealOrder) {
        Intent intent = new Intent(getActivity(), DealInformationActivity.class);
        intent.putExtra(DealInformationActivity.ARG_DEAL_ID, dealOrder.getDealId());
        intent.putExtra(DealInformationActivity.ARG_DEAL_TITLE, dealOrder.getTitle());
        intent.putExtra(DealInformationActivity.ARG_DEAL_IMAGE, dealOrder.getImage());
        startActivity(intent);
    }

    private void loadPurchases(final int page) {
        this.currentPage = page;

        purchasesListAdapter.destroy();
        listPurchasesView.removeFooterView(footerPaginationView);

        emptyView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.GONE);
        progressLoadView.setVisibility(View.VISIBLE);

        IOBus.getInstance().post(new LoadMyPurchasedDealsEvent(page, used, LocationDetector.getInstance(getActivity()).getLastLocation()));
    }

    @Subscribe
    public void onPurchasesLoaded(OnMyPurchasedDealsLoadedEvent event) {
        if (event.isUsed() != used) return;

        progressLoadView.setVisibility(View.GONE);
        loadedPurchases = event.getPurchasedDeals();
        if (loadedPurchases.getPaging().getCount() == 0)
            emptyView.setVisibility(View.VISIBLE);
        else {
            purchasesListAdapter.setPurchases(loadedPurchases.getPurchasedDeals());
            listPurchasesView.addFooterView(footerPaginationView = new FooterViewWrapper(loadedPurchases.getPaging()).getView());
        }
    }

    @Subscribe
    public void onPurchasesLoadError(OnMyPurchasedDealsErrorEvent errorEvent) {
        if (errorEvent.isUsed() != used) return;

        progressLoadView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), errorEvent.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void viewOnTheMap() {
        mapViewContainer.addView(mapView);
        mapViewContainer.setVisibility(View.VISIBLE);
        switchToMap();
    }

    @Override
    public void viewOnTheList() {
        mapViewContainer.removeView(mapView);
        mapViewContainer.setVisibility(View.GONE);
    }

    private void switchToMap() {
        if (googleMap == null) {
            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    MyPurchasesFragment.this.googleMap = googleMap;
                    initMap();
                }
            });
        } else initMap();
    }

    private void initMap() {
        dealMarkers.clear();
        googleMap.clear();
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int pos = 0; pos < dealMarkers.size(); pos++) {
                    final int listingId = dealMarkers.keyAt(pos);
                    Pair<Marker, PurchasedDeal> deal = dealMarkers.get(listingId);
                    if (marker.equals(deal.first))
                        viewDealDetail(deal.second.getDealOrder());
                }
            }
        });

        LatLngBounds latLngBounds = null;
        if (loadedPurchases != null) {
            for (PurchasedDeal purchasedDeal : loadedPurchases.getPurchasedDeals()) {
                final Double latitude = purchasedDeal.getListing().getLatitude();
                final Double longitude = purchasedDeal.getListing().getLongitude();
                if (latitude != null && longitude != null) {
                    final PurchasedDeal.DealOrder order = purchasedDeal.getDealOrder();
                    final LatLng position = new LatLng(latitude, longitude);
                    if (dealMarkers.indexOfKey(purchasedDeal.getListing().getIdInt()) < 0) {
                        final Marker marker = addMarker(order, position);
                        dealMarkers.put(purchasedDeal.getListing().getIdInt(), new Pair<>(marker, purchasedDeal));
                    } else {
                        calcMarkerWithBetterDiscount(dealMarkers.get(purchasedDeal.getListing().getIdInt()), purchasedDeal, position);
                    }
                    if (latLngBounds == null)
                        latLngBounds = new LatLngBounds(position, position);
                    else latLngBounds = latLngBounds.including(position);
                }
            }
        }

        if (latLngBounds != null) {
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLngBounds.getCenter(), 16);
            googleMap.animateCamera(cameraUpdate);
        }
    }

    private void calcMarkerWithBetterDiscount(Pair<Marker, PurchasedDeal> dealPrev, PurchasedDeal dealNew, LatLng newDealPosition) {
        final PurchasedDeal.DealOrder orderNew = dealNew.getDealOrder();
        if (orderNew.getDiscountValue() > dealPrev.second.getDealOrder().getDiscountValue()) {
            dealPrev.first.remove();
            dealMarkers.remove(dealPrev.second.getListing().getIdInt());

            final Marker marker = addMarker(orderNew, newDealPosition);
            dealMarkers.put(dealNew.getListing().getIdInt(), new Pair<>(marker, dealNew));
        }
    }

    private Marker addMarker(PurchasedDeal.DealOrder order, LatLng position) {
        return googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(order.getTitle())
                .snippet(getResources().getQuantityString(used ? R.plurals.used_codes_format : R.plurals.available_codes_format, order.getQuantityValue(), order.getQuantity()))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_deals_map)));
    }

    private class PurchasesListAdapter extends BaseAdapter {

        final List<PurchasedDeal> purchasesOfTheDeals = new ArrayList<>();

        public void setPurchases(List<PurchasedDeal> purchasedDeals) {
            purchasesOfTheDeals.clear();
            purchasesOfTheDeals.addAll(purchasedDeals);
            notifyDataSetChanged();
        }

        public void destroy() {
            purchasesOfTheDeals.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return purchasesOfTheDeals.size();
        }

        @Override
        public PurchasedDeal getItem(int position) {
            return purchasesOfTheDeals.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PurchasedDeal purchase = getItem(position);
            DealRowVH rowVH;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.deal_purchase_pagination_row, parent, false);
                rowVH = new DealRowVH(convertView);
                convertView.setTag(rowVH);
                convertView.findViewById(R.id.view_coupon_code).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getTag() != null) {
                            PurchasedDeal dealPurchase = (PurchasedDeal) view.getTag();
                            Intent intent = new Intent(getActivity(), CouponCodesActivity.class);
                            intent.putExtra(CouponCodesActivity.ARG_DEAL_PURCHASE, dealPurchase.getDealOrder());
                            intent.putExtra(CouponCodesActivity.ARG_LISTING_TITLE, dealPurchase.getListing().getTitle());
                            intent.putExtra(CouponCodesActivity.ARG_USED, used);
                            startActivity(intent);
                        }
                    }
                });
            } else rowVH = (DealRowVH) convertView.getTag();

            convertView.findViewById(R.id.view_coupon_code).setTag(purchase);
            ((TextView) convertView.findViewById(R.id.available_count)).setText(getString(used ? R.string.used_format : R.string.available_format, purchase.getDealOrder().getQuantity()));
            rowVH.fillDealView(purchase.getListing(), purchase.getDealOrder());
            return convertView;
        }
    }

    class FooterViewWrapper {
        Paging mPaging;

        @InjectView(R.id.load_next_page)
        Button nextButton;

        @InjectView(R.id.load_prev_page)
        Button previousButton;


        FooterViewWrapper(Paging paging) {
            mPaging = paging;
        }

        View getView() {
            View view = getLayoutInflater().inflate(R.layout.listing_pagination_footer, null);
            ButterKnife.inject(this, view);
            int nextVisibility = View.GONE;
            int prevVisibility = View.GONE;
            int buttonCount = 0;
            Button onlyVisibleButton = null;
            if (mPaging.hasNextPage()) {
                buttonCount++;
                nextVisibility = View.VISIBLE;
                onlyVisibleButton = nextButton;
            }
            if (mPaging.hasPreviousPage()) {
                buttonCount++;
                prevVisibility = View.VISIBLE;
                onlyVisibleButton = previousButton;
            }
            nextButton.setVisibility(nextVisibility);
            String limit = String.valueOf(mPaging.getLimit());
            nextButton.setText(getResources().getString(R.string.load_next_listing_page, limit));

            previousButton.setVisibility(prevVisibility);
            previousButton.setText(getResources().getString(R.string.load_prev_listing_page, limit));
            if (onlyVisibleButton != null && buttonCount == 1) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) onlyVisibleButton.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
            }

            return view;
        }

        @OnClick(R.id.load_next_page)
        void onNextClick() {
            loadPurchases(mPaging.getPage() + 1);
        }

        @OnClick(R.id.load_prev_page)
        void onPrevClick() {
            loadPurchases(mPaging.getPage() - 1);
        }
    }
}
