package edu.niu.android.instagroc.user.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.niu.android.instagroc.adapter.OrderAdapter;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.FragmentOrdersBinding;
import edu.niu.android.instagroc.model.HelperClass;
import edu.niu.android.instagroc.model.OnClick;
import edu.niu.android.instagroc.model.OrderModel;

public class OrdersFragment extends Fragment {
    FragmentOrdersBinding binding;
    OrderAdapter orderAdapter;
    DatabaseHelper databaseHelper;
    List<OrderModel> list = new ArrayList<>();

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Handle any arguments if needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrdersBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(requireContext());
        // Set the label for the top bar
        binding.rlTop.tvLabel.setText("Orders");
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();

        // Clear the existing list and fetch order data from the database
        list.clear();
        list.addAll(databaseHelper.getOrderData(HelperClass.users.getId()));

        if (list.size() > 0) {
            // If there are orders, hide the "No Data Found" message and show the RecyclerView
            binding.noDataFound.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.VISIBLE);

            // Set up RecyclerView with LinearLayoutManager
            binding.rvGoals.setLayoutManager(new LinearLayoutManager(requireContext()));

            // Create and set the OrderAdapter
            orderAdapter = new OrderAdapter(list, requireContext(), databaseHelper, new OnClick() {
                @Override
                public void clicked(int pos) {
                    // Handle click on an order item (optional)
                    OrderModel orderModel = list.get(pos);
                    databaseHelper.deleteOrder(orderModel.getId());
                    list.remove(pos);
                    orderAdapter.notifyDataSetChanged();
                }
            });
            binding.rvGoals.setAdapter(orderAdapter);
            orderAdapter.notifyDataSetChanged();
        } else {
            // If no orders, show the "No Data Found" message and hide the RecyclerView
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.rvGoals.setVisibility(View.GONE);
        }
    }
}
