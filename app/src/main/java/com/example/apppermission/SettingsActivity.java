package com.example.apppermission;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.apppermission.utils.MainService;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);

        ImageView backArrow = findViewById(R.id.settings_backarrow);

        final ImageView ad_enable = findViewById(R.id.ads_enabler);
        final ImageView ad_disable = findViewById(R.id.ads_disabler);

        ad_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad_disable.setVisibility(View.VISIBLE);
                ad_enable.setVisibility(View.INVISIBLE);
                sharedPreferences.edit().putBoolean("addAdsStatus", false).apply();
            }
        });

        ad_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ad_enable.setVisibility(View.VISIBLE);
                ad_disable.setVisibility(View.INVISIBLE);
                sharedPreferences.edit().putBoolean("addAdsStatus", true).apply();
            }
        });

        if(sharedPreferences.getBoolean("addAdsStatus", true)) {
            ad_enable.setVisibility(View.VISIBLE);
            ad_disable.setVisibility(View.INVISIBLE);
        }
        else {
            ad_disable.setVisibility(View.VISIBLE);
            ad_enable.setVisibility(View.INVISIBLE);
        }

        final ImageView unuse_enable = findViewById(R.id.unused_enabler);
        final ImageView unuse_disable = findViewById(R.id.unused_disabler);

        unuse_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unuse_disable.setVisibility(View.VISIBLE);
                unuse_enable.setVisibility(View.INVISIBLE);
                sharedPreferences.edit().putBoolean("addUnuseStatus", false).apply();
            }
        });

        unuse_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unuse_enable.setVisibility(View.VISIBLE);
                unuse_disable.setVisibility(View.INVISIBLE);
                sharedPreferences.edit().putBoolean("addUnuseStatus", true).apply();
            }
        });

        if(sharedPreferences.getBoolean("addUnuseStatus", true)) {
            unuse_enable.setVisibility(View.VISIBLE);
            unuse_disable.setVisibility(View.INVISIBLE);
        }
        else {
            unuse_disable.setVisibility(View.VISIBLE);
            unuse_enable.setVisibility(View.INVISIBLE);
        }

        final ImageView blacklist_enable = findViewById(R.id.blacklist_enabler);
        final ImageView blacklist_disable = findViewById(R.id.blacklist_disabler);

        blacklist_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blacklist_disable.setVisibility(View.VISIBLE);
                blacklist_enable.setVisibility(View.INVISIBLE);
                sharedPreferences.edit().putBoolean("addBlacklistStatus", false).apply();
            }
        });

        blacklist_disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blacklist_enable.setVisibility(View.VISIBLE);
                blacklist_disable.setVisibility(View.INVISIBLE);
                sharedPreferences.edit().putBoolean("addBlacklistStatus", true).apply();
            }
        });

        if(sharedPreferences.getBoolean("addBlacklistStatus", true)) {
            blacklist_enable.setVisibility(View.VISIBLE);
            blacklist_disable.setVisibility(View.INVISIBLE);
        }
        else {
            blacklist_disable.setVisibility(View.VISIBLE);
            blacklist_enable.setVisibility(View.INVISIBLE);
        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Spinner spinner = findViewById(R.id.spinner_setting);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interval_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner.setSelection(sharedPreferences.getInt("selectedSpinner", 1));

        ConstraintLayout aboutUs = findViewById(R.id.aboutus);
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(SettingsActivity.this, "==============" + i, Toast.LENGTH_SHORT).show();
        if (sharedPreferences.getInt("selectedSpinner", 1) != i) {
            sharedPreferences.edit().putInt("selectedSpinner", i).apply();
            switch (i) {
                case 0:
                    Intent intent = new Intent(this, MainService.class);
                    stopService(intent);

                    sharedPreferences.edit().putInt("unUsedAppsTimer", 1).apply();
                    Intent startIntent1 = new Intent(this, MainService.class);
                    startService(startIntent1);
                    break;
                case 1:
                    Intent intent2 = new Intent(this, MainService.class);
                    stopService(intent2);

                    sharedPreferences.edit().putInt("unUsedAppsTimer", 7).apply();
                    Intent startIntent2 = new Intent(this, MainService.class);
                    startService(startIntent2);
                    break;
                case 2:
                    Intent intent3 = new Intent(this, MainService.class);
                    stopService(intent3);

                    sharedPreferences.edit().putInt("unUsedAppsTimer", 30).apply();
                    Intent startIntent3 = new Intent(this, MainService.class);
                    startService(startIntent3);
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}