package edu.niu.android.instagroc.user.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import edu.niu.android.instagroc.adapter.ProductsAdapter;
import edu.niu.android.instagroc.adapter.ShopAdapter;
import edu.niu.android.instagroc.adapter.UserCategoriesAdapter;
import edu.niu.android.instagroc.adapter.UserShopAdapter;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.FragmentHomeBinding;
import edu.niu.android.instagroc.model.CategoryModel;
import edu.niu.android.instagroc.model.ProductsModel;
import edu.niu.android.instagroc.model.ShopModel;
import edu.niu.android.instagroc.user.NearestShopsActivity;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    UserShopAdapter shopAdapter;
    ProductsAdapter productsAdapter;
    DatabaseHelper databaseHelper;
    List<ShopModel> list = new ArrayList<>();
    List<ProductsModel> listProducts = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle fragment arguments if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize DatabaseHelper and set the label
        databaseHelper = new DatabaseHelper(requireContext());
        binding.rlTop.tvLabel.setText("Home");

        binding.cvNearestShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), NearestShopsActivity.class));
            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();

        // Clear existing lists and fetch categories and products with offers
        list.clear();
        listProducts.clear();
        list.addAll(databaseHelper.getAllShops());
        listProducts.addAll(databaseHelper.getAllProductsWithOffer());

        // Set up the RecyclerView for available offers
        if (listProducts.size() > 0) {
            binding.tvAvailableOffers.setVisibility(View.VISIBLE);
            binding.rvOffers.setVisibility(View.VISIBLE);
            binding.rvOffers.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            productsAdapter = new ProductsAdapter(listProducts, requireContext(), "userHome", databaseHelper);
            binding.rvOffers.setAdapter(productsAdapter);
            productsAdapter.notifyDataSetChanged();
        } else {
            binding.tvAvailableOffers.setVisibility(View.GONE);
            binding.rvOffers.setVisibility(View.GONE);
        }

        // Set up the RecyclerView for categories
        if (list.size() > 0) {
            binding.noDataFound.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.VISIBLE);
            binding.rvGoals.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            shopAdapter = new UserShopAdapter(list, requireContext(), databaseHelper);
            binding.rvGoals.setAdapter(shopAdapter);
            shopAdapter.notifyDataSetChanged();
        } else {
            // Display appropriate views when there are no categories or offers
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.rvGoals.setVisibility(View.GONE);
            binding.tvAvailableOffers.setVisibility(View.GONE);
            binding.tvCategories.setVisibility(View.GONE);
            binding.rvOffers.setVisibility(View.GONE);
        }
    }
}
