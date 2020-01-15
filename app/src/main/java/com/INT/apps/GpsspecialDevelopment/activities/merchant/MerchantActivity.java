package com.INT.apps.GpsspecialDevelopment.activities.merchant;

import static android.Manifest.permission.CAMERA;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.activities.BaseActivity;
import com.INT.apps.GpsspecialDevelopment.fragments.DialogActionListener;
import com.INT.apps.GpsspecialDevelopment.fragments.merchant.EmptyFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.merchant.MerchantDealsFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.merchant.MerchantListingsFragment;
import com.INT.apps.GpsspecialDevelopment.fragments.merchant.MerchantPurchasesFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Michael Soyma (Created on 10/2/2017).
 *         Company: Thinkmobiles
 *         Email:  michael.soyma@thinkmobiles.com
 */
public class MerchantActivity extends BaseActivity implements DialogActionListener {

    private static final int PERMISSION_CAMERA = 1;
    @InjectView(R.id.pagerTabs)
    protected ViewPager pagerTabsView; //Purchases, Deals, Listings
    @InjectView(R.id.tabs)
    protected TabLayout tabLayoutView;

    private TabsAdapter tabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        ButterKnife.inject(this);

        setToolbar().setTitle(R.string.merchant_profile);

        initTabs();
    }

    private void initTabs() {
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), getResources());
        pagerTabsView.setOffscreenPageLimit(2);
        pagerTabsView.setAdapter(tabsAdapter);
        tabLayoutView.setupWithViewPager(pagerTabsView);
    }

    @Override
    public void onPositiveButtonPressed() {
     finish();
    }

    @OnClick(R.id.scan_qr_button)
    protected void scanQR() {
        if (checkAndRequestCameraPermission()) {
            startActivity(new Intent(this, MerchantScanQRActivity.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length == 0) {
            return;
        }

        if (requestCode == PERMISSION_CAMERA && grantResults[0] == PERMISSION_GRANTED) {
            scanQR();
        }
    }

    private boolean checkAndRequestCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[] {CAMERA}, PERMISSION_CAMERA);
        return false;
    }

    private final class TabsAdapter extends FragmentStatePagerAdapter {

        private static final int POS_PURCHASES = 0;
        private static final int POS_DEALS = 1;
        private static final int POS_LISTINGS = 2;

        private String[] tabLabels = new String[3];

        public TabsAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            tabLabels[0] = resources.getString(R.string.purchases);
            tabLabels[1] = resources.getString(R.string.deals);
            tabLabels[2] = resources.getString(R.string.listings);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case POS_PURCHASES:
                    return MerchantPurchasesFragment.newInstance();
                case POS_DEALS:
                    return MerchantDealsFragment.newInstance();
                case POS_LISTINGS:
                    return MerchantListingsFragment.newInstance();
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
