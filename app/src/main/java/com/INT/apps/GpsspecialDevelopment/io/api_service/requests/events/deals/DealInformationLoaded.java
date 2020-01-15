package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.deals;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.deals.DealInformation;

public class DealInformationLoaded
{
    DealInformation mDealInformation;
    public DealInformationLoaded(DealInformation dealInformation)
    {
        mDealInformation = dealInformation;
    }

    public DealInformation getDealInformation() {
        return mDealInformation;
    }
}
