package edu.niu.android.instagroc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.admin.categories.CategoriesActivity;
import edu.niu.android.instagroc.admin.products.ProductsActivity;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.model.CategoryModel;
import edu.niu.android.instagroc.model.ShopModel;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {
    List<ShopModel> list;
    Context context;
    DatabaseHelper databaseHelper;
    String checkFrom;

    public ShopAdapter(List<ShopModel> list, Context context, DatabaseHelper databaseHelper, String checkFrom) {
        this.list = list;
        this.context = context;
        this.databaseHelper = databaseHelper;
        this.checkFrom = checkFrom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ShopModel shopModel = list.get(position);
        holder.tvName.setText(shopModel.getName());
        holder.tvLocation.setText(shopModel.getLocation());
        Glide.with(context)
                .asBitmap()
                .load(Uri.parse(shopModel.getImage()))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Now you have the Bitmap, you can set it as the image resource.
                        holder.ivImage.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // This is called when the Drawable is cleared, for example, when the view is recycled.
                    }
                });
        if (checkFrom.contentEquals("user")){
            holder.ivDelete.setVisibility(View.GONE);
            holder.tvLocation.setVisibility(View.GONE);
        }else{
            holder.ivDelete.setVisibility(View.VISIBLE);
            holder.tvLocation.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFrom.contentEquals("user")){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", shopModel);
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_productsFragment, bundle);
                }else{
                    Intent intent = new Intent(context, CategoriesActivity.class);
                    intent.putExtra("data", shopModel);
                    context.startActivity(intent);
                }
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                databaseHelper.deleteShop(shopModel.getId());
                list.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation;
        ImageView ivImage, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
