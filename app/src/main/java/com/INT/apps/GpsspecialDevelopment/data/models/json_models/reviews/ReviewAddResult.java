package com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shrey on 20/6/15.
 */
public class ReviewAddResult
{
    @SerializedName("reviewResult")
    @Expose
    ReviewResult reviewResult;

    public ReviewResult getReviewResult()
    {
        return reviewResult;
    }

    public static class ReviewResult
    {
        Boolean result = false;
        String message;
        Boolean redirectBack = false;
        @SerializedName("errors")
        List<Error> errors = new ArrayList<>();
        boolean published = true;
        String id;
        public boolean getPublished() {
            return published;
        }

        public String getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }

        public Boolean getRedirectBack() {
            return redirectBack;
        }

        public Boolean getResult() {
            return result;
        }

        public List<Error> getErrors() {
            return errors;
        }
    }

    public static class Error
    {
        String field;
        String error;

        public String getError() {
            return error;
        }

        public String getField() {
            return field;
        }
    }
}
