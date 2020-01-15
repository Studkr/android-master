package com.INT.apps.GpsspecialDevelopment.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.INT.apps.GpsspecialDevelopment.R;

public class AlertDialogFragment extends DialogFragment {

    private final static String EXTRA_TITLE = "extra_title";

    private DialogActionListener listener;

    public static AlertDialogFragment getInstance(String title) {
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (DialogActionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement DialogActionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(EXTRA_TITLE);

        return new AlertDialog.Builder(getActivity())
                .setMessage(title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onPositiveButtonPressed();
                    }
                })
                .create();
    }
}
