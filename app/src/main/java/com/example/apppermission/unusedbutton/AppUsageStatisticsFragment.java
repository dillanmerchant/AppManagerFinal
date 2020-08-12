/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.apppermission.unusedbutton;

import android.app.Dialog;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.apppermission.HomeActivity;
import com.example.apppermission.R;
import com.example.apppermission.categorybutton.CategoryActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Fragment that demonstrates how to use App Usage Statistics API.
 */
public class AppUsageStatisticsFragment extends Fragment {

    private static final String TAG = AppUsageStatisticsFragment.class.getSimpleName();

    //VisibleForTesting for variables below
    UsageStatsManager mUsageStatsManager;
    UsageListAdapter mUsageListAdapter;
    Spinner spinner;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    TextView emptyList;
    ImageView info;
    double checkDays = 7.0;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link AppUsageStatisticsFragment}.
     */
    public static Fragment newInstance() {
        AppUsageStatisticsFragment fragment = new AppUsageStatisticsFragment();
        return fragment;
    }

    public AppUsageStatisticsFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsageStatsManager = (UsageStatsManager) getActivity()
                .getSystemService(Context.USAGE_STATS_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.unused_page, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        spinner = (Spinner) rootView.findViewById(R.id.timespan);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.timespans, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    checkDays = 7.0;
                }
                if(i == 1) {
                    checkDays = 1.0;
                }
                if(i == 2) {
                    checkDays = 30.0;
                }
                if(i == 3) {
                    checkDays = 0.0;
                }
                List<UsageStats> usageStatsList = getUsageStatistics(UsageStatsManager.INTERVAL_BEST);
                Collections.sort(usageStatsList, new LastTimeLaunchedComparatorDesc());
                updateAppsList(usageStatsList);
            }
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //RecyclerView Creation
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.scrollToPosition(0);

        emptyList = (TextView) rootView.findViewById(R.id.noapps);

        info = (ImageView) rootView.findViewById(R.id.unused_info);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUnusedAlertDialog();
            }
        });

        ImageView backArrow = rootView.findViewById(R.id.unused_backarrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Returns the {@link #mRecyclerView} including the time span specified by the
     * intervalType argument.
     *
     * @param intervalType The time interval by which the stats are aggregated.
     *                     Corresponding to the value of {@link UsageStatsManager}.
     *                     E.g. {@link UsageStatsManager#INTERVAL_DAILY}, {@link
     *                     UsageStatsManager#INTERVAL_WEEKLY},
     *
     * @return A list of {@link android.app.usage.UsageStats}.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public List<UsageStats> getUsageStatistics(int intervalType) {
        // Get the app statistics since one year ago from the current time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = mUsageStatsManager
                .queryUsageStats(intervalType, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            showAlertDialog();
        }
        return queryUsageStats;
    }

    /**
     * Updates the {@link #mRecyclerView} with the list of {@link UsageStats} passed as an argument.
     *
     * @param usageStatsList A list of {@link UsageStats} from which update the
     *                       {@link #mRecyclerView}.
     */
    //VisibleForTesting
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void updateAppsList(List<UsageStats> usageStatsList) {
        try {
            List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
            long lastTimeUsed;
            long current;
            long diff;
            long dayConvert;
            String thisPackage = getActivity().getPackageName();
            Log.e("thisPackage", thisPackage);
            for (int i = 0; i < usageStatsList.size(); i++) {
                ApplicationInfo app = null;
                try {
                    app = getActivity().getPackageManager().getApplicationInfo(usageStatsList.get(i).getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                if (app != null) {
                    if (!isSystemPackage(app)) {
                        CustomUsageStats customUsageStats = new CustomUsageStats();
                        customUsageStats.usageStats = usageStatsList.get(i);
                        customUsageStats.packageName = usageStatsList.get(i).getPackageName();
                        for (int j = i + 1; j < usageStatsList.size(); j++) {
                            String getter = usageStatsList.get(j).getPackageName();
                            if (usageStatsList.get(i).getPackageName().equals(getter)) {
                                usageStatsList.remove(j);
                                j--;
                            }
                        }
                        try {
                            Drawable appIcon = getActivity().getPackageManager()
                                    .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                            customUsageStats.appIcon = appIcon;
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.w(TAG, String.format("App Icon is not found for %s",
                                    customUsageStats.usageStats.getPackageName()));
                            customUsageStats.appIcon = getActivity()
                                    .getDrawable(R.drawable.ic_default_app_launcher);
                        }
                        lastTimeUsed = customUsageStats.usageStats.getLastTimeUsed();
                        current = System.currentTimeMillis();
                        dayConvert = (long) (1000 * 60 * 60 * 24);
                        diff = current - lastTimeUsed;
                        if (diff > (checkDays * dayConvert)) {
                            if (!customUsageStats.packageName.equalsIgnoreCase(thisPackage)) {
                                customUsageStatsList.add(customUsageStats);
                            }
                        }
                    }
                }
                }

                if (customUsageStatsList.size() > 0) {
                    mUsageListAdapter = new UsageListAdapter(getContext(), customUsageStatsList);
                    mRecyclerView.setAdapter(mUsageListAdapter);
                    mRecyclerView.scrollToPosition(0);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    emptyList.setVisibility(View.INVISIBLE);
                } else {
                    emptyList.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The {@link Comparator} to sort a collection of {@link UsageStats} sorted by the timestamp
     * last time the app was used in the descendant order.
     */
    private static class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
        }
    }

    /**
     * Enum represents the intervals for {@link android.app.usage.UsageStatsManager} so that
     * values for intervals can be found by a String representation.
     *
     */
    //VisibleForTesting
    static enum StatsUsageInterval {
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            for (StatsUsageInterval statsUsageInterval : values()) {
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }

    private void showAlertDialog() {
        try {
            final Dialog dialog = new Dialog(getContext());
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

    private boolean isSystemPackage(ApplicationInfo pkgInfo) {
        return (pkgInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    private void showUnusedAlertDialog() {
        try {
            final Dialog dialog = new Dialog(getContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_unused_info);
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
