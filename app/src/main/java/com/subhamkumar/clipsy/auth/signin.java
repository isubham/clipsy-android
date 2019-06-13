package com.subhamkumar.clipsy.auth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.SignInApiResponse;
import com.subhamkumar.clipsy.panel.panel;
import com.subhamkumar.clipsy.utils.Tools;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signin extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        return new HashMap<>();
    }

    @Override
    protected void handleErrorResponse(VolleyError error) {

        Log.e("n/w bug", error.getMessage());

        showNetworkUnavailableDialog();
        Tools.hideNetworkLoadingDialog(networkLoadingDialog, "signin hide");
    }

    private void showNetworkUnavailableDialog() {
        final Dialog dialog = new Dialog(signin.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            signIn();
        });

        dialog.show();
    }

    private Dialog networkLoadingDialog;


    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(signin.this).add(stringRequest);
    }

    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }

    @Override
    public String setHttpUrl() {
        return Constants.request_user_sign_in;
    }


    @Override
    public Map makeParams() {
        Map param = new HashMap<String, String>();
        param.put("email", ((EditText) findViewById(R.id.signin_email)).getText().toString().trim());
        param.put("password", ((EditText) findViewById(R.id.signin_pass)).getText().toString().trim());
        return param;
    }

    @Override
    public void handleResponse(String response) {
        Log.i("signin_data", response);

        Gson gson = new Gson();
        SignInApiResponse signInApiResponse = gson.fromJson(response, SignInApiResponse.class);

        if (signInApiResponse.success.equals(Constants.status_failed)) {
            ((TextView) findViewById(R.id.signin_status)).setText(signInApiResponse.message);
        } else {

            SharedPreferences localStore =
                     getApplicationContext().getSharedPreferences(Constants.myFile , Context.MODE_PRIVATE);

            saveLoginDetails(localStore, signInApiResponse.data.token, signInApiResponse.data.id);

            startActivity(new Intent(signin.this, panel.class)
                    .putExtra("token", signInApiResponse.data.token)
                    .putExtra("id", signInApiResponse.data.id)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );

            this.finish();
        }

        Tools.hideNetworkLoadingDialog(networkLoadingDialog, "sigin hide");
    }


    private String email;
    private Bundle bundle;

    private void saveLoginDetails(SharedPreferences localStore, String token, String id) {
        localStore.edit()
                .putString("token", token)
                .putString("id", id)
                .commit();

    }

    public void startSignin(View V) {
        signIn();
    }

    private void signIn() {
        if (validateFields()) {
            Tools.showNetworkLoadingDialog(networkLoadingDialog, "sign show");
            makeRequest();
        }
    }


    private boolean showLabelIfEmptyField(String message, TextView label, EditText editText) {
        if (editText.getText().toString().trim().equals("")) {
            label.setText(message);
            return false;
        }
        label.setText("");
        return true;
    }

    private boolean validateFields() {
        boolean isEmailEmpty = showLabelIfEmptyField("Email cannot by empty.", label_signin_email, signin_email);
        boolean isPasswordEmpty = showLabelIfEmptyField("Password cannot by empty.", label_signin_pass, signin_pass);
        return isEmailEmpty && isPasswordEmpty;
    }

    public void gotoSignup(View V) {
        startActivity(new Intent(signin.this, signup.class));
    }

    public void startForgetPassword(View V) {
        startActivity(new Intent(signin.this, forgot_password.class).putExtra("email",
                ((EditText) findViewById(R.id.signin_email)).getText().toString().trim()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenshotOff();
        setContentView(R.layout.signin);
        setTabHeightToZero();
        initializeViews();
    }

    private TextView label_signin_email;
    private TextView label_signin_pass;
    private EditText signin_email;
    private EditText signin_pass;

    private void initializeViews() {
        label_signin_email = findViewById(R.id.signin_email_label);
        label_signin_pass = findViewById(R.id.signin_pass_label);

        signin_email = findViewById(R.id.signin_email);
        signin_pass = findViewById(R.id.signin_pass);

        networkLoadingDialog = new Dialog(signin.this, R.style.TranslucentDialogTheme);
    }

    private void setTabHeightToZero() {
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
    }

    private void screenshotOff() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(signin.this, home.class));
    }

}
