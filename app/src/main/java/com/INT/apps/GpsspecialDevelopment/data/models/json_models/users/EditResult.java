package com.INT.apps.GpsspecialDevelopment.data.models.json_models.users;

import com.google.gson.annotations.SerializedName;

public class EditResult {

    @SerializedName("editResult")
    EditResult_ editResult;

    public EditResult(EditResult_ editResult) {
        this.editResult = editResult;
    }

    public EditResult_ getEditResult() {
        return editResult;
    }

    public static class EditResult_ {

        @SerializedName("User")
        User user;

        public User getUser() {
            return user;
        }
    }

}
