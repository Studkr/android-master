package com.INT.apps.GpsspecialDevelopment.activities.merchant;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.BaseActivity;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.merchant.MerchantPurchases;
import com.INT.apps.GpsspecialDevelopment.fragments.merchant.EmptyFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.merchant.MerchantPurchaseCouponsFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Michael Soyma (Created on 10/4/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public final class MerchantPurchaseCouponsActivity extends BaseActivity {

    public static final String ARG_PURCHASE_TITLE = "ARG_PURCHASE_TITLE";
    public static final String ARG_PURCHASE_DEAL_ID = "ARG_PURCHASE_DEAL_ID";
    public static final String ARG_UNUSED_COUNT = "ARG_UNUSED_COUNT";
    public static final String ARG_USED_COUNT = "ARG_USED_COUNT";
    public static final String ARG_SEL_USED_TAB = "ARG_SEL_USED_TAB";

    @InjectView(R.id.pagerTabs)
    protected ViewPager pagerTabsView; //Used, Unused
    @InjectView(R.id.tabs)
    protected TabLayout tabLayoutView;

    private TabsAdapter tabsAdapter;

    private int defaultSelectedTab = 0;
    private int usedCount, unusedCount;
    private String purchaseTitle, purchaseDealId;

    public static Bundle getIntentExtras(final MerchantPurchases.PurchaseDeal purchaseDeal, boolean selectedUsed) {
        final Bundle bundle = new Bundle();
        bundle.putString(ARG_PURCHASE_TITLE, purchaseDeal.getTitle());
        bundle.putString(ARG_PURCHASE_DEAL_ID, purchaseDeal.getDealId());
        bundle.putInt(ARG_UNUSED_COUNT, purchaseDeal.getUnusedCouponsCount());
        bundle.putInt(ARG_USED_COUNT, purchaseDeal.getUsedCouponsCount());
        bundle.putBoolean(ARG_SEL_USED_TAB, selectedUsed);
        return bundle;
    }

    private void readArgs() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usedCount = extras.getInt(ARG_USED_COUNT, 0);
            unusedCount = extras.getInt(ARG_UNUSED_COUNT, 0);
            purchaseTitle = extras.getString(ARG_PURCHASE_TITLE);
            purchaseDealId = extras.getString(ARG_PURCHASE_DEAL_ID);
            defaultSelectedTab = extras.getBoolean(ARG_SEL_USED_TAB, false) ? 1 : 0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readArgs();
        setContentView(R.layout.activity_merchant_purchase_coupons);
        ButterKnife.inject(this);

        setToolbar(true).setTitle(getString(R.string.format_purchases, purchaseTitle));

        initTabs();
    }

    private void initTabs() {
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), getResources());
        pagerTabsView.setAdapter(tabsAdapter);
        pagerTabsView.setCurrentItem(defaultSelectedTab);
        tabLayoutView.setupWithViewPager(pagerTabsView);
    }

    public void updateUsedCount(final int usedCount) {
        this.usedCount = usedCount;
        tabsAdapter.syncCounter(getResources());
        tabsAdapter.notifyDataSetChanged();
    }

    public void updateUnusedCount(final int unusedCount) {
        this.unusedCount = unusedCount;
        tabsAdapter.syncCounter(getResources());
        tabsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.scan_qr_button)
    protected void scanQR() {
        startActivity(new Intent(this, MerchantScanQRActivity.class));
    }

    private final class TabsAdapter extends FragmentStatePagerAdapter {

        private static final int POS_UNUSED = 0;
        private static final int POS_USED = 1;

        private String[] tabLabels = new String[2];

        public TabsAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            syncCounter(resources);
        }

        public void syncCounter(Resources resources) {
            tabLabels[0] = resources.getString(R.string.unused_format, String.valueOf(unusedCount));
            tabLabels[1] = resources.getString(R.string.used_format, String.valueOf(usedCount));
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POS_UNUSED:
                    return MerchantPurchaseCouponsFragment.newInstance(purchaseDealId, false);
                case POS_USED:
                    return MerchantPurchaseCouponsFragment.newInstance(purchaseDealId, true);
                default:
                    return EmptyFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return tabLabels.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabLabels[position];
        }
    }
}
