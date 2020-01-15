package com.INT.apps.GpsspecialDevelopment.fragments.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.DealInformationActivity;
import com.INT.apps.GpsspecialDevelopment.activities.merchant.MerchantPurchaseCouponsActivity;
import com.INT.apps.GpsspecialDevelopment.activities.merchant.SyncCouponsEvent;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchases;
import com.INT.apps.GpsspecialDevelopment.fragments.BaseFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.listings.gallery.DealRowVH;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantPurchasesError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantPurchasesEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.GetMerchantPurchasesResult;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class MerchantPurchasesFragment extends BaseFragment {

    @InjectView(R.id.loading_problem_layout)
    protected View problemLoadView;
    @InjectView(R.id.emptyView)
    protected View emptyView;
    @InjectView(R.id.progressView)
    protected View progressLoadView;

    @InjectView(android.R.id.list)
    protected ListView listPurchasesView;

    private PurchasesListAdapter purchasesListAdapter = new PurchasesListAdapter();
    private int currentPage;
    private View footerView;

    private MerchantPurchases merchantPurchases;

    public static MerchantPurchasesFragment newInstance() {
        return new MerchantPurchasesFragment();
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
        return inflater.inflate(R.layout.fragment_merchant_purchases, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        listPurchasesView.setAdapter(purchasesListAdapter);
        listPurchasesView.setFooterDividersEnabled(false);

        loadPurchases(1);
    }

    private void loadPurchases(final int page) {
        this.currentPage = page;

        purchasesListAdapter.destroy();
        listPurchasesView.removeFooterView(footerView);

        emptyView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.GONE);
        progressLoadView.setVisibility(View.VISIBLE);
        IOBus.getInstance().post(new GetMerchantPurchasesEvent(currentPage));
    }

    @Subscribe
    public void loadPurchasesSuccess(GetMerchantPurchasesResult response) {
        merchantPurchases = response.getMerchantPurchases();
        progressLoadView.setVisibility(View.GONE);
        if (merchantPurchases.getPaging().getCount() == 0)
            emptyView.setVisibility(View.VISIBLE);
        else {
            purchasesListAdapter.setPurchases(merchantPurchases.getPurchases());
            listPurchasesView.addFooterView(footerView = new FooterViewWrapper(merchantPurchases.getPaging()).getView());
        }
    }

    @Subscribe
    public void loadPurchasesError(GetMerchantPurchasesError error) {
        progressLoadView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void syncCouponsWhenRedeem(SyncCouponsEvent event) {
        for (MerchantPurchases.Purchase purchase: merchantPurchases.getPurchases()) {
            if (purchase.getDeal().getId().equals(event.getDealId())) {
                purchase.getDeal().setUnusedCouponsCount(purchase.getDeal().getUnusedCouponsCount() + (event.getCode().isUsed() ? -1 : 0));
                purchase.getDeal().setUsedCouponsCount(purchase.getDeal().getUsedCouponsCount() + (event.getCode().isUsed() ? 1 : 0));
                purchasesListAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @OnClick(R.id.retry_connection)
    protected void retry() {
        loadPurchases(currentPage);
    }

    @OnItemClick(android.R.id.list)
    protected void purchaseItemClick(final int position) {
        final MerchantPurchases.PurchaseDeal deal = merchantPurchases.getPurchases().get(position).getDeal();
        Intent intent = new Intent(getActivity(), DealInformationActivity.class);
        intent.putExtra(DealInformationActivity.ARG_DEAL_ID, deal.getDealId());
        intent.putExtra(DealInformationActivity.ARG_DEAL_TITLE, deal.getTitle());
        intent.putExtra(DealInformationActivity.ARG_DEAL_IMAGE, deal.getImage());
        startActivity(intent);
    }

    private class PurchasesListAdapter extends BaseAdapter {

        private List<MerchantPurchases.Purchase> purchases = new ArrayList<>();

        public void setPurchases(List<MerchantPurchases.Purchase> purchaseDeals) {
            purchases = purchaseDeals != null ? purchaseDeals : new ArrayList<MerchantPurchases.Purchase>();
            notifyDataSetChanged();
        }

        public void destroy() {
            purchases.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return purchases.size();
        }

        @Override
        public MerchantPurchases.Purchase getItem(int position) {
            return purchases.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MerchantPurchases.Purchase purchase = getItem(position);
            DealRowVH rowVH;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.merchant_purchase_pagination_row, parent, false);
                rowVH = new DealRowVH(convertView);
                convertView.setTag(rowVH);
                convertView.findViewById(R.id.unused_tab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getTag() != null) {
                            MerchantPurchases.PurchaseDeal deal = (MerchantPurchases.PurchaseDeal) view.getTag();
                            Intent sendIntent = new Intent(getActivity(), MerchantPurchaseCouponsActivity.class);
                            sendIntent.putExtras(MerchantPurchaseCouponsActivity.getIntentExtras(deal, false));
                            startActivity(sendIntent);
                        }
                    }
                });
                convertView.findViewById(R.id.used_tab).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getTag() != null) {
                            MerchantPurchases.PurchaseDeal deal = (MerchantPurchases.PurchaseDeal) view.getTag();
                            Intent sendIntent = new Intent(getActivity(), MerchantPurchaseCouponsActivity.class);
                            sendIntent.putExtras(MerchantPurchaseCouponsActivity.getIntentExtras(deal, true));
                            startActivity(sendIntent);
                        }
                    }
                });
            } else rowVH = (DealRowVH) convertView.getTag();

            convertView.findViewById(R.id.used_tab).setTag(purchase.getDeal());
            convertView.findViewById(R.id.unused_tab).setTag(purchase.getDeal());
            ((TextView) convertView.findViewById(R.id.used_count)).setText(String.valueOf(purchase.getDeal().getUsedCouponsCount()));
            ((TextView) convertView.findViewById(R.id.unused_count)).setText(String.valueOf(purchase.getDeal().getUnusedCouponsCount()));

            rowVH.fillDealView(purchase.getListing(), purchase.getDeal());
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
