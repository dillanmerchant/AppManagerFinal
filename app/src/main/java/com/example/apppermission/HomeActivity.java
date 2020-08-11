package com.example.apppermission;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Spinner;

import com.example.apppermission.adsbutton.AdsActivity;
import com.example.apppermission.blacklistbutton.BlacklistActivity;
import com.example.apppermission.categorybutton.CategoryActivity;
import com.example.apppermission.databutton.DataActivity;
import com.example.apppermission.unusedbutton.UnusedActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String SCREEN_CLASS_NAME = "com.android.settings.RunningServices";

    private Context mContext;
    private Activity mActivity;

    private Spinner mSpinner;
    private TextView mTextView;
    private boolean isPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/applegothic.ttf");

        RelativeLayout personalData = (RelativeLayout) findViewById(R.id.personalDataButton);
        RelativeLayout backgroundRunning = (RelativeLayout) findViewById(R.id.backgroundButton);
        RelativeLayout batteryUsage = (RelativeLayout) findViewById(R.id.batteryUsageButton);
        RelativeLayout unusedApps = (RelativeLayout) findViewById(R.id.unusedButton);
        RelativeLayout blacklistApps = (RelativeLayout) findViewById(R.id.blacklistButton);
        RelativeLayout categoryApps = (RelativeLayout) findViewById(R.id.categoryButton);
        RelativeLayout adsApps = (RelativeLayout) findViewById(R.id.containAdsButton);
        ImageView settings = (ImageView) findViewById(R.id.settings_button);


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

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

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
                    if(resolveInfo != null){
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

}