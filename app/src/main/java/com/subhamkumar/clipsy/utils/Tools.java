package com.subhamkumar.clipsy.utils;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
        if(editText.getText().toString().trim().equals("")) {
            label.setText(message);
            return false;
        }
        else return true;
    }

}
