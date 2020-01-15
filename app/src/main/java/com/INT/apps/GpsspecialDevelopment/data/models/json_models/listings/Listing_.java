package com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.data.models.json_models.Paging;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.listings.assets.FieldInformation;
import com.INT.apps.GpsspecialDevelopment.utils.CvUrls;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shrey on 24/4/15.
 */
public class Listing_ implements Serializable {
    @Expose
    @SerializedName("id")
    public String id;
    @Expose
    public String title;
    String description;
    String image;
    String created;
    @SerializedName("review_count")
    Integer reviewCount;
    @SerializedName("lat")
    String latitude;
    @SerializedName("lng")
    String longitude;
    @SerializedName("category_list")
    String[] categoryList;
    @SerializedName("category_names")
    String[] categoryNames;
    @SerializedName("address_data")
    String addressData;

    @SerializedName("address")
    String address;
    @SerializedName("city")
    String city;
    @SerializedName("zipcode")
    String zipcode;

    @SerializedName("is_featured")
    String isFeatured;

    String distance;

    String userAvg;

    HashMap<Integer, String> mCategoryTitles;

    @SerializedName("bookMarked")
    Boolean bookMarked = false;

    @SerializedName("deals_info")
    DealInfoObj dealInfo = new DealInfoObj();

    @SerializedName("viewUrl")
    String viewUrl;

    boolean checkedIn = false;

    private FieldInformation[] fieldInformation;

    public String getId() {
        return id;
    }

    public int getIdInt() {
        return Integer.parseInt(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getReviewCount() {
        if (reviewCount == null) {
            reviewCount = 0;
        }
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = Integer.parseInt(reviewCount);
    }

    public Double getLatitude() {
        return TextUtils.isEmpty(latitude) ? null : Double.parseDouble(latitude);
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return TextUtils.isEmpty(longitude) ? null : Double.parseDouble(longitude);
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer[] getCategoryList() {
        ArrayList<Integer> ids = new ArrayList<>();
        for (int i = 0; i < categoryList.length; i++) {
            ids.add(Integer.parseInt(categoryList[i]));
        }
        return (Integer[]) ids.toArray(new Integer[ids.size()]);
    }

    public void setCategoryList(String[] categoryList) {
        this.categoryList = categoryList;
    }

    public String getAddressData() {
        if (!TextUtils.isEmpty(addressData))
            return addressData;
        else {
            final List<String> manualAddress = new ArrayList<>();
            if (!TextUtils.isEmpty(address)) manualAddress.add(address);
            if (!TextUtils.isEmpty(city)) manualAddress.add(city);
            if (!TextUtils.isEmpty(zipcode)) manualAddress.add(zipcode);
            return TextUtils.join(", ", manualAddress);
        }
    }

    public void setAddressData(String addressData) {
        this.addressData = addressData;
    }

    public void setCategoryTitles(HashMap<Integer, String> categoryTitles) {
        mCategoryTitles = categoryTitles;
    }

    @SuppressLint("UseSparseArrays")
    public HashMap<Integer, String> getCategoryTitles() {
        if (categoryNames != null && categoryList != null && categoryList.length > 0 && mCategoryTitles == null) {
            mCategoryTitles = new HashMap<>();
            for (int i = 0; i < categoryList.length; i++)
                mCategoryTitles.put(Integer.parseInt(categoryList[i]), categoryNames[i]);
        } else mCategoryTitles = new HashMap<>();
        return mCategoryTitles;
    }

    public String getImageUrlForSize(Integer height, Integer width, boolean crop) {
        return getImageUrlForSize(getImage(), height, width, crop);
    }

    public static String getImageUrlForSize(String image, Integer height, Integer width, boolean crop) {
        return CvUrls.getImageUrlForSize(image, height, width, crop);
    }

    public String getImageUrlForSize(Integer height, Integer width) {
        return getImageUrlForSize(height, width, false);
    }

    public String getUserAvg() {
        return userAvg;
    }

    public String getDistance() {
        return distance;
    }

    public FieldInformation[] getFieldInformation() {
        return fieldInformation;
    }

    public String getPhoneValue() {
        String phoneValue = null;
        if (getFieldInformation() != null) {
            for (FieldInformation fieldInformation : getFieldInformation()) {
                if (fieldInformation.getType().equals("phone") || fieldInformation.getField().equals("phone") || fieldInformation.getField().equals("phone_number")) {
                    phoneValue = fieldInformation.getDisplayValue();
                    if (phoneValue != null && phoneValue.length() > 0) {
                        break;
                    }
                }
            }
        }
        return phoneValue;
    }

    public boolean isFeatured() {
        return isFeatured != null && isFeatured.equals("1");
    }

    public void setBookMarked(Boolean bookMarked) {
        this.bookMarked = bookMarked;
    }

    public Boolean isBookMarked() {
        return bookMarked;
    }

    public List<DealInfo> getDeals() {
        return dealInfo.getDeals();
    }

    public Paging getDealsPaging() {
        return dealInfo.getPaging();
    }

    public boolean hasDeals() {
        return dealInfo.getPaging().getCount() > 0;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public boolean getCheckedIn() {
        return checkedIn;
    }
}
