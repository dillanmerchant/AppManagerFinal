package com.example.apppermission.unusedbutton;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apppermission.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Provide views to RecyclerView with the directory entries.
 */
public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {
    Context ctx;
    List<CustomUsageStats> list;
    DateFormat mDateFormat = new SimpleDateFormat();
    PackageManager packageManager;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView fraudapp_name, fraudapp_size, fruadapp_last;
        public ImageView fraudapp_icon,fraudapps_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            fraudapp_name = itemView.findViewById(R.id.fraudapp_name);
            fraudapp_icon = itemView.findViewById(R.id.fraudapp_icon);
            fraudapp_size = itemView.findViewById(R.id.fraudapp_size);
            fraudapps_delete = itemView.findViewById(R.id.fraudapps_delete);
            fruadapp_last = itemView.findViewById(R.id.fraudapp_last_used);

        }
    }

    public UsageListAdapter(Context ctx, List<CustomUsageStats> list) {
        this.ctx = ctx;
        this.list = list;
        packageManager = ctx.getPackageManager();
    }

    @Override
    public UsageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_unused, parent, false);

        return new UsageListAdapter.ViewHolder(itemView);
    }

    public void updateAdapter(List<CustomUsageStats> listdata) {
        this.list = listdata;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(list.get(position).packageName,
                    PackageManager.GET_META_DATA));
            holder.fraudapp_name.setText(appName);
            Drawable icon = ctx.getPackageManager().getApplicationIcon(list.get(position).packageName);
            holder.fraudapp_icon.setImageDrawable(icon);
            long lastTimeUsed = list.get(position).usageStats.getLastTimeUsed();
            holder.fruadapp_last.setText(mDateFormat.format(new Date(lastTimeUsed)));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        holder.fraudapps_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String response_new = list.get(position).packageName;
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + list.get(position)));

                updateAdapter(list);
                ctx.startActivity(intent);
                updateAdapter(list);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}