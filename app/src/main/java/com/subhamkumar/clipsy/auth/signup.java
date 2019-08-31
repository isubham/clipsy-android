package com.subhamkumar.clipsy.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.ApiResponse;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.utils.wrapper;
import com.subhamkumar.clipsy.utils.Tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class signup extends AppCompatActivity {

    private void signupRequest(String email, String password, String name) {

        wrapper signUpRequest = new wrapper() {

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
                return Constants.request_signup;
            }


            @Override
            public Map makeParams() {
                Map param = new HashMap<String, String>();
                param.put(Constants.param_email, email);
                param.put(Constants.param_password, password);
                param.put(Constants.param_name, name);
                param.put(Constants.param_type, Constants.param_value_public);
                return param;
            }

            @Override
            public void makeVolleyRequest(StringRequest stringRequest) {
                Volley.newRequestQueue(signup.this).add(stringRequest);
            }

            @Override
            public void handleResponse(String response) {

                Gson gson = new Gson();
                ApiResponse apiResponse = gson.fromJson(response, ApiResponse.class);

                if (apiResponse.success.equals(Constants.status_success)) {
                    hideErrorMessage();
                    showSignUpSuccessAndGoToEmailVerification();
                } else {
                    showErrorMessage(apiResponse.message);
                }
                Tools.hideNetworkLoadingDialog(networkLoadingDialog, "signup hide");
            }

            @Override
            public void handleErrorResponse(VolleyError error) {

                showNetworkUnavailableDialog();
            }
        };

        signUpRequest.makeRequest();

    }


    private void showNetworkUnavailableDialog() {
        final Dialog dialog = new Dialog(signup.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            createAccount();
        });

        dialog.show();
    }


    private void hideErrorMessage() {
        ((TextView) findViewById(R.id.signup_error_message)).setText("");
    }

    private void showErrorMessage(String message) {
        ((TextView) findViewById(R.id.signup_error_message)).setText(message);
    }


    private void createAccount() {
        if (validateFields()) {
            Tools.showNetworkLoadingDialog(networkLoadingDialog, "signup show");
            signupRequest(
                    Tools.text(findViewById(R.id.signup_email)),
                    Tools.text(findViewById(R.id.signup_password)),
                    Tools.text(findViewById(R.id.signup_name))
            );
        }
    }


    private void showSignUpSuccessAndGoToEmailVerification() {
        Toast.makeText(signup.this, Constants.message_signup_account_created, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(signup.this, email_verification.class)
                .putExtra("email", Tools.text(findViewById(R.id.signup_email))));
        this.finish();
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
        boolean isEmailEmpty = showLabelIfEmptyField("Email cannot by empty.", emailLabel, email);
        boolean isPassEmpty = showLabelIfEmptyField("Password cannot by empty.", passLabel, pass);
        boolean isNameEmpty = showLabelIfEmptyField("Name cannot by empty.", nameLabel, name);
        return isEmailEmpty && isNameEmpty && isPassEmpty;
    }

    private void findViewByIds() {
        emailLabel = findViewById(R.id.signup_email_label);
        passLabel = findViewById(R.id.signup_password_label);
        nameLabel = findViewById(R.id.signup_name_label);

        email = findViewById(R.id.signup_email);
        pass = findViewById(R.id.signup_password);
        name = findViewById(R.id.signup_name);

    }

    private TextView emailLabel;
    private TextView passLabel;
    private TextView nameLabel;

    private EditText email;
    private EditText pass;
    private EditText name;

    private Dialog networkLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        networkLoadingDialog = new Dialog(signup.this, R.style.TranslucentDialogTheme);
        setActionBar();
        findViewByIds();
    }

    private void setActionBar() {
        ActionBar bar= getSupportActionBar();
        Objects.requireNonNull(bar).setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.create_account);
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

    public void toLogin(View V) {
        startActivity(new Intent(signup.this, signin.class));
    }

    public void createAccount(View V) {
        createAccount();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
