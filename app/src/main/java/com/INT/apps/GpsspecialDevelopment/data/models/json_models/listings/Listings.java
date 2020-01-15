package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.data.models.Category;
import com.INT.apps.GpsspecialDevelopment.data.models.CategoryBrowser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by shrey on 24/4/15.
 */
public class Listings {
    public List<Listing> listings;

    public List<Listing> getListings() {
        return listings;
    }

    private boolean categoriesSet = false;

    public void setListings(List<Listing> listings) {
        categoriesSet = false;
        this.listings = listings;
    }

    public void setListingCategories() {
        if (categoriesSet)
            return;
        categoriesSet = true;
        HashSet<Integer> categories = new HashSet<>();
        for (Listing listing : this.listings) {
            categories.addAll(Arrays.asList(listing.getListing().getCategoryList()));
        }
        HashMap<Integer, Category> categoriesList = CategoryBrowser.getInstance(CrowdvoxApplication.getAppInstance())
                .getCategoryByIds(new ArrayList<>(categories));
        for (Listing listing : this.listings) {
            HashMap<Integer, String> stringCategoryList = new HashMap<Integer, String>();
            for (Integer categoryId : listing.getListing().getCategoryList()) {
                if (categoriesList.size() > 0) {
                    stringCategoryList.put(categoryId, categoriesList.get(categoryId).getTitle());
                }
            }
            listing.getListing().setCategoryTitles(stringCategoryList);
        }
    }

    static public void setListingCategories(List<Listing_> listings) {
        HashSet<Integer> categories = new HashSet<>();
        for (Listing_ listing : listings) {
            categories.addAll(Arrays.asList(listing.getCategoryList()));
        }
        HashMap<Integer, Category> categoriesList = CategoryBrowser.getInstance(CrowdvoxApplication.getAppInstance())
                .getCategoryByIds(new ArrayList<>(categories));
        for (Listing_ listing : listings) {
            HashMap<Integer, String> stringCategoryList = new HashMap<Integer, String>();
            for (Integer categoryId : listing.getCategoryList()) {
                stringCategoryList.put(categoryId, categoriesList.get(categoryId).getTitle());
            }
            listing.setCategoryTitles(stringCategoryList);
        }
    }
}

