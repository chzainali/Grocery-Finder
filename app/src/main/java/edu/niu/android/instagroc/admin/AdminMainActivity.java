package edu.niu.android.instagroc.admin;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.admin.store.ShopActivity;
import edu.niu.android.instagroc.auth.LoginActivity;
import edu.niu.android.instagroc.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends AppCompatActivity {
    ActivityAdminMainBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Set the label for the top text view
        binding.rlTop.tvLabel.setText("Admin Dashboard");

        // Handle click event for managing products
        binding.cvManageProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start CategoriesActivity when the "Manage Products" card view is clicked
                startActivity(new Intent(AdminMainActivity.this, ShopActivity.class));
            }
        });

        // Handle click event for logging out
        binding.cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to start LoginActivity
                Intent intent = new Intent(AdminMainActivity.this, LoginActivity.class);

                // Set flags to clear the task stack and start LoginActivity as a new task
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Start LoginActivity and finish the current activity
                startActivity(intent);
                finish();
            }
        });
    }
}
