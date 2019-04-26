package com.subhamkumar.clipsy.auth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.panel.panel;

import java.util.Objects;

public class home extends AppCompatActivity {

    private SharedPreferences localStore;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        bundle = getIntent().getExtras();
        localStore = getApplicationContext().getSharedPreferences(Constants.myFile , Context.MODE_PRIVATE);

        if (bundle != null) handleState(bundle, localStore);

    }


    private void handleState(Bundle bundle, SharedPreferences localStore) {
        boolean closeButtonClicked = bundle.containsKey(Constants.CLOSE);
        boolean signOutButtonClicked = bundle.containsKey(Constants.SIGNOUT);
        boolean loginDetailsExist =  checkLoginDetails();

        if (closeButtonClicked || signOutButtonClicked) {
            String token = bundle.getString("token");
            String id = bundle.getString("id");

            // if close is clicked from panel check if login details exist if not save login details and
            if (closeButtonClicked) {
                if (! checkLoginDetails())
                    saveLoginDetails(localStore, token, id);
                this.finish();
            }

            // if sign_out is clicked from panel delete login details and stay
            else {
                deleteLoginDetails(localStore);
            }

        }

        // else login details exist goto panel
        else
        {
            if(loginDetailsExist){
                gotoPanelIFLoginDetailsExist(localStore);
            }
        }
    }



    @SuppressLint("ApplySharedPref")
    private void saveLoginDetails(SharedPreferences localStore, String token, String id) {
        localStore.edit()
                .putString("token", token)
                .putString("id", id)
                .commit();

    }

    private boolean checkLoginDetails() {
        return localStore.contains("token") && localStore.contains("token");
    }

    private void gotoPanelIFLoginDetailsExist(SharedPreferences localStore) {
        Log.i("check_login", "contains email");
        startActivity(new Intent(home.this, panel.class)
                .putExtra("token", localStore.getString("token", ""))
                .putExtra("id", localStore.getString("id", "")));

        this.finish();
    }


    @SuppressLint("ApplySharedPref")
    private void deleteLoginDetails(SharedPreferences localStore) {
        localStore.edit().clear().commit();
    }



    /*
    *  click listeners
    * */
    public void gotoSignUp(View view) {
        startActivity(new Intent(home.this, signup.class));
    }

    public void gotoSignIn(View view) {
        startActivity(new Intent(home.this, signin.class).putExtra("sign_out", "1"));
    }

    public void gotoForgotPassword(View view) {
        startActivity(new Intent(home.this, forgot_password.class));
    }

}