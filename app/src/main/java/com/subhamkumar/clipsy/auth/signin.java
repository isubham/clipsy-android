package com.subhamkumar.clipsy.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.models.CONSTANTS;
import com.subhamkumar.clipsy.panel;
import com.subhamkumar.clipsy.utils.wrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class signin extends wrapper {

    @Override
    public void make_volley_request(StringRequest stringRequest) {
        Volley.newRequestQueue(signin.this).add(stringRequest);
    }

    @Override
    public Map makeParams() {
        Map param = new HashMap<String, String>();
        param.put("fx", CONSTANTS.OPERATION_SIGN_IN);
        param.put("email", ((EditText) findViewById(R.id.signin_email)).getText().toString().trim());
        param.put("password", ((EditText) findViewById(R.id.signin_pass)).getText().toString().trim());
        return param;
    }

    @Override
    public void handle_response(String response) {
        /*
        {"1":{"name":"subham","email":"subham@gmail.com","type":"1"}}
         */
        try {
            JSONObject jsonObject = new JSONObject(response);

            // if failed attempt
            if (jsonObject.has("status")) {
                Toast.makeText(this, jsonObject.getString("status"), Toast.LENGTH_SHORT).show();
            }
            // TODO 3 : handle user data put in bundle and goto user panel
            else {

                JSONArray user_keys = (jsonObject.names());
                String user_id = (String) user_keys.get(0);

                String email = jsonObject.getJSONObject(user_id).getString("email");
                String type = jsonObject.getJSONObject(user_id).getString("type");
                String name = jsonObject.getJSONObject(user_id).getString("name");
                String profile_pic = jsonObject.getJSONObject(user_id).getString("profile_pic");

                /*
                @dependency
                name, user_id, type, c_user_id
                 */
                startActivity(new Intent(signin.this, panel.class)
                        .putExtra("email", email)
                        .putExtra("type", type)
                        .putExtra("name", name)
                        .putExtra("user_id", user_id)
                        .putExtra("profile_pic", profile_pic)
                        );

                save_login_details(email, type, name, user_id);
                this.finish();

            }

        } catch (JSONException e) {
            Log.e("json ex", e.getMessage());
        }
    }


    public void start_signin(View V) {
        make_request();
    }

    public void gotosignup(View V) {
        startActivity(new Intent(signin.this, signup.class));
    }

     public void start_forget_password(View V) {
        startActivity(new Intent(signin.this, send_verify_token.class).putExtra("email",
                ((EditText) findViewById(R.id.signin_email)).getText().toString().trim()));
     }

     String email;

    Bundle bundle;

    SharedPreferences localStore;
    static String myFile = "theAwesomeDataInMain", myKey = "52521";

    public void save_login_details(String email, String type, String name, String user_id) {
        localStore = getApplicationContext().getSharedPreferences(myFile, Context.MODE_PRIVATE);
        localStore.edit()
                .putString("email", email)
                .putString("type", type)
                .putString("name", name)
                .putString("user_id", user_id)
                .commit();

    }

    public void check_login_details() {
        localStore = getApplicationContext().getSharedPreferences(myFile, Context.MODE_PRIVATE);
        if (localStore.contains("email")){
            Log.i("check_login", "contains email");
                 startActivity(new Intent(signin.this, panel.class)
                         .putExtra("email", localStore.getString("email",""))
                        .putExtra("type", localStore.getString("type",""))
                        .putExtra("name", localStore.getString("name",""))
                        .putExtra("user_id", localStore.getString("user_id","")));
                 this.finish();
        }
        else{
            Log.i("check_login", "donot contains email");
        }
    }

    public void delete_login_details() {
        localStore = getApplicationContext().getSharedPreferences(myFile, Context.MODE_PRIVATE);
        localStore.edit().clear().commit();
    }

    String sign_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.signin);

        bundle = getIntent().getExtras();

        email = "";
        if (bundle!= null) {
            email = getIntent().getExtras().getString("email");
            ((EditText) findViewById(R.id.signin_email)).setText(email);
            if(bundle.containsKey("sign_out"))
                delete_login_details();
            else{
                check_login_details();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
