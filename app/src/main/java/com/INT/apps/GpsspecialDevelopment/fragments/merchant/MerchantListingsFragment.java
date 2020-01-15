package com.INT.apps.GpsspecialDevelopment.fragments.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.ListingViewActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.ListingsPaging;
import com.INT.apps.GpsspecialDevelopment.fragments.AlertDialogFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.BaseFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.ListingRowViewHolderDealsMarker;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantListingsError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantListingsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantListingsResult;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author Michael Soyma (Created on 10/11/2017).
 * Company: Thinkmobiles
 * Email:  michael.soyma@thinkmobiles.com
 */
public final class MerchantListingsFragment extends BaseFragment {

    @InjectView(R.id.loading_problem_layout)
    protected View problemLoadView;
    @InjectView(R.id.emptyView)
    protected View emptyView;
    @InjectView(R.id.progressView)
    protected View progressLoadView;
    @InjectView(android.R.id.list)
    protected ListView listListingsView;

    private ListingListAdapter listingsListAdapter = new ListingListAdapter();
    private int currentPage;
    private View footerView;

    private ListingsPaging listingsPaging;

    public static MerchantListingsFragment newInstance() {
        return new MerchantListingsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IOBus.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IOBus.getInstance().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_merchant_listings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        listListingsView.setAdapter(listingsListAdapter);
        listListingsView.setFooterDividersEnabled(false);

        loadPurchases(1);
    }

    private void loadPurchases(final int page) {
        this.currentPage = page;

        listingsListAdapter.destroy();
        listListingsView.removeFooterView(footerView);

        emptyView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.GONE);
        progressLoadView.setVisibility(View.VISIBLE);
        IOBus.getInstance().post(new GetMerchantListingsEvent(currentPage));
    }

    @Subscribe
    public void loadPurchasesSuccess(GetMerchantListingsResult response) {
        listingsPaging = response.getListingsPaging();
        progressLoadView.setVisibility(View.GONE);
        if (listingsPaging.getPaging().getCount() == 0)
            emptyView.setVisibility(View.VISIBLE);
        else {
            listingsListAdapter.setListings(listingsPaging.getListings());
            listListingsView.addFooterView(footerView = new FooterViewWrapper(listingsPaging.getPaging()).getView());
        }
    }

    @Subscribe
    public void loadPurchasesError(GetMerchantListingsError error) {
        progressLoadView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.VISIBLE);
        boolean isBlocked = error.getStatusCode() == 401;
        if (isBlocked) {
            AlertDialogFragment dialog = AlertDialogFragment.getInstance(getString(R.string.error_merchant_was_blocked));
            dialog.setCancelable(false);
            dialog.show(getChildFragmentManager(), "dialog");
        } else {
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.retry_connection)
    protected void retry() {
        loadPurchases(currentPage);
    }

    @OnItemClick(android.R.id.list)
    protected void listingItemClick(final int position) {
        final Listing_ listing = listingsPaging.getListings().get(position);
        openListingView(listing);
    }

    protected void openListingView(Listing_ listing) {
        Intent intent = new Intent(getActivity(), ListingViewActivity.class);
        intent.putExtra(ListingViewActivity.ARG_LISTING_ID, listing.getId());
        intent.putExtra(ListingViewActivity.ARG_LISTING_TITLE, listing.getTitle());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_left_to_right);
    }

    private class ListingListAdapter extends BaseAdapter {

        final List<Listing_> listings = new ArrayList<>();

        public void setListings(List<Listing_> _listings) {
            listings.clear();
            listings.addAll(_listings);
            notifyDataSetChanged();
        }

        public void destroy() {
            listings.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listings.size();
        }

        @Override
        public Listing_ getItem(int position) {
            return listings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Listing_ listing = getItem(position);
            ListingRowViewHolderDealsMarker rowVH;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listing_pagination_row, parent, false);
                rowVH = new ListingRowViewHolderDealsMarker(80, 80);
                convertView.setTag(rowVH);
            } else rowVH = (ListingRowViewHolderDealsMarker) convertView.getTag();

            rowVH.setViewContent(getActivity(), listing, convertView, "TYPE_LIST");
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
            int prevVisiblity = View.GONE;
            int buttonCount = 0;
            Button onlyVisibleButton = null;
            if (mPaging.hasNextPage()) {
                buttonCount++;
                nextVisibility = View.VISIBLE;
                onlyVisibleButton = nextButton;
            }
            if (mPaging.hasPreviousPage()) {
                buttonCount++;
                prevVisiblity = View.VISIBLE;
                onlyVisibleButton = previousButton;
            }
            nextButton.setVisibility(nextVisibility);
            String limit = String.valueOf(mPaging.getLimit());
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
