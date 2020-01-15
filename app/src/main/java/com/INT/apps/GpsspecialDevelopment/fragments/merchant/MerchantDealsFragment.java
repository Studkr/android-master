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
import com.INT.apps.GpsspecialDevelopment.activities.DealInformationActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.Deal;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealsPaging;
import com.INT.apps.GpsspecialDevelopment.fragments.BaseFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.DealRowVH;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantDealsError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantDealsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantDealsResult;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author Michael Soyma (Created on 10/12/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class MerchantDealsFragment extends BaseFragment {

    @InjectView(R.id.loading_problem_layout)
    protected View problemLoadView;
    @InjectView(R.id.emptyView)
    protected View emptyView;
    @InjectView(R.id.progressView)
    protected View progressLoadView;
    @InjectView(android.R.id.list)
    protected ListView listDealsView;

    private ListingListAdapter dealsListAdapter = new ListingListAdapter();
    private int currentPage;
    private View footerView;

    private DealsPaging dealsPaging;

    public static MerchantDealsFragment newInstance() {
        return new MerchantDealsFragment();
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
        return inflater.inflate(R.layout.fragment_merchant_deals, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        listDealsView.setAdapter(dealsListAdapter);
        listDealsView.setFooterDividersEnabled(false);

        loadPurchases(1);
    }

    private void loadPurchases(final int page) {
        this.currentPage = page;

        dealsListAdapter.destroy();
        listDealsView.removeFooterView(footerView);

        emptyView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.GONE);
        progressLoadView.setVisibility(View.VISIBLE);
        IOBus.getInstance().post(new GetMerchantDealsEvent(currentPage));
    }

    @Subscribe
    public void loadPurchasesSuccess(GetMerchantDealsResult response) {
        dealsPaging = response.getDealsPaging();
        progressLoadView.setVisibility(View.GONE);
        if (dealsPaging.getPaging().getCount() == 0)
            emptyView.setVisibility(View.VISIBLE);
        else {
            dealsListAdapter.setDeals(dealsPaging.getDeals());
            listDealsView.addFooterView(footerView = new FooterViewWrapper(dealsPaging.getPaging()).getView());
        }
    }

    @Subscribe
    public void loadPurchasesError(GetMerchantDealsError error) {
        progressLoadView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.retry_connection)
    protected void retry() {
        loadPurchases(currentPage);
    }

    @OnItemClick(android.R.id.list)
    protected void dealsItemClick(final int position) {
        final Deal.Deal_ deal = dealsPaging.getDeals().get(position).getDeal();
        openDealView(deal);
    }

    protected void openDealView(Deal.Deal_ dealInfo) {
        Intent intent = new Intent(getActivity(), DealInformationActivity.class);
        intent.putExtra(DealInformationActivity.ARG_DEAL_ID, dealInfo.getId());
        intent.putExtra(DealInformationActivity.ARG_DEAL_TITLE, dealInfo.getTitle());
        intent.putExtra(DealInformationActivity.ARG_DEAL_IMAGE, dealInfo.getImage());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_left_to_right);
    }

    private class ListingListAdapter extends BaseAdapter {

        final List<Deal> deals = new ArrayList<>();

        public void setDeals(List<Deal> _deals) {
            deals.clear();
            deals.addAll(_deals);
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
        public Deal getItem(int position) {
            return deals.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Deal dealInfo = getItem(position);
            DealRowVH rowVH;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.deal_pagination_row, parent, false);
                rowVH = new DealRowVH(convertView);
                convertView.setTag(rowVH);
            } else rowVH = (DealRowVH) convertView.getTag();

            rowVH.fillDealView(dealInfo.getListing(), dealInfo.getDeal());
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
