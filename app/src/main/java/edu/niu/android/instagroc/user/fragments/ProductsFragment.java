package edu.niu.android.instagroc.user.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.niu.android.instagroc.adapter.ProductsAdapter;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.FragmentProductsBinding;
import edu.niu.android.instagroc.model.CategoryModel;
import edu.niu.android.instagroc.model.ProductsModel;

public class ProductsFragment extends Fragment {
    FragmentProductsBinding binding;
    CategoryModel categoryModel;
    ProductsAdapter productsAdapter;
    DatabaseHelper databaseHelper;
    List<ProductsModel> list = new ArrayList<>();

    public ProductsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryModel = (CategoryModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the label based on the categoryModel
        if (categoryModel != null) {
            binding.rlTop.tvLabel.setText(categoryModel.getName() + " Products");
        }

        // Initialize DatabaseHelper and set up the UI components
        databaseHelper = new DatabaseHelper(requireContext());
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        // Set up the SearchView listener for filtering products
        binding.searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    productsAdapter.setList(list);
                } else {
                    filter(newText);
                }
                return false;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();

        // Clear existing list and fetch products by category
        list.clear();
        if (categoryModel != null) {
            list.addAll(databaseHelper.getProductsByCategory(categoryModel.getId()));
        }

        // Set up the RecyclerView for displaying products
        if (list.size() > 0) {
            binding.noDataFound.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.VISIBLE);
            binding.rvGoals.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            productsAdapter = new ProductsAdapter(list, requireContext(), "user", databaseHelper);
            binding.rvGoals.setAdapter(productsAdapter);
            productsAdapter.notifyDataSetChanged();
        } else {
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.rvGoals.setVisibility(View.GONE);
        }
    }

    // Filter products based on the provided text
    public void filter(String text) {
        List<ProductsModel> filteredList = new ArrayList<>();
        for (ProductsModel model : list) {
            if (model.getName().toLowerCase(Locale.ROOT).contains(text.toLowerCase())) {
                filteredList.add(model);
            }
        }
        productsAdapter.setList(filteredList);
    }
}
