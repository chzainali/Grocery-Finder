package edu.niu.android.instagroc.adapter;
import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.model.OnClick;
import edu.niu.android.instagroc.model.OrderModel;
import edu.niu.android.instagroc.model.ProductsModel;
import edu.niu.android.instagroc.model.ShopModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    List<OrderModel> list;
    Context context;
    OnClick onClick;
    DatabaseHelper databaseHelper;

    public OrderAdapter(List<OrderModel> list, Context context, DatabaseHelper databaseHelper, OnClick onClick) {
        this.list = list;
        this.context = context;
        this.databaseHelper = databaseHelper;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items_carts, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        OrderModel orderModel = list.get(position);
        ProductsModel productsModel = databaseHelper.getProductByIdAndCategory(orderModel.getProductId(), orderModel.getCategoryId());
        ShopModel shopModel = databaseHelper.getShopById(productsModel.getShopId());
        String productName = orderModel.getName();
        String shopName = shopModel.getName();
        SpannableString spannableProductName = new SpannableString(productName);
        spannableProductName.setSpan(new AbsoluteSizeSpan(14, true), 0, productName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spannableShopName = new SpannableString(shopName);
        spannableShopName.setSpan(new AbsoluteSizeSpan(12, true), 0, shopName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        CharSequence finalText = TextUtils.concat(spannableProductName, ", ", spannableShopName);
        holder.tvProductName.setText(finalText);
        holder.tvTime.setText(orderModel.getTime());
        String OriginalpriceString = orderModel.getPrice();
        double OriginalpriceValue;
        try {
            OriginalpriceValue = Double.parseDouble(OriginalpriceString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return;
        }
        java.text.DecimalFormat df1 = new java.text.DecimalFormat("$0.00");
        String OrigionalformattedPrice = df1.format(OriginalpriceValue);
        holder.tvProductPrice.setText(OrigionalformattedPrice +" for "+ orderModel.getQuantity()+" Item");
        Glide.with(context)
                .asBitmap()
                .load(Uri.parse(orderModel.getImageUri()))
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
        holder.ivDelete.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvProductPrice,tvTime;
        ImageView ivImage,ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvTime = itemView.findViewById(R.id.tvtime);
        }
    }
}
