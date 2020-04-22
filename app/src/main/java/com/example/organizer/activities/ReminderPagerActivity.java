package com.example.organizer.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.organizer.R;
import com.example.organizer.data.Reminder;
import com.example.organizer.data.ReminderLab;
import com.example.organizer.fragments.reminderfragment.ReminderFragment;

import java.util.List;
import java.util.UUID;

public class ReminderPagerActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private List<Reminder> mReminders;

    private static final String EXTRA_REMINDER_ID = "reminder_id";

    public static Intent newIntent(Context context, UUID reminderId) {
        Intent intent = new Intent(context, ReminderPagerActivity.class);
        intent.putExtra(EXTRA_REMINDER_ID, reminderId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_pager);

        mViewPager = (ViewPager) findViewById(R.id.reminder_view_pager);

        UUID reminderId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_REMINDER_ID);

        mReminders = ReminderLab.get(this).getReminders();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Reminder reminder = mReminders.get(position);
                return ReminderFragment.newInstance(reminder.getUuid());
            }

            @Override
            public int getCount() {
                return mReminders.size();
            }
        });

        for (int i = 0; i < mReminders.size(); i++) {
            if (mReminders.get(i).getUuid().equals(reminderId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
