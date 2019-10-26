package com.subhamkumar.clipsy.panel;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.auth.home;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.panel.fragments.fragment_clips;
import com.subhamkumar.clipsy.panel.fragments.fragment_profile;
import com.subhamkumar.clipsy.panel.fragments.fragment_search;
import com.subhamkumar.clipsy.utils.CustomViewPager;
import com.subhamkumar.clipsy.utils.Daemon;
import com.subhamkumar.clipsy.utils.LoginPersistance;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.subhamkumar.clipsy.utils.Message.getId;
import static com.subhamkumar.clipsy.utils.Message.getToken;

public class panel extends AppCompatActivity {

    private enum TABS { _CLIPSY, _SEARCH, _PROFILE };


    private TABS initialTab;

    // End of Top Right Action Menu

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

        void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
            if (!"".equals("")) {
                mFragmentTitleList.add("");
            }
        }

    }
    // end of adapter classs


    private TabLayout tabLayout;

    private void initializeVaribles() {
        initialTab =  TABS._CLIPSY;
        CustomViewPager viewPager = findViewById(R.id.user_panel_viewpager);
        tabLayout = findViewById(R.id.user_panel_tabs);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setElevation(0);
        setActionIcons();
    }

    private void setActionIcons() {
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.round_library_books_black_48);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.round_search_black_48);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.round_person_black_48);
    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        user_details.putString(Constants.TO_HOME,
                Constants.PANEL);

        addClip(viewPagerAdapter);
        addSearch(viewPagerAdapter);
        addProfile(viewPagerAdapter);

        viewPager.setAdapter(viewPagerAdapter);
        addTitleToDifferentTabs(viewPager);

    }


    private void addProfile(ViewPagerAdapter viewPagerAdapter) {
        fragment_profile fragment_profile = new fragment_profile();
        fragment_profile.setArguments(user_details);
        viewPagerAdapter.addFragment(fragment_profile);
    }

    private void addSearch(ViewPagerAdapter viewPagerAdapter) {
        fragment_search fragment_search = new fragment_search();
        fragment_search.setArguments(user_details);
        viewPagerAdapter.addFragment(fragment_search);
    }

    private void addClip(ViewPagerAdapter viewPagerAdapter) {
        fragment_clips fragment_clips = new fragment_clips();
        fragment_clips.setArguments(user_details);
        viewPagerAdapter.addFragment(fragment_clips);
    }


    private void addTitleToDifferentTabs(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                switch (tabPosition) {
                    case 0:
                        Objects.requireNonNull(getSupportActionBar()).setTitle(Constants.CLIPSY);
                        break;
                    case 1:
                        Objects.requireNonNull(getSupportActionBar()).setTitle(Constants.SEARCH);
                        break;
                    case 2:
                        Objects.requireNonNull(getSupportActionBar()).setTitle(Constants.PROFILE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    Menu menuGlobal;



    private Bundle user_details;

    private void toCreateClip() {
        startActivity(new Intent(panel.this, editor.class)
                .putExtra("token", token)
                .putExtra("action", "save")

        );
    }


    // for persistence

    @Override
    public void onBackPressed() {

        showExitDialog();

    }

    private void showExitDialog() {
        final Dialog dialog = new Dialog(panel.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_backbutton_click);

        dialog.findViewById(R.id.dialog_backbutton_close).setOnClickListener(v -> {
            toHome();
            dialog.dismiss();
        });
        dialog.findViewById(R.id.dialog_backbutton_cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void toHome() {
        Intent toHomeForClose = new Intent(panel.this, home.class)
                .putExtras(user_details).putExtra(Constants.CLOSE, "1");
        startActivity(toHomeForClose);
        finish();
    }

    private String id;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        if (getIntent().getExtras() != null) {
            user_details = getIntent().getExtras();
        }
        else{
            user_details = new Bundle();
            user_details.putString("id", LoginPersistance.GetId(getApplicationContext()));
            user_details.putString("token", LoginPersistance.GetToken(getApplicationContext()));
        }

        token = getToken(user_details);
        id = getId(user_details);
        initializeVaribles();
        ctx = this;
        Daemon daemon = new Daemon(ctx);
        Intent daemonIntent = new Intent(ctx, daemon.getClass());
        if (!isNotificationServiceRunning(daemonIntent.getClass())) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(daemonIntent);
                }

                startService(daemonIntent);
        }
    }

    private Context ctx;
    public Context getCtx() {
        return ctx;
    }

    private boolean isNotificationServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    @Override
    protected void onDestroy() {
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }



}
