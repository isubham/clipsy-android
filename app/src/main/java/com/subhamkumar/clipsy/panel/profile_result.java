package com.subhamkumar.clipsy.panel;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.auth.home;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.panel.fragments.fragment_profile;

import java.util.Objects;

public class profile_result extends AppCompatActivity {

    // profile of user => linerarlayout ( fragment_complete_profile )
    // TODO @input => viewed_id, viewer_id

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isFromNotification()) {
            Intent toHome = new Intent(profile_result.this, home.class);
            startActivity(toHome);
            finish();
            return true;
        }
        else{
            if (item.getItemId() == android.R.id.home) {
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean isFromNotification() {
        if (user_detail.containsKey(Constants.fromActivity)) {
            return user_detail.getString(Constants.fromActivity) == Constants.fromActivity_Notification;
        }
        return false;
    }

    Bundle user_detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_result);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        LinearLayout profile_result = findViewById(R.id.profile_result);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragment_profile fragment_profile = new fragment_profile();

        if(getIntent().getExtras() != null) {
            user_detail = getIntent().getExtras();
            fragment_profile.setArguments(user_detail);

            fragmentTransaction.add(profile_result.getId(), fragment_profile);
            fragmentTransaction.commit();
        }

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
