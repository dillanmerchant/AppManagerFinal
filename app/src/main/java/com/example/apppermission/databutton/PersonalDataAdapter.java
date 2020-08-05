package com.example.apppermission.databutton;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.apppermission.R;

import java.util.ArrayList;


public class PersonalDataAdapter extends RecyclerView.Adapter<PersonalDataAdapter.FraudViewHolder> {

    Context ctx;
    ArrayList<String> list;
    PackageManager packageManager;


    public PersonalDataAdapter(Context ctx, ArrayList<String> list) {
        this.ctx = ctx;
        this.list = list;
        packageManager = ctx.getPackageManager();
    }

    @Override
    public FraudViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new FraudViewHolder(itemView);
    }
    public void updateAdapter(ArrayList<String> listdata) {
        this.list = listdata;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final FraudViewHolder holder, final int position) {

        try {
            String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(list.get(position),
                    PackageManager.GET_META_DATA));
            holder.fraudapp_name.setText(appName);
            Drawable icon = ctx.getPackageManager().getApplicationIcon(list.get(position));
            holder.fraudapp_icon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        holder.fraudapps_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String response_new= list.get(position);
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

    class FraudViewHolder extends RecyclerView.ViewHolder {
        public TextView fraudapp_name, fraudapp_size;
        public ImageView fraudapp_icon,fraudapps_delete;

        public FraudViewHolder(View itemView) {
            super(itemView);
            fraudapp_name = itemView.findViewById(R.id.fraudapp_name);
            fraudapp_icon = itemView.findViewById(R.id.fraudapp_icon);
            fraudapp_size = itemView.findViewById(R.id.fraudapp_size);
            fraudapps_delete = itemView.findViewById(R.id.fraudapps_delete);
        }
    }
}
