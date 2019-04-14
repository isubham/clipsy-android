package com.subhamkumar.clipsy.panel;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.auth.home;
import com.subhamkumar.clipsy.auth.signin;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.panel.fragments.fragment_clips;
import com.subhamkumar.clipsy.panel.fragments.fragment_complete_profile;
import com.subhamkumar.clipsy.panel.fragments.fragment_search;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class panel extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.write_clip:
                toCreateClip();
                break;
            case R.id.sign_out:
                toSignOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toSignOut() {
        startActivity(new Intent(panel.this, home.class).putExtra(Constants.SIGNOUT, "1"));
        this.finish();
    }

    //  Creating adapter for the viewpager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            if (!title.equals("")) {
                mFragmentTitleList.add(title);
            }
        }

    }

    // end of adapter classs
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private void initializeVaribles() {
        viewPager = findViewById(R.id.user_panel_viewpager);
        tabLayout = findViewById(R.id.user_panel_tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setElevation(0);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.newsfeed);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.search);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.user);

    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        user_details.putString(getString(R.string.bundle_param_caller_activity_to_fragment_clips),
                getString(R.string.bundle_param_caller_activity_panel));

        fragment_clips fragment_clips = new fragment_clips();
        fragment_clips.setArguments(user_details);
        viewPagerAdapter.addFragment(fragment_clips, "");

        fragment_search fragment_search = new fragment_search();
        fragment_search.setArguments(user_details);
        viewPagerAdapter.addFragment(fragment_search, "");

        fragment_complete_profile fragment_complete_profile = new fragment_complete_profile();
        fragment_complete_profile.setArguments(user_details);
        viewPagerAdapter.addFragment(fragment_complete_profile, "");


        viewPager.setAdapter(viewPagerAdapter);
    }

    private Bundle user_details;

    private void toCreateClip() {
        startActivity(new Intent(panel.this, editor.class)
                .putExtra("token", token)
                .putExtra("action", "save")


        );
    }

    private String token; // for createClip

    // for persistence

    @Override
    public void onBackPressed() {

        final Dialog dialog = new Dialog(panel.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_backbutton_click);

        dialog.findViewById(R.id.dialog_backbutton_close).setOnClickListener(v -> {
            Intent toHomeForClose = new Intent(panel.this, home.class)
                    .putExtras(user_details).putExtra(Constants.CLOSE, "1");
            startActivity(toHomeForClose);
            finish();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.dialog_backbutton_cancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        if (getIntent().getExtras() != null) {
            user_details = getIntent().getExtras();
            token = user_details.getString("token");
            id = user_details.getString("id");
        }

        initializeVaribles();
    }
}
