package com.norbertotaveras.flixiago.activities;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.norbertotaveras.flixiago.R;
import com.norbertotaveras.flixiago.ui.movies.MoviesFragment;
import com.norbertotaveras.flixiago.ui.shows.ShowsFragment;

import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class ContentActivity
        extends
        AppCompatActivity
        implements
        MoviesFragment.ActivityInterface,
        ShowsFragment.ActivityInterface{

    private static final String TAG = "ContentActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_movies, R.id.navigation_dashboard, R.id.navigation_watchlist, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public void fragmentFailed(MoviesFragment fragment) {
        finish();
    }

    @Override
    public void setActivityTitle(@StringRes int titleResId) {
        AppCompatActivity activity = this;
        ActionBar actionBar = activity.getSupportActionBar();

        if (actionBar == null) {
            Log.e(TAG, "No action bar!");
            return;
        }

        actionBar.setTitle(titleResId);
    }

    @Override
    public void fragmentFailed(ShowsFragment fragment) {
        finish();
    }

    @Override
    public void setShowActivityTitle(@StringRes int titleResId) {
        AppCompatActivity activity = this;
        ActionBar actionBar = activity.getSupportActionBar();

        if (actionBar == null) {
            Log.e(TAG, "No action bar!");
            return;
        }

        actionBar.setTitle(titleResId);
    }

    @Override
    public void pressBack() {
        super.onBackPressed();
    }
}