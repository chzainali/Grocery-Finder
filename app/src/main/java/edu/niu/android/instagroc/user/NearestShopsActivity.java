package edu.niu.android.instagroc.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.database.DatabaseHelper;
import edu.niu.android.instagroc.databinding.ActivityMainBinding;
import edu.niu.android.instagroc.databinding.ActivityNearestShopsBinding;

public class NearestShopsActivity extends AppCompatActivity implements OnMapReadyCallback {
    ActivityNearestShopsBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout using view binding
        binding = ActivityNearestShopsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        binding.rlTop.tvLabel.setText("Nearest Shops");
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v ->
                finish());

        // Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyANUkNEMNuY8-Aa5hY0eSdUoQFkQn5J72s");
        placesClient = Places.createClient(this);

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, start location updates
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Move camera to current location
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

                        // Add a marker at the current location
                        mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("You are here"));

                        // Fetch nearby gyms and add markers
                        fetchNearbyGyms(currentLocation);
                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchNearbyGyms(LatLng location) {

        // Create a new Places API client
        PlacesClient placesClient = Places.createClient(this);

        // Create a Nearby Search request
        String locationString = location.latitude + "," + location.longitude;
        String radius = "50000"; // 5000 meters (adjust as needed)

        String type = "grocery"; // Type for gyms
        String keyword = "grocery"; // Keyword for gyms

        String apiKey = "AIzaSyD9-7VF_J-YFK2g8_UethYoeR73R_pNGtY";

        String url = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=%s&type=%s&keyword=%s&key=%s",
                locationString, radius, type, keyword, apiKey);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Make an HTTP request and parse the response
                    URL apiUrl = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) apiUrl.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        // Parse the JSON response and add markers for each gym
                        parseNearbyShopsResponse(in);
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Handle any post-execution tasks if needed
            }
        }.execute();
    }


    private void parseNearbyShopsResponse(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder responseStringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }

            // Parse the JSON response
            JSONObject response = new JSONObject(responseStringBuilder.toString());

            // Check if the response contains results
            if (response.has("results")) {
                JSONArray resultsArray = response.getJSONArray("results");

                // Limit the number of gyms to display
                int numberOfGyms = Math.min(resultsArray.length(), 5);

                // Create LatLngBounds to include all markers
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                for (int i = 0; i < numberOfGyms; i++) {
                    JSONObject shopObject = resultsArray.getJSONObject(i);
                    JSONObject locationObject = shopObject.getJSONObject("geometry").getJSONObject("location");

                    double lat = locationObject.getDouble("lat");
                    double lng = locationObject.getDouble("lng");
                    LatLng shopLocation = new LatLng(lat, lng);

                    // Get the address for the location
                    String address = getAddressFromLocation(shopLocation);

                    // Add marker for each gym with address
                    runOnUiThread(() -> {
                        try {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(shopLocation)
                                    .title(shopObject.getString("name"))
                                    .snippet(address) // Set the address as snippet
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                            // Add marker to the map
                            Marker marker = mMap.addMarker(markerOptions);

                            // Include the marker in the bounds
                            boundsBuilder.include(shopLocation);

                            // Set a click listener for the marker
                            mMap.setOnMarkerClickListener(clickedMarker -> {
                                // Show info window when marker is clicked
                                clickedMarker.showInfoWindow();

                                // Set a click listener for the info window
                                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                    @Override
                                    public void onInfoWindowClick(Marker marker) {
                                        // Open Google Maps with the specific location when info window is clicked
                                        openGoogleMaps(marker.getPosition());
                                    }
                                });

                                // Return true to indicate that the click event is consumed
                                return true;
                            });

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

                // Build the LatLngBounds from the markers' positions
                LatLngBounds bounds = boundsBuilder.build();

                // Calculate a reasonable zoom level based on the distance between markers
                int padding = getMapPadding(); // Set padding as needed (in pixels)
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Helper method to calculate padding for the newLatLngBounds
    private int getMapPadding() {
        // Set padding as needed (in pixels)
        return 100;
    }


    // Helper method to get the address from the LatLng location
    private String getAddressFromLocation(LatLng location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Helper method to open Google Maps with the specific location
    private void openGoogleMaps(LatLng location) {
        Uri gmmIntentUri = Uri.parse("geo:" + location.latitude + "," + location.longitude + "?q=" + location.latitude + "," + location.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


}