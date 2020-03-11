package com.example.organizer.activities;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.organizer.fragments.ReminderFragment;

import java.util.UUID;

public class ReminderActivity extends SingleFragmentActivity {

    public static final String EXTRA_REMINDER_ID = "rmeinder_id";

    public static Intent newIntent(Context packageContext, UUID reminderId) {
        Intent intent = new Intent(packageContext, ReminderActivity.class);
        intent.putExtra(EXTRA_REMINDER_ID, reminderId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID reminderId = (UUID) getIntent().getSerializableExtra(EXTRA_REMINDER_ID);
        return ReminderFragment.newInstance(reminderId);
    }
}

