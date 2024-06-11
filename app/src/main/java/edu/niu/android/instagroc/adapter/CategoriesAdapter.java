package edu.niu.android.instagroc.adapter;
import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.admin.products.ProductsActivity;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.model.CategoryModel;

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

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    List<CategoryModel> list;
    Context context;
    DatabaseHelper databaseHelper;
    String checkFrom;

    public CategoriesAdapter(List<CategoryModel> list, Context context, DatabaseHelper databaseHelper, String checkFrom) {
        this.list = list;
        this.context = context;
        this.databaseHelper = databaseHelper;
        this.checkFrom = checkFrom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CategoryModel categoryModel = list.get(position);
        holder.tvName.setText(categoryModel.getName());
        Glide.with(context)
                .asBitmap()
                .load(Uri.parse(categoryModel.getImage()))
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
        }else{
            holder.ivDelete.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFrom.contentEquals("user")){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", categoryModel);
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_productsFragment, bundle);
                }else{
                    Intent intent = new Intent(context, ProductsActivity.class);
                    intent.putExtra("data", categoryModel);
                    intent.putExtra("shopId", categoryModel.getShopId());
                    context.startActivity(intent);
                }
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view) {
                databaseHelper.deleteCategoryAndProducts(categoryModel.getId());
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
        TextView tvName;
        ImageView ivImage, ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
