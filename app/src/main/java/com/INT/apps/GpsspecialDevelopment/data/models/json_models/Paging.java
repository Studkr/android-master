package com.INT.apps.GpsspecialDevelopment.data.models.json_models;

import java.io.Serializable;

/**
 * Created by shrey on 30/4/15.
 */
public class Paging implements Serializable {
    public int page;
    public int count;
    public int limit;
    public int pageCount;

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    public int getLimit() {
        return limit;
    }

    public int getPageCount() {
        return pageCount;
    }

    public boolean hasNextPage() {
        return getPage() < getPageCount();
    }

    public boolean hasPreviousPage() {
        return getPage() > 1;
    }
}
