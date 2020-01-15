package com.INT.apps.GpsspecialDevelopment.data.models.json_models.reviews;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shrey on 27/5/15.
 */
public class Review_
{
    @Expose
    String id;
    String title;
    String body;
    String created;
    @SerializedName("vote_count")
    String voteCount;
    @SerializedName("avg_score")
    String avgScore;
    @SerializedName("user_id")
    String userId;
    @SerializedName("guest_id")
    String guestId;
    Poster poster;
    String checkedIn;
    @SerializedName("listing_id")
    String listingId;
    @SerializedName("listing_title")
    String listingTitle;
    @SerializedName("listing_image")
    String listingImage;
    ReviewRating[] ratings;


    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getCreated() {
        return created;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public String getAvgScore() {
        return avgScore;
    }

    public String getUserId() {
        return userId;
    }

    public String getGuestId() {
        return guestId;
    }

    public Poster getPoster() {
        return poster;
    }

    public Integer getCheckedIn()
    {
        Integer checkedIn = 0;
        if(this.checkedIn != null)
        {
            checkedIn = Integer.parseInt(this.checkedIn);
        }
        return checkedIn;
    }

    public String getListingId() {
        return listingId;
    }

    public String getListingImage() {
        return listingImage;
    }

    public String getListingTitle() {
        return listingTitle;
    }

    public ReviewRating[] getRatings() {
        return ratings;
    }

    public class ReviewRating
    {
        public String label;
        public Double rating;
    }
}
