package edu.niu.android.instagroc.admin.products;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import java.text.DecimalFormat;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.ActivityAddProductBinding;
import edu.niu.android.instagroc.model.CategoryModel;
import edu.niu.android.instagroc.model.ProductsModel;
import edu.niu.android.instagroc.model.ShopModel;

public class AddProductActivity extends AppCompatActivity {
    ActivityAddProductBinding binding;
    String name, price, offerPrice, quantity, description, imageUri = "";
    DatabaseHelper databaseHelper;
    CategoryModel categoryModel;
    ProductsModel updateModel;
    String checkFrom;
    DecimalFormat df;
    int shopId = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));
        df = new DecimalFormat("0.00");

        // Check if there are extras in the intent
        if (getIntent().getExtras() != null) {
            // Retrieve the checkFrom value from the intent extras
            checkFrom = getIntent().getStringExtra("checkFrom");

            // Check if the operation is to add a new product
            if (checkFrom.contentEquals("Add")) {
                // Retrieve the CategoryModel from the intent extras
                categoryModel = (CategoryModel) getIntent().getSerializableExtra("data");
                shopId = getIntent().getIntExtra("shopId", 0);
                if (categoryModel != null) {
                    // Set the label for the top text view based on the category name
                    binding.rlTop.tvLabel.setText("Add " + categoryModel.getName() + " Product");
                }
            } else {
                // The operation is to update an existing product
                updateModel = (ProductsModel) getIntent().getSerializableExtra("data");

                // Set the label for the top text view to indicate updating a product
                binding.rlTop.tvLabel.setText("Update Product");

                // Set the values in the UI to the existing product details
                binding.nameEt.setText(updateModel.getName());
                String formattedPrice1 = df.format(Double.parseDouble(updateModel.getOriginalPrice()));
                binding.priceEt.setText(formattedPrice1);
                if (!updateModel.getOfferPrice().isEmpty()){
                    String formattedPrice2 = df.format(Double.parseDouble(updateModel.getOfferPrice()));
                    binding.offerPriceEt.setText(formattedPrice2);
                }
                binding.quantityEt.setText(updateModel.getQuantity());
                binding.descriptionEt.setText(updateModel.getDescription());

                // Load the product image using Glide
                Glide.with(binding.ivImage)
                        .asBitmap()
                        .load(Uri.parse(updateModel.getImageUri()))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                // Set the Bitmap as the image resource
                                binding.ivImage.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // Called when the Drawable is cleared, for example, when the view is recycled
                            }
                        });

                // Set the imageUri to the existing product's imageUri
                imageUri = updateModel.getImageUri();

                // Change the button text to "Update"
                binding.btnAdd.setText("Update");
            }
        }

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Show the back button in the top bar and set its click listener to finish the activity
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());

        // Set up the click listener for selecting an image
        binding.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the image picker
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });

        // Set up the text change listener for the product name
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

        // Set up the click listener for the "Add" or "Update" button
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate user input and proceed with adding or updating the product
                if (isValidated()) {
                    offerPrice = binding.offerPriceEt.getText().toString();

                    // Check if offer price is empty and set it to an empty string
                    if (offerPrice.isEmpty()) {
                        offerPrice = "";
                    }

                    // Check if the operation is to add a new product
                    if (categoryModel != null) {
                        // Create a new ProductsModel object and insert it into the database
                        ProductsModel productsModel = new ProductsModel(shopId, categoryModel.getId(), name, price, offerPrice, quantity, description, imageUri);
                        databaseHelper.insertProduct(productsModel);
                        showMessage("Successfully Added");
                    } else {
                        // The operation is to update an existing product
                        ProductsModel productsModel = new ProductsModel(updateModel.getId(), updateModel.getShopId(),updateModel.getCategoryId(), name, price, offerPrice, quantity, description, imageUri);
                        databaseHelper.updateProduct(productsModel);
                        showMessage("Updated Successfully");
                    }

                    // Finish the activity
                    finish();
                }
            }
        });
    }

    // Validate user input for adding or updating a product
    private Boolean isValidated() {
        name = binding.nameEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        offerPrice = binding.offerPriceEt.getText().toString().trim();
        quantity = binding.quantityEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();

        // Check if an image is selected
        if (imageUri.isEmpty()) {
            showMessage("Please pick an image");
            return false;
        }

        // Check if the product name is empty
        if (name.isEmpty()) {
            showMessage("Please enter a name");
            return false;
        }

        // Check if the price is empty
        if (price == null || price.isEmpty()) {
            showMessage("Please enter a price");
            return false;
        }

        // Check if the offer price is provided and less than the regular price
        if (offerPrice != null && !offerPrice.isEmpty()) {
            try {
                double regularPriceValue = Double.parseDouble(price);
                double offerPriceValue = Double.parseDouble(offerPrice);

                // Offer price should be less than the original price
                if (offerPriceValue >= regularPriceValue) {
                    showMessage("Offer price should be less than the original price");
                    return false;
                }
            } catch (NumberFormatException e) {
                // Handle the case where price or offerPrice is not a valid double
                e.printStackTrace();
                showMessage("Invalid price or offer price");
                return false;
            }
        }

        // Check if the quantity is empty
        if (quantity.isEmpty()) {
            showMessage("Please enter a quantity");
            return false;
        }

        // Check if the quantity is greater than 0
        if (quantity.contentEquals("0")) {
            showMessage("Please enter a quantity greater than 0");
            return false;
        }

        // Check if the description is empty
        if (description.isEmpty()) {
            showMessage("Please enter a description");
            return false;
        }

        // Validation successful
        return true;
    }

    // Show a toast message
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Handle the result of selecting an image from the gallery
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            // Set the selected image URI to the ImageView
            imageUri = data.getData().toString();
            binding.ivImage.setImageURI(data.getData());
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
