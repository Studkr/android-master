package com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category;

import retrofit.RetrofitError;

/**
 * Created by msaqib on 7/16/2016.
 */
public class DynamicCategoryErrorModel {

    private RetrofitError error;

    public DynamicCategoryErrorModel(RetrofitError error) {
        this.error = error;
    }

    public RetrofitError getError() {
        return error;
    }
}
