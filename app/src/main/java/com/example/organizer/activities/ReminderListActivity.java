package com.example.organizer.activities;


import androidx.fragment.app.Fragment;

import android.content.Intent;


import com.example.organizer.R;
import com.example.organizer.data.Reminder;
import com.example.organizer.fragments.ReminderFragment;
import com.example.organizer.fragments.ReminderListFragment;

public class ReminderListActivity extends SingleFragmentActivity implements ReminderListFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new ReminderListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Reminder reminder) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = ReminderPagerActivity.newIntent(this, reminder.getUuid());
            startActivity(intent);
        } else {
            Fragment newDetail = ReminderFragment.newInstance(reminder.getUuid());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }
}

