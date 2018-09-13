package com.subhamkumar.clipsy;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class panel extends AppCompatActivity {


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

    }

    private void settingVariables() {

        setupViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {

        fragment_search fragment_search = new fragment_search();
        fragment_search.setArguments(user_details);

        fragment_complete_profile fragment_complete_profile = new fragment_complete_profile();
        fragment_complete_profile.setArguments(user_details);

        fragment_clips fragment_clips = new fragment_clips();
        // TODO : add new method for fetching post of following
        user_details.putString("fx", "read_clips");
        fragment_clips.setArguments(user_details);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(fragment_clips, "Clips");
        viewPagerAdapter.addFragment(fragment_search, "Search");
        viewPagerAdapter.addFragment(fragment_complete_profile , "Profile");

        viewPager.setAdapter(viewPagerAdapter);
    }

    Bundle user_details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel);

        if(getIntent().getExtras() != null) {
            user_details = getIntent().getExtras();
        }

        initializeVaribles();

        settingVariables();

        setupViewPager(viewPager);
    }
}
