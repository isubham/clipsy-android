package com.subhamkumar.clipsy.panel.fragments;


import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.subhamkumar.clipsy.R;

import java.util.Objects;

public class InsertLinkDialogFragment extends AppCompatDialogFragment {

    private OnInsertClickListener listener;

    public static InsertLinkDialogFragment newInstance() {
        return new InsertLinkDialogFragment();
    }

    public void setOnInsertClickListener(@NonNull OnInsertClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_insert_link, null);
        final TextInputEditText textToDisplayEditText = view.findViewById(R.id.text_to_display);
        final TextInputEditText linkToEditText = view.findViewById(R.id.link_to);

        AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        dialog.setTitle(R.string.title_insert_link);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.insert, (dialog1, which) -> {
            String title = Objects.requireNonNull(textToDisplayEditText.getText()).toString().trim();
            String url = Objects.requireNonNull(linkToEditText.getText()).toString().trim();

            if (listener != null) {
                listener.onInsertClick(title, url);
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, (dialog12, which) -> dialog12.cancel());

        return dialog.create();
    }

    public interface OnInsertClickListener {
        void onInsertClick(@NonNull String title, @NonNull String url);
    }
}
