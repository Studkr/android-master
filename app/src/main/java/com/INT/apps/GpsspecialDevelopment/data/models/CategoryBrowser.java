package com.INT.apps.GpsspecialDevelopment.data.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.INT.apps.GpsspecialDevelopment.data.models.helpers.CvSQLiteHelper;

import java.util.ArrayList;
import java.util.HashMap;

import timber.log.Timber;

/**
 * Created by shrey on 13/4/15.
 */
public class CategoryBrowser {
    private CategoriesHelper mCategoriesHelper;

    private CategoryBrowser(Context context) {
        mCategoriesHelper = CategoriesHelper.getInstance(context);
    }

    private static CategoryBrowser sCategoryBrowser;

    public static CategoryBrowser getInstance(Context context) {
        if (sCategoryBrowser == null) {
            sCategoryBrowser = new CategoryBrowser(context);
        }
        return sCategoryBrowser;
    }

    public ArrayList<Category> getChildrenCategories(Integer parentId) {

        //SQLiteDatabase db = mCategoriesHelper.getReadableDatabase();
        SQLiteDatabase db = mCategoriesHelper.getWritableDatabase();

        String query = "";
        if (parentId == null || parentId == 0) {
            query = "parent_id is null";
        } else {
            query = "parent_id=" + parentId;
        }

        Timber.tag("category").d( "Queriying category table %s",  query);
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + "categories" + "( _id INTEGER," +
                "category_name TEXT ,image_name TEXT ,cv_id INTEGER ,parent_id INTEGER , children_count INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        Cursor cursor = db.query(mCategoriesHelper.getCategoryTable(), mCategoriesHelper.getColumns(), query, null, null, null, null, null);
        ArrayList<Category> categories = cursorToCategories(cursor);
        cursor.close();
        db.close();
        return categories;
    }

    private ArrayList<Category> cursorToCategories(Cursor cursor) {
        ArrayList<Category> categories = new ArrayList<Category>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category category = new Category();
            category.setId(cursor.getInt(0));
            category.setTitle(cursor.getString(1));
            category.setLogoPath(cursor.getString(2));
            category.setChildrenCount(cursor.getInt(5));
            categories.add(category);
            cursor.moveToNext();
        }
        return categories;
    }

    public HashMap<Integer, Category> getCategoryByIds(ArrayList<Integer> categoryIds) {
        if (categoryIds.size() == 0) {
            return new HashMap<Integer, Category>();
        }
        String integerList = TextUtils.join(",", categoryIds.toArray());
        String query = "_id IN (" + integerList + ")";
        SQLiteDatabase db = mCategoriesHelper.getReadableDatabase();
        Cursor cursor = db.query(mCategoriesHelper.getCategoryTable(), mCategoriesHelper.getColumns(), query, null, null, null, null, null);
        ArrayList<Category> categories = cursorToCategories(cursor);
        HashMap<Integer, Category> categoryHashMap = new HashMap<>();
        for (Category category : categories) {
            categoryHashMap.put(category.getId(), category);
        }
        cursor.close();
        db.close();
        return categoryHashMap;
    }
}

class CategoriesHelper extends CvSQLiteHelper {
    private final static String DB_NAME = "categories.sqlite";
    private static CategoriesHelper sCategoriesHelper;

    private CategoriesHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    static protected CategoriesHelper getInstance(Context context) {

        if (sCategoriesHelper == null) {
            sCategoriesHelper = new CategoriesHelper(context);
        }
        return sCategoriesHelper;
    }

    @Override
    protected String getDBName() {
        return DB_NAME;
    }

    public String getCategoryTable() {
        return "categories";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String[] getColumns() {
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
