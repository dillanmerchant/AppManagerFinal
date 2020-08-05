package com.example.apppermission.databutton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.apppermission.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataActivity extends AppCompatActivity {
    private Context mContext;
    private Activity mActivity;

    private RecyclerView recyclerview;
    private PersonalDataAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        // Get the application context
        mContext = getApplicationContext();
        mActivity = DataActivity.this;

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);

        ArrayList<String> listPackageNames = new ArrayList<String>();

        HashMap<String,String> map = getInstalledPackages();

        // Get the values array from hash map
        final String[] values = map.values().toArray(new String[map.size()]);
        final String[] keys = map.keySet().toArray(new String[map.size()]);

        boolean isPermission;
        for(int i=0;i<values.length;i++){
            String packageName = keys[i];
            //String label = values[i];
            isPermission = getPermissionsByPackageName(packageName);
            if(isPermission){
                listPackageNames.add(packageName);
            }
        }

        adapter = new PersonalDataAdapter(DataActivity.this, listPackageNames);
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected boolean getPermissionsByPackageName(String packageName){
        boolean isPermission = false;

        try {
            // Get the package info
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

            // Loop through the package info requested permissions
            List<String> permissions = Arrays.asList(getResources().getStringArray(R.array.app_permissions));
            for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    String permission =packageInfo.requestedPermissions[i];
                    // To make permission name shorter
                    //permission = permission.substring(permission.lastIndexOf(".")+1);
                    if(permissions.contains(permission)) {
                        isPermission = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isPermission;
    }
}