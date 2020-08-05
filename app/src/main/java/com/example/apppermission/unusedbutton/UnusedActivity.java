package com.example.apppermission.unusedbutton;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apppermission.R;
import com.example.apppermission.unusedbutton.AppUsageStatisticsFragment;

public class UnusedActivity extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_unused);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, AppUsageStatisticsFragment.newInstance())
                        .commit();
            }
        }
    }