package com.subhamkumar.clipsy.panel;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import com.subhamkumar.clipsy.R;
import com.subhamkumar.clipsy.auth.home;
import com.subhamkumar.clipsy.models.Constants;
import com.subhamkumar.clipsy.panel.fragments.fragment_clips;
import com.subhamkumar.clipsy.panel.fragments.fragment_profile;
import com.subhamkumar.clipsy.panel.fragments.fragment_search;
import com.subhamkumar.clipsy.utils.Daemon;
import com.subhamkumar.clipsy.utils.LoginDb;
import com.subhamkumar.clipsy.utils.LoginDetails;
import com.subhamkumar.clipsy.utils.NotificationBroadcast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.subhamkumar.clipsy.utils.Message.getId;
import static com.subhamkumar.clipsy.utils.Message.getToken;
import static com.subhamkumar.clipsy.utils.NotificationHelper.notificationClickAction;

public class panel extends AppCompatActivity {

    // Top Right Action Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        createTopRightOptionMenu(menu);
        return true;
    }

    private void createTopRightOptionMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
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
        setActionIcons();
    }

    private void setActionIcons() {
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.newsfeed);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.search);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.user);
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
        viewPagerAdapter.addFragment(fragment_profile, "");
    }

    private void addSearch(ViewPagerAdapter viewPagerAdapter) {
        fragment_search fragment_search = new fragment_search();
        fragment_search.setArguments(user_details);
        viewPagerAdapter.addFragment(fragment_search, "");
    }

    private void addClip(ViewPagerAdapter viewPagerAdapter) {
        fragment_clips fragment_clips = new fragment_clips();
        fragment_clips.setArguments(user_details);
        viewPagerAdapter.addFragment(fragment_clips, "");
    }


    private void addTitleToDifferentTabs(ViewPager viewPager) {
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getSupportActionBar().setTitle("Clipsy");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Search");
                        break;
                    case 2:
                        getSupportActionBar().setTitle("Profile");
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

    String id, token;

    protected LoginDetails getloginDetails() {
        return new LoginDb(this).getLoginDetails();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panel);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);

        if (getIntent().getExtras() != null) {
            user_details = getIntent().getExtras();
        }
        else{
            LoginDetails loginDetails = getloginDetails();
            user_details = new Bundle();
            user_details.putString("id", loginDetails.ID);
            user_details.putString("token", loginDetails.TOKEN);
        }

        token = getToken(user_details);
        id = getId(user_details);
        initializeVaribles();
        ctx = this;
        daemon = new Daemon(ctx);
        daemonIntent = new Intent(ctx, daemon.getClass());
        if (!isNotificationServiceRunning(daemonIntent.getClass())) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(daemonIntent);
                }

                startService(daemonIntent);
        }
    }

    Context ctx;
    public Context getCtx() {
        return ctx;
    }

    private Daemon daemon;
    private Intent daemonIntent;

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

    public void startNotificationService() {

            // Setup a PendingIntent that will perform a broadcast
            Intent notificationIntent = new Intent(this, NotificationBroadcast.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);

    }



}
