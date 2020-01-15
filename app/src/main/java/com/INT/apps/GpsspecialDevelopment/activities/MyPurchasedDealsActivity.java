package com.INT.apps.GpsspecialDevelopment.activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.fragments.deals.IPurchasesRender;
import com.INT.apps.GpsspecialDevelopment.fragments.deals.MyPurchasesFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MyPurchasedDealsActivity extends BaseActivity {

    @InjectView(R.id.pagerTabs)
    ViewPager pagerTabsView; //Active, Used
    @InjectView(R.id.tabs)
    TabLayout tabLayoutView;

    private boolean mapTypeRender; //List, by default
    private MenuItem renderTypeItem;
    private TabsAdapter tabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purchased_deals);
        ButterKnife.inject(this);

        setToolbar(true).setTitle(R.string.title_activity_purchases);
        initMenuAction();

        initTabs();
    }

    private void initMenuAction() {
        getToolbar().inflateMenu(R.menu.menu_deal_purchases);
        renderTypeItem = getToolbar().getMenu().findItem(R.id.action_map);
        syncRenderTypeIcon();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                viewOnTheMap(!mapTypeRender);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void viewOnTheMap(boolean showMap) {
        mapTypeRender = showMap;
        syncRenderTypeIcon();
        for (Fragment page : getSupportFragmentManager().getFragments()) {
            if (page != null && page instanceof IPurchasesRender) {
                IPurchasesRender purchasesRender = (IPurchasesRender) page;
                if (mapTypeRender)
                    purchasesRender.viewOnTheMap();
                else purchasesRender.viewOnTheList();
            }
        }
    }

    private void syncRenderTypeIcon() {
        renderTypeItem.setIcon(mapTypeRender ? R.drawable.ic_action_action_view_list_light : R.drawable.nav_action_maps);
    }

    private void initTabs() {
        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), getResources());
        pagerTabsView.setAdapter(tabsAdapter);
        tabLayoutView.setupWithViewPager(pagerTabsView);
    }

    private final class TabsAdapter extends FragmentStatePagerAdapter {

        private boolean[] fragmentsArgs = new boolean[]{false, true};
        private String[] tabLabels = new String[2];

        public TabsAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            tabLabels[0] = resources.getString(R.string.active);
            tabLabels[1] = resources.getString(R.string.used);
        }

        @Override
        public Fragment getItem(int position) {
            return MyPurchasesFragment.newInstance(fragmentsArgs[position]);
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
