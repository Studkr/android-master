package com.INT.apps.GpsspecialDevelopment.io.api_service.requests;

import java.util.HashMap;

/**
 * Created by shrey on 10/6/15.
 */
public interface Postable
{
    //can be done via reflection also.
    HashMap<String,String> getFieldValues();

    String getPostModelName();
}
