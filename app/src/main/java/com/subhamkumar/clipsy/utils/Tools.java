package com.subhamkumar.clipsy.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Profile;

public class Tools {

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


    public static void showNetworkLoadingDialog(Dialog networkLoadingDialog, String activity) {
        Log.e("tools n/w ", activity);
        networkLoadingDialog.setContentView(R.layout.dialog_network_loading);
        networkLoadingDialog.show();
    }

    public static void hideNetworkLoadingDialog(Dialog networkLoadingDialog, String... activity) {
        Log.e("tools n/w ", activity[0]);
        networkLoadingDialog.dismiss();
    }
}

