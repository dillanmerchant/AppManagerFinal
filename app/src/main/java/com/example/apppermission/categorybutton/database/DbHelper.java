package com.example.apppermission.categorybutton.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "category_db";


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create notes table
        db.execSQL(DbModel.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DbModel.TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ DbModel.TABLE_NAME);
        db.close();
    }

    public long insertData(String packageName, String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DbModel.COLUMN_PACKAGENAME, packageName);
        values.put(DbModel.COLUMN_CATEGORY, category);

        // insert row
        long id = db.insert(DbModel.TABLE_NAME, null, values);
        db.close();
        return id; //returns row of insertion
    }

    public List<DbModel> getAllApps() {
        List<DbModel> apps = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DbModel.TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DbModel app = new DbModel();
                app.setId(cursor.getInt(cursor.getColumnIndex(DbModel.COLUMN_ID)));
                app.setPackageName(cursor.getString(cursor.getColumnIndex(DbModel.COLUMN_PACKAGENAME)));
                app.setCategory(cursor.getString(cursor.getColumnIndex(DbModel.COLUMN_CATEGORY)));

                apps.add(app);
            } while (cursor.moveToNext());
        }

        db.close();

        // return apps list
        return apps;
    }

}
