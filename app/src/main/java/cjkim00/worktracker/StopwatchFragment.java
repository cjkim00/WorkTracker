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
import android.widget.TextView;
import android.widget.Toast;
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


import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class StopwatchFragment extends Fragment implements SensorEventListener {

    boolean timerIsStarted = false;
    boolean isSaved = false;
    Chronometer chronometer;
    TextView steps;
    TextView todayStepsView;
    TextView todayTimeView;
    TextView textView;
    TextView textView2;

    Button saveButton;
    Button stopButton;

    int timeWhenStopped = 0;
    int totalSteps = 0;
    int todaySteps = 0;
    int todayTime = 0;
    long stoppedTime = 0;
    long elapsedSeconds = 0;
    long baseTime = 0;
    String todaysDate;
    SensorManager sensorManager;
    View v;
    private OnSaveButtonPressedListener mListener;

    public StopwatchFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        setViews(v);
        createFile(v);
        sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);



        saveButton = v.findViewById(R.id.save_button);
        stopButton = v.findViewById(R.id.start_button);
        stopButton.setOnClickListener(v12 -> {

            if(!timerIsStarted) {
                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                baseTime = SystemClock.elapsedRealtime() + timeWhenStopped;
                chronometer.start();
                saveButton.setVisibility(View.INVISIBLE);
                stopButton.setText("Stop");
                timerIsStarted = true;
            } else {
                timeWhenStopped = (int) (chronometer.getBase() - SystemClock.elapsedRealtime());
                adjustForTimeError();
                chronometer.stop();
                elapsedSeconds = Math.abs(timeWhenStopped);

                saveButton.setVisibility(View.VISIBLE);
                stopButton.setText("Start");
                timerIsStarted = false;
                isSaved = false;
            }
        });

        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setOnClickListener(v1 -> {
            if(!timerIsStarted) {
                if(!isSaved) {
                    todayTime += Math.abs(elapsedSeconds);
                    writeFile(v1, Math.abs(elapsedSeconds));
                    isSaved = true;
                    todaySteps += totalSteps;
                    setTodayValues();
                    elapsedSeconds = 0;
                    totalSteps = 0;
                    timeWhenStopped = 0;
                    stoppedTime = 0;
                    steps.setText(String.valueOf(totalSteps));
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    mListener.OnSaveButtonPressed();
                }
            } else {
                Toast.makeText(this.getContext(), "Cannot save while running", Toast.LENGTH_SHORT).show();
            }

        });
        getPreferences();
        return v;
    }

    public void setTodayValues() {
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
            for(int i = 0; i < newTimes.size(); i++) {
                if(newDates.get(i).equals(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()))) {
                    int[] convertedTodayTime = StopwatchFragment.convertTime(newTimes.get(i));
                    todayTimeView.setText(String.format("%02d",convertedTodayTime[0]) + ":" + String.format("%02d",convertedTodayTime[1]) + ":" + String.format("%02d",convertedTodayTime[2]));
                    todayStepsView.setText(String.valueOf(newSteps.get(i)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int[] convertTime(int time) {
        int hours = time / 3600;
        int remainder = time - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int seconds = remainder;

        return new int[]{hours, mins, seconds};
    }

    public void setViews(View v) {
        todayStepsView = v.findViewById(R.id.today_steps);
        todayTimeView = v.findViewById(R.id.today_time);

        textView = v.findViewById(R.id.textView10);
        textView2 = v.findViewById(R.id.textView6);

        todayStepsView.setText("0");
        todayTimeView.setText("00:00:00");

        chronometer =  v.findViewById(R.id.stopwatch);

        steps = (TextView) v.findViewById(R.id.step_counter2);
        steps.setText(String.valueOf(totalSteps));
    }

    public void createFile(View v) {
        if(!fileExists(v, "Work_Data_Final2.txt")) {
            try {
                FileOutputStream fileOutputStream = v.getContext().openFileOutput("Work_Data_Final2.txt", MODE_PRIVATE);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean fileExists(View v, String fileName) {
        File file = v.getContext().getFileStreamPath(fileName);
        return file != null && file.exists();
    }

    public void writeFile(View v, long elapsedSeconds) {
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String finalString = (elapsedSeconds / 1000) + " " + date + " " + totalSteps;
        try {
            FileInputStream fileInputStream = v.getContext().openFileInput("Work_Data_Final2.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

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

    public void resetTodaysStats() {
        getPreferences();
        todaySteps = 0;
        todayTime = 0;
        todayTimeView.setText(String.valueOf(todayTime));
        todayStepsView.setText(String.valueOf(todaySteps));
    }

    @Override
    public void onResume() {
        super.onResume();
        //getPreferences();
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

    private void shutdownToDo() {
        SharedPreferences stepPreferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("steps", Context.MODE_PRIVATE);
        SharedPreferences.Editor stepEditor = stepPreferences.edit();
        SharedPreferences preferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        elapsedSeconds = (int) (chronometer.getBase() - SystemClock.elapsedRealtime());
        String[] arr = String.valueOf(chronometer.getText()).split(":");
        editor.putInt("stoppedTime", (int) (chronometer.getBase() - SystemClock.elapsedRealtime()));
        if(timerIsStarted) {

        } else {
            editor.putInt("timeWhenStopped", (int) timeWhenStopped);
        }
        editor.putBoolean("isTimerRunning", timerIsStarted);
        editor.putInt("todayTime", todayTime);
        editor.putInt("totalSteps", totalSteps);
        editor.putInt("elapsedTime", (int) elapsedSeconds);
        editor.putLong("base", System.currentTimeMillis());
        editor.putLong("chromBase", chronometer.getBase());
        editor.commit();
        if(timerIsStarted) {
            Intent intent = new Intent(this.getContext(), PedometerService.class);
            this.getContext().startService(intent);
        }
    }

    /**
     * The chronometer sometimes will be off by up to half a second so this method
     * adjusts the timeWhenStopped variable to compensate for that difference
     * @return
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
        //shutdownToDo();
    }

    public void getPreferences() {
        int stoppedSteps = PedometerService.getSteps();
        if(stoppedSteps < 0) {
            stoppedSteps = 0;
        }
        Intent intent = new Intent(this.getContext(), PedometerService.class);
        this.getContext().stopService(intent);
        SharedPreferences preferences = Objects.requireNonNull(this.getActivity()).getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
        setTodayValues();
        if(preferences.contains("stoppedTime")) {
            stoppedTime = preferences.getLong("base", 0) - System.currentTimeMillis();
        }
        if(preferences.contains("timeWhenStopped")) {
            timeWhenStopped = preferences.getInt("timeWhenStopped", 0);
        }
        if(preferences.contains("elapsedTime")) {
            elapsedSeconds = preferences.getInt("elapsedTime", 0);
        }
        if(preferences.contains("isTimerRunning")) {
            if(preferences.getBoolean("isTimerRunning", false)) {
                Log.v("LOGS3", " TIMEWHENSTOPPED: " + timeWhenStopped + " ELAPSEDSECONDS: " + elapsedSeconds);
                chronometer.setBase(SystemClock.elapsedRealtime() + stoppedTime + elapsedSeconds);
                chronometer.start();
                saveButton.setVisibility(View.INVISIBLE);
                stopButton.setText("Stop");
                timerIsStarted = true;
            } else {
                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                saveButton.setVisibility(View.VISIBLE);
            }
        }

        if(preferences.contains("todayTime")) {
            todayTime = preferences.getInt("todayTime", 0);
            int[]convertedTime = convertTime((int) todayTime / 1000);
            String todaysTimeString = String.format("%02d",convertedTime[0]) + ":" + String.format("%02d",convertedTime[1]) + ":" + String.format("%02d",convertedTime[2]);
        }

        if(preferences.contains("totalSteps")) {
            totalSteps = preferences.getInt("totalSteps", 0) + stoppedSteps;
            if(timerIsStarted) {
                totalSteps--;
            }
            Log.v("STEPS2", " STEPS: " + totalSteps + " STOPPEDSTEPS: " + stoppedSteps);
            steps.setText(String.valueOf(totalSteps));
        }
    }

    public void setTime(TextView v, int[] convertedTime) {
        String todaysTimeString = String.format("%02d",convertedTime[0]) + ":" + String.format("%02d",convertedTime[1]) + ":" + String.format("%02d",convertedTime[2]);
        v.setText(todaysTimeString);
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


