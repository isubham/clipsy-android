package com.subhamkumar.clipsy.panel.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.subhamkumar.clipsy.R;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_complete_profile extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        setUiVariables();
        addProfileAndClipFragments();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_profile, container, false);
    }

    private void addProfileAndClipFragments() {

    }


    private void setUiVariables() {

        if (Objects.requireNonNull(getActivity()).getIntent().getExtras() != null) {
            Bundle user_detail = getActivity().getIntent().getExtras();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onStop() {
        super.onStop();
    }

}
