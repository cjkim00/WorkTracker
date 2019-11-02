package cjkim00.worktracker;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.FileInputStream;
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

    GraphView graphView;
    GraphView timeGraph;

    LineGraphSeries<DataPoint> series;
    LineGraphSeries<DataPoint> timeSeries;

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
        graphView = v.findViewById(R.id.graph);
        timeGraph = v.findViewById(R.id.time_graph);

        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(8);

        timeGraph.getViewport().setMinX(0);
        timeGraph.getViewport().setMaxX(8);
        //graphView.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
        //graphView.getViewport().setScrollable(true);  // activate horizontal scrolling
        //graphView.getViewport().setScalableY(true);  // activate horizontal and vertical zooming and scrolling
        //graphView.getViewport().setScrollableY(true);  // activate vertical scrolling

        //timeGraph.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
        //timeGraph.getViewport().setScrollable(true);  // activate horizontal scrolling
        //timeGraph.getViewport().setScalableY(true);  // activate horizontal and vertical zooming and scrolling
        //timeGraph.getViewport().setScrollableY(true);  // activate vertical scrolling

        series = new LineGraphSeries<>();
        timeSeries = new LineGraphSeries<>();
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

        graphView = v.findViewById(R.id.graph);
        timeGraph = v.findViewById(R.id.time_graph);

        series = new LineGraphSeries<>();
        timeSeries = new LineGraphSeries<>();

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

            //update the graph here
            /*
            for(int i = 0; i < newSteps.size(); i++) {
                series.appendData(new DataPoint(i, newSteps.get(i)), true, newSteps.size());
                timeSeries.appendData(new DataPoint(i, newTimes.get(i) / 60), true, newTimes.size());
            }
            */

            //get the latest 7 days to display on the graph
            //to be changed to disabling swiping the tab layout so the graph can be scrolled through
            ArrayList<DataPoint> stepPoints = new ArrayList<>();
            ArrayList<DataPoint> timePoints = new ArrayList<>();
            int size = 0;
            for(int i = newSteps.size() -1; i >= 0; i--) {
                stepPoints.add(new DataPoint(i, newSteps.get(i)));
                timePoints.add(new DataPoint(i, newTimes.get(i) / 60));
                size++;
                if(size > 8) {
                    break;
                }
            }
            ArrayList<DataPoint> revSteps = new ArrayList<>();
            ArrayList<DataPoint> revTimes = new ArrayList<>();
            for (int i = stepPoints.size() - 1; i >= 0; i--) {
                revSteps.add(stepPoints.get(i));
                revTimes.add(timePoints.get(i));
            }
            //reverse the points list as to order them correctly

            for(int i = 0; i < revSteps.size(); i++) {
                series.appendData(revSteps.get(i), true, revSteps.size());
                timeSeries.appendData(revTimes.get(i), true, revTimes.size());
            }
            graphView.addSeries(series);

            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX) {
                        if(value % 1 != 0) {
                            return "";
                        } else {
                            if(newDates.isEmpty()) {
                                return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            } else {
                                String[] temp = newDates.get((int) value).split("-");
                                return temp[1] + "/" + temp[2];
                            }
                        }
                    } else {
                        return super.formatLabel(value, isValueX);
                    }
                }
            });


            timeGraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                @Override
                public String formatLabel(double value, boolean isValueX) {
                    if(isValueX) {
                        if(value % 1 != 0) {
                            return "";
                        } else {
                            if(newDates.isEmpty()) {
                                return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            } else {
                                String[] temp = newDates.get((int) value).split("-");
                                return temp[1] + "/" + temp[2];
                            }
                        }
                    } else {
                        return super.formatLabel(value, isValueX);
                    }
                }
            });
            timeGraph.addSeries(timeSeries);

            minSteps.setText(String.valueOf(getMin(newSteps)));
            averageSteps.setText(String.valueOf(getAverage(newSteps)));
            maxSteps.setText(String.valueOf(getMax(newSteps)));

            minTimes.setText(formatTime(getMin(newTimes)));
            averageTimeWorked.setText(formatTime(getAverage(newTimes)));
            maxTimeWorked.setText(formatTime(getMax(newTimes)));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String formatTime(int time) {
        int[] convertedTodayTime = StopwatchFragment.convertTime(time);
        return String.format("%02d",convertedTodayTime[0]) + ":" + String.format("%02d",convertedTodayTime[1]) + ":" + String.format("%02d",convertedTodayTime[2]);
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
