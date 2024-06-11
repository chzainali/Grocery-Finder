package edu.niu.android.instagroc.user.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.auth.LoginActivity;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.FragmentProfileBinding;
import edu.niu.android.instagroc.model.HelperClass;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    DatabaseHelper databaseHelper;

    public ProfileFragment() {
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
        binding = FragmentProfileBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set the label for the top bar
        binding.rlTop.tvLabel.setText("Profile");

        // Set user details
        binding.tvName.setText(HelperClass.users.getUserName());
        String formatNumber = formatPhoneNumber(HelperClass.users.getPhone());
        binding.tvPhone.setText(formatNumber);
        binding.tvEmail.setText(HelperClass.users.getEmail());

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(requireContext());

        // Set up click listener for changing password
        binding.tvPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        // Set up click listener for logout button
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Logout and navigate to LoginActivity
                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void showDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_forgot_dialog);

        TextInputEditText passwordEt = dialog.findViewById(R.id.passwordEt);
        TextInputEditText newPasswordEt = dialog.findViewById(R.id.newPasswordEt);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                // Handle the password reset logic
                String oldPassword = passwordEt.getText().toString();
                String newPassword = newPasswordEt.getText().toString();

                if (oldPassword.isEmpty()) {
                    showMessage("Please enter old password");
                } else if (newPassword.isEmpty()) {
                    showMessage("Please enter new password");
                } else if (!oldPassword.contentEquals(HelperClass.users.getPassword())) {
                    showMessage("Old password is wrong");
                } else {
                    // Update the password in the database
                    databaseHelper.updatePassword(HelperClass.users.getId(), newPassword);
                    showMessage("Password Reset Successfully");
                    dialog.dismiss();

                    // Logout the user and navigate to LoginActivity
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                }
            }
        });

        dialog.show();
    }

    private String formatPhoneNumber(String phone) {
        // Format the phone number to (XXX) XXX-XXXX
        String numericPhone = phone.replaceAll("[^0-9]", "");

        if (numericPhone.length() == 10) {
            return String.format("(%s) %s-%s",
                    numericPhone.substring(0, 3),
                    numericPhone.substring(3, 6),
                    numericPhone.substring(6));
        } else {
            return phone;
        }
    }

    private void showMessage(String message){
        // Display a toast message
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
