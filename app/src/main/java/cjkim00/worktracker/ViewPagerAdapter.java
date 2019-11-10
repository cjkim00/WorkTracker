package cjkim00.worktracker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MotionEvent;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    StopwatchFragment stopwatchFragment;
    PedometerFragment pedometerFragment;
    StatisticsFragment statisticsFragment;
    CalandarFragment calandarFragment;
    private Context context;

    private int[] icons = {
        R.drawable.ic_timer_black_24dp,
        R.drawable.ic_assessment_black_24dp,
            R.drawable.ic_today_black_24dp
    };

    public ViewPagerAdapter(FragmentManager fm,
                            StopwatchFragment stopwatchFragment,
                            PedometerFragment pedometerFragment,
                            StatisticsFragment statisticsFragment,
                            CalandarFragment calandarFragment,
                            Context context) {

        super(fm);
        this.stopwatchFragment = stopwatchFragment;
        this.pedometerFragment = pedometerFragment;
        this.statisticsFragment = statisticsFragment;
        this.calandarFragment = calandarFragment;

        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return stopwatchFragment;
            case 1:
                return statisticsFragment;
            case 2:
                return calandarFragment;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        /*
        switch(position) {
            case 0:
                Drawable image = ResourcesCompat.getDrawable(this.context.getResources(),  R.drawable.ic_timer_black_24dp, null);
                image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
                SpannableString sb = new SpannableString(" ");
                ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
                sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return sb;
            case 1:
                Drawable image2 = ResourcesCompat.getDrawable(this.context.getResources(),R.drawable.ic_assessment_black_24dp, null);
                image2.setBounds(0, 0, image2.getIntrinsicWidth(), image2.getIntrinsicHeight());
                SpannableString sb2 = new SpannableString(" ");
                ImageSpan imageSpan2 = new ImageSpan(image2, ImageSpan.ALIGN_BOTTOM);
                sb2.setSpan(imageSpan2, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return sb2;
            case 2:
                Drawable image3 = ResourcesCompat.getDrawable(this.context.getResources(), R.drawable.ic_today_black_24dp, null);
                image3.setBounds(0, 0, image3.getIntrinsicWidth(), image3.getIntrinsicHeight());
                SpannableString sb3 = new SpannableString(" ");
                ImageSpan imageSpan3 = new ImageSpan(image3, ImageSpan.ALIGN_BOTTOM);
                sb3.setSpan(imageSpan3, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                return sb3;
            default:
                return null;
        }
        */
        switch(position) {
            case 0:
                return "Stopwatch";
            case 1:
                return "Statistics";
            case 2:
                return "Calandar";
            default:
                return null;
        }

    }



    @Override
    public int getCount() {
        return 3;
    }
}
