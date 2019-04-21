package com.subhamkumar.clipsy.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.utils.Tools;
import com.subhamkumar.clipsy.utils.wrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class change_password extends wrapper {

    @Override
    protected void handleErrorResponse(VolleyError error) {

        showNetworkUnavailableDialog();

    }

    private void showNetworkUnavailableDialog() {

        final Dialog dialog = new Dialog(change_password.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            changePassword();
        });

        dialog.show();
    }

    @Override
    public Map<String, String> _getHeaders() {
        return new HashMap<>();
    }
    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }
    @Override
    public String setHttpUrl() {
        return Constants.request_user_update_password;
    }
    @Override
    public Map makeParams() {
        Map param = new HashMap<String, String>();
        param.put("email", getEmailFromBundle());
        param.put("password", password);
        param.put("verify_token", token);
        Log.i("changePassParam", param.toString());
        return param;
    }

    @Override
    public void handleResponse(String response) {
        Log.i("change_pass", response);

        Gson gson = new Gson();
        ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

        if( apiResponse.success.equals(Constants.status_success) ) {
            startActivity(new Intent(change_password.this, signin.class)
                    .putExtra("email", getEmailFromBundle())
                    .putExtra("sign_out", "1")
            );
        }
        else {
            statusLabel.setText(apiResponse.message);
        }
        Tools.hideNetworkLoadingDialog(networkLoadingDialog, "change_password hide");

    }


    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    String confirmPassword;
    private String password;
    private String token;
    private Bundle bundle;
    private TextView confirmPasswordLabel;
    private TextView passwordLabel;
    private TextView tokenLabel;
    private TextView statusLabel;
    private EditText confirmPasswordEditText;
    private EditText passwordEditText;
    private EditText tokenEditText;
    private Dialog networkLoadingDialog;



    private void init() {
        passwordLabel = findViewById(R.id.change_password_new_password_label);
        confirmPasswordLabel = findViewById(R.id.change_password_confirm_new_password_label);
        tokenLabel = findViewById(R.id.change_password_token_label);
        statusLabel = findViewById(R.id.change_password_status);

        confirmPasswordEditText = findViewById(R.id.change_password_confirm_new_password);
        passwordEditText    = findViewById(R.id.change_password_new_password);
        tokenEditText    = findViewById(R.id.change_password_token);
        networkLoadingDialog = new Dialog(change_password.this, R.style.TranslucentDialogTheme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        init();
    }

    private String getEmailFromBundle() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            return getIntent().getExtras().getString("email");
        }
        return "";
    }

    private String text(int et) {
        return ((EditText) findViewById(et)).getText().toString().trim();
    }

    private boolean validateFields() {
        boolean isPassEmpty  = showLabelIfEmptyField("Password cannot by empty.", passwordLabel, passwordEditText);
        boolean isConfirmPassEmpty = showLabelIfEmptyField("Confirm password cannot by empty.", confirmPasswordLabel, confirmPasswordEditText);
        boolean isTokenEmpty = showLabelIfEmptyField("Token cannot by empty.", tokenLabel, tokenEditText);
        return isConfirmPassEmpty  && isPassEmpty && isTokenEmpty;
    }


    private boolean showLabelIfEmptyField(String message, TextView label, EditText editText) {
        if(editText.getText().toString().trim().equals("")) {
            label.setText(message);
            return false;
        }
        label.setText("");
        return true;
    }


    public void update_password(View V) {

        changePassword();
    }

    private void changePassword() {
        if(validateFields()) {
            if (text(R.id.change_password_new_password).equals(text(R.id.change_password_confirm_new_password))) {
                Tools.showNetworkLoadingDialog(networkLoadingDialog, "change_password show");
                password = text(R.id.change_password_new_password);
                token = text(R.id.change_password_token);
                makeRequest();
                statusLabel.setText("");
            }else{
                confirmPasswordLabel.setText("Password Don't Match");
            }
        }
    }

}
