package com.example.apppermission.categorybutton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.apppermission.R;

import java.util.List;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    Context ctx;
    List<CategoryModel> list;
    PackageManager packageManager;
    ProgressDialog progressDialog;

    public CategoryAdapter(Context ctx, List<CategoryModel> list) {
        this.ctx = ctx;
        this.list = list;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);

        return new CategoryViewHolder(itemView);
    }

    public void updateAdapter(List<CategoryModel> listdata) {
        this.list = listdata;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, final int position) {

        try {
            String appName = list.get(position).appName;
            //String appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(list.get(position).packageName,
                    //PackageManager.GET_META_DATA));
            holder.fraudapp_name.setText(appName);
            Drawable icon = ctx.getPackageManager().getApplicationIcon(list.get(position).packageName);
            holder.fraudapp_icon.setImageDrawable(icon);
            String category = list.get(position).category;
            holder.fraudapp_category.setText(category);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Log.e("position:", String.valueOf(position));
        Log.e("size:", String.valueOf(list.size()));

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

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        public TextView fraudapp_name, fraudapp_size, fraudapp_category;
        public ImageView fraudapp_icon,fraudapps_delete;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            fraudapp_name = itemView.findViewById(R.id.fraudapp_name);
            fraudapp_icon = itemView.findViewById(R.id.fraudapp_icon);
            fraudapp_size = itemView.findViewById(R.id.fraudapp_size);
            fraudapps_delete = itemView.findViewById(R.id.fraudapps_delete);
            fraudapp_category = itemView.findViewById(R.id.fraudapp_category);
        }
    }
}
