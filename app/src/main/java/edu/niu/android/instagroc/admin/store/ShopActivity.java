package edu.niu.android.instagroc.admin.store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.adapter.ShopAdapter;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.ActivityShopBinding;
import edu.niu.android.instagroc.model.ShopModel;

public class ShopActivity extends AppCompatActivity {
    ActivityShopBinding binding;
    ShopAdapter shopAdapter;
    DatabaseHelper databaseHelper;
    List<ShopModel> list = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using view binding
        binding = ActivityShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Set the label for the top text view
        binding.rlTop.tvLabel.setText("Stores");

        // Show the back button in the top bar and set its click listener to finish the activity
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());

        // Set up the click listener for adding a new category
        binding.rlAdd.setOnClickListener(view -> {
            startActivity(new Intent(ShopActivity.this, AddShopActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();

        // Clear the existing list and populate it with all categories from the database
        list.clear();
        list.addAll(databaseHelper.getAllShops());

        // Check if there are categories to display
        if (list.size() > 0) {
            // If categories are available, set up the RecyclerView and adapter
            binding.noDataFound.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.VISIBLE);
            binding.lineView.setVisibility(View.VISIBLE);
            binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
            shopAdapter = new ShopAdapter(list, this, databaseHelper, "admin");
            binding.rvGoals.setAdapter(shopAdapter);
            shopAdapter.notifyDataSetChanged();
        } else {
            // If no categories are available, show appropriate views
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.lineView.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.GONE);
        }
    }
}