package com.example.projeto;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigationrail.NavigationRailView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projeto.databinding.ActivityMainBinding;
import com.example.projeto.data.remote.MySqlHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        
        SharedPreferences themePrefs = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE);
        boolean isDarkMode = themePrefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(isDarkMode ? 
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MySqlHelper.initDatabase();

        setSupportActionBar(binding.toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_dashboard, R.id.nav_transactions, R.id.nav_goals, 
                    R.id.nav_categories, R.id.nav_reports, R.id.nav_settings)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(binding.navView, navController);

            updateNavHeader();

            // Adaptive Navigation: Rail or BottomNav
            NavigationRailView navRail = findViewById(R.id.nav_rail);
            BottomNavigationView bottomNav = binding.contentMain.bottomNavView;

            if (navRail != null) {
                NavigationUI.setupWithNavController(navRail, navController);
            }
            if (bottomNav != null) {
                NavigationUI.setupWithNavController(bottomNav, navController);
            }

            // Visibilidade dinâmica
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                boolean isAuth = id == R.id.nav_login || id == R.id.nav_register || id == R.id.nav_forgot_password;
                
                if (!isAuth) {
                    updateNavHeader();
                }

                binding.toolbar.setVisibility(isAuth ? View.GONE : View.VISIBLE);
                if (bottomNav != null) bottomNav.setVisibility(isAuth ? View.GONE : View.VISIBLE);
                if (navRail != null) navRail.setVisibility(isAuth ? View.GONE : View.VISIBLE);
                
                binding.drawerLayout.setDrawerLockMode(isAuth ? DrawerLayout.LOCK_MODE_LOCKED_CLOSED : DrawerLayout.LOCK_MODE_UNLOCKED);
            });

            binding.navView.setNavigationItemSelectedListener(item -> {
                if (item.getItemId() == R.id.nav_logout) {
                    navController.navigate(R.id.nav_login);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
                if (handled) binding.drawerLayout.closeDrawer(GravityCompat.START);
                return handled;
            });
        }
    }

    private void updateNavHeader() {
        View headerView = binding.navView.getHeaderView(0);
        if (headerView != null) {
            TextView txtName = headerView.findViewById(R.id.txt_header_name);
            TextView txtEmail = headerView.findViewById(R.id.txt_header_email);

            SharedPreferences prefs = getSharedPreferences("Session", Context.MODE_PRIVATE);
            String name = prefs.getString("user_name", "Usuário");
            String email = prefs.getString("user_email", "");

            if (txtName != null) txtName.setText("Olá, " + name);
            if (txtEmail != null) txtEmail.setText(email);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        if (navHostFragment != null) {
            return NavigationUI.navigateUp(navHostFragment.getNavController(), mAppBarConfiguration)
                    || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}