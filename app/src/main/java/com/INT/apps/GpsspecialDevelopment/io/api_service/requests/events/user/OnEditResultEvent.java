package com.INT.apps.GpsspecialDevelopment.io.api_service.requests.events.user;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.EditResult;

public class OnEditResultEvent {

    EditResult result;

    public OnEditResultEvent(EditResult result) {
        this.result = result;
    }

    public EditResult getResult() {
        return result;
    }
}
