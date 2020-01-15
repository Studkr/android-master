package com.INT.apps.GpsspecialDevelopment.data.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.INT.apps.GpsspecialDevelopment.data.models.helpers.CvSQLiteHelper;
import com.INT.apps.GpsspecialDevelopment.data.models.json_models.users.LoginResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by shrey on 9/4/15.
 */
public class JsonCacheManager
{
    private static  JsonCacheManager sCacheManager;
    private JsonCacheManagerSqliteHelper sqliteHelper;
    private JsonCacheManager(Context context)
    {
        sqliteHelper = JsonCacheManagerSqliteHelper.getInstance(context);
    }

    public String cacheObject(String entity,String entityId,Object jsonObject)
    {
        Gson gsonInstance = new GsonBuilder().
                disableHtmlEscaping().
                serializeNulls().create();
        String json = gsonInstance.toJson(jsonObject);
        ContentValues values = new ContentValues();
        values.put("entity",entity);
        values.put("entity_id",entityId);
        values.put("json",json);
        String existingId = getExistingCacheKey(entity,entityId);
        Long id=null;
        SQLiteDatabase dbHandle = sqliteHelper.getWritableDatabase();
        if(existingId != null)
        {
            id = Long.parseLong(existingId);
            dbHandle.update("json_cache_manager",values,"id=?",new String[]{existingId});

        }else
        {
           id = dbHandle.insert("json_cache_manager", null, values);
        }
        dbHandle.close();
        return id.toString();
    }

    private String getExistingCacheKey(String entity,String entityId)
    {
        SQLiteDatabase dbHandle = sqliteHelper.getReadableDatabase();
        Cursor result = dbHandle.query(true, "json_cache_manager", null, "entity=? and entity_id=?",
                new String[]{entity, entityId}, null, null, null, null, null);
        String id = null;

        if(result.getCount() > 0)
        {
            result.moveToFirst();
            id = result.getString(0);
        }
        result.close();
        dbHandle.close();
        return id;
    }
    public Object getCachedObject(String entity,String entityId,Class generatorClass)
    {
        SQLiteDatabase dbHandle = sqliteHelper.getReadableDatabase();
        Cursor result = dbHandle.query(true, "json_cache_manager", null, "entity=? and entity_id=?",
            new String[]{entity, entityId}, null, null, null, null, null);
        Object o = null;
        if(result.getCount() > 0)
        {
            result.moveToFirst();
            String json = result.getString(3);
            Gson gsonInstance = new GsonBuilder().
                    disableHtmlEscaping().
                    serializeNulls().create();
            if(json != null)
            {
                o = gsonInstance.fromJson(json, generatorClass);
            }
        }
        result.close();
        dbHandle.close();
        return o;
    }

    public void deleteCaching(String entity,String entityId)
    {
        SQLiteDatabase dbHandle = sqliteHelper.getWritableDatabase();
        if (entityId != null) {
            dbHandle.delete("json_cache_manager", "entity=? and entity_id=?", new String[]{entity, entityId});
        } else {
            dbHandle.delete("json_cache_manager","entity=?", new String[]{entity});
        }
    }

    public Object getCacheObjectByKey(String key,Class generatorClass)
    {
        SQLiteDatabase dbHandle = sqliteHelper.getReadableDatabase();
        Cursor result = dbHandle.query(true, "json_cache_manager", null, "id=?",
                new String[]{key}, null, null, null, null, null);
        Object cacheObject = null;
        if(result.getCount() > 0)
        {
            result.moveToFirst();
            String entityId = result.getString(0);
            String entity = result.getString(1);
            cacheObject = getCachedObject(entity,entityId,generatorClass);
        }
        result.close();
        dbHandle.close();
        return cacheObject;
    }

    public void setUserSession(LoginResult.LoginResult_ authToken)
    {
        cacheObject("UserSession","1",authToken);
    }

    public void removeUserSession()
    {
        deleteCaching("UserSession","1");
    }

    public LoginResult.LoginResult_ getUserSession()
    {
        LoginResult.LoginResult_ cachResult = ((LoginResult.LoginResult_)getCachedObject("UserSession","1",LoginResult.LoginResult_.class));
        if(cachResult != null && cachResult.getUserAuthToken().isExpired())
        {
            deleteCaching("UserSession","1");
            cachResult = null;
        }
        return cachResult;
    }

    public static JsonCacheManager getInstance(Context context)
    {
        if(sCacheManager == null)
        {
            sCacheManager = new JsonCacheManager(context);
        }
        return sCacheManager;
    }

    public void purgeOld()
    {
        String query = "delete from json_cache_manager where id in ( SELECT id FROM json_cache_manager where entity != \"UserSession\" and entity != \"RegisterForm\" and entity != \"ReviewForm\" limit 400,1000)";
        SQLiteDatabase dbHandle = sqliteHelper.getReadableDatabase();
        dbHandle.execSQL(query);
    }
}

class JsonCacheManagerSqliteHelper extends CvSQLiteHelper
{
    private final static String DB_NAME = "json_cache_manager.sqlite";

    private static JsonCacheManagerSqliteHelper sHelper;
    private JsonCacheManagerSqliteHelper(Context context)
    {
        super(context,DB_NAME,null,1);
    }

    static protected JsonCacheManagerSqliteHelper getInstance(Context context)
    {
        if(sHelper == null)
        {
            sHelper = new JsonCacheManagerSqliteHelper(context);
        }
        return sHelper;
    }
    @Override
    protected String getDBName()
    {
        return DB_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE  TABLE  \"json_cache_manager\" (\"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , \"entity\" TEXT, \"entity_id\" VARCHAR, \"json\" BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String[] getColumns()
    {
        String a[] = {
            "id",
            "entity",
            "entity_id",
            "json"
        };
        return a;
    }
}