package com.INT.apps.GpsspecialDevelopment;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.bonuses.BonusInfo;

public interface BonusInfoCallback {

    void onBonusPointsReceivingError(Throwable t);

    void onBonusPointsReceived(BonusInfo info);
}
