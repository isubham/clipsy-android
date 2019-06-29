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

import com.subhamkumar.clipsy.R;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_complete_profile extends Fragment {


    private LinearLayout _profile;
    private LinearLayout _clips;
    private View V;
    private Bundle user_detail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        V = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        setUiVariables();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_complete_profile, container, false);
    }

    fragment_profile fragment_profile;
    fragment_clips fragment_clips;
    private void addProfileAndClipFragments() {
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment_profile = new fragment_profile();
        fragment_clips = new fragment_clips();


        if (getActivity().getIntent().getExtras() != null) {
            user_detail = getActivity().getIntent().getExtras();
            fragment_profile.setArguments(user_detail);
            fragment_clips.setArguments(user_detail);
        }


        fragmentTransaction.add(_profile.getId(), fragment_profile);
        fragmentTransaction.add(_clips.getId(), fragment_clips);

        fragmentTransaction.commit();
    }


    private void setUiVariables() {
        // inflat fragment_clips and fragment_profile
        _profile = V.findViewById(R.id.profile_complete_linearLayout);
        _clips = V.findViewById(R.id.clips_complete_linearLayout);
    }

    @Override
    public void onResume() {
        addProfileAndClipFragments();
        super.onResume();

    }


    @Override
    public void onPause() {
        removeProfileAndClips();
        super.onPause();
    }




    private void removeProfileAndClips() {
        try{
            FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();

            transaction.remove(fragment_clips);
            transaction.remove(fragment_profile);

            transaction.commit();
        }catch(Exception e){
        }
    }
}
