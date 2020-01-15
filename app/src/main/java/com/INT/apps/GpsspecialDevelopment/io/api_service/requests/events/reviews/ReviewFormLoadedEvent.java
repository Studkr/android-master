package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.reviews;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperties;

/**
 * Created by shrey on 18/6/15.
 */
public class ReviewFormLoadedEvent
{
    FieldsProperties mFieldsProperties;
    String mListingId;
    public ReviewFormLoadedEvent(FieldsProperties fieldsProperties,String listingId)
    {
        mFieldsProperties = fieldsProperties;
        mListingId = listingId;
    }

    public String getListingId() {
        return mListingId;
    }

    public FieldsProperties getFieldsProperties() {
        return mFieldsProperties;
    }
}
