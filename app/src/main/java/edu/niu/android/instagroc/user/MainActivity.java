package edu.niu.android.instagroc.user;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import edu.niu.android.instagroc.R;
import edu.niu.android.instagroc.databinding.ActivityMainBinding;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    NavHostFragment navHostFragment;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout using view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the status bar color
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        // Get the NavHostFragment and NavController
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Set up MeowBottomNavigation
        setBottomNavigation();

        // Request SMS permission if not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.SEND_SMS}, 100);
        }

        // Request Location permission if not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 110);
        }
    }

    // Set up MeowBottomNavigation
    private void setBottomNavigation() {

        // Add navigation items to the bottom navigation bar
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.cart));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.order));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_baseline_person_24));

        // Navigate to HomeFragment initially
        navController.navigate(R.id.homeFragment);
        binding.bottomNav.bottomNavigation.show(1, true);

        // Set click listener for bottom navigation items
        binding.bottomNav.bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                switch (model.getId()) {
                    case 1:
                        navController.navigate(R.id.homeFragment);
                        break;

                    case 2:
                        navController.navigate(R.id.cartFragment);
                        break;

                    case 3:
                        navController.navigate(R.id.ordersFragment);
                        break;
                    case 4:
                        navController.navigate(R.id.profileFragment);
                        break;
                }

                return null;
            }
        });

        // Set click listeners for custom bottom navigation buttons
        binding.bottomNav.home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.homeFragment);
                binding.bottomNav.bottomNavigation.show(1, true);
            }
        });
        binding.bottomNav.cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.bottomNav.bottomNavigation.show(2, true);
                navController.navigate(R.id.cartFragment);
            }
        });
        binding.bottomNav.orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.ordersFragment);
                binding.bottomNav.bottomNavigation.show(3, true);
            }
        });

        binding.bottomNav.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.profileFragment);
                binding.bottomNav.bottomNavigation.show(4, true);
            }
        });
    }
}
