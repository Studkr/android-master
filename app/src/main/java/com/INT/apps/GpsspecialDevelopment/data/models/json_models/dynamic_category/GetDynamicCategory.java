package com.INT.apps.GpsspecialDevelopment.data.models.json_models.dynamic_category;

/**
 * Created by msaqib on 7/14/2016.
 */
public class GetDynamicCategory {
    // "Dynamic"
    private  boolean Status;

    public boolean isStatus() {
        return Status;
    }

    public GetDynamicCategory(boolean status) {

        Status = status;
    }
}
