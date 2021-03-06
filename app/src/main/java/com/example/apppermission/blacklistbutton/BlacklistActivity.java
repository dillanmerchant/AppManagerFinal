package com.example.apppermission.blacklistbutton;

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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apppermission.R;
import com.example.apppermission.categorybutton.CategoryActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlacklistActivity extends AppCompatActivity {

    private static final String TAG = "app";
    private RecyclerView recyclerview;
    private TextView emptyList;
    private RemoveFraudApplicationsAdapter adapter;
    private ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        info = (ImageView) findViewById(R.id.blacklist_info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBlacklistAlertDialog();
            }
        });

        ImageView backArrow = findViewById(R.id.blacklist_backarrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //RecyclerView Setup
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);

        emptyList = (TextView) findViewById(R.id.noapps);
        //Method Call to both send information and receive response correctly
        apiCall();
    }

    private void apiCall() {
        //The parameter array (what we will send the info in)
        JsonArray array = new JsonArray();

        // Get the installed package list
        HashMap<String,String> map = getInstalledPackages();

        // Get the values array from hash map
        final String[] values = map.values().toArray(new String[map.size()]);
        final String[] keys = map.keySet().toArray(new String[map.size()]);

        String thisPackage = getPackageName();

        for(int i=0;i<values.length;i++) {
            JsonObject jsonObject = new JsonObject();
            String packageName = keys[i];
            String label = values[i];
            ApplicationInfo check = null;
            try {
                check = getPackageManager().getApplicationInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (check != null) {
                if (!isSystemPackage(check)) {
                    //Add string params to the Json Object
                    if (!packageName.equalsIgnoreCase(thisPackage)) {
                        jsonObject.addProperty("appName", label);
                        jsonObject.addProperty("packageName", packageName);
                        array.add(jsonObject); //add the object to the Json array
                    }
                }
            }
        }

        //Get the API stored into the variable apiService
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        //call the getBlackListApps method of the API and store it in "BlackList Model" form
        Call<BlackListModel> call = apiService.getBlackListApps(array);

        call.enqueue(new Callback<BlackListModel>() {
            @Override
            public void onResponse(Call<BlackListModel> call, Response<BlackListModel> response) {
                //List with all the blacklisted apps
                List<com.example.apppermission.blacklistbutton.Response> totalList = response.body().getResponse();
                //Make into ArrayList in order to pass through into the Adapter
                try {
                    ArrayList<com.example.apppermission.blacklistbutton.Response> arrayList = new ArrayList<>(totalList);
                //if there are blacklisted apps, set up the adapter to the recyclerview
                    if(arrayList!=null)
                    {
                        if(arrayList.size() > 0) {
                            adapter = new RemoveFraudApplicationsAdapter(BlacklistActivity.this, arrayList);
                            recyclerview.setAdapter(adapter);
                            recyclerview.setVisibility(View.VISIBLE);
                            emptyList.setVisibility(View.INVISIBLE);
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    emptyList.setVisibility(View.VISIBLE);
                    recyclerview.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onFailure(Call<BlackListModel> call, Throwable t) {
                Log.e(TAG, "Server is Down"+ t.getMessage() );
            }
        });
    }

    /*
     * Returns a Map of all the installed applications package name(key) and app name(value)
     */
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

    private void showBlacklistAlertDialog() {
        try {
            final Dialog dialog = new Dialog(BlacklistActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_blacklist_info);
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