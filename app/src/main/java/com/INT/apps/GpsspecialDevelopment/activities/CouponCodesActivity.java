package com.INT.apps.GpsspecialDevelopment.activities;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.PurchasedDeal;
import com.INT.apps.GpsspecialDevelopment.fragments.deals.CouponShareDialogFragment;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;

/**
 * @author Michael Soyma (Created on 9/26/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class CouponCodesActivity extends BaseActivity {

    public static final String ARG_DEAL_PURCHASE = "ARG_DEAL_PURCHASE";
    public static final String ARG_LISTING_TITLE = "ARG_LISTING_TITLE";
    public static final String ARG_USED = "ARG_USED";

    private PurchasedDeal.DealOrder purchasedDeal;
    private String listingTitle;
    private boolean used;

    @InjectView(android.R.id.list)
    protected ListView couponsListView;

    private void readArgs() {
        purchasedDeal = (PurchasedDeal.DealOrder) getIntent().getSerializableExtra(ARG_DEAL_PURCHASE);
        used = getIntent().getBooleanExtra(ARG_USED, false);
        listingTitle = getIntent().getStringExtra(ARG_LISTING_TITLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readArgs();
        setContentView(R.layout.activity_coupon_codes);
        ButterKnife.inject(this);

        setToolbar(true).setTitle(purchasedDeal.getTitle());

        couponsListView.setAdapter(new CouponsListAdapter(purchasedDeal.getCodes()));
    }

    @OnItemClick(android.R.id.list)
    protected void onCouponSelect(int position) {
        final String coupon = purchasedDeal.getCodes().get(position);
        final String dealTitle = purchasedDeal.getTitle();
        final String finalPrice = String.format("%s%s", purchasedDeal.getCurrencySymbol(), purchasedDeal.getFinalPrice());
        final String regularPrice = String.format("%s%s", purchasedDeal.getCurrencySymbol(), purchasedDeal.getRegularPrice());

        CouponShareDialogFragment.instance(coupon, used, listingTitle, dealTitle, finalPrice, regularPrice)
                .show(getSupportFragmentManager(), CouponCodesActivity.class.getSimpleName());
    }

    private class CouponsListAdapter extends BaseAdapter {

        private final List<String> coupons;

        public CouponsListAdapter(List<String> coupons) {
            this.coupons = coupons;
        }

        @Override
        public int getCount() {
            return coupons.size();
        }

        @Override
        public String getItem(int position) {
            return coupons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String coupon = getItem(position);
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.coupon_row, parent, false);
                if (used) {
                    TextView codeView = (TextView) convertView;
                    codeView.setPaintFlags(codeView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
            ((TextView) convertView).setText(coupon);
            return convertView;
        }
    }
}
