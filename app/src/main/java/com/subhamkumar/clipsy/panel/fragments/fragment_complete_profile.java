package com.subhamkumar.clipsy.panel.fragments;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.subhamkumar.clipsy.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_complete_profile extends Fragment {


    private View V;
    private Bundle user_detail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        V = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        setUiVariables();
        addProfileAndClipFragments();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_profile, container, false);
    }

    private void addProfileAndClipFragments() {

    }


    private void setUiVariables() {

        if (getActivity().getIntent().getExtras() != null) {
            user_detail = getActivity().getIntent().getExtras();
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
