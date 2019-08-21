package com.subhamkumar.clipsy.panel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.panel.fragments.fragment_profile;

import java.util.Objects;

public class profile_result extends AppCompatActivity {

    // profile of user => linerarlayout ( fragment_complete_profile )
    // TODO @input => viewed_id, viewer_id

    private LinearLayout profile_result;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_result);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_result = findViewById(R.id.profile_result);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment_profile fragment_profile = new fragment_profile();

        if(getIntent().getExtras() != null) {
            Bundle user_detail = getIntent().getExtras();
            fragment_profile.setArguments(user_detail);
        }

        fragmentTransaction.add(profile_result.getId(), fragment_profile);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }



    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
