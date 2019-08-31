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

public class InsertTableDialogFragment extends AppCompatDialogFragment {

    private OnInsertClickListener listener;

    public static InsertTableDialogFragment newInstance() {
        return new InsertTableDialogFragment();
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_insert_table, null);
        final TextInputEditText columnCountEditText = view.findViewById(R.id.column_count);
        final TextInputEditText rowCountEditText = view.findViewById(R.id.row_count);

        AlertDialog.Builder dialog = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        dialog.setTitle(R.string.title_insert_table);
        dialog.setView(view);
        dialog.setPositiveButton(R.string.insert, (dialog1, which) -> {
            String colCount = Objects.requireNonNull(columnCountEditText.getText()).toString().trim();
            String rowCount = Objects.requireNonNull(rowCountEditText.getText()).toString().trim();

            if (listener != null) {
                listener.onInsertClick(Integer.valueOf(colCount), Integer.valueOf(rowCount));
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, (dialog12, which) -> dialog12.cancel());

        return dialog.create();
    }

    interface OnInsertClickListener {
        void onInsertClick(int colCount, int rowCount);
    }
}
