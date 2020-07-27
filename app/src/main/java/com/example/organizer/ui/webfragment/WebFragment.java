package com.example.organizer.ui.webfragment;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.organizer.R;


public class WebFragment extends DialogFragment {

    private static final String ARG_LINK = "link";
    public static final String ARG_TITLE = "title";

//    private ProgressBar progressBar;

    public static WebFragment newInstance(String link, String title) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LINK, link);
        args.putSerializable(ARG_TITLE, title);

        WebFragment webFragment = new WebFragment();
        webFragment.setArguments(args);
        return webFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dialog d = getDialog();
        if (d != null) {
            d.getWindow().setLayout(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String link = (String) getArguments().getSerializable(ARG_LINK);
        String title = (String) getArguments().getSerializable(ARG_TITLE);

        View v = LayoutInflater.from(getContext()).inflate(R.layout.web_fragment, null);

//        progressBar = v.findViewById(R.id.web_progress_bar);

        WebView webView = (WebView) v.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true); //enable advertising)))
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                progressBar.setVisibility(View.VISIBLE);
//                progressBar.setTag("Loading...");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                progressBar.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(link);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(title)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
    }


}