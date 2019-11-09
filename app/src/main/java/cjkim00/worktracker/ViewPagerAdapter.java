package cjkim00.worktracker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MotionEvent;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    StopwatchFragment stopwatchFragment;
    PedometerFragment pedometerFragment;
    StatisticsFragment statisticsFragment;
    private Context context;

    private int[] icons = {
        R.drawable.ic_timer_black_24dp,
        R.drawable.ic_assessment_black_24dp
    };

    public ViewPagerAdapter(FragmentManager fm,
                            StopwatchFragment stopwatchFragment,
                            PedometerFragment pedometerFragment,
                            StatisticsFragment statisticsFragment,
                            Context context) {

        super(fm);
        this.stopwatchFragment = stopwatchFragment;
        this.pedometerFragment = pedometerFragment;
        this.statisticsFragment = statisticsFragment;

        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return stopwatchFragment;
            case 1:
                return statisticsFragment;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

                Drawable image = context.getResources().getDrawable(icons[position]);
                image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                SpannableString sb = new SpannableString(" ");
                ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return sb;


    }



    @Override
    public int getCount() {
        return 2;
    }
}
