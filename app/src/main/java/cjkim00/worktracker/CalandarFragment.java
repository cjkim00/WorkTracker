package cjkim00.worktracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalandarFragment extends Fragment {

    TextView log;
    ScrollView scrollView;
    public CalandarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calandar, container, false);
        log = v.findViewById(R.id.calandar_date_log);
        scrollView = (ScrollView) v.findViewById(R.id.calandar_scroller);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
        CalendarView calendarView = v.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //Toast.makeText(v.getContext(), String.valueOf(year) + " " + String.valueOf(month) + " " + String.valueOf(dayOfMonth), Toast.LENGTH_SHORT).show();
                log.setText("");
                setLogs(v, year, month, dayOfMonth);
            }
        });
        return v;
    }

    public void setLogs(View v, int year, int month, int dayOfMonth) {
        String date = String.valueOf(year) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(dayOfMonth);
        try {
            FileInputStream fileInputStream = v.getContext().openFileInput("Work_Data_Final2.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String lines;
            String[] split;
            StringBuilder stringBuffer = new StringBuilder();

            //skips the first line in the file to remove the placeholder 0 value
            if ((lines = bufferedReader.readLine()) != null) { }

            while ((lines = bufferedReader.readLine()) != null) {
                split = lines.split("\\s+");
                Log.v("datelog", "DATE: " + date + " DATE2:" + split[1] + " " + date.equals(split[1]));
                if (date.equals(split[1])) {
                    stringBuffer.append("Steps: ").append(String.valueOf(split[2])).append("    Time: ").append(StopwatchFragment.formatTime(Integer.parseInt(split[0]))).append("\n");
                }
            }
            log.setText(stringBuffer);
        } catch(FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
