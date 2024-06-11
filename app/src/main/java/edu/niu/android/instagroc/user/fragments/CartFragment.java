package edu.niu.android.instagroc.user.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.adapter.CartAdapter;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.FragmentCartBinding;
import edu.niu.android.instagroc.model.CartModel;
import edu.niu.android.instagroc.model.HelperClass;
import edu.niu.android.instagroc.model.OnClick;
import edu.niu.android.instagroc.model.OrderModel;
import edu.niu.android.instagroc.model.ProductsModel;

public class CartFragment extends Fragment {
    FragmentCartBinding binding;
    CartAdapter cartAdapter;
    String currentDateAndTime;
    DatabaseHelper databaseHelper;
    List<CartModel> list = new ArrayList<>();
    double totalAmount = 0;
    TextInputEditText expiryDateEt;
    java.text.DecimalFormat df;
    String details = "";

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check and request SMS permission if not granted
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.SEND_SMS}, 100);
        }

        databaseHelper = new DatabaseHelper(requireContext());
        binding.rlTop.tvLabel.setText("Cart");
        df = new java.text.DecimalFormat("0.00");

        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check SMS permission and show dialog if granted
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    showDialog();
                } else{
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.SEND_SMS}, 123);
                }
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void showDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_checkout_dialog);

        TextInputEditText priceEt = dialog.findViewById(R.id.priceEt);
        TextInputEditText nameEt = dialog.findViewById(R.id.nameEt);
        TextInputEditText cardNumberEt = dialog.findViewById(R.id.cardNumberEt);
        expiryDateEt = dialog.findViewById(R.id.expiryDateEt);
        TextInputEditText cvcEt = dialog.findViewById(R.id.cvcEt);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        String amount = df.format(totalAmount);
        priceEt.setText("$" + amount);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Set up text change listeners for validation
        cardNumberEt.addTextChangedListener(new CardNumberTextWatcher(cardNumberEt));
        expiryDateEt.addTextChangedListener(new ExpiryDateTextWatcher(expiryDateEt));

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
                String cardNumber = cardNumberEt.getText().toString();
                String expiryDate = expiryDateEt.getText().toString();
                String cvc = cvcEt.getText().toString();

                getCurrentDateAndTime();
                if (name.isEmpty() || !validateCardNumber(cardNumber) || expiryDate.length()<5 || !validateCvc(cvc)) {
                    showMessage("Please enter valid card details");
                } else {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            OrderModel orderModel = new OrderModel(
                                    HelperClass.users.getId(),
                                    list.get(i).getCategoryId(),
                                    list.get(i).getProductId(),
                                    list.get(i).getName(),
                                    list.get(i).getPrice(),
                                    currentDateAndTime,
                                    list.get(i).getQuantity(),
                                    list.get(i).getDescription(),
                                    list.get(i).getImageUri()
                            );

                            // Insert the item into the order table
                            databaseHelper.insertOrder(orderModel);
                            details = details +"\n"+ list.get(i).getQuantity() + " Items of "+list.get(i).getName();
                            // Delete the item from the cart table
                            databaseHelper.deleteCartData(list.get(i).getId());

                            // Remove the item from the list
                            list.remove(i);

                            // Notify the adapter of the change
                            cartAdapter.notifyDataSetChanged();
                            // Adjust the loop counter after removing an item
                            i--;
                        }
                    }

                    SmsManager smsManager = SmsManager.getDefault();
                    String amount = df.format(totalAmount);
                    smsManager.sendTextMessage(HelperClass.users.getPhone(), null, "Your Order has been Placed of $"+amount+"\n"+details, null, null);
                    binding.tvTotalAmount.setVisibility(View.GONE);
                    binding.btnCheckout.setVisibility(View.GONE);
                    binding.noDataFound.setVisibility(View.VISIBLE);
                    showMessage("Order Placed Successfully");
                    dialog.dismiss();

                }
            }
        });

        dialog.show();
    }

    private boolean validateCardNumber(String cardNumber) {
        // Perform card number validation logic
        return !TextUtils.isEmpty(cardNumber) && cardNumber.length() == 19;
    }

    private boolean validateCvc(String cvc) {
        // Perform CVV validation logic
        return !TextUtils.isEmpty(cvc) && cvc.length() == 3;
    }

    private class CardNumberTextWatcher implements TextWatcher {
        private TextInputEditText editText;

        CardNumberTextWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            String formattedText = formatCardNumber(text);
            editText.removeTextChangedListener(this);
            editText.setText(formattedText);
            editText.setSelection(formattedText.length());
            editText.addTextChangedListener(this);
        }
    }

    private String formatCardNumber(String cardNumber) {
        // Remove any non-digit characters
        String cleanedNumber = cardNumber.replaceAll("\\D", "");

        // Insert space after every 4 digits
        StringBuilder formattedNumber = new StringBuilder();
        for (int i = 0; i < cleanedNumber.length(); i++) {
            formattedNumber.append(cleanedNumber.charAt(i));
            if ((i + 1) % 4 == 0 && i + 1 < cleanedNumber.length()) {
                formattedNumber.append(" ");
            }
        }
        return formattedNumber.toString();
    }

    private class ExpiryDateTextWatcher implements TextWatcher {
        private TextInputEditText editText;

        ExpiryDateTextWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // You can leave this method empty or add any specific logic you need after text changes
            String text = editable.toString();
            String formattedText = formatExpiryDate(text);
            editText.removeTextChangedListener(this);
            editText.setText(formattedText);
            editText.setSelection(formattedText.length());
            editText.addTextChangedListener(this);
        }
    }

    private String formatExpiryDate(String expiryDate) {
        // Remove any non-digit characters
        String cleanedDate = expiryDate.replaceAll("\\D", "");

        // Check if the cleaned string has a length of 4 and follows the desired format (MM/YY)
        if (!TextUtils.isEmpty(cleanedDate) && cleanedDate.length() == 4 && cleanedDate.matches("^\\d{4}$")) {
            // Extract month and year from the cleaned string
            int month = Integer.parseInt(cleanedDate.substring(0, 2));
            int year = Integer.parseInt(cleanedDate.substring(2));

            // Get the current month and last 2 digits of the current year
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
            int last2DigitsOfYear = calendar.get(java.util.Calendar.YEAR) % 100;

            // Perform validations
            if (month < 1 || month > 12) {
                showMessage("Please enter a valid month (01-12)");
                cleanedDate = "";
            } else if (year < last2DigitsOfYear || (year == last2DigitsOfYear && month < currentMonth)) {
                showMessage("Please enter a valid expiry date (future date)");
                cleanedDate = "";
            } else {
                // Insert a "/" after the first two digits (month)
                return cleanedDate.substring(0, 2) + "/" + cleanedDate.substring(2);
            }
        }

        // If validations fail, return the original cleaned date
        return cleanedDate;
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onResume() {
        super.onResume();

        list.clear();
        list.addAll(databaseHelper.getCartData(HelperClass.users.getId()));
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                totalAmount = totalAmount + Double.parseDouble(list.get(i).getPrice());
            }
            String formattedPrice = df.format(totalAmount);
            binding.tvTotalAmount.setText("Total Amount: $" + formattedPrice);
            binding.noDataFound.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.VISIBLE);
            binding.tvTotalAmount.setVisibility(View.VISIBLE);
            binding.btnCheckout.setVisibility(View.VISIBLE);
            binding.rvGoals.setLayoutManager(new LinearLayoutManager(requireContext()));
            cartAdapter = new CartAdapter(list, requireContext(), databaseHelper, new OnClick() {
                @Override
                public void clicked(int pos) {
                    CartModel cartModel = list.get(pos);
                    ProductsModel productsModel = databaseHelper.getProductByIdAndCategory(cartModel.getProductId(), cartModel.getCategoryId());
                    if (productsModel.getName() != null){
                        int updatedQuantity = Integer.parseInt(productsModel.getQuantity()) + Integer.parseInt(cartModel.getQuantity());
                        productsModel.setQuantity(String.valueOf(updatedQuantity));
                        databaseHelper.updateProduct(productsModel);
                    }
                    databaseHelper.deleteCartData(cartModel.getId());
                    list.remove(pos);
                    cartAdapter.notifyDataSetChanged();
                    list.clear();
                    totalAmount = 0;
                    list.addAll(databaseHelper.getCartData(HelperClass.users.getId()));
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            totalAmount = totalAmount + Double.parseDouble(list.get(i).getPrice());
                        }
                        String formattedPrice = df.format(totalAmount);
                        binding.tvTotalAmount.setText("Total Amount: $" + formattedPrice);
                    } else {
                        binding.noDataFound.setVisibility(View.VISIBLE);
                        binding.tvTotalAmount.setVisibility(View.GONE);
                        binding.rvGoals.setVisibility(View.GONE);
                        binding.btnCheckout.setVisibility(View.GONE);
                    }
                }
            });
            binding.rvGoals.setAdapter(cartAdapter);
            cartAdapter.notifyDataSetChanged();
        } else {
            binding.noDataFound.setVisibility(View.VISIBLE);
            binding.tvTotalAmount.setVisibility(View.GONE);
            binding.rvGoals.setVisibility(View.GONE);
            binding.btnCheckout.setVisibility(View.GONE);
        }
    }

    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void getCurrentDateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss aa", Locale.getDefault());
        currentDateAndTime = sdf.format(new Date());
    }
}
