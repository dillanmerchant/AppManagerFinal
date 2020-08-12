package com.example.apppermission.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.apppermission.HomeActivity;
import com.example.apppermission.MainActivity;
import com.example.apppermission.R;
import com.example.apppermission.blacklistbutton.ApiClient;
import com.example.apppermission.blacklistbutton.ApiInterface;
import com.example.apppermission.blacklistbutton.BlackListModel;
import com.example.apppermission.blacklistbutton.BlacklistActivity;
import com.example.apppermission.blacklistbutton.RemoveFraudApplicationsAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainService extends Service {


    private long timerCount = 24 * 60 * 60 * 1000;
    //private final long timerCount = 10 * 1000;
    private final ArrayList<String> arrayList = new ArrayList<>();
    private final ArrayList<String> installedApps = new ArrayList<>();
    private final ArrayList<String> filteredList = new ArrayList<>();
    private final ArrayList<String> adsList = new ArrayList<>();
    int iterations = 0, selectedSettingsValue = 7;
    private SharedPreferences sharedPreferences;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            iterations++;
            Log.e("=--=========", iterations + "----------->" + selectedSettingsValue);
            if (iterations == selectedSettingsValue) {
                filterApplications();
                iterations = 0;
            }
            handler.postDelayed(this, timerCount);
        }
    };

    private void filterApplications() {
        try {
            filteredList.clear();
            adsList.clear();
            installedApps();
            List<UsageStats> usageStats = getUsageStatsList(MainService.this);
            arrayList.clear();
            for (int i = 0; i < usageStats.size(); i++) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ApplicationInfo app = this.getPackageManager().getApplicationInfo(usageStats.get(i).getPackageName(), 0);
                        if (!isSystemPackage(app)) {
                            arrayList.add(usageStats.get(i).getPackageName());
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < installedApps.size(); i++) {
                if (!arrayList.contains(installedApps.get(i))) {
                    filteredList.add(installedApps.get(i));
                }
            }
            if (filteredList.size() > 0) {
                Utility.saveData(MainService.this, filteredList);
                if(sharedPreferences.getBoolean("addUnuseStatus", true)) {
                    sendNotification("Unused Apps " + filteredList.size(), 1);
                }
                if(sharedPreferences.getBoolean("addBlacklistStatus", true)) {
                    apiCall();
                }
            }
            if (adsList.size() > 0 && sharedPreferences.getBoolean("addAdsStatus", true)) {
                sendNotification("Apps Contains Ads " + adsList.size(), 3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isSystemPackage(ApplicationInfo pkgInfo) {
        return (pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    public void installedApps() {
        try {
            installedApps.clear();
            List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
            for (int i = 0; i < packList.size(); i++) {
                PackageInfo packInfo = packList.get(i);
                if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    if (!packInfo.applicationInfo.packageName.equals(getPackageName())) {
                        installedApps.add(packInfo.applicationInfo.packageName);
                    }

                    ApplicationInfo appInfo  = getPackageManager().getApplicationInfo(packInfo.applicationInfo.packageName, PackageManager.GET_META_DATA);
                    Bundle bundle = appInfo.metaData;

                    if (bundle != null) {
                        for (String metaKey : bundle.keySet()) {
                            String adFlag = "com.google.android.gms.ads";
                            if (metaKey.contains(adFlag)) {
                                if(!adsList.contains(packInfo.applicationInfo.packageName)) {
                                    adsList.add(packInfo.applicationInfo.packageName);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<UsageStats> getUsageStatsList(Context context) {
        List<UsageStats> usageStatsList;
        try {
            UsageStatsManager usm = getUsageStatsManager(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                usageStatsList = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, sharedPreferences.getLong("startTime", System.currentTimeMillis()), System.currentTimeMillis());
            } else {
                usageStatsList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            usageStatsList = new ArrayList<>();
        }
        return usageStatsList;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startMyOwnForeground() {
        try {
            String NOTIFICATION_CHANNEL_ID = "com.example.apppermission";
            String channelName = getResources().getString(R.string.app_name);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel chan;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
                chan.setLightColor(Color.BLUE);
                chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                assert manager != null;
                manager.createNotificationChannel(chan);
            }


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
            Notification notification;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                notification = notificationBuilder.setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("")
                        .setPriority(NotificationManager.IMPORTANCE_MIN)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .build();
            } else {
                notification = notificationBuilder.setOngoing(true)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("")
                        .build();
            }

            startForeground(2, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(2, new Notification());
        }
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        sharedPreferences.edit().putLong("startTime", System.currentTimeMillis()).apply();
        selectedSettingsValue = sharedPreferences.getInt("unUsedAppsTimer", 7);

        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, timerCount);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }


    private UsageStatsManager getUsageStatsManager(Context context) {
        UsageStatsManager usm = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            }
        }
        return usm;
    }

    private void sendNotification(String msg1, int value) {
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder = null;
        Random generate = new Random();
        int pushNotificationId = generate.nextInt();
        String id = "AppManager" + pushNotificationId;
        String name = getResources().getString(R.string.app_name);
        String description = "";


        final NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            if (mNotificationManager != null) {
                NotificationChannel mChannel = mNotificationManager.getNotificationChannel(id);
                if (mChannel == null) {
                    mChannel = new NotificationChannel(id, name, importance);
                    mChannel.setDescription(description);
                    mNotificationManager.createNotificationChannel(mChannel);
                }
                builder = new NotificationCompat.Builder(this, id);


                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("comingFrom", value);
                intent.putExtra("NOTIFICATION_MSG", msg1);


                pendingIntent = PendingIntent.getActivity(this, pushNotificationId, intent, PendingIntent.FLAG_ONE_SHOT);
                builder.setSmallIcon(R.mipmap.ic_launcher) // required
                        .setContentTitle(name)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg1))
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                        .setContentText(msg1).setAutoCancel(true);
                builder.setContentIntent(pendingIntent);


            }
        } else {
            builder = new NotificationCompat.Builder(this, id);
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("NOTIFICATION_MSG", msg1);
            pendingIntent = PendingIntent.getActivity(this, pushNotificationId
                    , intent, PendingIntent.FLAG_ONE_SHOT);

            builder.setSmallIcon(R.mipmap.ic_launcher) // required
                    .setContentTitle(name)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg1))
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                    .setContentText(msg1).setAutoCancel(true);
            builder.setContentIntent(pendingIntent);
        }
        if (builder != null) {
            Notification notification = builder.build();
            if (mNotificationManager != null) {
                mNotificationManager.notify(pushNotificationId, notification);
            }
        }
    }

    public void apiCall() {
        //The parameter array (what we will send the info in)
        JsonArray array = new JsonArray();

        // Get the installed package list
        HashMap<String,String> map = getInstalledPackages();

        // Get the values array from hash map
        final String[] values = map.values().toArray(new String[map.size()]);
        final String[] keys = map.keySet().toArray(new String[map.size()]);

        for(int i=0;i<values.length;i++) {
            JsonObject jsonObject = new JsonObject();
            String packageName = keys[i];
            String label = values[i];
            try {
                ApplicationInfo app = getPackageManager().getApplicationInfo(packageName, 0);
                if (!isSystemPackage(app)) {
                    //Add string params to the Json Object
                    jsonObject.addProperty("appName", label);
                    jsonObject.addProperty("packageName", packageName);
                    array.add(jsonObject); //add the object to the Json array
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        //Get the API stored into the variable apiService
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        //call the getBlackListApps method of the API and store it in "BlackList Model" form
        Call<BlackListModel> call = apiService.getBlackListApps(array);

        call.enqueue(new Callback<BlackListModel>() {
            @Override
            public void onResponse(Call<BlackListModel> call, Response<BlackListModel> response) {
                if(response != null) {
                    if(response.body() != null) {
                        //List with all the blacklisted apps
                        List<com.example.apppermission.blacklistbutton.Response> totalList = response.body().getResponse();
                        //Make into ArrayList in order to pass through into the Adapter
                        try {
                            ArrayList<com.example.apppermission.blacklistbutton.Response> arrayList = new ArrayList<>(totalList);
                            //if there are blacklisted apps, set up the adapter to the recyclerview
                            if (arrayList != null) {
                                if(arrayList.size() > 0) {
                                    sendNotification("Blacklisted Apps " + arrayList.size(), 2);
                                }
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BlackListModel> call, Throwable t) {

            }
        });
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
}
