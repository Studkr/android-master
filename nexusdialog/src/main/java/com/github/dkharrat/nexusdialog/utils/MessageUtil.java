package com.github.dkharrat.nexusdialog.utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.github.dkharrat.nexusdialog.R;

public class MessageUtil {
    public static AlertDialog showAlertMessage(String title, String message, Context context) {
        Log.d("context", context + "");
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.ok), new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
               alertDialog.dismiss();
           }
        });
        alertDialog.show();
        return alertDialog;
    }

    public static ProgressDialog newProgressIndicator(String message, Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true);

        return progressDialog;
    }
}
