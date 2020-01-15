package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfo;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.DealInfoObj;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.Listing_;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.DealRowVH;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadListOfDeals;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadListOfDealsError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals.LoadListOfDealsResponse;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author Michael Soyma (Created on 9/20/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class ListDealsPaginationActivity extends BaseActivity {

    public static final String ARG_LISTING = "ARG_LISTING";

    private Listing_ currentListing;
    private int page = 1;

    private DealInfoObj dealInfo;

    @InjectView(android.R.id.list)
    protected ListView listDealsView;
    @InjectView(R.id.deals_no_found)
    protected View dealsNotFoundView;
    @InjectView(R.id.loader_animation)
    protected View loaderView;
    @InjectView(R.id.loading_problem_layout)
    protected View loadingErrorView;

    private DealsListAdapter dealsListAdapter = new DealsListAdapter();
    private View footerView;

    private void readArgs() {
        currentListing = (Listing_) getIntent().getSerializableExtra(ARG_LISTING);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readArgs();
        setContentView(R.layout.activity_deals_pagination);
        ButterKnife.inject(this);

        setToolbar(true);
        getToolbar().setTitle(currentListing.getTitle());

        listDealsView.setAdapter(dealsListAdapter);
        listDealsView.setFooterDividersEnabled(false);

        loadDeals(page);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IOBus.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        IOBus.getInstance().unregister(this);
    }

    @OnClick(R.id.retry_connection)
    protected void retry() {
        loadDeals(page);
    }

    @OnItemClick(android.R.id.list)
    protected void onDealClick(int position) {
        final DealInfo dealInfoItem = dealInfo.getDeals().get(position);
        Intent intent = new Intent(this, DealInformationActivity.class);
        intent.putExtra(DealInformationActivity.ARG_DEAL_ID, dealInfoItem.getId());
        intent.putExtra(DealInformationActivity.ARG_DEAL_TITLE, dealInfoItem.getTitle());
        intent.putExtra(DealInformationActivity.ARG_DEAL_IMAGE, dealInfoItem.getImage());
        startActivity(intent);
    }

    @Subscribe
    public void loadDealsSuccess(LoadListOfDealsResponse response) {
        dealInfo = response.getDealInfo();
        loaderView.setVisibility(View.GONE);
        if (dealInfo.getPaging().getCount() == 0)
            dealsNotFoundView.setVisibility(View.VISIBLE);
        else {
            dealsListAdapter.setDeals(dealInfo.getDeals());
            listDealsView.addFooterView(footerView = new FooterViewWrapper(dealInfo.getPaging()).getView());
        }
    }

    @Subscribe
    public void loadDealsError(LoadListOfDealsError error) {
        loaderView.setVisibility(View.GONE);
        loadingErrorView.setVisibility(View.VISIBLE);
        Toast.makeText(this, error.getMsg(), Toast.LENGTH_SHORT).show();
    }

    private void loadDeals(final int page) {
        this.page = page;
        dealsListAdapter.destroy();
        listDealsView.removeFooterView(footerView);

        dealsNotFoundView.setVisibility(View.GONE);
        loadingErrorView.setVisibility(View.GONE);
        loaderView.setVisibility(View.VISIBLE);
        IOBus.getInstance().post(new LoadListOfDeals(currentListing.getId(), page));
    }

    private class DealsListAdapter extends BaseAdapter {

        final List<DealInfo> deals = new ArrayList<>();

        public void setDeals(List<DealInfo> dealInfos) {
            deals.clear();
            deals.addAll(dealInfos);
            notifyDataSetChanged();
        }

        public void destroy() {
            deals.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return deals.size();
        }

        @Override
        public DealInfo getItem(int position) {
            return deals.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DealInfo deal = getItem(position);
            DealRowVH rowVH;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listing_deal_pagination_row, parent, false);
                rowVH = new DealRowVH(convertView);
                convertView.setTag(rowVH);
            } else rowVH = (DealRowVH) convertView.getTag();

            rowVH.fillDealView(currentListing, deal);
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
            loadDeals(mPaging.getPage() + 1);
        }

        @OnClick(R.id.load_prev_page)
        void onPrevClick() {
            loadDeals(mPaging.getPage() - 1);
        }
    }
}
