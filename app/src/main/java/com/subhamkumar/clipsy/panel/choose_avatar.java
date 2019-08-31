package com.subhamkumar.clipsy.panel;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.GridView;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.adapter.image_adapter;

import java.util.Objects;

import static com.subhamkumar.clipsy.models.Constants.profilePicChangeRequest;

public class choose_avatar extends AppCompatActivity {



    private String profile_pic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_avatar);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        GridView gridview = findViewById(R.id.choose_avatar_gridview);
        gridview.setAdapter(new image_adapter(this));

        gridview.setOnItemClickListener((parent, v, position, id) -> {
            Intent toProfileSetting = new Intent();
            toProfileSetting.putExtra("profile_pic", Integer.toString(position));
            setResult(profilePicChangeRequest, toProfileSetting);
            finish();
        });
    }
}
