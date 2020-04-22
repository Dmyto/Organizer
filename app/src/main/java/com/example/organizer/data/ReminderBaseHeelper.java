package com.example.organizer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.organizer.data.ReminderDBSchema.ReminderTable;

public class ReminderBaseHeelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "reminder.db";
    private static final int VERSION = 1;

    public ReminderBaseHeelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ReminderTable.NAME + "(" +
                ReminderTable.Cols.UUID + ", " +
                ReminderTable.Cols.TITLE + ", " +
                ReminderTable.Cols.DATE + ", " +
                ReminderTable.Cols.DETAILS + ", " +
                ReminderTable.Cols.CONTACT + ", " +
                ReminderTable.Cols.CONTACT_NUMBER + ", " +
                ReminderTable.Cols.NOTIFICATION + ", " +
                ReminderTable.Cols.LONGITUDE + ", " +
                ReminderTable.Cols.LATITUDE  +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
