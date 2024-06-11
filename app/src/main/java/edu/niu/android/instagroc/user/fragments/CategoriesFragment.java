package edu.niu.android.instagroc.user.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.adapter.CategoriesAdapter;
import edu.niu.android.instagroc.adapter.ProductsAdapter;
import edu.niu.android.instagroc.adapter.UserCategoriesAdapter;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.FragmentCartBinding;
import edu.niu.android.instagroc.databinding.FragmentCategoriesBinding;
import edu.niu.android.instagroc.databinding.FragmentHomeBinding;
import edu.niu.android.instagroc.model.CategoryModel;
import edu.niu.android.instagroc.model.ShopModel;

public class CategoriesFragment extends Fragment {
    FragmentCategoriesBinding binding;
    UserCategoriesAdapter categoriesAdapter;
    DatabaseHelper databaseHelper;
    List<CategoryModel> list = new ArrayList<>();
    ShopModel shopModel;
    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopModel = (ShopModel) getArguments().getSerializable("data");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCategoriesBinding.inflate(getLayoutInflater());
        // Set the label based on the categoryModel
        if (shopModel != null) {
            binding.rlTop.tvLabel.setText(shopModel.getName() + " Categories");
        }

        // Initialize DatabaseHelper and set up the UI components
        databaseHelper = new DatabaseHelper(requireContext());
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();

        // Clear existing list and fetch products by category
        list.clear();
        if (shopModel != null) {
            list.addAll(databaseHelper.getAllCategories(shopModel.getId()));
        }

        // Set up the RecyclerView for displaying products
        if (list.size() > 0) {
            binding.noDataFound.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.VISIBLE);
            binding.rvGoals.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            categoriesAdapter = new UserCategoriesAdapter(list, requireContext(), databaseHelper);
            binding.rvGoals.setAdapter(categoriesAdapter);
            categoriesAdapter.notifyDataSetChanged();
        } else {
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.rvGoals.setVisibility(View.GONE);
        }
    }

}