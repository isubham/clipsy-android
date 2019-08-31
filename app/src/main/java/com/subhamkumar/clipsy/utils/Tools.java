package com.subhamkumar.clipsy.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {

    public static void _log(String message) {
        int i = 1;
        if (i == 1) Log.e("clipsy", message);
    }

    public static String text(View V) {
        return ((EditText) V.findViewById(V.getId())).getText().toString().trim();
    }

    public EditText getEditText(View view, int id) {
        return (EditText) view.findViewById(id);
    }

    public TextView getTextView(View view, int id) {
        return (TextView) view.findViewById(id);
    }

    public static boolean LabelMessageIfEmptyField(EditText editText, TextView label, String message) {
        if (editText.getText().toString().trim().equals("")) {
            label.setText(message);
            return false;
        } else return true;
    }

    public static void showSnackBar(View V, String message, int Color) {
        Snackbar.make(V, message, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color)
                .show();
    }

    public static String getToken(Context context) {
        return LoginPersistance.GetToken(context);
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span, Context context) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(@NonNull View view) {
                CustomTabs.openTab(context, span.getURL());
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    public static void showNetworkLoadingDialog(Dialog networkLoadingDialog, String activity) {
        // Log.e("tools n/w ", activity);
        networkLoadingDialog.setContentView(R.layout.dialog_network_loading);
        networkLoadingDialog.show();
    }

    public static void hideNetworkLoadingDialog(Dialog networkLoadingDialog, String... activity) {
       //  Log.e("tools n/w ", activity[0]);
        networkLoadingDialog.dismiss();
    }

    public static String getTimeStamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }



    public static void setNoHistory(Intent to_profile_result) {
        to_profile_result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    }}

