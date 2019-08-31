package com.subhamkumar.clipsy.panel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.auth.home;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.models.ProfileApiResponse;
import com.subhamkumar.clipsy.utils.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.view.View.GONE;
import static com.subhamkumar.clipsy.models.Constants.profilePicChangeRequest;
import static com.subhamkumar.clipsy.models.Constants.request_user_update_avatar;

public class ProfileSetting extends wrapper {

    @Override
    public Map<String, String> _getHeaders() {
        Map params = new HashMap<String, String>();
        params.put(Constants.header_authentication, token);
        return params;
    }

    @Override
    protected void handleErrorResponse(VolleyError error) {
        showNetworkUnavailableDialog();
    }

    private void showNetworkUnavailableDialog() {
        final Dialog dialog = new Dialog(ProfileSetting.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_network_unavailable_confirmation);

        dialog.findViewById(R.id.dialog_nonet_exit).setOnClickListener(v -> dialog.dismiss());

        dialog.findViewById(R.id.dialog_nonet_continue).setOnClickListener(v -> {
            dialog.dismiss();
            makeRequest();
        });

        dialog.show();
    }

    @Override
    public int setHttpMethod() {
        return Request.Method.GET;
    }

    @Override
    public String setHttpUrl() {
        return String.format(Constants.request_user_get_user, areSameUser ? id : searcheUserId);
    }

    @Override
    public Map makeParams() {
        return new HashMap<String, String>();
    }

    String initialName, initialProfilePic;

    public String getInitialName() {
        return uiName.getText().toString().trim();
    }

    public void setInitialName(String initialName) {
        this.initialName = initialName;
    }

    public String getInitialProfilePic() {
        return initialProfilePic;
    }

    public void setInitialProfilePic(String initialProfilePic) {
        this.initialProfilePic = initialProfilePic;
    }

    @Override
    public void handleResponse(String response) {
        Log.i("setting", response);
        Gson gson = new Gson();
        ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);

        String profilePic = profileApiResponse.data.get(0).profile_pic;
        String parsedProficPic = profilePic.equals("") ? "0" : profilePic;

        inflatProfilePic(parsedProficPic);
        uiName.setText(profileApiResponse.data.get(0).name);

        setInitialName(profileApiResponse.data.get(0).name);
        setInitialProfilePic(parsedProficPic);
    }

    private void inflatProfilePic(String parsedProficPic) {
        int _profile_pic = Integer.parseInt(parsedProficPic);
        int imageResource = Constants.mThumbIds[_profile_pic];
        uiProfilePic.setImageResource(imageResource);
    }

    @Override
    public void makeVolleyRequest(StringRequest stringRequest) {
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void updateUser() {


        wrapper updateUserVolleyWrapper = new wrapper() {
            @Override
            protected Map makeParams() {

                Map updateUserParams = new HashMap<String, String>();
                updateUserParams.put("profile_pic", getInitialProfilePic());
                updateUserParams.put("name", getInitialName());
                return updateUserParams;

            }

            @Override
            protected void handleResponse(String response) {

                Gson gson = new Gson();
                ProfileApiResponse profileApiResponse = gson.fromJson(response, ProfileApiResponse.class);
                uiName.setText(profileApiResponse.data.get(0).name);
                inflatProfilePic(profileApiResponse.data.get(0).profile_pic);

                Toast.makeText(ProfileSetting.this, "Profile Updated.", Toast.LENGTH_SHORT).show();
                ProfileSetting.this.finish();
            }

            @Override
            protected void makeVolleyRequest(StringRequest stringRequest) {
                Volley.newRequestQueue(ProfileSetting.this).add(stringRequest);
            }

            @Override
            protected int setHttpMethod() {
                return Request.Method.POST;
            }

            @Override
            protected String setHttpUrl() {
                return String.format(request_user_update_avatar, id);
            }

            @Override
            protected Map<String, String> _getHeaders() {
                Map params = new HashMap<String, String>();
                params.put(Constants.header_authentication, token);
                return params;
            }

            @Override
            protected void handleErrorResponse(VolleyError error) {
                showNetworkUnavailableDialog();
            }
        };
        updateUserVolleyWrapper.makeRequest();
    }


    private String id;
    private String searcheUserId;
    private String token;
    private Button update_user_setting;
    private ImageView uiProfilePic;
    private boolean areSameUser;
    private EditText uiName;


    private String getName() {
        return ((EditText) findViewById(R.id.setting_name)).getText().toString().trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_settings);

        profileSettingLayout = findViewById(R.id.profileSettingLayout);

        Dialog networkLoadingDialog = new Dialog(ProfileSetting.this, R.style.TranslucentDialogTheme);
        setActionBar();

        geIdTokenSearchedIdFromBundle();

        setUiVariables();
        setClickListener();

        areSameUser = id.equals(searcheUserId);
        showUiElementsForSameUser(areSameUser);

        addChangeProfilePicTextListener();
        makeRequest();

    }

    private void geIdTokenSearchedIdFromBundle() {
        id = Objects.requireNonNull(getIntent().getExtras()).getString("id");
        searcheUserId = getIntent().getExtras().getString("searched_id");
        token = getIntent().getExtras().getString("token");
    }

    private void setUiVariables() {
        update_user_setting = findViewById(R.id.update_user_setting);
        uiProfilePic = findViewById(R.id.medium_avatar);
        uiName = findViewById(R.id.setting_name);
    }

    private void setClickListener() {
        update_user_setting.setOnClickListener(v -> updateUser());
    }

    private LinearLayout profileSettingLayout;

    private void showUiElementsForSameUser(boolean areSameUser) {
        if (areSameUser) {
            // TODO update profile pic and name

        } else {
            update_user_setting.setVisibility(GONE);
        }
    }

    private void addChangeProfilePicTextListener() {
        (findViewById(R.id.change_profile_pic_text)).setOnClickListener(v -> startActivityForResult(new Intent(ProfileSetting.this,
                        choose_avatar.class)
                , profilePicChangeRequest));
    }


    private String profile_pic;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == profilePicChangeRequest) {
            profile_pic = data.getStringExtra("profile_pic");
            setInitialProfilePic(profile_pic);
            inflatProfilePic(profile_pic);
        }
    }

    @Override
    protected void onRestart() {
        // makeRequest();
        super.onRestart();
    }

    private void setActionBar() {
        ActionBar bar = getSupportActionBar();
        Objects.requireNonNull(bar).setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.edit_profile);
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


    public void goToSignUp(View V) {
        Intent toSignUp = new Intent(ProfileSetting.this, home.class).putExtra(Constants.SIGNOUT, "1");
        toSignUp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(toSignUp);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
