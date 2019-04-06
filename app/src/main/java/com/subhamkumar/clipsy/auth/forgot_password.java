package com.subhamkumar.clipsy.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

public class forgot_password extends wrapper {
    @Override
    protected void handleErrorResponse(VolleyError error) {

        showNetworkUnavailableDialog();
    }

    private void showNetworkUnavailableDialog() {
        final Dialog dialog = new Dialog(forgot_password.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            sendForgotPassword();
        });

        dialog.show();
    }

    @Override
    public Map<String, String> _getHeaders() {
        return new HashMap<String, String>();
    }
    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap();
        params.put("email", getEmail());
        return params;
    }

    @NonNull
    private String getEmail() {
        String email = email_to_send.getText().toString().trim();
        return email;
    }

    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }

    @Override
    public String setHttpUrl() {
        return getString(R.string.request_user_forgot_password);
    }

    @Override
    public void handleResponse(String response) {

        Log.i("forgot_password", response);

        Gson gson = new Gson();

        ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

        if (apiResponse.success.equals(getString(R.string.status_success))) {
            startActivity(new Intent(forgot_password.this, change_password.class)
                    .putExtra("email", email_to_send.getText().toString().trim()));
        } else {
            status.setText(apiResponse.message);
        }

        Tools.hideNetworkLoadingDialog(networkLoadingDialog, "forgot_password hide");

    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private EditText email_to_send;
    private TextView email_label;
    private TextView status;
    private Dialog networkLoadingDialog;


    private void init() {
        email_to_send = findViewById(R.id.forgot_password_email);
        email_label = findViewById(R.id.forgot_password_email_label);
        status =  findViewById(R.id.forgot_password_status);
        networkLoadingDialog = new Dialog(forgot_password.this, R.style.TranslucentDialogTheme);
    }

    public void sendVerifyToken(View V) {
        sendForgotPassword();
    }

    private void sendForgotPassword() {
        // TODO check for proper email format
        if(getEmail().length() > 0) {
            Tools.showNetworkLoadingDialog(networkLoadingDialog, "forgot password show");
            status.setText("");
            email_label.setText("");
            makeRequest();
        }
        else{
            ((TextView) findViewById(R.id.forgot_password_email_label)).setText("Please fill email");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        init();

    }
}
