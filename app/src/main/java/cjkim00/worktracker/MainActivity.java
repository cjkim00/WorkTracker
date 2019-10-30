package cjkim00.worktracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;


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
    public void onPause() {
        super.onPause();
        SharedPreferences preferences = getSharedPreferences("sharedPrefs", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("CLASSLABEL", this.getLocalClassName()); // value to store

        // Commit to storage
        editor.commit();
    }

    @Override
    public void onBackPressed() {
        if (true) {
            //add popup window to save before closing eventually
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public void OnSaveButtonPressed() {
        statisticsFragment.updateStats();
    }

}