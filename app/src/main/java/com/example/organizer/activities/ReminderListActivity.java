package com.example.organizer.activities;

import androidx.fragment.app.Fragment;

import android.util.Log;

import com.example.organizer.R;
import com.example.organizer.fragments.ReminderListFragment;

public class ReminderListActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return new ReminderListFragment();
    }
}
