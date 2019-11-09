package cjkim00.worktracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {

    BarChart chart ;
    ArrayList<BarEntry> BARENTRY ;
    BarDataSet Bardataset ;
    BarData BARDATA;

    BarChart stepChart;
    ArrayList<BarEntry> STEPENTRY ;
    BarDataSet stepBardataset ;
    BarData STEPDATA;

    TextView minSteps;
    TextView minTimes;
    TextView maxSteps;
    TextView maxTimeWorked;
    TextView averageSteps;
    TextView averageTimeWorked;
    View v;
    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_statistics, container, false);


        chart = (BarChart) v.findViewById(R.id.horizontalBarChart);
        chart.setFitBars(true);
        chart.getDescription().setEnabled(false);

        stepChart = (BarChart) v.findViewById(R.id.stepBarChart);
        stepChart.setFitBars(true);
        stepChart.getDescription().setEnabled(false);

        updateStats();

        return v;
    }

    /**
     * Sets the views of the statistics fragment
     */
    public void setViews() {
        minSteps = v.findViewById(R.id.MinSteps);
        minTimes = v.findViewById(R.id.MinTime);
        maxSteps = v.findViewById(R.id.MaxSteps);
        maxTimeWorked = v.findViewById(R.id.MaxTime);
        averageSteps = v.findViewById(R.id.AverageSteps);
        averageTimeWorked = v.findViewById(R.id.AverageTime);

        BARENTRY = new ArrayList<>();
        STEPENTRY = new ArrayList<>();


        minSteps.setText("0");
        minTimes.setText("0");
        maxSteps.setText("0");
        maxTimeWorked.setText("0");
        averageSteps.setText("0");
        averageTimeWorked.setText("0");
    }

    /**
     * Updates the stats and the graphs to reflect new data that has been put into the data file.
     */
    public void updateStats() {
        setViews();

        //chart.clear();
        chart = (BarChart) v.findViewById(R.id.horizontalBarChart);
        chart.getAxisRight().setAxisMinimum(0);
        chart.getAxisLeft().setAxisMinimum(0);
        //stepChart.clear();
        stepChart = (BarChart) v.findViewById(R.id.stepBarChart);
        stepChart.getAxisRight().setAxisMinimum(0);
        stepChart.getAxisLeft().setAxisMinimum(0);

        ArrayList<Integer> times = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> steps = new ArrayList<>();
        try {
            FileInputStream fileInputStream = v.getContext().openFileInput("Work_Data_Final2.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String lines;
            String[] split;
            while ((lines = bufferedReader.readLine()) != null) {
                split = lines.split("\\s+");
                times.add(Integer.parseInt(split[0]));
                dates.add(split[1]);
                steps.add(Integer.parseInt(split[2]));
            }
            fileInputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
            ArrayList<Integer> newTimes = new ArrayList<>();
            ArrayList<String> newDates = new ArrayList<>();
            ArrayList<Integer> newSteps = new ArrayList<>();
            //fill the new arraylist with the sum of the values of a any arbitrary day
            if(!dates.isEmpty()) {
                for(int i = 0; i < dates.size(); i++) {
                    //if the new arraylist does not contain the value, add it then search for
                    //values that are equal to it
                    if(!newDates.contains(dates.get(i))) {
                        newDates.add(dates.get(i));
                        newTimes.add(times.get(i));
                        newSteps.add(steps.get(i));
                        // if the value is in the arraylist then add to the time and steps
                    } else {
                        int tempTime = times.get(i);
                        int tempSteps = steps.get(i);
                        int index = newDates.indexOf(dates.get(i));

                        tempTime += newTimes.get(index);
                        tempSteps += newSteps.get(index);

                        newSteps.set(index, tempSteps);
                        newTimes.set(index, tempTime);
                    }
                }
            }
            Log.v("TESTDATA", String.valueOf(newDates.size()));

            //update both graphs
            for(int i = 0; i < newDates.size(); i++) {
                STEPENTRY.add(new BarEntry(i, newSteps.get(i)));
                BARENTRY.add(new BarEntry(i, (float) newTimes.get(i) / (float) 60));
            }

            Bardataset = new BarDataSet(BARENTRY, "Daily Times");
            BARDATA = new BarData(Bardataset);
            Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            chart.setData(BARDATA);

            chart.setVisibleXRangeMaximum(5f);
            chart.notifyDataSetChanged();
            setxAxis(chart, newDates);
            chart.invalidate();

            stepBardataset = new BarDataSet(STEPENTRY, "Daily Steps");
            STEPDATA = new BarData(stepBardataset);
            stepBardataset.setColors(ColorTemplate.COLORFUL_COLORS);
            stepChart.setData(STEPDATA);

            stepChart.setVisibleXRangeMaximum(5f);
            stepChart.notifyDataSetChanged();
            setxAxis(stepChart, newDates);
            stepChart.invalidate();

            //setxAxis(chart, newDates);
            //setxAxis(stepChart, newDates);

            minSteps.setText(String.valueOf(getMin(newSteps)));
            averageSteps.setText(String.valueOf(getAverage(newSteps)));
            maxSteps.setText(String.valueOf(getMax(newSteps)));

            minTimes.setText(formatTime(getMin(newTimes)));
            averageTimeWorked.setText(formatTime(getAverage(newTimes)));
            maxTimeWorked.setText(formatTime(getMax(newTimes)));

        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the x-axis labels for the graphs
     * @param chart the graph whose labels are to be changed
     * @param newDates the array containing the dates saved in the data file
     */
    public void setxAxis(BarChart chart, ArrayList<String> newDates) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if(newDates.size() == 0) {
                    return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                }
                if((int) value == newDates.size()) {
                    return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                }
                return newDates.get((int) value);
            }
        });
    }

    /**
     * Format the given time into the HH:MM:SS format.
     * @param time the amount of time given in seconds
     * @return the formatted string in the HH:MM:SS format.
     */
    public String formatTime(int time) {
        int[] convertedTodayTime = StopwatchFragment.convertTime(time);
        return String.format("%02d",convertedTodayTime[0]) + ":" + String.format("%02d",convertedTodayTime[1]) + ":" + String.format("%02d",convertedTodayTime[2]);
    }

    /**
     * Returns the average of the given list
     * @param list the list of integers
     * @return the average of the list
     */
    public int getAverage(ArrayList<Integer> list) {
        int sum = 0;
        if(!list.isEmpty()) {
            for(Integer value: list) {
                sum += value;
            }
            return (int) Math.ceil(sum / list.size());
        }
        return sum;
    }

    /**
     * Returns the maximum value of the list.
     * @param list the list of integers
     * @return the maximum value of the list
     */
    public int getMax(ArrayList<Integer> list) {
        int max = 0;
        if(!list.isEmpty()) {
            for(Integer value:list) {
                if(value > max) {
                    max = value;
                }
            }
            return max;
        }
        return max;
    }

    /**
     * Returns the minimum value of the list.
     * @param list the list of integers
     * @return the minimum value of the list
     */
    public int getMin(ArrayList<Integer> list) {
        int min = Integer.MAX_VALUE;
        if(!list.isEmpty()) {
            for(Integer value: list) {
                if(value < min) {
                    min = value;
                }
            }
            return min;
        } else {
            return 0;
        }
    }
}