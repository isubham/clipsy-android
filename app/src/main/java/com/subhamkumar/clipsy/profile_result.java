package com.subhamkumar.clipsy;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class profile_result extends AppCompatActivity {

    LinearLayout profile_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_result);

        profile_result = (LinearLayout) findViewById(R.id.profile_result);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment_complete_profile fragment_profile = new fragment_complete_profile();
        if(getIntent().getExtras() != null) {
            Bundle user_detail = getIntent().getExtras();
            user_detail.putString("fx", "read_clips");
            fragment_profile.setArguments(user_detail);
        }
        fragmentTransaction.add(profile_result.getId(), fragment_profile);
        fragmentTransaction.commit();
    }
}
