package edu.niu.android.instagroc.admin.products;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.ActivityProductDetailsBinding;
import edu.niu.android.instagroc.model.ProductsModel;
import edu.niu.android.instagroc.model.ShopModel;

public class ProductDetailsActivity extends AppCompatActivity {
    ActivityProductDetailsBinding binding;
    ProductsModel productsModel;
    ShopModel shopModel;
    DatabaseHelper databaseHelper;
    java.text.DecimalFormat df;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Create a decimal format for currency display
        df = new java.text.DecimalFormat("0.00");

        // Set up a text watcher for the product name EditText
        binding.nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (!charSequence.toString().isEmpty()){
                    validateInput(charSequence.toString());
                }
                // Not used in this example
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used in this example
            }
        });

        // Set the label for the top text view
        binding.rlTop.tvLabel.setText("Product Details");

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Show the back button in the top bar and set its click listener to finish the activity
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());

        // Check if there are extras in the intent
        if (getIntent().getExtras() != null) {
            // Retrieve the ProductsModel from the intent extras
            productsModel = (ProductsModel) getIntent().getSerializableExtra("data");
            shopModel = databaseHelper.getShopById(productsModel.getShopId());

            // Set the product details in the UI
            binding.nameEt.setText(productsModel.getName());
            String formattedPrice;

            // Format and display either the offer price or the original price
            if (productsModel.getOfferPrice().isEmpty()){
                formattedPrice = df.format(Double.parseDouble(productsModel.getOriginalPrice()));
            } else {
                formattedPrice = df.format(Double.parseDouble(productsModel.getOfferPrice()));
            }
            binding.priceEt.setText("$" + formattedPrice);
            binding.quantityEt.setText(productsModel.getQuantity());
            binding.descriptionEt.setText(productsModel.getDescription());
            if (shopModel != null){
                binding.locationEt.setText(shopModel.getLocation());
            }

            // Load the product image using Glide
            Glide.with(this)
                    .asBitmap()
                    .load(Uri.parse(productsModel.getImageUri()))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Set the Bitmap as the image resource.
                            binding.ivImage.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Called when the Drawable is cleared, for example, when the view is recycled.
                        }
                    });

            // Set up click listeners for the "Update" and "Delete" buttons
            binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Start the AddProductActivity for editing the product
                    Intent intent = new Intent(ProductDetailsActivity.this, AddProductActivity.class);
                    intent.putExtra("checkFrom", "Edit");
                    intent.putExtra("data", productsModel);
                    startActivity(intent);
                    finish();
                }
            });

            binding.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Delete the product from the database and show a toast message
                    databaseHelper.deleteProduct(productsModel.getId());
                    Toast.makeText(ProductDetailsActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }

    // Validate the product name input
    private void validateInput(String input) {
        if (!isValidProductName(input)) {
            // If the input is invalid, clear the product name EditText
            binding.nameEt.setText("");
        }
    }

    // Check if the product name is valid
    private boolean isValidProductName(String productName) {
        // Add your validation logic here
        // For example, check if the product name contains only alphabets, numbers, and underscores
        return productName.matches("[a-zA-Z][a-zA-Z0-9_\\s]*");
    }
}
