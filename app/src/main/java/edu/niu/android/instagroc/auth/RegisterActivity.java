package edu.niu.android.instagroc.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.ActivityRegisterBinding;
import edu.niu.android.instagroc.model.UsersModel;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    String name, email, phone, password;
    DatabaseHelper databaseHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Set the label for the top text view
        binding.rlTop.tvLabel.setText("Register");

        // Set visibility and click listeners for the back button and bottom layout
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());
        binding.llBottom.setOnClickListener(v -> finish());

        // Set a click listener for the register button
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate user input and register if valid
                if (isValidated()){
                    UsersModel users = new UsersModel(name, email, phone, password);
                    databaseHelper.register(users);
                    showMessage("Successfully Registered");
                    finish();
                }
            }
        });

        // Set a text watcher for the name field to perform input validation
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
    }

    // Validate user input for registration
    private Boolean isValidated(){
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();
        password = binding.passET.getText().toString().trim();

        if (name.isEmpty()){
            showMessage("Please enter name");
            return false;
        }
        if (email.isEmpty()){
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }
        if (phone.isEmpty() || phone.length()<10){
            showMessage("Please enter correct phone number");
            return false;
        }
        if (password.isEmpty()){
            showMessage("Please enter password");
            return false;
        }

        return true;
    }

    // Show a toast message
    private void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Reset input fields when the activity resumes
    @Override
    protected void onResume() {
        super.onResume();

        binding.nameEt.setText("");
        binding.emailEt.setText("");
        binding.phoneEt.setText("");
        binding.passET.setText("");
    }

    // Perform input validation for the name field
    private void validateInput(String input) {
        if (!isValidProductName(input)) {
            binding.nameEt.setText("");
        }
    }

    // Validate product name (example validation logic)
    private boolean isValidProductName(String productName) {
        // Add your validation logic here
        // For example, check if the product name contains only alphabets, numbers, and underscores
        return productName.matches("[a-zA-Z][a-zA-Z0-9_\\s]*");
    }
}
