package com.example.apppermission.blacklistbutton;

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
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.apppermission.R;

import java.util.ArrayList;


public class RemoveFraudApplicationsAdapter extends RecyclerView.Adapter<RemoveFraudApplicationsAdapter.FraudViewHolder> {

    Context ctx;
    ArrayList<Response> list;

    public RemoveFraudApplicationsAdapter(Context ctx, ArrayList<Response> list) {
        this.ctx = ctx;
        this.list = list;
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

    @Override
    public FraudViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new FraudViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FraudViewHolder holder, final int position) {
        holder.fraudapp_name.setText(list.get(position).getAppName());
        try {
            Drawable icon = ctx.getPackageManager().getApplicationIcon(list.get(position).getPackageName());
            holder.fraudapp_icon.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        holder.fraudapps_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response response_new = list.get(position);
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + list.get(position).getPackageName()));

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

    public void updateAdapter(ArrayList<Response> listdata) {
        this.list = listdata;
        notifyDataSetChanged();
    }

}
