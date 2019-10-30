package cjkim00.worktracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View v = inflater.inflate(R.layout.fragment_statistics, container, false);
        v = inflater.inflate(R.layout.fragment_statistics, container, false);
        //setViews(v);
        updateStats();
        return v;
    }

    public void setViews(View v) {
        minSteps = v.findViewById(R.id.MinSteps);
        minTimes = v.findViewById(R.id.MinTime);
        maxSteps = v.findViewById(R.id.MaxSteps);
        maxTimeWorked = v.findViewById(R.id.MaxTime);
        averageSteps = v.findViewById(R.id.AverageSteps);
        averageTimeWorked = v.findViewById(R.id.AverageTime);

        minSteps.setText("0");
        minTimes.setText("0");
        maxSteps.setText("0");
        maxTimeWorked.setText("0");
        averageSteps.setText("0");
        averageTimeWorked.setText("0");
    }

    public void updateStats() {
        setViews(v);
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

            int minTime = getMin(newTimes);
            int avgTime = getAverage(newTimes);
            int maxTime = getMax(newTimes);

            int minStep = getMin(newSteps);
            int avgStep = getAverage(newSteps);
            int maxStep = getMax(newSteps);

            int[] convertedMinTime = StopwatchFragment.convertTime(minTime);
            int[] convertedAvgTime = StopwatchFragment.convertTime(avgTime);
            int[] convertedMaxTime = StopwatchFragment.convertTime(maxTime);
            //String hours = String.format("%02d",convertedTime[0]);
            String minTimeString = String.format("%02d",convertedMinTime[0]) + ":" + String.format("%02d",convertedMinTime[1]) + ":" + String.format("%02d",convertedMinTime[2]);
            String avgTimeString = String.format("%02d",convertedAvgTime[0]) + ":" + String.format("%02d",convertedAvgTime[1]) + ":" + String.format("%02d",convertedAvgTime[2]);
            String maxTimeString = String.format("%02d",convertedMaxTime[0]) + ":" + String.format("%02d",convertedMaxTime[1]) + ":" + String.format("%02d",convertedMaxTime[2]);

            minSteps.setText(String.valueOf(minStep));
            averageSteps.setText(String.valueOf(avgStep));
            maxSteps.setText(String.valueOf(maxStep));

            minTimes.setText(minTimeString);
            averageTimeWorked.setText(avgTimeString);
            maxTimeWorked.setText(maxTimeString);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }




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
