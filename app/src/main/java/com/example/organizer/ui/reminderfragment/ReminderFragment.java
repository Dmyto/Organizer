package com.example.organizer.ui.reminderfragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.organizer.AlarmManagerBroadcastReceiver;
import com.example.organizer.R;
import com.example.organizer.data.PictureUtils;
import com.example.organizer.data.Reminder;
import com.example.organizer.data.ReminderLab;
import com.github.clans.fab.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ReminderFragment extends Fragment {
    private Reminder mReminder;
    private EditText mTitleField, mDetailsField;
    private TextView mContactInfo, mPositionEditText, mQrCodeTextView;


    private ImageButton mCallContactButton;

    private ImageView mPhotoImageView, mMarkerImageView;

    private File mPhotoFile;

    private FloatingActionButton mShareButton, mAddContactButton, mAddPhotoButton, mQrCodeScanButton;

    private NotificationManagerCompat mNotificationManagerCompat;

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    private static final String ARG_REMINDER_ID = "reminder_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_VIEWER = "DialogView";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        mAddContactButton = view.findViewById(R.id.add_contact_reminder_button);
        mAddPhotoButton = view.findViewById(R.id.add_photo_reminder_button);
        mShareButton = view.findViewById(R.id.share_reminder_button);
        mQrCodeScanButton = view.findViewById(R.id.qr_scan_reminder_button);

        mCallContactButton = view.findViewById(R.id.call_to_contact_button);
        mContactInfo = view.findViewById(R.id.contact_info);


        mTitleField = view.findViewById(R.id.reminder_title);
        mDetailsField = view.findViewById(R.id.reminder_notes);
        mPositionEditText = view.findViewById(R.id.position_coordinates);

        mPhotoImageView = view.findViewById(R.id.reminder_photo);
        mMarkerImageView = view.findViewById(R.id.position_marker);

        mQrCodeTextView = view.findViewById(R.id.reminder_qr_code_info);

        mTitleField.setText(mReminder.getTitle());
        mDetailsField.setText(mReminder.getDetails());

        mAlarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(getActivity(), AlarmManagerBroadcastReceiver.class);


        mPendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);

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

//        updateDate();

        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                PhotoViewerFragment dialog = PhotoViewerFragment.newInstance(mPhotoFile);
                dialog.setTargetFragment(ReminderFragment.this, REQUEST_PHOTO);
                dialog.show(fragmentManager, DIALOG_VIEWER);
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mimeType = "text/plain";

                Intent intent = ShareCompat.IntentBuilder.from(requireActivity())
                        .setType(mimeType)
                        .setText(getReminderreport())
                        .getIntent();
                startActivity(intent);
            }
        });


        mCallContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReminder.getContactNumber() == null) {
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL,
                            Uri.fromParts("tel", mReminder.getContactNumber(), null));
                    startActivity(callIntent);
                }

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
            mContactInfo.setText(mReminder.getContact() + "\n" + mReminder.getContactNumber());
            mContactInfo.setVisibility(View.VISIBLE);
            mCallContactButton.setVisibility(View.VISIBLE);
        }

        PackageManager packageManager = getActivity().getPackageManager();

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mAddPhotoButton.setEnabled(canTakePhoto);

        mAddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(requireActivity(), "com.example.orgaziner.data.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mQrCodeScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCodeScan();
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

        return view;
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
        FragmentManager fragmentManager = getFragmentManager();
        DetailsViewerFragment dialog = DetailsViewerFragment.newInstance(mReminder.getLongitude(), mReminder.getLatitude());
        dialog.setTargetFragment(ReminderFragment.this, REQUEST_DATE);
        dialog.show(fragmentManager, DIALOG_DATE);
    }

    private void datePicker() {
        FragmentManager fragmentManager = getFragmentManager();
        DatePickerFragment dialog = DatePickerFragment.newInstance(mReminder.getDate());
        dialog.setTargetFragment(ReminderFragment.this, REQUEST_DATE);
        dialog.show(fragmentManager, DIALOG_DATE);
    }

    private void timePicker() {
        FragmentManager fragmentManager = getFragmentManager();
        TimePickerFragment dialog = TimePickerFragment.newInstance(mReminder.getDate());
        dialog.setTargetFragment(ReminderFragment.this, REQUEST_TIME);
        dialog.show(fragmentManager, DIALOG_TIME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("RESULT", requestCode + resultCode + "");
        if (requestCode == REQUEST_DATE && data != null) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mReminder.setDate(date);
//            updateDate();
            timePicker();
        } else if (requestCode == REQUEST_TIME && data != null) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mReminder.setDate(date);
//            updateDate();
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
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhoto();
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

    private void updatePhoto() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoImageView.setImageDrawable(null);
            mPhotoImageView.setVisibility(View.GONE);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoImageView.setImageBitmap(bitmap);
            mPhotoImageView.setVisibility(View.VISIBLE);
        }
    }
}
