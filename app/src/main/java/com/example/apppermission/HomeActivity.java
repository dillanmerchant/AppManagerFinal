package com.example.apppermission;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apppermission.adsbutton.AdsActivity;
import com.example.apppermission.blacklistbutton.BlacklistActivity;
import com.example.apppermission.categorybutton.CategoryActivity;
import com.example.apppermission.databutton.DataActivity;
import com.example.apppermission.unusedbutton.UnusedActivity;
import com.example.apppermission.utils.MainService;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String SCREEN_CLASS_NAME = "com.android.settings.RunningServices";

    private Context mContext;
    private Activity mActivity;

    private Spinner mSpinner;
    private TextView mTextView;
    private boolean isPermission;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        RelativeLayout personalData = (RelativeLayout) findViewById(R.id.personalDataButton);
        RelativeLayout backgroundRunning = (RelativeLayout) findViewById(R.id.backgroundButton);
        RelativeLayout batteryUsage = (RelativeLayout) findViewById(R.id.batteryUsageButton);
        RelativeLayout unusedApps = (RelativeLayout) findViewById(R.id.unusedButton);
        RelativeLayout blacklistApps = (RelativeLayout) findViewById(R.id.blacklistButton);
        RelativeLayout categoryApps = (RelativeLayout) findViewById(R.id.categoryButton);
        RelativeLayout adsApps = (RelativeLayout) findViewById(R.id.containAdsButton);
        RelativeLayout sideloadApps = (RelativeLayout) findViewById(R.id.sideloadButton);
        ImageView settings = (ImageView) findViewById(R.id.settings_button);

        if(!hasPermission(HomeActivity.this)){
            showAccessAlertDialog();
        }

        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                int value = getIntent().getExtras().getInt("comingFrom");
                switch (value){
                    case 1:
                        Intent intent = new Intent(HomeActivity.this, UnusedActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        Intent intent1 = new Intent(HomeActivity.this, BlacklistActivity.class);
                        startActivity(intent1);
                        break;
                    case 3:
                        Intent intent2 = new Intent(HomeActivity.this, AdsActivity.class);
                        startActivity(intent2);
                        break;
                }
            }
        }


        personalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, DataActivity.class);
                startActivity(intent);
            }
        });

        backgroundRunning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBackgroundAlertDialog();
            }
        });

        unusedApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, UnusedActivity.class);
                startActivity(intent);
            }
        });

        blacklistApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, BlacklistActivity.class);
                startActivity(intent);
            }
        });

        categoryApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        adsApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AdsActivity.class);
                startActivity(intent);
            }
        });

        batteryUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBatteryAlertDialog();
            }
        });

        sideloadApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SideloadActivity.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });




        if (!isMyServiceRunning()) {
            Intent intent = new Intent(this, MainService.class);
            startService(intent);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MainService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void showBackgroundAlertDialog() {
        try {
            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_background_run);
            dialog.setCancelable(true);
            dialog.show();
            Button yesBtn = (Button) dialog.findViewById(R.id.okBtn);
            Button noBtn = (Button) dialog.findViewById(R.id.cancelBtn);
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setClassName(APP_DETAILS_PACKAGE_NAME, SCREEN_CLASS_NAME);
                    startActivity(intent);
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //no button functionality
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBatteryAlertDialog() {
        try {
            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_battery);
            dialog.setCancelable(true);
            dialog.show();
            Button yesBtn = (Button) dialog.findViewById(R.id.okBtn);
            Button noBtn = (Button) dialog.findViewById(R.id.cancelBtn);
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent powerUsageIntent = new Intent(Intent.ACTION_POWER_USAGE_SUMMARY);
                    ResolveInfo resolveInfo = getPackageManager().resolveActivity(powerUsageIntent, 0);
                    if (resolveInfo != null) {
                        startActivity(powerUsageIntent);
                    }
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //no button functionality
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAccessAlertDialog() {
        try {
            final Dialog dialog = new Dialog(HomeActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_usage_access);
            dialog.setCancelable(true);
            dialog.show();
            Button yesBtn = (Button) dialog.findViewById(R.id.okBtn);
            Button noBtn = (Button) dialog.findViewById(R.id.cancelBtn);
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //no button functionality
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean hasPermission(@NonNull final Context context) {
        final AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        if (appOpsManager == null) {
            return false;
        }
        final int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        if (mode != AppOpsManager.MODE_ALLOWED) {
            return false;
        }
        final long now = System.currentTimeMillis();
        final UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, now - 1000 * 10, now);
        return (stats != null && !stats.isEmpty());
    }

}