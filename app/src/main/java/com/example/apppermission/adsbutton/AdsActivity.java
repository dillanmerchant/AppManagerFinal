package com.example.apppermission.adsbutton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.apppermission.HomeActivity;
import com.example.apppermission.R;
import com.example.apppermission.databutton.DataActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdsActivity extends AppCompatActivity {
    AdsAdapter adapter;
    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);

        ImageView backArrow = (ImageView) findViewById(R.id.ads_backarrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        @Nullable ApplicationInfo appInfo = null;
        recyclerview = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);

        HashMap<String,String> map = getInstalledPackages();

        // Get the values array from hash map
        final String[] values = map.values().toArray(new String[map.size()]);
        final String[] packageKeys = map.keySet().toArray(new String[map.size()]);

        String adFlag = "com.google.android.gms.ads";

        ArrayList<String> appsAdsList = new ArrayList<String>();

        for(int i=0;i<values.length;i++) {
            String packageName = packageKeys[i];
            try {
                appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                Bundle bundle = appInfo.metaData;

                if (bundle != null){
                    for (String metaKey: bundle.keySet())
                    {
                        if(metaKey.contains(adFlag)) {
                            appsAdsList.add(packageName);
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        adapter = new AdsAdapter(AdsActivity.this, appsAdsList);
        recyclerview.setAdapter(adapter);
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
}