package com.INT.apps.GpsspecialDevelopment.data.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.INT.apps.GpsspecialDevelopment.data.models.helpers.CvSQLiteHelper;

/**
 * Created by shrey on 22/7/15.
 */
public class RecentlyViewedListings
{

}

class RecentlyViewedListingsHelper extends CvSQLiteHelper
{
    private final static String DB_NAME = "categories.sqlite";
    private static RecentlyViewedListingsHelper sHelper;
    private RecentlyViewedListingsHelper(Context context)
    {
        super(context,DB_NAME,null,1);
    }

    static protected RecentlyViewedListingsHelper getInstance(Context context)
    {
        if(sHelper == null)
        {
            sHelper = new RecentlyViewedListingsHelper(context);
        }
        return sHelper;
    }
    @Override
    protected String getDBName()
    {
        return DB_NAME;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public String[] getColumns()
    {
        String a[] = {
                "_id",
                "category_name",
                "image_name",
                "cv_id",
                "parent_id",
                "children_count"
        };
        return a;
    }
}