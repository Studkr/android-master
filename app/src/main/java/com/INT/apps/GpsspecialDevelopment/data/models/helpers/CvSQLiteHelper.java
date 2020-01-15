package com.INT.apps.GpsspecialDevelopment.data.models.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by shrey on 9/4/15.
 */
public abstract class CvSQLiteHelper extends SQLiteOpenHelper
{
    public CvSQLiteHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context,name,factory,version);
    }
    abstract protected String getDBName();
}
