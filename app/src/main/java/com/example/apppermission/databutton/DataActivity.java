package com.example.apppermission.databutton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.apppermission.HomeActivity;
import com.example.apppermission.R;
import com.example.apppermission.adsbutton.AdsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataActivity extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;

    private RecyclerView recyclerview;
    private PersonalDataAdapter adapter;
    private ImageView info_page;
    TextView emptyList;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        ImageView backArrow = findViewById(R.id.data_backarrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Get the application context
        mContext = getApplicationContext();
        mActivity = DataActivity.this;

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);

        emptyList = (TextView) findViewById(R.id.noapps);

        info_page = (ImageView) findViewById(R.id.data_info);

        info_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDataAlertDialog();
            }
        });

        ArrayList<ListModel> finalList = new ArrayList<ListModel>();
        ArrayList<String> listPermissions = new ArrayList<>();

        HashMap<String,String> map = getInstalledPackages();

        // Get the values array from hash map
        final String[] values = map.values().toArray(new String[map.size()]);
        final String[] keys = map.keySet().toArray(new String[map.size()]);

        String thisPackage = getPackageName();
        for(int i=0;i<values.length;i++){
            String packageName = keys[i];
            ApplicationInfo check = null;
            try {
                check = getPackageManager().getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (check != null) {
                if (!isSystemPackage(check)) {
                    listPermissions = getPermissionsByPackageName(packageName);
                    if (listPermissions.size() > 0) {
                        ListModel app = new ListModel();
                        app.packageName = packageName;
                        app.personalPermissions = listPermissions;
                        if (!app.packageName.equalsIgnoreCase(thisPackage)) {
                            finalList.add(app);
                        }
                    }
                }
            }
        }

        if(finalList.size() > 0) {
            adapter = new PersonalDataAdapter(DataActivity.this, finalList);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected ArrayList<String> getPermissionsByPackageName(String packageName){
        ArrayList<String> listPermissions = new ArrayList<>();
        String addPermission = "";

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

            List<String> permissions = Arrays.asList(getResources().getStringArray(R.array.app_permissions));
            for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    String permission =packageInfo.requestedPermissions[i];
                    // To make permission name shorter
                    if(permissions.contains(permission)) {
                        addPermission = permission.substring(permission.lastIndexOf(".")+1);
                        listPermissions.add(addPermission);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listPermissions;
    }

    private void showDataAlertDialog() {
        try {
            final Dialog dialog = new Dialog(DataActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_data_info);
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