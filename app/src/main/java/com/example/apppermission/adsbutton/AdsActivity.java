package com.example.apppermission.adsbutton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apppermission.HomeActivity;
import com.example.apppermission.R;
import com.example.apppermission.databutton.DataActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdsActivity extends AppCompatActivity {
    AdsAdapter adapter;
    RecyclerView recyclerview;
    TextView emptyList;
    ImageView info;

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

        emptyList = (TextView) findViewById(R.id.noapps);

        info = (ImageView) findViewById(R.id.ads_info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAdsAlertDialog();
            }
        });

        HashMap<String,String> map = getInstalledPackages();

        // Get the values array from hash map
        final String[] values = map.values().toArray(new String[map.size()]);
        final String[] packageKeys = map.keySet().toArray(new String[map.size()]);

        String adFlag = "com.google.android.gms.ads";

        ArrayList<String> appsAdsList = new ArrayList<String>();

        String thisPackage = getPackageName();
        for(int i=0;i<values.length;i++) {
            String packageName = packageKeys[i];
            ApplicationInfo check = null;
            try {
                check = getPackageManager().getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (check != null) {
                if (!isSystemPackage(check)) {
                    try {
                        appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                        Bundle bundle = appInfo.metaData;

                        if (bundle != null) {
                            for (String metaKey : bundle.keySet()) {
                                if (metaKey.contains(adFlag)) {
                                    if (!appsAdsList.contains(packageName)) {
                                        if (!packageName.equalsIgnoreCase(thisPackage)) {
                                            appsAdsList.add(packageName);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if(appsAdsList.size() > 0) {
            adapter = new AdsAdapter(AdsActivity.this, appsAdsList);
            recyclerview.setAdapter(adapter);
            recyclerview.setVisibility(View.VISIBLE);
            emptyList.setVisibility(View.INVISIBLE);
        }
        else {
            emptyList.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.INVISIBLE);
        }
    }

    protected HashMap<String,String> getInstalledPackages(){
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

    private boolean isSystemPackage(ApplicationInfo pkgInfo) {
        return (pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    private void showAdsAlertDialog() {
        try {
            final Dialog dialog = new Dialog(AdsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_ads_info);
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
}