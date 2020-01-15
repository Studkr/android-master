package com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 27/5/15.
 */
public class Review
{
    @SerializedName("Review")
    Review_ Review;

    public Review_ getReview()
    {
        return Review;
    }
}
