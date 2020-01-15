package com.INT.apps.GpsspecialDevelopment.data.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import timber.log.Timber;

/**
 * Created by msaqib on 7/13/2016.
 */
public class DynamicCategoryHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "categories.sqlite";
    private final static String TABLE_NAME = "categories";
    private final static int DB_VERSION = 1;

    public DynamicCategoryHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addNewCategory(ArrayList<DynamicCategoryModel> list) {

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < list.size(); i++) {
            Timber.d("category name: %s", list.get(i).getCategory_name());
            Timber.d("## cate id ->  %d", list.get(i).get_id());
            Timber.d("## cate name ->  %s", list.get(i).getCategory_name());
            //System.out.print("## cate image -> "+list.get(i).getImage_name());
            Timber.d("## cate cv id ->  %d", list.get(i).getCv_id());
            Timber.d("## cate parent id -> %d", list.get(i).getParent_id());
            //System.out.print("## cate child count -> "+list.get(i).getChildren_count());

            DynamicCategoryModel model = new DynamicCategoryModel(
                    list.get(i).get_id(),
                    list.get(i).getCategory_name(),
                    list.get(i).getImage_name(),
                    list.get(i).getCv_id(),
                    list.get(i).getParent_id(),
                    list.get(i).getChildren_count());

            ContentValues values = new ContentValues();
            values.put("_id", model.get_id());
            values.put("category_name", model.getCategory_name());
            values.put("image_name", model.getImage_name());
            values.put("cv_id", model.getCv_id());
            values.put("parent_id", model.getParent_id());
            values.put("children_count", model.getChildren_count());

           long status = db.insert(TABLE_NAME, null, values);

        }
        db.close();
    }

    public void addNewCategoryViaModel(DynamicCategoryModel model) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_id", model.get_id());
        values.put("category_name", model.getCategory_name());
        values.put("image_name", model.getImage_name());
        values.put("cv_id", model.getCv_id());
        values.put("parent_id", model.getParent_id());
        values.put("children_count", model.getChildren_count());

        long status = db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public int getCategoryCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public void removeOldCategory() {
        Timber.i("remove old categories");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }
}
