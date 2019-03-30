package com.subhamkumar.clipsy.panel;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.panel.fragments.fragment_complete_profile;

import java.util.Objects;

public class profile_result extends AppCompatActivity {

    // profile of user => linerarlayout ( fragment_complete_profile )
    // TODO @input => viewed_id, viewer_id

    private LinearLayout profile_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_result);

        profile_result = findViewById(R.id.profile_result);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment_complete_profile fragment_complete_profile = new fragment_complete_profile();

        if(getIntent().getExtras() != null) {
            Bundle user_detail = getIntent().getExtras();
            fragment_complete_profile.setArguments(user_detail);
        }

        fragmentTransaction.add(profile_result.getId(), fragment_complete_profile);
        fragmentTransaction.commit();
    }
}
