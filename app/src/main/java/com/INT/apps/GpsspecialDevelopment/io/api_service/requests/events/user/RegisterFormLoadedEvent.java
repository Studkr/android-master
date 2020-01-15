package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.field_properties.FieldsProperties;

/**
 * Created by shrey on 15/6/15.
 */
public class RegisterFormLoadedEvent
{
    FieldsProperties mFieldsProperties;
    public RegisterFormLoadedEvent(FieldsProperties fieldsProperties)
    {
        mFieldsProperties = fieldsProperties;
    }

    public FieldsProperties getFieldsProperties() {
        return mFieldsProperties;
    }
}
