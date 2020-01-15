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
import android.widget.Toast;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.merchant.MerchantPurchaseCouponsActivity;
import com.INT.apps.GpsspecialDevelopment.activities.merchant.RedeemCouponActivity;
import com.INT.apps.GpsspecialDevelopment.activities.merchant.SyncCouponsEvent;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchaseCoupons;
import com.INT.apps.GpsspecialDevelopment.fragments.BaseFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.merchant.items.CouponVH;
import com.INT.apps.GpsspecialDevelopment.io.IOBus;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.GetMerchantPurchaseCouponsError;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.GetMerchantPurchaseCouponsEvent;
import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.merchant.coupons.GetMerchantPurchaseCouponsResult;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * @author Michael Soyma (Created on 10/9/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class MerchantPurchaseCouponsFragment extends BaseFragment {

    public static final String ARG_DEAL_ID = "ARG_DEAL_ID";
    public static final String ARG_IS_USED = "ARG_IS_USED";

    public static final int RC_COUPON_REDEEM = 8008;

    @InjectView(R.id.loading_problem_layout)
    protected View problemLoadView;
    @InjectView(R.id.emptyView)
    protected View emptyView;
    @InjectView(R.id.progressView)
    protected View progressLoadView;

    @InjectView(android.R.id.list)
    protected ListView listPurchaseCouponsView;

    private PurchaseCouponsListAdapter couponsListAdapter = new PurchaseCouponsListAdapter();
    private int currentPage;
    private View footerView;

    private boolean isUsed;
    private String dealId;

    private MerchantPurchaseCoupons merchantPurchaseCoupons;

    public static MerchantPurchaseCouponsFragment newInstance(String dealId, boolean isUsed) {
        final MerchantPurchaseCouponsFragment fragment = new MerchantPurchaseCouponsFragment();
        final Bundle arguments = new Bundle();
        arguments.putString(ARG_DEAL_ID, dealId);
        arguments.putBoolean(ARG_IS_USED, isUsed);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isUsed = getArguments().getBoolean(ARG_IS_USED);
            dealId = getArguments().getString(ARG_DEAL_ID);
        }
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
        return inflater.inflate(R.layout.fragment_merchant_purchase_coupons, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        listPurchaseCouponsView.setAdapter(couponsListAdapter);
        listPurchaseCouponsView.setFooterDividersEnabled(false);

        loadPurchaseCoupons(1);
    }

    private void loadPurchaseCoupons(final int page) {
        this.currentPage = page;

        couponsListAdapter.destroy();
        listPurchaseCouponsView.removeFooterView(footerView);

        emptyView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.GONE);
        progressLoadView.setVisibility(View.VISIBLE);
        IOBus.getInstance().post(new GetMerchantPurchaseCouponsEvent(dealId, currentPage, isUsed));
    }

    @Subscribe
    public void loadPurchaseCouponsSuccess(GetMerchantPurchaseCouponsResult response) {
        if (response.isUsed() != isUsed) return;

        merchantPurchaseCoupons = response.getMerchantPurchaseCoupons();
        progressLoadView.setVisibility(View.GONE);
        if (merchantPurchaseCoupons.getPaging().getCount() == 0)
            emptyView.setVisibility(View.VISIBLE);
        else {
            couponsListAdapter.setCoupons(merchantPurchaseCoupons.getCoupons());
            listPurchaseCouponsView.addFooterView(footerView = new FooterViewWrapper(merchantPurchaseCoupons.getPaging()).getView());
        }

        if (isUsed)
            ((MerchantPurchaseCouponsActivity) getActivity()).updateUsedCount(merchantPurchaseCoupons.getPaging().getCount());
        else ((MerchantPurchaseCouponsActivity) getActivity()).updateUnusedCount(merchantPurchaseCoupons.getPaging().getCount());
    }

    @Subscribe
    public void loadPurchaseCouponsError(GetMerchantPurchaseCouponsError error) {
        if (error.isUsed() != isUsed) return;

        progressLoadView.setVisibility(View.GONE);
        problemLoadView.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void syncCouponsWhenRedeem(SyncCouponsEvent event) {
        loadPurchaseCoupons(currentPage);
    }

    @OnClick(R.id.retry_connection)
    protected void retry() {
        loadPurchaseCoupons(currentPage);
    }

    @OnItemClick(android.R.id.list)
    protected void couponSelect(final int position) {
        final MerchantPurchaseCoupons.Coupon coupon = merchantPurchaseCoupons.getCoupons().get(position);
        final Intent sendIntent = new Intent(getActivity(), RedeemCouponActivity.class);
        sendIntent.putExtras(RedeemCouponActivity.getIntentExtras(coupon));
        startActivityForResult(sendIntent, RC_COUPON_REDEEM);
    }

    private class PurchaseCouponsListAdapter extends BaseAdapter {

        final List<MerchantPurchaseCoupons.Coupon> coupons = new ArrayList<>();

        public void setCoupons(List<MerchantPurchaseCoupons.Coupon> purchaseCoupons) {
            coupons.clear();
            coupons.addAll(purchaseCoupons);
            notifyDataSetChanged();
        }

        public void destroy() {
            coupons.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return coupons.size();
        }

        @Override
        public MerchantPurchaseCoupons.Coupon getItem(int position) {
            return coupons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MerchantPurchaseCoupons.Coupon coupon = getItem(position);
            CouponVH couponVH;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.merchant_purchase_coupon_pagination_row, parent, false);
                couponVH = new CouponVH(convertView);
                convertView.setTag(couponVH);
            } else couponVH = (CouponVH) convertView.getTag();
            couponVH.inject(coupon);
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
            loadPurchaseCoupons(mPaging.getPage() + 1);
        }

        @OnClick(R.id.load_prev_page)
        void onPrevClick() {
            loadPurchaseCoupons(mPaging.getPage() - 1);
        }
    }
}
