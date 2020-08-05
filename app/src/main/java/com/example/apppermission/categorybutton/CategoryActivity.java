package com.example.apppermission.categorybutton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.apppermission.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    List<CategoryModel> categoryModelList;
    Spinner categoryMenu;
    String categoryString;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryMenu = (Spinner) findViewById(R.id.category_menu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryMenu.setAdapter(adapter);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        categoryMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryString = categoryMenu.getSelectedItem().toString().toUpperCase();
                new FetchCategoryTask().execute();
            }
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public class FetchCategoryTask extends AsyncTask<Void, Void, Void> {

        private final String TAG = FetchCategoryTask.class.getSimpleName();
        private PackageManager pm;
/*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CategoryActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }
*/
        @Override
        protected Void doInBackground(Void... errors) {
            HashMap<String,String> map = getInstalledPackages();

            final String[] values = map.values().toArray(new String[map.size()]);
            final String[] keys = map.keySet().toArray(new String[map.size()]);

            categoryModelList = new ArrayList<>();
            for(int i=0;i<values.length;i++) {
                CategoryModel categoryModel = new CategoryModel();
                String packageName = keys[i];
                String appName = values[i];
                String appCategoryType = parseAndExtractCategory(packageName);
                categoryModel.category = appCategoryType;
                categoryModel.packageName = packageName;
                categoryModel.appName = appName;
                if(appCategoryType.equals(categoryString)) {
                    categoryModelList.add(categoryModel);
                }
                else if (categoryString.equals("ALL")) {
                    categoryModelList.add(categoryModel);
                }
            }

            return null;
        }

        private String parseAndExtractCategory(String packageName) {
            String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
            String appCategoryType = null;
            String CATEGORY_STRING = "category/";
            String url = GOOGLE_URL + packageName + "&hl=en"; //{https://play.google.com/store/apps/details?id=com.example.app&hl=en}
            String appName = null;
            try {
//            if (!extractionApps.contains(packageName)) {
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).timeout(30 * 1000).get();
                    if (doc != null) {
                        //TODO: START_METHOD_1
                        //Extract category String from a <anchor> tag value directly.
                        //NOTE: its return sub category text, for apps with multiple sub category.
                        //Comment this method {METHOD_1}, if you wish to extract category by href value.
                        //Element CATEGORY_SUB_CATEGORY = doc.select("a[itemprop=genre]").first();
                        //if (CATEGORY_SUB_CATEGORY != null) {
                        //    appCategoryType = CATEGORY_SUB_CATEGORY.text();
                        //}
                        //TODO: END_METHOD_1
                        //TODO: START_METHOD_2
                        // Use below code only if you wist to extract category by href value.
                        //Its return parent or Main Category Text for all app.
                        //Comment this method {METHOD_2}, If you wihs to extract category from a<anchor> value.
                        if (appCategoryType == null || appCategoryType.length() < 1) {
                            Elements text = doc.select("a[itemprop=genre]");
                            if (text != null) {
                                if (appCategoryType == null || appCategoryType.length() < 2) {
                                    String href = text.attr("abs:href");
                                    if (href != null && href.length() > 4 && href.contains(CATEGORY_STRING)) {
                                        appCategoryType = getCategoryTypeByHref(href);
                                    }
                                }
                            }
                        }
                        //TODO: END_METHOD_2
                        if (appCategoryType != null && appCategoryType.length() > 1) {
                            appCategoryType = replaceSpecialCharacter(appCategoryType);
                        }
                    }
                } catch (IOException e) {
                    appCategoryType = "OTHERS";
                    //TODO:: Handle Exception
                    e.printStackTrace();
                }
            } catch (Exception e) {
                //TODO:: Handle Exception
                e.printStackTrace();
            }
            return appCategoryType;
        }
        /**
         * @param href
         * @return
         */
        private String getCategoryTypeByHref(String href) {
            String appCategoryType = null;
            String CATEGORY_STRING = "category/";
            int CAT_SIZE = 9;
            String CATEGORY_GAME_STRING = "GAME_";// All games start with this prefix
            try {
                appCategoryType = href.substring((href.indexOf(CATEGORY_STRING) + CAT_SIZE), href.length());
                if (appCategoryType != null && appCategoryType.length() > 1) {
                    if (appCategoryType.contains(CATEGORY_GAME_STRING)) {
                        //appCategoryType = appContext.getString(R.string.games);
                        appCategoryType = "GAMES";
                    }
                }
            } catch (Exception e) {
                //TODO:: Handle Exception
                e.printStackTrace();
            }
            return appCategoryType;
        }
        /**
         * @param appCategoryType
         * @return: formatted String
         */
        private String replaceSpecialCharacter(String appCategoryType) {
            try {
                //Find and Replace '&amp;' with '&' in category Text
                if (appCategoryType.contains("&amp;")) {
                    appCategoryType = appCategoryType.replace("&amp;", " & ");
                }
                //Find and Replace '_AND_' with ' & ' in category Text
                if (appCategoryType.contains("_AND_")) {
                    appCategoryType = appCategoryType.replace("_AND_", " & ");
                }
                //Find and Replace '_' with ' ' <space> in category Text
                if (appCategoryType.contains("_")) {
                    appCategoryType = appCategoryType.replace("_", " ");
                }
            } catch (Exception e) {
                //TODO:: Handle Exception
                e.printStackTrace();
            }
            return appCategoryType;
        }

        protected HashMap<String,String> getInstalledPackages(){
            PackageManager packageManager = getPackageManager();

            // Initialize a new intent
            Intent intent = new Intent(Intent.ACTION_MAIN,null);
            // Set the intent category
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // Set the intent flags
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

            // Initialize a new list of resolve info
            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent,0);

            // Initialize a new hash map of package names and application label
            HashMap<String,String> map = new HashMap<>();

            // Loop through the resolve info list
            for(ResolveInfo resolveInfo : resolveInfoList){
                // Get the activity info from resolve info
                ActivityInfo activityInfo = resolveInfo.activityInfo;

                // Get the package name
                String packageName = activityInfo.applicationInfo.packageName;

                // Get the application label
                String label = (String) packageManager.getApplicationLabel(activityInfo.applicationInfo);

                // Put the package name and application label to hash map
                map.put(packageName,label);
            }
            return map;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            CategoryAdapter categoryAdapter = new CategoryAdapter(CategoryActivity.this, categoryModelList);
            mRecyclerView.setAdapter(categoryAdapter);
            //if(progressDialog.isShowing())
            //progressDialog.dismiss();
        }
    }
}