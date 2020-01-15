package com.INT.apps.GpsspecialDevelopment.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.INT.apps.GpsspecialDevelopment.CrowdvoxApplication;
import com.INT.apps.GpsspecialDevelopment.data.cache.JsonCacheManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by shrey on 9/4/15.
 */
public class Installer {
    public final static void install(final Context context) throws IOException {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String[] copyDatabases = {
                        "categories.sqlite"
                };

                for (String db : copyDatabases) {
                    try {
                        createDataBase(context, db);
                    } catch (IOException e) {

                    }
                }
                JsonCacheManager.getInstance(CrowdvoxApplication.getAppInstance()).purgeOld();
                return null;
            }
        };
        task.execute();

    }

    private static void createDataBase(Context context, String dbName) throws IOException {
        boolean dbExist = checkDataBase(context, dbName);
        if (dbExist) {
            return;
        }
        InstallerSqliteHelper installerSqliteHelper = new InstallerSqliteHelper(context, dbName);
        try {
            installerSqliteHelper.getReadableDatabase();
            copyDataBase(context, dbName);
        } catch (IOException e) {
            throw new Error(e.getMessage());
        } finally {
            installerSqliteHelper.close();
        }
    }

    private static class InstallerSqliteHelper extends SQLiteOpenHelper {
        InstallerSqliteHelper(Context context, String name) {
            super(context, name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private static void copyDataBase(Context context, String dbName) throws IOException {
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(dbName);
        // Path to the just created empty db
        String outFileName = getDBPath(context) + dbName;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    private static boolean checkDataBase(Context context, String dbName) {
        SQLiteDatabase checkDb = null;
        try {
            String dbPath = getDBPath(context) + dbName;
            checkDb = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //don't exists
        } finally {
            if (checkDb != null) {
                checkDb.close();
            }
        }
        return checkDb == null ? false : true;
    }

    private static String getDBPath(Context context) {
        String packageName = context.getPackageName();
        return "/data/data/" + packageName + "/databases/";
    }
}
