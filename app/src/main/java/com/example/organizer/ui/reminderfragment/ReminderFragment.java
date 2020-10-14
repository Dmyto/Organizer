package com.example.organizer.ui.reminderfragment;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.organizer.R;
import com.example.organizer.data.PictureUtils;
import com.example.organizer.data.Reminder;
import com.example.organizer.data.ReminderLab;
import com.example.organizer.ui.reminderfragment.dialogfragments.DatePickerFragment;
import com.example.organizer.ui.reminderfragment.dialogfragments.DetailsViewerFragment;
import com.example.organizer.ui.reminderfragment.dialogfragments.PhotoViewerFragment;
import com.example.organizer.ui.reminderfragment.dialogfragments.TimePickerFragment;
import com.github.clans.fab.FloatingActionButton;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.zxing.integration.android.IntentIntegrator;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class ReminderFragment extends Fragment {
    private Reminder mReminder;
    private EditText mTitleField, mDetailsField;
    private TextView mContactInfo, mPositionEditText, mDateTextView;

    private LinearLayout choseTimeLinearLayout;

    private ImageButton mCallContactButton;

    private ImageView mPhotoImageView, mMarkerImageView;

    private File mPhotoFile;

    private FloatingActionButton mAddLocationButton, mAddContactButton, mAddPhotoButton, mNotificationButton;

    private NotificationManagerCompat mNotificationManagerCompat;

    private static final String ARG_REMINDER_ID = "reminder_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_VIEWER = "DialogView";
    private static final String DIALOG_LOCATION_PICKER = "DialogLocationPicker";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_PICK_PLACE = 4;

    public static ReminderFragment newInstance(UUID uuid) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_REMINDER_ID, uuid);

        ReminderFragment fragment = new ReminderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID reminderId = (UUID) requireArguments().getSerializable(ARG_REMINDER_ID);
        mReminder = ReminderLab.get(getActivity()).getReminder(reminderId);
        mPhotoFile = ReminderLab.get(getActivity()).getPhotoFile(mReminder);
        mNotificationManagerCompat = NotificationManagerCompat.from(requireContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        ReminderLab.get(getActivity()).updateReminder(mReminder);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        mAddContactButton = view.findViewById(R.id.add_contact_reminder_button);
        mAddPhotoButton = view.findViewById(R.id.add_photo_reminder_button);
        mAddLocationButton = view.findViewById(R.id.add_location__reminder_button);
        mNotificationButton = view.findViewById(R.id.notification_reminder_button);

        mDateTextView = view.findViewById(R.id.reminder_date_text);

        mCallContactButton = view.findViewById(R.id.call_to_contact_button);
        mContactInfo = view.findViewById(R.id.contact_info);

        mTitleField = view.findViewById(R.id.reminder_title);
        mDetailsField = view.findViewById(R.id.reminder_notes);
        mPositionEditText = view.findViewById(R.id.position_coordinates);

        mPhotoImageView = view.findViewById(R.id.reminder_photo);
        mMarkerImageView = view.findViewById(R.id.position_marker);

        mTitleField.setText(mReminder.getTitle());
        mDetailsField.setText(mReminder.getDetails());

        choseTimeLinearLayout = view.findViewById(R.id.choose_time);

        choseTimeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker();
            }
        });

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mReminder.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDetailsField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mReminder.setDetails(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updateDate();

        mPhotoImageView.setOnClickListener(v -> openImage());

        mAddLocationButton.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                addLocation();
            }
            return false;
        });

        mCallContactButton.setOnClickListener(v -> {
            if (mReminder.getContactNumber() != null) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.fromParts("tel", mReminder.getContactNumber(), null));
                startActivity(callIntent);
            }

        });

        final Uri uriCont = Uri.parse("content://contacts");
        final Intent pickContact = new Intent(Intent.ACTION_PICK, uriCont)
                .setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        mAddContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mReminder.getContact() != null && mReminder.getContactNumber() != null) {
            mContactInfo.setText(getString(R.string.contact_info, mReminder.getContact(), mReminder.getContactNumber()));
//            mContactInfo.setText(mReminder.getContact() + "\n" + mReminder.getContactNumber());
            mContactInfo.setVisibility(View.VISIBLE);
            mCallContactButton.setVisibility(View.VISIBLE);
        }

        PackageManager packageManager = requireActivity().getPackageManager();

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mAddPhotoButton.setEnabled(canTakePhoto);

        mAddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(requireActivity(), "com.example.orgaziner.data.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = requireActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    requireActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarReminder();
            }
        });

        updatePhoto();

        if (mReminder.getLongitude() != null && mReminder.getLatitude() != null) {
//            mPositionEditText.setText(getString(R.string.position_label, mReminder.getLatitude(), mReminder.getLongitude()));
            mPositionEditText.setText(mReminder.getLatitude() + " " + mReminder.getLongitude());
            mPositionEditText.setVisibility(View.VISIBLE);
            mMarkerImageView.setVisibility(View.VISIBLE);
        }

        mMarkerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapDialog();
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_reminder_button:
                String mimeType = "text/plain";

                Intent intent = ShareCompat.IntentBuilder.from(requireActivity())
                        .setType(mimeType)
                        .setText(getReminderreport())
                        .getIntent();
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.reminder_menu, menu);
    }

    private void qrCodeScan() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt("Scanning");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }

    private void mapDialog() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        DetailsViewerFragment dialog = DetailsViewerFragment.newInstance(mReminder.getLongitude(), mReminder.getLatitude());
        dialog.setTargetFragment(ReminderFragment.this, REQUEST_DATE);
        dialog.show(fragmentManager, DIALOG_DATE);
    }

    private void datePicker() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        DatePickerFragment dialog = DatePickerFragment.newInstance(mReminder.getDate());
        dialog.setTargetFragment(ReminderFragment.this, REQUEST_DATE);
        dialog.show(fragmentManager, DIALOG_DATE);
    }

    private void timePicker() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        TimePickerFragment dialog = TimePickerFragment.newInstance(mReminder.getDate());
        dialog.setTargetFragment(ReminderFragment.this, REQUEST_TIME);
        dialog.show(fragmentManager, DIALOG_TIME);
    }

    private void openImage() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        PhotoViewerFragment dialog = PhotoViewerFragment.newInstance(mPhotoFile);
        dialog.setTargetFragment(ReminderFragment.this, REQUEST_PHOTO);
        dialog.show(fragmentManager, DIALOG_VIEWER);
    }

    private void calendarReminder() {
        if (mReminder.getNotification()) {
            Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, mReminder.getUuidReminder());
            Intent intent = new Intent(Intent.ACTION_EDIT).setData(uri);
            intent.putExtra(CalendarContract.Events.TITLE, mReminder.getTitle());
            startActivity(intent);
        } else {

            Long id = generateUniqueId();

            mReminder.setUuidReminder(id);
            mReminder.setNotification(true);

            Calendar cal = Calendar.getInstance();
            cal.setTime(mReminder.getDate());
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", cal.getTimeInMillis());
            intent.putExtra(CalendarContract.Reminders.EVENT_ID, id);
            intent.putExtra("allDay", false);
            intent.putExtra("rule", "FREQ=NEVER");
            intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
            intent.putExtra("title", mReminder.getTitle());
            startActivity(intent);
        }
    }

    private void addLocation() {
//        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
//        PickAddressFragment dialog = new PickAddressFragment();
//        dialog.setTargetFragment(ReminderFragment.this, REQUEST_PICK_PLACE);
//        dialog.show(fragmentManager, DIALOG_LOCATION_PICKER);
    }

    private Long generateUniqueId() {
        long val = -1;
        do {
            val = UUID.randomUUID().getMostSignificantBits();
        } while (val < 0);
        return val;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("RESULT", requestCode + resultCode + "");
        if (requestCode == REQUEST_DATE && data != null) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mReminder.setDate(date);
            updateDate();
            timePicker();
        } else if (requestCode == REQUEST_TIME && data != null) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mReminder.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };

            try (Cursor c = requireActivity().getContentResolver().query(contactUri, queryFields, null, null, null)) {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String contact = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                mReminder.setContact(contact);

                String number = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mReminder.setContactNumber(number);

                mContactInfo.setText(contact + "\n" + number);
                mContactInfo.setVisibility(View.VISIBLE);
                mCallContactButton.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == REQUEST_PHOTO && data != null) {
            Uri uri = FileProvider.getUriForFile(requireActivity(), "com.example.orgaziner.data.fileprovider", mPhotoFile);
            requireActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhoto();
        } else if (requestCode == REQUEST_PICK_PLACE && data != null) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePhoto();
    }

    private String getReminderreport() {
        String details = mReminder.getDetails() + " ";
        String contact = mReminder.getContact();
        String number = mReminder.getContactNumber();
        if (contact == null) {
            contact = getString(R.string.reminder_no_contact);
        } else {
            contact = getString(R.string.reminder_report_contact, mReminder.getContact());
            number = getString(R.string.reminder_report_contact_number, mReminder.getContactNumber());
        }

        String report = getString(R.string.send_report) + " \n" + mReminder.getTitle() + "\n" + contact + number + "\n" + details;
        return report;
    }

    private void updateDate() {
        if (mReminder.getNotification() != null) {
            mDateTextView.setText(DateFormat.getDateInstance().format(mReminder.getDate()) + " " + DateFormat.getTimeInstance(DateFormat.SHORT).format(mReminder.getDate()));
        }
    }

    private void updatePhoto() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoImageView.setImageDrawable(null);
            mPhotoImageView.setVisibility(View.GONE);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), requireActivity());
            mPhotoImageView.setImageBitmap(bitmap);
            mPhotoImageView.setVisibility(View.VISIBLE);
        }
    }


}
