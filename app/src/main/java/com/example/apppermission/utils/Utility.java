package com.example.apppermission.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

import com.example.apppermission.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static void saveData(Context context, ArrayList<String> applicationInfos) {
        SharedPreferences appSharedPrefs = context
                .getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(applicationInfos);
        appSharedPrefs.edit().putString("MyObject", json).apply();
    }

    public static ArrayList<String> getData(Context context) {
        SharedPreferences appSharedPrefs = context
                .getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String response = appSharedPrefs.getString("MyObject", "");

        return gson.fromJson(response,
                new TypeToken<List<String>>() {
                }.getType());
    }
}

