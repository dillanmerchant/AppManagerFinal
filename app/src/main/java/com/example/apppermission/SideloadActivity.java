package com.example.apppermission;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apppermission.adsbutton.AdsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SideloadActivity extends AppCompatActivity {
    AdsAdapter adapter;
    RecyclerView recyclerview;
    TextView emptyList;
    ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sideload);

        ImageView backArrow = (ImageView) findViewById(R.id.sideload_backarrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerview = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);

        emptyList = (TextView) findViewById(R.id.noapps);

        info = (ImageView) findViewById(R.id.sideload_info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSideloadAlertDialog();
            }
        });

        HashMap<String,String> map = getInstalledPackages();

        // Get the values array from hash map
        final String[] values = map.values().toArray(new String[map.size()]);
        final String[] packageKeys = map.keySet().toArray(new String[map.size()]);

        ArrayList<String> sideloadList = new ArrayList<String>();

        //String thisPackage = getPackageName();

        PackageManager pm = getPackageManager();

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
                    boolean result = isPlayStoreApp(packageName);
                    if(!result) {
                        sideloadList.add(packageName);
                    }
                }
            }
        }

        if(sideloadList.size() > 0) {
            adapter = new AdsAdapter(SideloadActivity.this, sideloadList);
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

    private void showSideloadAlertDialog() {
        try {
            final Dialog dialog = new Dialog(SideloadActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_sideload_info);
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

    public  boolean isPlayStoreApp(String packageName) {
        boolean result = false;
        try {
            String installer = getPackageManager()
                    .getInstallerPackageName(packageName);
            result = !TextUtils.isEmpty(installer);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }
}