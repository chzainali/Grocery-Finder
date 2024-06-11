package edu.niu.android.instagroc.admin.store;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.ActivityAddShopBinding;
import edu.niu.android.instagroc.model.ShopModel;

public class AddShopActivity extends AppCompatActivity {
    ActivityAddShopBinding binding;
    String name, location, imageUri = "";
    DatabaseHelper databaseHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using view binding
        binding = ActivityAddShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Set the label for the top text view
        binding.rlTop.tvLabel.setText("Add Store");

        // Show the back button in the top bar and set its click listener to finish the activity
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());

        // Set up the click listener for selecting an image
        binding.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the document gallery to pick an image
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, 100);
            }
        });

        // Set up the click listener for the "Add" button
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the input is validated before adding the shop
                if (isValidated()){
                    // Create a ShopModel with the provided name and imageUri
                    ShopModel shopModel = new ShopModel(name, location, imageUri);

                    // Insert the shop into the database
                    databaseHelper.insertShop(shopModel);

                    // Show a success message
                    showMessage("Successfully Added");

                    // Finish the activity
                    finish();
                }
            }
        });

        // Set up a TextWatcher for the name EditText to validate input
        binding.nameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not used in this example
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Validate the input when the text changes
                if (!charSequence.toString().isEmpty()){
                    validateInput(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used in this example
            }
        });
    }
    // Validate the input based on the specified criteria
    private void validateInput(String input) {
        if (!isValidProductName(input)) {
            // Clear the name EditText if the input is not valid
            binding.nameEt.setText("");
        }
    }

    // Validate the product name using a regular expression
    private boolean isValidProductName(String productName) {
        // Product name can contain alphabets, numbers, underscores, and special characters
        return productName.matches("[a-zA-Z0-9_\\s\\p{Punct}]*");
    }


    // Validate the input for name and imageUri
    private Boolean isValidated(){
        name = binding.nameEt.getText().toString().trim();
        location = binding.locationEt.getText().toString().trim();

        if (imageUri.isEmpty()){
            // Show a message if the image is not selected
            showMessage("Please pick an image");
            return false;
        }

        if (name.isEmpty()){
            // Show a message if the name is not entered
            showMessage("Please enter a name");
            return false;
        }

        if (location.isEmpty()){
            // Show a message if the location is not entered
            showMessage("Please enter location");
            return false;
        }

        return true;
    }

    // Show a toast message
    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Handle the result from selecting an image
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            // Get the selected image URI and set it to the ImageView
            imageUri = data.getData().toString();
            binding.ivImage.setImageURI(data.getData());
        }
    }
}