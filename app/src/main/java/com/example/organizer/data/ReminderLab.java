package com.example.organizer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReminderLab {
    private static ReminderLab reminderLab;
    private List<Reminder> reminders;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ReminderLab get(Context context) {
        if (reminderLab == null) {
            reminderLab = new ReminderLab(context);
        }
        return reminderLab;
    }

    private ReminderLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ReminderBaseHeelper(mContext).getWritableDatabase();
    }

    public List<Reminder> getReminders() {
        List<Reminder> reminders = new ArrayList<>();

        ReminderCursorWrapper cursorWrapper = queryReminders(null, null);
        try {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) {
                reminders.add(cursorWrapper.getReminder());
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return reminders;
    }

    public void addReminder(Reminder reminder) {
        ContentValues values = getContentValues(reminder);
        mDatabase.insert(ReminderDBSchema.ReminderTable.NAME, null, values);
    }

    public void updateReminder(Reminder reminder) {
        String uuidString = reminder.getUuid().toString();
        ContentValues values = getContentValues(reminder);

        mDatabase.update(ReminderDBSchema.ReminderTable.NAME, values,
                ReminderDBSchema.ReminderTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    public void deleteReminder(Reminder reminder) {
        String uuidString = reminder.getUuid().toString();
        mDatabase.delete(ReminderDBSchema.ReminderTable.NAME, ReminderDBSchema.ReminderTable.Cols.UUID + " = ?", new String[]{uuidString});
    }

    private ReminderCursorWrapper queryReminders(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                ReminderDBSchema.ReminderTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new ReminderCursorWrapper(cursor);
    }

    public Reminder getReminder(UUID uuid) {

        ReminderCursorWrapper cursorWrapper = queryReminders(
                ReminderDBSchema.ReminderTable.Cols.UUID + " = ?",
                new String[]{uuid.toString()}
        );
        try {
            if (cursorWrapper.getCount() == 0) {
                return null;
            }
            cursorWrapper.moveToFirst();
            return cursorWrapper.getReminder();
        } finally {
            cursorWrapper.close();
        }
    }

    public static ContentValues getContentValues(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(ReminderDBSchema.ReminderTable.Cols.UUID, reminder.getUuid().toString());
        values.put(ReminderDBSchema.ReminderTable.Cols.TITLE, reminder.getTitle());
        values.put(ReminderDBSchema.ReminderTable.Cols.DATE, reminder.getDate().getTime());
        values.put(ReminderDBSchema.ReminderTable.Cols.DETAILS, reminder.getDetails());
        values.put(ReminderDBSchema.ReminderTable.Cols.CONTACT, reminder.getContact());
        values.put(ReminderDBSchema.ReminderTable.Cols.CONTACT_NUMBER, reminder.getContactNumber());
        return values;
    }

    public File getPhotoFile(Reminder reminder) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, reminder.getPhotoFilename());
    }
}
