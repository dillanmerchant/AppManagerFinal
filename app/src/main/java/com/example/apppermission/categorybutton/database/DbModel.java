package com.example.apppermission.categorybutton.database;

public class DbModel {
    public static final String TABLE_NAME = "category_apps";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PACKAGENAME = "packageName";
    public static final String COLUMN_CATEGORY = "category";

    private int id;
    private String category;
    private String packageName;
    private String appName;

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_PACKAGENAME + " TEXT,"
                    + COLUMN_CATEGORY + " TEXT"
                    + ")";

    public DbModel() {

    }

    public DbModel(int id, String packageName, String category) {
        this.id = id;
        this.packageName = packageName;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
