package com.subhamkumar.clipsy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class email_verification extends wrapper {

    @Override
    public Map makeParams() {
        Map<String, String> params = new HashMap();
        params.put("email", email);
        params.put("fx", "verify_email_verification");
        params.put("verify_token", verify_token.getText().toString().trim());

        return params;
    }

    @Override
    public void handle_response(String response) {
        Log.i("response", "email_verification : " + response);

        // TODO handles response
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getString("status").equals("verified") ) {

                switch (callback) {
                    case "1": startActivity(new Intent(email_verification.this, signin.class)
                            .putExtra("email", email)); break;
                    case "2": startActivity(new Intent(email_verification.this, change_password.class)
                            .putExtra("email", email)); break;
                }
            } else {
                Toast.makeText(this, "Code is invalid", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("json ex", e.getMessage());
        }
    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    String email;
    EditText verify_token;
    Button button_to_email;

    private void init() {
        verify_token = (EditText) findViewById(R.id.verify_token);
        button_to_email = (Button) findViewById(R.id.send_verify_token);
    }

    String callback;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_verification);

        bundle = getIntent().getExtras();
        email = "";
        if(bundle!= null) {
            email = getIntent().getExtras().getString("email");
            callback = getIntent().getExtras().getString("callback");

        }
        init();
        button_to_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                make_request();
            }
        });

    }
}
