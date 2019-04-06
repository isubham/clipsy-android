package com.subhamkumar.clipsy.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.utils.Tools;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class email_verification extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        return new HashMap<String, String>();
    }

    @Override
    protected void handleErrorResponse(VolleyError error) {

        showNetworkUnavailableDialog();

    }

    private void showNetworkUnavailableDialog() {
        final Dialog dialog = new Dialog(email_verification.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            sendEmailVerification();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public Map makeParams() {
        Map params = new HashMap<String, String>();
        params.put(getString(R.string.params_email), email);
        params.put(getString(R.string.params_verify_token), verify_token.getText().toString().trim());
        return params;
    }

    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }

    @Override
    public String setHttpUrl() {
        return getString(R.string.request_user_verify_email);
    }

    @Override
    public void handleResponse(String response) {
        Log.i("response", "email_verification : " + response);

        Gson gson = new Gson();
        ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

        if (apiResponse.success.equals(getString(R.string.status_email_verification_email_verified))) {
            startActivity(new Intent(email_verification.this, signin.class));
        }
        else if(apiResponse.success.equals(getString(R.string.status_email_verification_email_unverified))){
            ((TextView) findViewById(R.id.email_verification_status_message)).setText(apiResponse.message);
        }
        Tools.hideNetworkLoadingDialog(networkLoadingDialog, "email verification hide");
    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private String email;
    private EditText verify_token;
    private Button button_to_email;

    private void init() {
        verify_token = findViewById(R.id.verify_token);
        button_to_email = findViewById(R.id.send_verify_token);
        networkLoadingDialog = new Dialog(email_verification.this, R.style.TranslucentDialogTheme);


    }

    private String callback;
    private Bundle bundle;
    private Dialog networkLoadingDialog;


    private void setCallbackFromBundle() {
        bundle = getIntent().getExtras();
        email = "";
        if (bundle != null) {
            email = Objects.requireNonNull(getIntent().getExtras()).getString("email");
            callback = getIntent().getExtras().getString(getString(R.string.bundle_param_caller_activity_to_email_verification));
        }
    }

    private boolean showLabelIfEmptyField(String message, TextView label, EditText editText) {
        if(editText.getText().toString().trim().equals("")) {
            label.setText(message);
            return true;
        }

        label.setText("");
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        setCallbackFromBundle();
        init();
        button_to_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendEmailVerification();
            }
        });

    }

    private void sendEmailVerification() {
        Tools.showNetworkLoadingDialog(networkLoadingDialog, "email verification show");
        TextView statusTextView = findViewById(R.id.verify_token_status);
        if (!showLabelIfEmptyField("Token cannot be empty", statusTextView, verify_token) ) {
            makeRequest();
        }
    }
}
