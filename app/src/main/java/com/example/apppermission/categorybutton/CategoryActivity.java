package com.example.apppermission.categorybutton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.apppermission.R;
import com.example.apppermission.adsbutton.AdsActivity;
import com.example.apppermission.categorybutton.database.DbHelper;
import com.example.apppermission.categorybutton.database.DbModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    Spinner categoryMenu;
    String categoryString;
    ProgressDialog progressDialog;
    private DbHelper mDatabase;
    private CategoryAdapter categoryAdapter;
    TextView emptyList;
    ImageView info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ImageView backArrow = findViewById(R.id.category_backarrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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

        emptyList = (TextView) findViewById(R.id.noapps);

        info = (ImageView) findViewById(R.id.category_info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoryAlertDialog();
            }
        });


        mDatabase = new DbHelper(CategoryActivity.this);

        categoryMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryString = categoryMenu.getSelectedItem().toString().toUpperCase();
                ArrayList<String> checkSize = new ArrayList<>();
                HashMap<String,String> map = getInstalledPackages();

                final String[] values = map.values().toArray(new String[map.size()]);
                final String[] keys = map.keySet().toArray(new String[map.size()]);

                String thisPackage = getPackageName();

                for(int j=0;j<values.length;j++) {
                    String packageName = keys[j];
                    ApplicationInfo check = null;
                    try {
                        check = getPackageManager().getApplicationInfo(packageName, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (check != null) {
                        if (!isSystemPackage(check)) {
                            if (!packageName.equalsIgnoreCase(thisPackage)) {
                                checkSize.add(packageName);
                            }
                        }
                    }
                }

                if(mDatabase.getAllApps().size() != checkSize.size()){
                    mDatabase.deleteAll();
                    new FetchCategoryTask().execute();
                }
                else {
                    populateView();
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public class FetchCategoryTask extends AsyncTask<Void, Void, Void> {

        private final String TAG = FetchCategoryTask.class.getSimpleName();
        private PackageManager pm;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CategoryActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... errors) {
            HashMap<String,String> map = getInstalledPackages();

            final String[] values = map.values().toArray(new String[map.size()]);
            final String[] keys = map.keySet().toArray(new String[map.size()]);

            String thisPackage = getPackageName();

            for(int i=0;i<values.length;i++) {
                String packageName = keys[i];
                ApplicationInfo check = null;
                try {
                    check = getPackageManager().getApplicationInfo(packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (check != null) {
                    if (!isSystemPackage(check)) {
                        String appCategoryType = parseAndExtractCategory(packageName);
                        if (!packageName.equalsIgnoreCase(thisPackage)) {
                            mDatabase.insertData(packageName, appCategoryType);
                        }
                    }
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
                        //TODO: END_METHOD_1
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            populateView();
            if(progressDialog.isShowing())
            progressDialog.dismiss();
        }
    }

    public void populateView() {
        List<DbModel> data = mDatabase.getAllApps();
        List<DbModel> display = new ArrayList<DbModel>();
        for(int i = 0; i < data.size(); i++) {
            if(data.get(i).getCategory().equals(categoryString)){
                display.add(data.get(i));
            }
            else if(categoryString.equals("ALL")){
                display = data;
            }
        }
        if(display.size()>0) {
            categoryAdapter = new CategoryAdapter(CategoryActivity.this, display);
            mRecyclerView.setAdapter(categoryAdapter);
            emptyList.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        else {
            emptyList.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }
    }


    public HashMap<String,String> getInstalledPackages(){
        PackageManager packageManager = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent,0);
        HashMap<String,String> map = new HashMap<>();

        for(ResolveInfo resolveInfo : resolveInfoList){
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String packageName = activityInfo.applicationInfo.packageName;
            String label = (String) packageManager.getApplicationLabel(activityInfo.applicationInfo);
            map.put(packageName,label);
        }
        return map;
    }

    private void showCategoryAlertDialog() {
        try {
            final Dialog dialog = new Dialog(CategoryActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_category_info);
            dialog.setCancelable(true);
            dialog.show();
            Button yesBtn = (Button) dialog.findViewById(R.id.okBtn);
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSystemPackage(ApplicationInfo pkgInfo) {
        return (pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}