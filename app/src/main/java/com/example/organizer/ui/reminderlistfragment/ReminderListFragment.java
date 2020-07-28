package com.example.organizer.ui.reminderlistfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organizer.R;
import com.example.organizer.data.Reminder;
import com.example.organizer.data.ReminderLab;
import com.example.organizer.helper.ItemTouchHelperAdapter;
import com.example.organizer.helper.OnStartDragListener;
import com.example.organizer.helper.SimpleItemTouchHelperCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.util.Collections;
import java.util.List;

public class ReminderListFragment extends Fragment implements OnStartDragListener {
    private RecyclerView reminderRecyclerView;
    private ReminderAdapter reminderAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private Callbacks mCallbacks;
    private ProgressBar mProgressBar;

    public interface Callbacks {
        void onCrimeSelected(Reminder reminder);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.reminder_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_reminder:
                Reminder reminder = new Reminder();
                ReminderLab.get(getActivity()).addReminder(reminder);
                updateUI();
                mCallbacks.onCrimeSelected(reminder);
                break;
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Reminder reminder = new Reminder();
                ReminderLab.get(getActivity()).addReminder(reminder);
                updateUI();
                mCallbacks.onCrimeSelected(reminder);
            }
        });

        reminderRecyclerView = view.findViewById(R.id.reminder_recycler_view);
        mProgressBar = view.findViewById(R.id.progress_bar);
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback((ItemTouchHelperAdapter) reminderAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(reminderRecyclerView);
        return view;
    }

    private void updateUI() {
        ReminderLab reminderLab = ReminderLab.get(getActivity());
        List<Reminder> reminders = reminderLab.getReminders();
        if (reminderAdapter == null) {
            mProgressBar.setVisibility(View.GONE);
            reminderAdapter = new ReminderAdapter(reminders);
            reminderRecyclerView.setAdapter(reminderAdapter);
        } else {
            reminderAdapter.setReminders(reminders);
            reminderAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    private class ReminderAdapter extends RecyclerView.Adapter<ReminderHolder> implements ItemTouchHelperAdapter {

        private List<Reminder> mReminders;

        public ReminderAdapter(List<Reminder> reminders) {
            this.mReminders = reminders;
        }

        @NonNull
        @Override
        public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ReminderHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ReminderHolder holder, int position) {
            Reminder reminder = mReminders.get(position);
            holder.bind(reminder);
        }

        @Override
        public int getItemCount() {
            return mReminders.size();
        }

        public void setReminders(List<Reminder> reminders) {
            mReminders = reminders;
        }


        @Override
        public void onItemDismiss(int position) {

            Reminder reminder = mReminders.get(position);
            mReminders.remove(position);
            ReminderLab.get(getActivity()).deleteReminder(reminder);
            notifyItemRemoved(position);

        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(mReminders, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }
    }


    private class ReminderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Reminder mReminder;


        public ReminderHolder(LayoutInflater layoutInflater, ViewGroup parent) {
            super(layoutInflater.inflate(R.layout.list_item_reminder, parent, false));
            mTitleTextView = itemView.findViewById(R.id.reminder_title_list);
            mDateTextView = itemView.findViewById(R.id.reminder_date);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Reminder reminder) {
            mReminder = reminder;
            mTitleTextView.setText(mReminder.getTitle());
            mDateTextView.setText(DateFormat.getDateInstance().format(mReminder.getDate()) + " " + DateFormat.getTimeInstance().format(mReminder.getDate()));
        }

        @Override
        public void onClick(View v) {
            mCallbacks.onCrimeSelected(mReminder);
        }
    }
}
