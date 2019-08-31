package com.subhamkumar.clipsy.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import android.view.MenuItem;
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
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.utils.Tools;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class email_verification extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        return new HashMap<>();
    }

    @Override
    protected void handleErrorResponse(VolleyError error) {

        showNetworkUnavailableDialog();

    }

    private void showNetworkUnavailableDialog() {
        final Dialog dialog = new Dialog(email_verification.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            sendEmailVerification();
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    public Map makeParams() {
        Map params = new HashMap<String, String>();
        params.put(Constants.param_email, email);
        params.put(Constants.param_verify_token, verify_token.getText().toString().trim());
        return params;
    }

    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }

    @Override
    public String setHttpUrl() {
        return Constants.request_user_verify_email;
    }

    @Override
    public void handleResponse(String response) {
        // Log.i("response", "email_verification : " + response);

        Gson gson = new Gson();
        ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

        if (apiResponse.success.equals(Constants.status_success)) {
            // TODO goto panel after saving login details
            startActivity(new Intent(email_verification.this, signin.class));
        }
        else if(apiResponse.success.equals(Constants.status_failed)){
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

    private Dialog networkLoadingDialog;


    private void setCallbackFromBundle() {
        Bundle bundle = getIntent().getExtras();
        email = "";
        if (bundle != null) {
            email = Objects.requireNonNull(getIntent().getExtras()).getString("email");
            String callback = getIntent().getExtras().getString(Constants.bundle_param_caller_activity_to_email_verification);
        }
    }

    private boolean showLabelIfEmptyField(TextView label, EditText editText) {
        if(editText.getText().toString().trim().equals("")) {
            label.setText("Token cannot be empty");
            return true;
        }

        label.setText("");
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);

        setActionBar();

        setCallbackFromBundle();
        init();
        button_to_email.setOnClickListener(view -> sendEmailVerification());

    }

    private void setActionBar() {
        ActionBar bar= getSupportActionBar();
        Objects.requireNonNull(bar).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(bar).setElevation(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return true;
    }

    private void sendEmailVerification() {
        Tools.showNetworkLoadingDialog(networkLoadingDialog, "email verification show");
        TextView statusTextView = findViewById(R.id.verify_token_status);
        if (!showLabelIfEmptyField(statusTextView, verify_token) ) {
            makeRequest();
        }
    }
}
