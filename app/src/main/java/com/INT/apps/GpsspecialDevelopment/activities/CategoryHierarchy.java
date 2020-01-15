package com.INT.apps.GpsspecialDevelopment.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.INT.apps.GpsspecialDevelopment.R;
import com.INT.apps.GpsspecialDevelopment.data.models.Category;
import com.INT.apps.GpsspecialDevelopment.fragments.categories.CategoryHiearchyBrowserFragment;
import com.INT.apps.GpsspecialDevelopment.utils.ListingPaginationQueryBuilder;

public class CategoryHierarchy extends BaseActivity implements CategoryHiearchyBrowserFragment.OnCategoryFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_hierarchy);
        Toolbar toolbar = setToolbar(true);
        toolbar.setTitle(getResources().getString(R.string.all_categories));
        Integer browseCategoryId;
        browseCategoryId = 0;
        if (savedInstanceState != null) {
            FragmentManager fm = getFragmentManager();
        } else {
            setFragmentForCategory(browseCategoryId);
        }

    }

    private void setFragmentForCategory(Integer parentId) {
        Fragment fragment = getFragmentManager().findFragmentByTag("category_" + parentId);
        if (fragment == null) {
            fragment = CategoryHiearchyBrowserFragment.newInstance(parentId);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.categories_browser, fragment, "category_" + parentId);
        ft.addToBackStack("categorystack_" + parentId);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() <= 1)
            finish();
        else super.onBackPressed();
    }

    @Override
    public void onCategoryFragmentInteraction(Category category) {
        if (category.getChildrenCount() > 0) {
            setFragmentForCategory(category.getId());
        } else {
            ListingPaginationQueryBuilder queryBuilder = ListingPaginationQueryBuilder.newInstance();
            queryBuilder.setSearchListingCategory(category.getId(), category.getTitle());
            Intent intent = new Intent(this, ListingIndexActivity.class);
            intent.putExtra(ListingIndexActivity.SEARCH_QUERY, queryBuilder.build());
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
