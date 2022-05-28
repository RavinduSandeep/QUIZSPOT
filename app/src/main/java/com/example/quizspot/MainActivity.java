package com.example.quizspot;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.quizspot.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizspot.databinding.ActivityMainBinding;

import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;

    private TextView drawerProfileName, drawerProfileText;

    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.navigation_home:_home:
                            setFragment(new CategoryFragment());
                            return true;

                        case R.id.navigation_leaderboard:_leaderboard:
                            setFragment(new LeaderboardFragment());
                            return true;

                        case R.id.navigation_account:_account:
                            setFragment(new AccountFragment() );
                            return true;
                    }

                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getSupportActionBar().setTitle("Categories");

        bottomNavigationView= findViewById(R.id.bottom_nav_bar);
        main_frame = findViewById(R.id.main_frame);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        drawerProfileName= navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_name);
        drawerProfileText=navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_text_image);

        String name = DbQuery.myProfile.getName();
        drawerProfileName.setText(name);

        drawerProfileText.setText(name.toUpperCase(Locale.ROOT).substring(0,1));


        setFragment(new CategoryFragment());

    }


    private void setFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(main_frame.getId(),fragment);
        transaction.commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();

        if (id == R.id.nav_home){
            setFragment(new CategoryFragment());
        }
        else if (id == R.id.nav_account){
            setFragment(new AccountFragment());
        }
        else if (id == R.id.nav_leaderboard){
            setFragment(new LeaderboardFragment());
        }

        DrawerLayout drawer =  (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}