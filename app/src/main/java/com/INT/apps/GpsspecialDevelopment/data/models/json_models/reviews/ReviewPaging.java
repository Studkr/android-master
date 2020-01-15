package com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shrey on 27/5/15.
 */
public class ReviewPaging
{
    @SerializedName("reviews")
    public List<Review> reviews;
    @SerializedName("paging")
    public Paging paging;

    @SerializedName("review")
    public Review review;

    public List<Review> getReviews() {
        return reviews;
    }

    public Paging getPaging() {
        return paging;
    }

    public Review getSingleReview()
    {
        return review;
    }
}
