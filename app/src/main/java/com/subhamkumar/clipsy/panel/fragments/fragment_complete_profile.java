package com.subhamkumar.clipsy.panel.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.subhamkumar.clipsy.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_complete_profile extends Fragment {


    LinearLayout _profile, _clips;
    View V;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        V = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        // inflat fragment_clips and fragment_profile
        _profile = (LinearLayout) V.findViewById(R.id.profile_complete_linearLayout);
        _clips = (LinearLayout) V.findViewById(R.id.clips_complete_linearLayout);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment_profile fragment_profile = new fragment_profile();
        fragment_clips fragment_clips = new fragment_clips();


        if(getActivity().getIntent().getExtras() != null) {
            Bundle user_detail = getActivity().getIntent().getExtras();
            fragment_profile.setArguments(user_detail);
            fragment_clips.setArguments(user_detail);
        }

        fragmentTransaction.add(_profile.getId(), fragment_profile);
        fragmentTransaction.add(_clips.getId(), fragment_clips);

        fragmentTransaction.commit();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_profile, container, false);
    }

}
