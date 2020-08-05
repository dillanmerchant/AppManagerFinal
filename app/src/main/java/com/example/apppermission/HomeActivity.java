package com.example.apppermission;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Spinner;

import com.example.apppermission.blacklistbutton.BlacklistActivity;
import com.example.apppermission.categorybutton.CategoryActivity;
import com.example.apppermission.databutton.DataActivity;
import com.example.apppermission.unusedbutton.UnusedActivity;

public class HomeActivity extends AppCompatActivity {

    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String SCREEN_CLASS_NAME = "com.android.settings.RunningServices";

    private RelativeLayout personalData;
    private TextView pDataText;
    private ImageView dropDownData;
    private ImageView dropUpData;
    private TextView dropDownDataInstruct;

    private TextView unusedApps;
    private TextView blacklistApps;
    private TextView categoryApps;
    private TextView backgroundRunning;
    private TextView adsApps;


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

        personalData = findViewById(R.id.datatext);
        pDataText = findViewById(R.id.pData);
        dropDownData = findViewById(R.id.dropdown);
        pDataText.setTypeface(customFont);
        dropDownDataInstruct = findViewById(R.id.datainstruct);
        dropUpData = findViewById(R.id.dropup);

        backgroundRunning = findViewById(R.id.backgroundtext);
        unusedApps = findViewById(R.id.unusedtext);
        blacklistApps = findViewById(R.id.blacktext);
        categoryApps = findViewById(R.id.categorytext);
        adsApps = findViewById(R.id.adstext);

        backgroundRunning.setTypeface(customFont);
        unusedApps.setTypeface(customFont);
        blacklistApps.setTypeface(customFont);
        categoryApps.setTypeface(customFont);
        adsApps.setTypeface(customFont);

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



        //=======================================================================
        dropDownData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropDownDataInstruct.setVisibility(View.VISIBLE);
                dropDownData.setVisibility(View.GONE);
                dropUpData.setVisibility(View.VISIBLE);
            }
        });

        dropUpData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dropDownDataInstruct.setVisibility(View.GONE);
                dropDownData.setVisibility(View.VISIBLE);
                dropUpData.setVisibility(View.GONE);
            }
        });


    }
}