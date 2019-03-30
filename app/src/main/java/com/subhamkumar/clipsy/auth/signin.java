package com.subhamkumar.clipsy.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.SignInApiResponse;
import com.subhamkumar.clipsy.panel.panel;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class signin extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        return new HashMap<String, String>();
    }

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
        String url = getString(R.string.request_user_sign_in);
        return url;
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

        if (signInApiResponse.success.equals(getString(R.string.status_signin_failed))) {
            ((TextView) findViewById(R.id.signin_status)).setText(signInApiResponse.message);
        }

        else {

            startActivity(new Intent(signin.this, panel.class)
                            .putExtra("token", signInApiResponse.data.token)
                            .putExtra("id", signInApiResponse.data.id)
            );

            saveLoginDetails(signInApiResponse.data.token, signInApiResponse.data.id);
            this.finish();
        }

    }



    private String email;
    private Bundle bundle;

    private SharedPreferences localStore;
    private static final String myFile = "theAwesomeDataInMain";
    static String myKey = "52521";

    private void saveLoginDetails(String token, String userId) {
        localStore = getApplicationContext().getSharedPreferences(myFile, Context.MODE_PRIVATE);
        localStore.edit()
                .putString("token", token)
                .putString("id", userId)
                .commit();

    }

    private void checkLoginDetails() {
        localStore = getApplicationContext().getSharedPreferences(myFile, Context.MODE_PRIVATE);
        if (localStore.contains("token")) {
            Log.i("check_login", "contains email");
            startActivity(new Intent(signin.this, panel.class)
                    //  .putExtra("email", localStore.getString("email",""))
                    // .putExtra("type", localStore.getString("type",""))
                    // .putExtra("name", localStore.getString("name",""))
                    .putExtra("token", localStore.getString("token", ""))
                    .putExtra("id", localStore.getString("id", "")));


            this.finish();
        } else {
            Log.i("check_login", "donot contains email");
        }
    }

    private void deleteLoginDetails() {
        localStore = getApplicationContext().getSharedPreferences(myFile, Context.MODE_PRIVATE);
        localStore.edit().clear().commit();
    }

    String sign_out;
    private TextView label_signin_email;
    private TextView label_signin_pass;
    private EditText signin_email;
    private EditText signin_pass;

    private void initializeViews() {
        label_signin_email = findViewById(R.id.signin_email_label);
        label_signin_pass = findViewById(R.id.signin_pass_label);

        signin_email = findViewById(R.id.signin_email);
        signin_pass  = findViewById(R.id.signin_pass);
    }


    public void startSignin(View V) {
        if(validateFields()){
            makeRequest();
        }
    }


    private boolean showLabelIfEmptyField(String message, TextView label, EditText editText) {
        if(editText.getText().toString().trim().equals("")) {
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.signin);
        initializeViews();
        DeleteLoginDetailsIfSignOutIsInBundleOrCheckLoginDetails();

    }

    private void DeleteLoginDetailsIfSignOutIsInBundleOrCheckLoginDetails() {
        bundle = getIntent().getExtras();

        email = "";
        if (bundle!= null) {
            email = Objects.requireNonNull(getIntent().getExtras()).getString("email");
            ((EditText) findViewById(R.id.signin_email)).setText(email);
            if(bundle.containsKey("sign_out"))
                deleteLoginDetails();
            else{
                checkLoginDetails();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
