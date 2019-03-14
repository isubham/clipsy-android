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

public class signin extends wrapper {
    @Override
    public Map<String, String> _getHeaders() {
        Map headers = new HashMap<String, String>();
        headers.put(getString(R.string.header_authentication), "");
        return headers;
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

    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        try {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (null != tm) {
                deviceUniqueIdentifier = tm.getDeviceId();
            }
            if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
                deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Allow IMEI to Continue", Toast.LENGTH_SHORT).show();
        }
        return deviceUniqueIdentifier;
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



    String email;
    Bundle bundle;

    SharedPreferences localStore;
    static String myFile = "theAwesomeDataInMain", myKey = "52521";

    public void saveLoginDetails(String token, String userId) {
        localStore = getApplicationContext().getSharedPreferences(myFile, Context.MODE_PRIVATE);
        localStore.edit()
                .putString("token", token)
                .putString("id", userId)
                .commit();

    }

    public void checkLoginDetails() {
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

    public void deleteLoginDetails() {
        localStore = getApplicationContext().getSharedPreferences(myFile, Context.MODE_PRIVATE);
        localStore.edit().clear().commit();
    }

    String sign_out;
    TextView label_signin_email, label_signin_pass;
    EditText signin_email, signin_pass;

    private void initializeViews() {
        label_signin_email = (TextView) findViewById(R.id.signin_email_label);
        label_signin_pass = (TextView) findViewById(R.id.signin_pass_label);

        signin_email = (EditText) findViewById(R.id.signin_email);
        signin_pass  = (EditText) findViewById(R.id.signin_pass);
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
            email = getIntent().getExtras().getString("email");
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
