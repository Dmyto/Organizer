package com.example.organizer.data;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class Reminder {
    private UUID uuid;
    private String title;
    private String details;
    private Date mDate;
    private String contact;
    private String contactNumber;


    public Reminder() {
        this(UUID.randomUUID());
    }

    public Reminder(UUID id) {
        uuid = id;
        mDate = new Date();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPhotoFilename() {
        return "IMG_" + getUuid().toString() + ".jpg";
    }


}
