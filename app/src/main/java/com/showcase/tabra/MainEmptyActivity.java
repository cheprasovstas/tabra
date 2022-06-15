package com.showcase.tabra;

import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.showcase.tabra.databinding.ActivityMainEmptyBinding;
import com.showcase.tabra.ui.login.LoginActivity;
import com.showcase.tabra.ui.product.ProductListFragment;
import com.showcase.tabra.utils.Util;
import org.jetbrains.annotations.NotNull;


public class MainEmptyActivity extends AppCompatActivity {

    private ActivityMainEmptyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainEmptyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_products, R.id.nav_profile)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // go straight to main if a token is stored
//        if (Util.getToken(this) == null) {
//            Intent activityIntent;
//            activityIntent = new Intent(this, LoginActivity.class);
//            startActivity(activityIntent);
//        }
    }
}