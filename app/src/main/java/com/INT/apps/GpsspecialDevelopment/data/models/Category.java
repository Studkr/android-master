package com.INT.apps.GpsspecialDevelopment.data.models;

/**
 * Created by shrey on 9/4/15.
 */
public class Category implements Model
{
    private String mTitle;
    private int mId;
    private String mLogoPath;
    private int mChildrenCount = 0;

    public Category()
    {

    }
    public Category(String title,int id,String logoPath)
    {
        mId = id;
        mTitle = title;
        mLogoPath = logoPath;
    }
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getLogoPath() {
        return mLogoPath;
    }

    public void setLogoPath(String logoPath) {
        mLogoPath = logoPath;
    }

    public int getChildrenCount() {
        return mChildrenCount;
    }

    public void setChildrenCount(int mChildrenCount) {
        this.mChildrenCount = mChildrenCount;
    }
}
