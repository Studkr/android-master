package com.INT.apps.GpsspecialDevelopment.data.models.json_models.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_MERCHANT = "ROLE_MERCHANT";

    @Expose
    private String id;
    @Expose
    private String username;
    @Expose
    private String email;
    @Expose
    private String avatar;
    @SerializedName("review_count")
    @Expose
    private String reviewCount;
    @SerializedName("listing_asset_count")
    @Expose
    private String listingAssetCount;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @Expose
    private String[] roles;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     *
     * @param avatar
     * The avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     *
     * @return
     * The reviewCount
     */
    public String getReviewCount() {
        return reviewCount;
    }

    /**
     *
     * @param reviewCount
     * The review_count
     */
    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    /**
     *
     * @return
     * The listingAssetCount
     */
    public String getListingAssetCount() {
        return listingAssetCount;
    }

    /**
     *
     * @param listingAssetCount
     * The listing_asset_count
     */
    public void setListingAssetCount(String listingAssetCount) {
        this.listingAssetCount = listingAssetCount;
    }

    /**
     *
     * @return
     * The displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @param displayName
     * The display_name
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isMerchantGranted() {
        if (roles == null) return false;
        for (String role: roles)
            if (role.equalsIgnoreCase(ROLE_MERCHANT))
                return true;
        return false;
    }
}