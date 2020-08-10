package com.example.apppermission;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
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
        RelativeLayout unusedApps = (RelativeLayout) findViewById(R.id.unusedButton);
        RelativeLayout blacklistApps = (RelativeLayout) findViewById(R.id.blacklistButton);
        RelativeLayout categoryApps = (RelativeLayout) findViewById(R.id.categoryButton);
        RelativeLayout adsApps = (RelativeLayout) findViewById(R.id.containAdsButton);

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
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setClassName(APP_DETAILS_PACKAGE_NAME, SCREEN_CLASS_NAME);
                startActivity(intent);
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

    }
}