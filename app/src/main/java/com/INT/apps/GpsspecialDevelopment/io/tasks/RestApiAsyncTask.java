package com.INT.apps.GpsspecialDevelopment.io.tasks;

import com.INT.apps.GpsspecialDevelopment.io.api_service.requests.ApiRequest;

/**
 * Created by shrey on 22/4/15.
 */
public class RestApiAsyncTask extends AsyncFragmentTask
{
    private ApiRequest mApiRequest;
    public RestApiAsyncTask()
    {

    }
    public RestApiAsyncTask(ApiRequest apiRequest)
    {
        mApiRequest = apiRequest;
    }
    @Override
    protected Void doInBackground(Void... params)
    {
        //mApiRequest.makeRequest();
        return null;
    }
}
