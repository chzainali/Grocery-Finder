package edu.niu.android.instagroc.auth;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.admin.AdminMainActivity;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.ActivityLoginBinding;
import edu.niu.android.instagroc.model.HelperClass;
import edu.niu.android.instagroc.model.UsersModel;
import edu.niu.android.instagroc.user.MainActivity;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    String adminEmail = "admin@gmail.com";
    String adminPassword = "admin@123";
    String email, password;
    DatabaseHelper databaseHelper;
    Boolean checkDetails = false;
    List<UsersModel> list = new ArrayList<>();
    int request_code = 123;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Check and request storage permission if needed
        if (!fnCheckReadStoragePermission()) {
            fnRequestStoragePermission(request_code);
        }

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Set the label for the top text view
        binding.rlTop.tvLabel.setText("Login");

        // Set a click listener to navigate to the RegisterActivity when the register layout is clicked
        binding.llRegister.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        // Set a click listener for the login button
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate user input
                if (isValidated()) {
                    // Check if the user is the admin
                    if (email.contentEquals(adminEmail) && password.contentEquals(adminPassword)) {
                        // Start the AdminMainActivity for the admin user
                        startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                        finish();
                    } else {
                        // Retrieve all users from the database
                        list = databaseHelper.getAllUsers();
                        for (UsersModel users : list) {
                            // Check if the entered email and password match any user
                            if (email.equals(users.getEmail()) && password.equals(users.getPassword())) {
                                checkDetails = true;
                                showMessage("Successfully Login");
                                // Set the logged-in user in the HelperClass
                                HelperClass.users = users;
                                // Start the MainActivity for the regular user
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                                break;
                            }
                        }
                        // Show a message if the credentials are incorrect
                        if (!checkDetails) {
                            showMessage("Wrong Credentials...\nPlease check email or password");
                        }
                    }
                }
            }
        });
    }

    // Validate user input for email and password
    private Boolean isValidated() {
        email = binding.emailEt.getText().toString().trim();
        password = binding.passET.getText().toString().trim();

        if (email.isEmpty()) {
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }
        if (password.isEmpty()) {
            showMessage("Please enter password");
            return false;
        }

        return true;
    }

    // Show a toast message
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Reset email and password fields when the activity resumes
    @Override
    protected void onResume() {
        super.onResume();

        binding.emailEt.setText("");
        binding.passET.setText("");
    }

    // Request storage permission using the appropriate method based on Android version
    public void fnRequestStoragePermission(int resultCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, resultCode);
        }
    }

    // Check if read storage permission is granted
    public boolean fnCheckReadStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }
}
