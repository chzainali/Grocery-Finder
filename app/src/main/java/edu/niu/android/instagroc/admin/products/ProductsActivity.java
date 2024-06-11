package edu.niu.android.instagroc.admin.products;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.adapter.ProductsAdapter;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.ActivityProductsBinding;
import edu.niu.android.instagroc.model.CategoryModel;
import edu.niu.android.instagroc.model.ProductsModel;

public class ProductsActivity extends AppCompatActivity {
    ActivityProductsBinding binding;
    CategoryModel categoryModel;
    ProductsAdapter productsAdapter;
    DatabaseHelper databaseHelper;
    List<ProductsModel> list = new ArrayList<>();
    int shopId = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Check if there are extras in the intent
        if (getIntent().getExtras() != null) {
            // Retrieve the CategoryModel from the intent extras
            categoryModel = (CategoryModel) getIntent().getSerializableExtra("data");
            shopId = getIntent().getIntExtra("shopId", 0);
            if (categoryModel != null) {
                // Set the label for the top text view using the category name
                binding.rlTop.tvLabel.setText(categoryModel.getName() + " Products");
            }
        }

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Show the back button in the top bar and set its click listener to finish the activity
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());

        // Set up the click listener for adding a new product
        binding.rlAdd.setOnClickListener(view -> {
            Intent intent = new Intent(ProductsActivity.this, AddProductActivity.class);
            intent.putExtra("checkFrom", "Add");
            intent.putExtra("data", categoryModel);
            intent.putExtra("shopId", shopId);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();

        // Clear the existing product list and populate it with products from the selected category
        list.clear();
        if (categoryModel != null) {
            list.addAll(databaseHelper.getProductsByCategory(categoryModel.getId()));
        }

        // Check if there are products to display
        if (list.size() > 0) {
            // If products are available, set up the RecyclerView and adapter
            binding.noDataFound.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.VISIBLE);
            binding.rvGoals.setLayoutManager(new GridLayoutManager(this, 2));
            productsAdapter = new ProductsAdapter(list, this, "admin", databaseHelper);
            binding.rvGoals.setAdapter(productsAdapter);
            productsAdapter.notifyDataSetChanged();
        } else {
            // If no products are available, show appropriate views
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.rvGoals.setVisibility(View.GONE);
        }
    }
}
