package com.subhamkumar.clipsy.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.utils.wrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class change_password extends wrapper {

    @Override
    public Map<String, String> _getHeaders() {
        return null;
    }
    @Override
    public int setHttpMethod() {
        return Request.Method.POST;
    }
    @Override
    public String setHttpUrl() {
        return getString(R.string.request_user_update_password);
    }
    @Override
    public Map makeParams() {
        Map param = new HashMap<String, String>();
        param.put("email", getEmailFromBundle());
        param.put("password", password);
        param.put("token", token);
        return param;
    }

    @Override
    public void handleResponse(String response) {
        Log.i("change_pass", response);

        Gson gson = new Gson();
        ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

        if(apiResponse.success.equals(getString(R.string.status_success))) {
            startActivity(new Intent(change_password.this, signin.class).putExtra("email", getEmailFromBundle()));
        }
        else {
            statusLabel.setText(apiResponse.message);
        }

    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    String confirmPassword, password, token;
    Bundle bundle;
    TextView confirmPasswordLabel, passwordLabel, tokenLabel, statusLabel;
    EditText confirmPasswordEditText, passwordEditText, tokenEditText;


    private void init() {
        passwordLabel = (TextView) findViewById(R.id.change_password_new_password_label);
        confirmPasswordLabel = (TextView) findViewById(R.id.change_password_confirm_new_password_label);
        tokenLabel = (TextView) findViewById(R.id.change_password_token_label);
        statusLabel = (TextView) findViewById(R.id.change_password_status);



        confirmPasswordEditText = (EditText) findViewById(R.id.change_password_confirm_new_password_label);
        passwordEditText    = (EditText) findViewById(R.id.change_password_new_password);
        tokenEditText    = (EditText) findViewById(R.id.change_password_token_label);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
    }

    private String getEmailFromBundle() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            return getIntent().getExtras().getString("email");
        }
        return "";
    }

    public String text(int et) {
        return ((EditText) findViewById(et)).getText().toString().trim();
    }

    private boolean validateFields() {
        boolean isPassEmpty  = showLabelIfEmptyField("Password cannot by empty.", passwordLabel, passwordEditText);
        boolean isConfirmPassEmpty = showLabelIfEmptyField("Email cannot by empty.", confirmPasswordLabel, confirmPasswordEditText);
        boolean isTokenEmpty = showLabelIfEmptyField("Name cannot by empty.", tokenLabel, tokenEditText);
        return isConfirmPassEmpty  && isPassEmpty && isPassEmpty;
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

        if(validateFields()) {
            if (text(R.id.change_password_new_password).equals(text(R.id.change_password_confirm_new_password))) {
                password = text(R.id.change_password_new_password);
                token = text(R.id.change_password_token);
                makeRequest();
            }else{
                confirmPasswordLabel.setText("Password Don't Match");
            }
        }
    }

}
