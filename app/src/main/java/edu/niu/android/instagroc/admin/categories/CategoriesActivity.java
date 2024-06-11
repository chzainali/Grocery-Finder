package edu.niu.android.instagroc.admin.categories;
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
import edu.niu.android.instagroc.adapter.CategoriesAdapter;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.ActivityCategoriesBinding;
import edu.niu.android.instagroc.model.CategoryModel;
import edu.niu.android.instagroc.model.ShopModel;

public class CategoriesActivity extends AppCompatActivity {
    ActivityCategoriesBinding binding;
    CategoriesAdapter categoriesAdapter;
    DatabaseHelper databaseHelper;
    List<CategoryModel> list = new ArrayList<>();
    ShopModel shopModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Check if there are extras in the intent
        if (getIntent().getExtras() != null) {
            // Retrieve the CategoryModel from the intent extras
            shopModel = (ShopModel) getIntent().getSerializableExtra("data");
            if (shopModel != null) {
                // Set the label for the top text view using the category name
                binding.rlTop.tvLabel.setText(shopModel.getName() + " Categories");
            }
        }

        // Show the back button in the top bar and set its click listener to finish the activity
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());

        // Set up the click listener for adding a new category
        binding.rlAdd.setOnClickListener(view -> {
            Intent intent = new Intent(CategoriesActivity.this, AddCategoryActivity.class);
            intent.putExtra("data", shopModel);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();

        // Clear the existing list and populate it with all categories from the database
        list.clear();
        list.addAll(databaseHelper.getAllCategories(shopModel.getId()));

        // Check if there are categories to display
        if (list.size() > 0) {
            // If categories are available, set up the RecyclerView and adapter
            binding.noDataFound.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.VISIBLE);
            binding.lineView.setVisibility(View.VISIBLE);
            binding.rvGoals.setLayoutManager(new LinearLayoutManager(this));
            categoriesAdapter = new CategoriesAdapter(list, this, databaseHelper, "admin");
            binding.rvGoals.setAdapter(categoriesAdapter);
            categoriesAdapter.notifyDataSetChanged();
        } else {
            // If no categories are available, show appropriate views
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.lineView.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.GONE);
        }
    }
}
