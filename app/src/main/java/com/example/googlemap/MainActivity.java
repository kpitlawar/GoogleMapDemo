package com.example.googlemap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    private Button btnToggleDark;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();

        navigationView = (NavigationView) findViewById(R.id.navigation_menu);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case  R.id.nav_my_location:
                        Intent intent = new Intent(MainActivity.this, GoogleMapActivity.class);
                        startActivity(intent);
                        break;

                    case  R.id.nav_current_location:
                        Intent intentCurrLoc = new Intent(MainActivity.this, CurrentLocationActivity.class);
                        startActivity(intentCurrLoc);
                        break;


                    case  R.id.nav_search_view:
                        Intent intentSearchView = new Intent(MainActivity.this, SearchViewActivity.class);
                        startActivity(intentSearchView);
                        break;


                    case  R.id.nav_marker:
                        Intent intentMarker = new Intent(MainActivity.this, MarkerActivity.class);
                        startActivity(intentMarker);
                        break;

                    case  R.id.nav_polyline:
                        Intent intentPolyline = new Intent(MainActivity.this, DrawerPolyActivity.class);
                        startActivity(intentPolyline);
                        break;

                    case  R.id.nav_nearby:
                        Intent intentNearBy = new Intent(MainActivity.this, NearbyPlaceActivity.class);
                        startActivity(intentNearBy);
                        break;
                }
                return false;
            }
        });
    }

    public void darkbutton(View view) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

    }

    public void setUpToolbar() {
        drawerLayout = findViewById(R.id.drawerLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorBlank));
        actionBarDrawerToggle.syncState();

    }



}
