package cjkim00.worktracker;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements StopwatchFragment.OnSaveButtonPressedListener{

    StopwatchFragment stopwatchFragment;
    PedometerFragment pedometerFragment;
    StatisticsFragment statisticsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        stopwatchFragment = new StopwatchFragment();
        pedometerFragment = new PedometerFragment();
        statisticsFragment = new StatisticsFragment();

        Toolbar toolbar = findViewById(R.id.tab_layout);

        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.pager2);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
                                                                 stopwatchFragment,
                                                                 pedometerFragment,
                                                                 statisticsFragment,
                                                                 this);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }



    @Override
    public void OnSaveButtonPressed(View v) {
        statisticsFragment.updateStats(v);
    }
}