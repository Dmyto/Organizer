package com.example.organizer.data;

public class ReminderDBSchema {
    public static final class ReminderTable {
        public static final String NAME = "reminders";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String DETAILS = "details";
            public static final String CONTACT = "contact";
            public static final String CONTACT_NUMBER = "contact_number";
            public static final String NOTIFICATION = "notification";
            public static final String LATITUDE = "latitude";
            public static final String LONGITUDE = "longitude";
            public static final String UUID_REMINDER = "uuidReminder";
        }
    }
}
