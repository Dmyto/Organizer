package com.example.organizer.data;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class ReminderCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public ReminderCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Reminder getReminder() {
        String uuidString = getString(getColumnIndex(ReminderDBSchema.ReminderTable.Cols.UUID));
        String title = getString(getColumnIndex(ReminderDBSchema.ReminderTable.Cols.TITLE));
        String details = getString(getColumnIndex(ReminderDBSchema.ReminderTable.Cols.DETAILS));
        long date = getLong(getColumnIndex(ReminderDBSchema.ReminderTable.Cols.DATE));
        String contact = getString(getColumnIndex(ReminderDBSchema.ReminderTable.Cols.CONTACT));
        String contactNumber = getString(getColumnIndex(ReminderDBSchema.ReminderTable.Cols.CONTACT_NUMBER));


        Reminder reminder = new Reminder(UUID.fromString(uuidString));
        reminder.setTitle(title);
        reminder.setDetails(details);
        reminder.setDate(new Date(date));
        reminder.setContact(contact);
        reminder.setContactNumber(contactNumber);

        return reminder;
    }
}
