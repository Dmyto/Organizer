package com.example.organizer.ui.reminderfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.organizer.R;
import com.example.organizer.data.PictureUtils;

import java.io.File;

public class PhotoViewerFragment extends DialogFragment {
    private static final String ARG_PHOTO = "photo";

    public static PhotoViewerFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, file);

        PhotoViewerFragment fragment = new PhotoViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        File file = (File) getArguments().getSerializable(ARG_PHOTO);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        ImageView mImageView = view.findViewById(R.id.dialog_photo);
        Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), getActivity());
        mImageView.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
