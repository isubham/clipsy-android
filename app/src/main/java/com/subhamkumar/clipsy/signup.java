package com.subhamkumar.clipsy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.models.CONSTANTS;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class signup extends wrapper {

    public void to_login(View V) {
        startActivity(new Intent(signup.this, signin.class));
    }

    public void create_account(View V) {

        make_request();
    }


    public String Text(int id) {
        return ((EditText) findViewById(id)).getText().toString().trim();
    }

    String _type;

    public void select_type(View V) {
        _type = (V.getId()) == R.id.__private ? "1" : "2";
    }

    @Override
    public Map makeParams() {
        Map param = new HashMap<String, String>();
        param.put("fx", CONSTANTS.OPERATION_SIGN_UP);
        param.put("email", Text(R.id.signup_email));
        param.put("password", Text(R.id.signup_password));
        param.put("name", Text(R.id.signup_name));
        param.put("type", _type);

        return param;
    }

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(signup.this).add(stringRequest);
    }


    @Override
    public void handle_error_response(VolleyError error) {
        Log.e("v_handle_error_res", error.getMessage());
    }

    @Override
    public void handle_response(String response) {
        Log.i("message", response);
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getString("status").equals("1") ) {
                // TODO : 1 go to signin after saying account created
                Toast.makeText(signup.this, "Account Created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(signup.this, signin.class).putExtra("email", text(R.id.signup_email)));
            } else {
                // TODO : 2 account didnot created give message show message
                Toast.makeText(signup.this, jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.e("json ex", e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

    }
}
