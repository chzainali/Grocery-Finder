package edu.niu.android.instagroc.user.fragments;

import static edu.niu.android.instagroc.user.NearestShopsActivity.LOCATION_PERMISSION_REQUEST_CODE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.FragmentProductDetailsBinding;
import edu.niu.android.instagroc.model.CartModel;
import edu.niu.android.instagroc.model.HelperClass;
import edu.niu.android.instagroc.model.ProductsModel;
import edu.niu.android.instagroc.model.ShopModel;
import edu.niu.android.instagroc.user.MainActivity;

public class ProductDetailsFragment extends Fragment {
    FragmentProductDetailsBinding binding;
    ProductsModel productsModel;
    String currentDateAndTime;
    DatabaseHelper databaseHelper;
    List<CartModel> list = new ArrayList<>();
    CartModel updatedModel;
    TextInputEditText priceEt;
    TextInputEditText quantityEt;
    private double actualPrice;
    double totalPrice;
    java.text.DecimalFormat df;
    ShopModel shopModel;
    MainActivity activity;

    public ProductDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) requireActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productsModel = (ProductsModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        df = new java.text.DecimalFormat("0.00");
        binding.rlTop.tvLabel.setText("Product Details");
        databaseHelper = new DatabaseHelper(activity);
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivRight.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
        binding.rlTop.ivRight.setImageResource(R.drawable.share_icon);

        if (productsModel != null) {
            shopModel = databaseHelper.getShopById(productsModel.getShopId());
            binding.nameEt.setText(productsModel.getName());
            String formattedPrice;
            if (productsModel.getOfferPrice().isEmpty()) {
                formattedPrice = df.format(Double.parseDouble(productsModel.getOriginalPrice()));
            } else {
                formattedPrice = df.format(Double.parseDouble(productsModel.getOfferPrice()));
            }
            binding.priceEt.setText("$" + formattedPrice);

            binding.quantityEt.setText(productsModel.getQuantity());
            binding.descriptionEt.setText(productsModel.getDescription());
            if (shopModel != null){
                binding.locationEt.setText(shopModel.getLocation());
            }
            Glide.with(this)
                    .asBitmap()
                    .load(Uri.parse(productsModel.getImageUri()))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Now you have the Bitmap, you can set it as the image resource.
                            binding.ivImage.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // This is called when the Drawable is cleared, for example, when the view is recycled.
                        }
                    });
            if (productsModel.getQuantity().contentEquals("0")) {
                binding.btnCart.setVisibility(View.GONE);
            } else {
                binding.btnCart.setVisibility(View.VISIBLE);
            }
            binding.btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });

            binding.rlTop.ivRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LatLng latLng = getLocationFromAddress(activity, shopModel.getLocation());
                    Glide.with(binding.getRoot()).asBitmap().load(productsModel.getImageUri()).into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            // Now you have the Bitmap, you can set it as the image resource.

                            // Create a share intent
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("image/*"); // Set the MIME type for image
                            if (latLng != null){
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "Product Title: " + productsModel.getName() + "\n\nProduct Price: " + binding.priceEt.getText().toString() + "\n\nAvailable Quantity: " + productsModel.getQuantity()+ "\n\nProduct Description: " + productsModel.getDescription()+ "\n\nFind Shop Location on Map: " + "http://maps.google.com/?q="
                                        + latLng.latitude + ","
                                        + latLng.longitude);
                            }else{
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "Product Title: " + productsModel.getName() + "\n\nProduct Price: " + binding.priceEt.getText().toString() + "\n\nAvailable Quantity: " + productsModel.getQuantity()+ "\n\nProduct Description: " + productsModel.getDescription());
                            }

                            // Convert the Bitmap to a Uri
                            Uri imageUri = getImageUri(activity, resource);

                            // Add the image to the share intent
                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

                            // Check the platform and start the share activity
                            Intent chooserIntent = Intent.createChooser(shareIntent, "Share product via");
                            if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                startActivity(chooserIntent);
                            } else {
                                showMessage("No apps available for sharing");
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // This is called when the Drawable is cleared, for example, when the view is recycled.
                        }
                    });
                }

            });

        }

        binding.btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                }else{
                    getLocationAndOpenMap();
                }
            }
        });


    }

    public void openMap(double startLat, double startLng, double endLat, double endLng) {
        Uri uri = Uri.parse("http://maps.google.com/maps?saddr=" + startLat + "," + startLng + "&daddr=" + endLat + "," + endLng);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        Intent chooserIntent = Intent.createChooser(intent, "Select an application");
        startActivity(chooserIntent);
    }

    private void getLocationAndOpenMap() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double userLat = location.getLatitude();
                    double userLng = location.getLongitude();

                    // Get shop's location (latitude and longitude)
                    String shopAddress = shopModel.getLocation();
                    LatLng shopLatLng = getLocationFromAddress(activity, shopAddress);

                    if (shopLatLng != null) {
                        double shopLat = shopLatLng.latitude;
                        double shopLng = shopLatLng.longitude;

                        // Call openMap method with user and shop coordinates
                        openMap(userLat, userLng, shopLat, shopLng);
                    } else {
                        showMessage("Failed to get shop's location, Please try again ");
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(@NonNull String provider) {}

                @Override
                public void onProviderDisabled(@NonNull String provider) {}
            }, null);
        }
    }

    private LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList;
        LatLng latLng = null;

        try {
            addressList = geocoder.getFromLocationName(strAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            showMessage(e.getMessage());
            e.printStackTrace();
        }

        return latLng;
    }


    // Helper method to convert Bitmap to Uri
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String title = "Title_" + System.currentTimeMillis();
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title, null);
        return Uri.parse(path);
    }

    @SuppressLint("SetTextI18n")
    public void showDialog() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_cart_dialog);

        TextInputEditText nameEt = (TextInputEditText) dialog.findViewById(R.id.nameEt);
        priceEt = (TextInputEditText) dialog.findViewById(R.id.priceEt);
        quantityEt = (TextInputEditText) dialog.findViewById(R.id.quantityEt);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);

        nameEt.setText(productsModel.getName());
        String formattedPrice;
        if (productsModel.getOfferPrice().isEmpty()) {
            formattedPrice = df.format(Double.parseDouble(productsModel.getOriginalPrice()));
        } else {
            formattedPrice = df.format(Double.parseDouble(productsModel.getOfferPrice()));
        }
        priceEt.setText("$" + formattedPrice);

        if (productsModel != null) {
            if (productsModel.getOfferPrice().isEmpty()) {
                actualPrice = Double.parseDouble(productsModel.getOriginalPrice());
            } else {
                actualPrice = Double.parseDouble(productsModel.getOfferPrice());
            }
            quantityEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    calculateTotalPrice(editable.toString());
                }
            });

        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantity = quantityEt.getText().toString();

                if (quantity.isEmpty()) {
                    showMessage("Please enter quantity");
                } else if (quantity.contentEquals("0")) {
                    showMessage("Please enter valid quantity");
                } else if (Integer.parseInt(quantity) > Integer.parseInt(productsModel.getQuantity())) {
                    showMessage("Total available products are " + productsModel.getQuantity() + ",\nPlease enter valid quantity");
                } else {
                    int remainingQuantity = Integer.parseInt(productsModel.getQuantity()) - Integer.parseInt(quantity);
                    productsModel.setQuantity(String.valueOf(remainingQuantity));
                    databaseHelper.updateProduct(productsModel);
                    list = databaseHelper.getCartData(HelperClass.users.getId());
                    if (!list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (productsModel.getId() == list.get(i).getProductId() && Objects.equals(productsModel.getName(), list.get(i).getName())) {
                                updatedModel = list.get(i);
                                break;
                            }
                        }
                    }
                    getCurrentDateAndTime();

                    if (updatedModel != null) {

                        double finalPrice = Double.parseDouble(updatedModel.getPrice()) + totalPrice;
                        int finalQuantity = Integer.parseInt(updatedModel.getQuantity()) + Integer.parseInt(quantity);
                        updatedModel.setPrice(String.valueOf(finalPrice));
                        updatedModel.setQuantity(String.valueOf(finalQuantity));
                        databaseHelper.updateCart(updatedModel);
                    } else {
                        CartModel cartModel = new CartModel(HelperClass.users.getId(), productsModel.getCategoryId(),
                                productsModel.getId(), productsModel.getName(), String.valueOf(totalPrice),
                                quantity, productsModel.getDescription(), productsModel.getImageUri());
                        databaseHelper.insertCart(cartModel);
                    }
                    showMessage("Added to Cart Successfully");
                    Navigation.findNavController(requireView()).navigateUp();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    @SuppressLint("SetTextI18n")
    private void calculateTotalPrice(String quantityStr) {
        if (!quantityStr.isEmpty()) {
            try {
                long quantity = Long.parseLong(quantityStr);
                totalPrice = actualPrice * quantity;
                String formattedPrice = df.format(totalPrice);
                priceEt.setText("$" + formattedPrice);
            } catch (NumberFormatException e) {
                // Handle the case where parsing fails
                priceEt.setText("Invalid quantity");
                e.printStackTrace(); // Log the exception for debugging purposes
            }
        } else {
            priceEt.setText("$0");
        }
    }

    private void showMessage(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private void getCurrentDateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
        currentDateAndTime = sdf.format(new Date());
    }

}