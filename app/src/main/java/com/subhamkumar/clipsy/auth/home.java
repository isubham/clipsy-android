package com.subhamkumar.clipsy.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.panel.panel;
import com.subhamkumar.clipsy.utils.Daemon;
import com.subhamkumar.clipsy.utils.LoginPersistance;

import java.util.Objects;

public class home extends AppCompatActivity {

    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        bundle = getIntent().getExtras();
        SharedPreferences localStore = getApplicationContext().getSharedPreferences(Constants.myFile, Context.MODE_PRIVATE);

        handleState(bundle, localStore);


    }


    private void handleState(Bundle bundle, SharedPreferences localStore) {

        if (bundle != null) {

            boolean closeButtonClicked = bundle.containsKey(Constants.CLOSE);
            boolean signOutButtonClicked = bundle.containsKey(Constants.SIGNOUT);
            boolean loginDetailsExist = checkLoginDetails();

            if (closeButtonClicked || signOutButtonClicked) {
                String token = bundle.getString("token");
                String id = bundle.getString("id");

                // if close is clicked from panel check if login details exist if not save login details and
                if (closeButtonClicked) {
                    if (!loginDetailsExist)
                        saveLoginDetails(localStore, token, id);
                    this.finish();
                }

                // if sign_out is clicked from panel delete login details and stay
                else {
                    deleteLoginDetails(localStore);
                }

            }
            else {
                if (loginDetailsExist) {
                    gotoPanelIFLoginDetailsExist(localStore);
                }
            }
        }
        else{
            boolean loginExist = checkLoginDetails();
            if (loginExist) {
                gotoPanelIFLoginDetailsExist(localStore);
            }
        }

    }


    private void saveLoginDetails(SharedPreferences localStore, String token, String id) {
        LoginPersistance.Save(id, token, getApplicationContext());
    }

    private boolean checkLoginDetails() {
        return LoginPersistance.GetToken(getApplicationContext()) !=  null;
    }

    private void gotoPanelIFLoginDetailsExist(SharedPreferences localStore) {

        startActivity(new Intent(home.this, panel.class)
                .putExtra("token", LoginPersistance.GetToken(getApplicationContext()))
                .putExtra("id", LoginPersistance.GetId(getApplicationContext())));

        this.finish();
    }


    private void deleteLoginDetails(SharedPreferences localStore) {
        LoginPersistance.Delete(getApplicationContext());
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

    public void startRingtone(View v) {
        sendBroadcast(new Intent(this, Daemon.class));
    }

    public void stopRingtone(View v) {
        stopService(new Intent(this, Daemon.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
