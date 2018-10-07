package com.subhamkumar.clipsy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.subhamkumar.clipsy.adapter.image_adapter;

public class choose_avatar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_avatar);

        GridView gridview = (GridView) findViewById(R.id.choose_avatar_gridview);
        gridview.setAdapter(new image_adapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(choose_avatar.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
