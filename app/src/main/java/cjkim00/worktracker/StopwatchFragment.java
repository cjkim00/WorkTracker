package cjkim00.worktracker;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class StopwatchFragment extends Fragment implements SensorEventListener {

    boolean timerIsStarted = false;
    boolean isSaved = true;
    Button saveButton;
    Button stopButton;
    Button resetButton;
    Chronometer chronometer;
    GraphView graphView;
    int timeWhenStopped = 0;
    int totalSteps = 0;
    int todaySteps = 0;
    int todayTime = 0;
    final int ZERO = 0;
    LineGraphSeries<DataPoint> series;
    long stoppedTime = 0;
    long elapsedSeconds = 0;
    long baseTime = 0;
    SensorManager sensorManager;
    String todaysDate;
    TextView steps;
    TextView todayStepsView;
    TextView todayTimeView;
    TextView textView;
    TextView textView2;
    TextView todays_values;
    ScrollView scrollView;
    View v;
    private OnSaveButtonPressedListener mListener;

    public StopwatchFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        setViews();
        createFile();
        //addTestData();
        Log.v("logtest", "resume");
        sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);

        saveButton = v.findViewById(R.id.save_button);
        stopButton = v.findViewById(R.id.start_button);
        resetButton = v.findViewById(R.id.reset_button);
        stopButton.setOnClickListener(v12 -> {
            if(!timerIsStarted) {
                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                baseTime = SystemClock.elapsedRealtime() + timeWhenStopped;
                chronometer.start();
                stopButton.setText(getString(R.string.Stop));
                timerIsStarted = true;
            } else {
                timeWhenStopped = (int) (chronometer.getBase() - SystemClock.elapsedRealtime());
                adjustForTimeError();
                chronometer.stop();
                elapsedSeconds = Math.abs(timeWhenStopped);
                stopButton.setText(getString(R.string.Start));
                timerIsStarted = false;
                isSaved = false;
            }
        });

        saveButton.setOnClickListener(v1 -> {
            if(!timerIsStarted) {
                if(!isSaved) {
                    SharedPreferences preferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("totalSteps", 0);
                    editor.apply();
                    todayTime += Math.abs(elapsedSeconds);
                    writeFile(Math.abs(elapsedSeconds));
                    todaySteps += totalSteps;
                    setTodayValues();
                    resetValues();
                    mListener.OnSaveButtonPressed();
                }
            } else {
                Toast.makeText(this.getContext(), "Cannot save while running", Toast.LENGTH_SHORT).show();
            }
        });

        resetButton.setOnClickListener(v -> {
            if(!timerIsStarted) {
                resetValues();
            } else {
                Toast.makeText(this.getContext(), "Cannot reset while running", Toast.LENGTH_SHORT).show();
            }
        });
        getPreferences();
        return v;
    }

    public void resetValues() {
        isSaved = true;
        elapsedSeconds = ZERO;
        totalSteps = ZERO;
        timeWhenStopped = ZERO;
        stoppedTime = ZERO;
        steps.setText(String.valueOf(totalSteps));
        chronometer.setBase(SystemClock.elapsedRealtime());
    }


    /**
     * reads through the file containing the data and adds together values that are on the same day.
     * then it finds today's date and creates a string that acts as the log of all of today's times and steps.
     * the method then uses the added values to show today's total steps and times.
     */
    public void setTodayValues() {
        ArrayList<Integer> times = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> steps = new ArrayList<>();
        try {
            if(todaysDate.equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()))) {
                Log.v("date2", "3: " + todaysDate + ", " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
                FileInputStream fileInputStream = v.getContext().openFileInput("Work_Data_Final2.txt");
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String lines;
                String[] split;
                StringBuilder stringBuffer = new StringBuilder();

                //skips the first line in the file which was set to 0 to allow the graph to update properly
                if ((lines = bufferedReader.readLine()) != null) {
                }

                while ((lines = bufferedReader.readLine()) != null) {
                    split = lines.split("\\s+");
                    times.add(Integer.parseInt(split[0]));
                    dates.add(split[1]);
                    steps.add(Integer.parseInt(split[2]));
                    if (split[1].equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()))) {
                        stringBuffer.append("Steps: ").append(String.valueOf(split[2])).append("    Time: ").append(formatTime(Integer.parseInt(split[0]))).append("\n");
                    }
                }

                todays_values.setText(stringBuffer);

                fileInputStream.close();
                inputStreamReader.close();
                bufferedReader.close();
                ArrayList<Integer> newTimes = new ArrayList<>();
                ArrayList<String> newDates = new ArrayList<>();
                ArrayList<Integer> newSteps = new ArrayList<>();
                //fill the new arraylist with the sum of the values of a any arbitrary day
                if (!dates.isEmpty()) {
                    for (int i = 0; i < dates.size(); i++) {
                        //if the new arraylist does not contain the value, add it then search for
                        //values that are equal to it
                        if (!newDates.contains(dates.get(i))) {
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

                //looks through the newDates list to find today's date and when it does it gets the corresponding
                //values from the newSteps and newTimes lists and sets those values as today's steps and time.
                for (int i = 0; i < newTimes.size(); i++) {
                    if (newDates.get(i).equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()))) {
                        todayTimeView.setText(formatTime(newTimes.get(i)));
                        todayStepsView.setText(String.valueOf(newSteps.get(i)));
                    }
                }
            } else {
                todaysDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                todayTimeView.setText(formatTime(0));
                todayStepsView.setText(String.valueOf(0));
                todays_values.setText("");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method takes an integer, converts it into hours minutes and seconds then returns a
     * string formatted in the HH:MM:SS format
     * @param time the time to be formatted; given in seconds
     * @return returns the time formatted in the HH:MM:SS format
     */
    public static String formatTime(int time) {
        int[] convertedTodayTime = convertTime(time);
        return String.format("%02d",convertedTodayTime[0]) + ":" + String.format("%02d",convertedTodayTime[1]) + ":" + String.format("%02d",convertedTodayTime[2]);
    }

    /**
     * Converts a time, given in seconds, into its corresponding hours, minutes, seconds
     * @param time a time given in seconds
     * @return the time converted into hours, minutes, and seconds
     */
    public static int[] convertTime(int time) {
        int hours = time / 3600;
        int remainder = time - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int seconds = remainder;

        return new int[]{hours, mins, seconds};
    }

    /**
     * Sets the views of the stopwatch fragment.
     *
     */
    public void setViews() {
        todayStepsView = v.findViewById(R.id.today_steps);
        todayTimeView = v.findViewById(R.id.today_time);
        todays_values = v.findViewById(R.id.todays_values);
        graphView = v.findViewById(R.id.stepBarChart);
        series = new LineGraphSeries<DataPoint>();
        scrollView = (ScrollView) v.findViewById(R.id.SCROLLER_ID);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        textView = v.findViewById(R.id.textView10);
        textView2 = v.findViewById(R.id.textView6);

        todayStepsView.setText("0");
        todayTimeView.setText(getString(R.string.zero_time));

        chronometer =  v.findViewById(R.id.stopwatch);

        steps = v.findViewById(R.id.step_counter2);
        steps.setText(String.valueOf(totalSteps));
    }

    /**
     * If the data file does not exits, which only happens on installation, create  a file for the data
     * and put a starter value of zero steps, today's date, and zero seconds to allow the bar graph
     * to properly update.
     */
    public void createFile() {
        if(!fileExists("Work_Data_Final2.txt")) {
            try {
                FileOutputStream fileOutputStream = v.getContext().openFileOutput("Work_Data_Final2.txt", MODE_PRIVATE);
                //insert today's date with time and steps at 0 so the graphs are able to update the visuals properly
                //without this the updated bar is invisible until the app is restarted
                //this does not cause problems later on as the values are zero.
                StringBuilder setZero = new StringBuilder();
                setZero.append("0").append(" ").append(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())).append(" ").append(0);
                fileOutputStream.write(setZero.toString().getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if a particular file exists.
     * @param fileName the name of the file
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists(String fileName) {
        File file = v.getContext().getFileStreamPath(fileName);
        return file != null && file.exists();
    }

    /**
     *Writes the current steps and time with today's date into the data file.
     * @param elapsedSeconds the number of seconds that have elapsed after the start button was pressed
     */
    public void writeFile(long elapsedSeconds) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String finalString = (elapsedSeconds / 1000) + " " + date + " " + totalSteps;
        try {
            FileInputStream fileInputStream = v.getContext().openFileInput("Work_Data_Final2.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuffer = new StringBuilder();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines).append("\n");
            }
            stringBuffer.append(finalString).append("\n");
            fileInputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
            try {
                FileOutputStream fileOutputStream = v.getContext().openFileOutput("Work_Data_Final2.txt", MODE_PRIVATE);
                fileOutputStream.write(stringBuffer.toString().getBytes());
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //shutdownToDo();
    }

    @Override
    public void onResume() {
        super.onResume();
        //getPreferences();
        Log.v("logtest", "resume2");
        setTodayValues();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this.getContext(), "Sensor Not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        shutdownToDo();
    }

    /**
     * Saves the necessary information into a SharedPreference before the app closes in order to
     * keep the app's stopwatch and pedometer running when closed.
     */
    private void shutdownToDo() {
        Log.v("logtest", "shutdown");
        SharedPreferences preferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        elapsedSeconds = (int) (chronometer.getBase() - SystemClock.elapsedRealtime());
        if (!timerIsStarted) {
            editor.putInt("timeWhenStopped", (int) timeWhenStopped);
        }
        editor.putBoolean("isTimerRunning", timerIsStarted);
        editor.putBoolean("isSaved", isSaved);
        editor.putInt("todayTime", todayTime);
        editor.putInt("totalSteps", totalSteps);
        editor.putInt("elapsedTime", (int) elapsedSeconds);
        editor.putLong("base", System.currentTimeMillis());
        editor.putLong("chromBase", chronometer.getBase());
        editor.putString("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        editor.apply();
        if(timerIsStarted) {
            Intent intent = new Intent(this.getContext(), PedometerService.class);
            Objects.requireNonNull(this.getContext()).startService(intent);
        }
    }

    /**
     * The chronometer sometimes will be off by up to half a second so this method
     * adjusts the timeWhenStopped variable to compensate for that difference
     */
    public void adjustForTimeError() {
        String[] arr = String.valueOf(chronometer.getText()).split(":");
        int second = Integer.parseInt(arr[arr.length - 1]);
        if(second < Math.abs(timeWhenStopped / 1000)) {
            timeWhenStopped -= (timeWhenStopped % 1000) - 250;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shutdownToDo();
    }

    /**
     * This method gets the SharedPreferences and assigns the values to their corresponding variables.
     */
    public void getPreferences() {
        int stoppedSteps = PedometerService.getSteps();
        if(stoppedSteps < 0) {
            stoppedSteps = 0;
        }
        Intent intent = new Intent(this.getContext(), PedometerService.class);
        Objects.requireNonNull(this.getContext()).stopService(intent);
        SharedPreferences preferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        if(preferences.contains("base")) {
            stoppedTime = preferences.getLong("base", ZERO) - System.currentTimeMillis();
        }
        if(preferences.contains("timeWhenStopped")) {
            timeWhenStopped = preferences.getInt("timeWhenStopped", ZERO);
        }
        if(preferences.contains("elapsedTime")) {
            elapsedSeconds = preferences.getInt("elapsedTime", ZERO);
        }
        if(preferences.contains("isSaved")) {
            isSaved = preferences.getBoolean("isSaved", false);
        }
        if(preferences.contains("isTimerRunning")) {
            if(preferences.getBoolean("isTimerRunning", false)) {
                Log.v("LOGS3", " TIMEWHENSTOPPED1: " + timeWhenStopped + " ELAPSEDSECONDS: " + elapsedSeconds);
                chronometer.setBase(SystemClock.elapsedRealtime() + stoppedTime + elapsedSeconds);
                chronometer.start();
                stopButton.setText(getString(R.string.Stop));
                timerIsStarted = true;
            } else {
                Log.v("LOGS3", " TIMEWHENSTOPPED2: " + timeWhenStopped + " ELAPSEDSECONDS: " + elapsedSeconds);
                timerIsStarted = false;
                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            }
        }
        if(preferences.contains("todayTime")) {
            todayTime = preferences.getInt("todayTime", ZERO);
        }
        if(preferences.contains("totalSteps")) {
            totalSteps = preferences.getInt("totalSteps", ZERO) + stoppedSteps;
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if(countSensor != null) {
                if(timerIsStarted) {
                    totalSteps--;
                }
            }

            Log.v("STEPS2", " STEPS: " + totalSteps + " STOPPEDSTEPS: " + stoppedSteps);
            steps.setText(String.valueOf(totalSteps));
        }
        if(preferences.contains("date")) {
            todaysDate = preferences.getString("date", "");
            Log.v("date2", todaysDate);
        } else {
            todaysDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        }
        setTodayValues();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(timerIsStarted) {
            totalSteps++;
            steps.setText(String.valueOf(totalSteps));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnSaveButtonPressedListener) context;
    }


    protected interface OnSaveButtonPressedListener {
        void OnSaveButtonPressed();
    }

}


