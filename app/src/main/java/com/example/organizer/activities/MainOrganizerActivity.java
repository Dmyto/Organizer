package com.example.organizer.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.organizer.R;
import com.example.organizer.data.Reminder;
import com.example.organizer.fragments.reminderfragment.ReminderFragment;
import com.example.organizer.fragments.reminderlistfragment.ReminderListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainOrganizerActivity extends SingleFragmentActivity implements ReminderListFragment.Callbacks {


    @Override
    protected Fragment createFragment() {
        return null;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Reminder reminder) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = ReminderPagerActivity.newIntent(this, reminder.getUuid());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Fragment newDetail = ReminderFragment.newInstance(reminder.getUuid());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_list)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}
