package com.INT.apps.GpsspecialDevelopment.utils;

import android.content.res.Resources;
import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.R;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by shrey on 30/4/15.
 */
public class ListingPaginationQueryBuilder {
    private HashMap<String, String> parameters = new HashMap<>();

    static public ListingPaginationQueryBuilder newInstance() {
        return new ListingPaginationQueryBuilder();
    }

    private ListingPaginationQueryBuilder() {
    }

    static public ListingPaginationQueryBuilder newInstance(HashMap<String, String> queryParameters) {
        ListingPaginationQueryBuilder builder = newInstance();
        if (queryParameters != null) {
            for (String key : queryParameters.keySet())
                builder.addParameter(key, queryParameters.get(key));
        }
        //By default
        builder.setDistance(builder.getDistance()).setSorting(builder.getSorting());
        return builder;
    }

    protected void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public ListingPaginationQueryBuilder setPageNumber(Integer pageNumber) {
        addParameter("page", pageNumber.toString());
        return this;
    }

    public ListingPaginationQueryBuilder setSearchKeyword(String keyword) {
        addParameter("keyword", keyword);
        return this;
    }

    public ListingPaginationQueryBuilder setSearchListingCategory(int categoryId, String categoryLabel) {
        addParameter("category[]", String.valueOf(categoryId));
        addParameter("categoryLabel", categoryLabel);
        return this;
    }

    public ListingPaginationQueryBuilder removeSearchListingCategory() {
        parameters.remove("category[]");
        parameters.remove("categoryLabel");
        return this;
    }

    public ListingPaginationQueryBuilder enableSearchByCoordinates() {
        parameters.remove("location");
        return this;
    }

    public ListingPaginationQueryBuilder setSearchLocationKeyword(String location) {
        addParameter("location", location);
        return this;
    }

    public ListingPaginationQueryBuilder setMethod(String type) {
        addParameter("method", type);
        return this;
    }

    public ListingPaginationQueryBuilder setNamedParameter(String key, String name) {
        addParameter(key, name);
        return this;
    }

    public HashMap<String, String> build() {
        return parameters;
    }

    public HashMap<String, String> buildForApi() {
        final HashMap<String, String> queryMap = new HashMap<>(parameters);
        queryMap.remove("categoryLabel");
        return queryMap;
    }

    public String getSearchListingCategory() {
        if (parameters.containsKey("categoryLabel"))
            return parameters.get("categoryLabel");
        else return "";
    }

    public String getSearchKeyword() {
        return getParameter("keyword");
    }

    public String getLocationSearchKeyword() {
        return getParameter("location");
    }

    public String getParameter(String key) {
        String val = parameters.get(key);
        if (val == null) {
            val = "";
        }
        return val;
    }

    public HashMap<String, String> getSortingOptions(Resources res) {
        String name = res.getString(R.string.sort_options_name);
        String distance = res.getString(R.string.sort_options_distance);
        String review = res.getString(R.string.sort_options_review);
        String rating = res.getString(R.string.sort_options_rating);

        HashMap<String, String> options = new LinkedHashMap<>();
        options.put("name", name);
        options.put("distance", distance);
        options.put("review", review);
        options.put("rating", rating);
        return options;
    }

    public HashMap<String, String> getDistanceOptions(Resources res) {
        String distance5 = res.getString(R.string.distance_options_5);
        String distance10 = res.getString(R.string.distance_options_10);
        String distance15 = res.getString(R.string.distance_options_15);

        HashMap<String, String> options = new LinkedHashMap<>();
        options.put("5mi", distance5);
        options.put("10mi", distance10);
        options.put("15mi", distance15);
        return options;
    }

    public String getSorting() {
        if (!TextUtils.isEmpty(getParameter("sorting[name]")))
            return "name";
        else if (!TextUtils.isEmpty(getParameter("sorting[distance]")))
            return "distance";
        else if (!TextUtils.isEmpty(getParameter("sorting[review]")))
            return "review";
        else if (!TextUtils.isEmpty(getParameter("sorting[rating]")))
            return "rating";
        return "distance";
    }

    public ListingPaginationQueryBuilder setSorting(String sorting) {
        parameters.remove("sorting[name]");
        parameters.remove("sorting[distance]");
        parameters.remove("sorting[review]");
        parameters.remove("sorting[rating]");
        addParameter(String.format("sorting[%s]", sorting), "ASC");
        return this;
    }

    public String getDistance() {
        String distance = getParameter("distance");
        if (distance == null || distance.length() == 0) {
            distance = "5mi";
        }
        return distance;
    }

    public ListingPaginationQueryBuilder setDistance(String distance) {
        addParameter("distance", distance);
        return this;
    }
}
