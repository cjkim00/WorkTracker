package cjkim00.worktracker;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class StopwatchFragment extends Fragment implements SensorEventListener {

    boolean timerIsStarted = false;
    boolean firstTimeStarted = false;
    boolean isSaved = false;
    Chronometer chronometer;
    TextView displayText;
    TextView steps;
    TextView todayStepsView;
    TextView todayTimeView;
    TextView textView;
    TextView textView2;

    long timeWhenStopped = 0;
    int totalSteps = 0;
    int todaySteps = 0;
    int todayTime = 0;
    int saveTime = 0;
    long elapsedSeconds = 0;
    SensorManager sensorManager;
    private OnSaveButtonPressedListener mListener;

    public StopwatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        chronometer =  v.findViewById(R.id.stopwatch);
        steps = (TextView) v.findViewById(R.id.step_counter2);
        steps.setText(String.valueOf(totalSteps));
        sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);

        todayStepsView = v.findViewById(R.id.today_steps);
        todayTimeView = v.findViewById(R.id.today_time);

        textView = v.findViewById(R.id.textView10);
        textView2 = v.findViewById(R.id.textView6);

        todayStepsView.setVisibility(View.INVISIBLE);
        todayTimeView.setVisibility(View.INVISIBLE);

        textView.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        if(!fileExists(v, "Work_Data_Final2.txt")) {
            try {
                FileOutputStream fileOutputStream = v.getContext().openFileOutput("Work_Data_Final2.txt", MODE_PRIVATE);
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final Button saveButton = v.findViewById(R.id.save_button);
        final Button stopButton = v.findViewById(R.id.start_button);
        stopButton.setOnClickListener(v12 -> {

            if(!timerIsStarted) {
                chronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                chronometer.start();
                saveButton.setVisibility(View.INVISIBLE);
                stopButton.setText("Stop");
                timerIsStarted = true;
            } else {
                //saveTime = (int) (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
                chronometer.stop();
                timeWhenStopped = chronometer.getBase() - SystemClock.elapsedRealtime();
                elapsedSeconds = SystemClock.elapsedRealtime() - chronometer.getBase();
                todayTime += (int) elapsedSeconds;

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
                    //int elapsedSeconds = (int) Math.floor((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000);
                    todayStepsView.setVisibility(View.VISIBLE);
                    todayTimeView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    textView2.setVisibility(View.VISIBLE);

                    todaySteps += totalSteps;
                    todayStepsView.setText(String.valueOf((int)Math.floor(todaySteps)));
                    todayTimeView.setText(String.valueOf((int) Math.floor(todayTime)));
                    writeFile(v1, elapsedSeconds);


                    mListener.OnSaveButtonPressed(v);
                    steps.setText(String.valueOf(totalSteps));
                    isSaved = true;
                    totalSteps = 0;
                    timeWhenStopped = 0;
                    chronometer.setBase(SystemClock.elapsedRealtime());
                }
            } else {
                Toast.makeText(this.getContext(), "Cannot save while running", Toast.LENGTH_SHORT).show();
            }

        });

        return v;
    }

    public boolean fileExists(View v, String fileName) {
        File file = v.getContext().getFileStreamPath(fileName);
        return file != null && file.exists();
    }

    public void writeFile(View v, long elapsedSeconds) {
        //final int elapsedSeconds = (int) (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String finalString = elapsedSeconds + " " + date + " " + totalSteps;
        try {
            FileInputStream fileInputStream = v.getContext().openFileInput("Work_Data_Final2.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuffer = new StringBuilder();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines).append("\n");
            }

            try {
                FileOutputStream fileOutputStream = v.getContext().openFileOutput("Work_Data_Final2.txt", MODE_PRIVATE);
                fileOutputStream.write(stringBuffer.toString().getBytes());
                fileOutputStream.write(finalString.getBytes());
                fileOutputStream.close();

                Toast.makeText(v.getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetTodaysStats() {
        todaySteps = 0;
        todayTime = 0;
        todayTimeView.setText(String.valueOf(todayTime));
        todayStepsView.setText(String.valueOf(todaySteps));
    }


    @Override
    public void onResume() {
        super.onResume();

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this.getContext(), "Sensor Not found!", Toast.LENGTH_SHORT).show();
        }
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
        void OnSaveButtonPressed(View v);
    }


}


