package com.subhamkumar.clipsy.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.SignInApiResponse;
import com.subhamkumar.clipsy.panel.panel;
import com.subhamkumar.clipsy.utils.LoginPersistance;
import com.subhamkumar.clipsy.utils.VolleySingleton;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.subhamkumar.clipsy.models.Constants.RC_SIGN_IN;

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


        SignInButton signInButton = findViewById(R.id.google_sign_button);
        signInButton.setOnClickListener(v -> {

            startGoogleLogin();
        });


    }

    private void startGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleSignInResult(task);
        }
    }

    private void createAccountOrSignInWithGoogle(String email, String name, String profilePic) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.request_google_signin,
                response -> {
                    SignInApiResponse signInApiResponse = new Gson().fromJson(response, SignInApiResponse.class);

                    SharedPreferences localStore =
                            getApplicationContext().getSharedPreferences(Constants.myFile, Context.MODE_PRIVATE);

                    saveLoginDetails(localStore, signInApiResponse.data.token, signInApiResponse.data.id);

                    if (signInApiResponse.success.equals("1")) {

                        // TODO account created goto choose interest
                        startActivity(new Intent(home.this, panel.class)
                                .putExtra("token", signInApiResponse.data.token)
                                .putExtra("id", signInApiResponse.data.id)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        );

                    }
                    // signed in goto panel
                    else {
                         startActivity(new Intent(home.this, panel.class)
                                .putExtra("token", signInApiResponse.data.token)
                                .putExtra("id", signInApiResponse.data.id)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        );
                    }

                },
                error -> {

                }
        ){


            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.param_email, email);
                params.put(Constants.param_name, name);
                params.put(Constants.param_profile_pic, profilePic);
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return new HashMap<>();

            }
        };

        VolleySingleton.getInstance(home.this).addToRequestQueue(stringRequest);

    }



    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String email = account.getEmail();
            String displayName = account.getDisplayName();
            String photoUrl = account.getPhotoUrl() == null ? "0" : account.getPhotoUrl().toString();
            createAccountOrSignInWithGoogle(email, displayName, photoUrl);

            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("GE", e.getMessage());
        }
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

            } else {
                if (loginDetailsExist) {
                    gotoPanelIFLoginDetailsExist(localStore);
                }
            }
        } else {
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
        return LoginPersistance.GetToken(getApplicationContext()) != null;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
