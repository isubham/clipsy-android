package com.subhamkumar.clipsy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.subhamkumar.clipsy.auth.signin;
import com.subhamkumar.clipsy.fragments.fragment_clips;
import com.subhamkumar.clipsy.fragments.fragment_complete_profile;
import com.subhamkumar.clipsy.fragments.fragment_search;

import java.util.ArrayList;
import java.util.List;

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
                to_create();
                break;
            case R.id.sign_out:
                to_sign_out();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void to_sign_out() {
        startActivity(new Intent(panel.this, signin.class).putExtra("sign_out", "1"));
        this.finish();
    }

    //    Creating adapter for the viewpager
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    // end of adapter classs
    ViewPager viewPager;
    TabLayout tabLayout;

    private void initializeVaribles() {
        viewPager = (ViewPager) findViewById(R.id.user_panel_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.user_panel_tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setElevation(6);

    }


    private void setupViewPager(ViewPager viewPager) {


        fragment_search fragment_search = new fragment_search();
        fragment_search.setArguments(user_details);

        fragment_complete_profile fragment_complete_profile = new fragment_complete_profile();
        fragment_complete_profile.setArguments(user_details);

        fragment_clips fragment_clips = new fragment_clips();
        // TODO : add new method for fetching post of following
        user_details.putString("fx", "following_clips");
        fragment_clips.setArguments(user_details);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(fragment_clips, "Clips");
        viewPagerAdapter.addFragment(fragment_search, "Search");
        viewPagerAdapter.addFragment(fragment_complete_profile , "Profile");

        viewPager.setAdapter(viewPagerAdapter);
    }

    Bundle user_details;

    public void to_create() {
        startActivity(new Intent(panel.this, editor.class).putExtra("user_id", user_id));
    }

    String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel);
        getSupportActionBar().setElevation(0);

        if(getIntent().getExtras() != null) {
            user_details = getIntent().getExtras();
            user_id = user_details.getString("user_id");
        }

        initializeVaribles();
        setupViewPager(viewPager);
    }
}
