package cjkim00.worktracker;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class StopwatchFragment extends Fragment {

    boolean timerIsStarted = false;
    Chronometer chronometer;
    TextView textView;
    TextView inputField;
    TextView displayText;
    Timer timer;
    long startTime;
    public StopwatchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stopwatch, container, false);
        textView = (TextView) v.findViewById(R.id.timer_text);
        chronometer = (Chronometer) v.findViewById(R.id.stopwatch);
        displayText = (TextView) v.findViewById(R.id.output);
        if(!fileExists(v, "Work_Data.txt")) {
            try {
                FileOutputStream fileOutputStream = v.getContext().openFileOutput("Work_Data.txt", MODE_PRIVATE);
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        chronometer.setBase(SystemClock.elapsedRealtime());
        final Button stopButton = (Button) v.findViewById(R.id.start_button);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(!timerIsStarted) {

                    chronometer.start();
                    stopButton.setText("Stop");
                    timerIsStarted = true;
                    //need to stop timer
                } else {
                    chronometer.stop();
                    stopButton.setText("Start");
                    timerIsStarted = false;
                }

            }
        });

        final Button saveButton = (Button) v.findViewById(R.id.save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int elapsedSeconds = (int) (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
                textView.setText(String.valueOf(elapsedSeconds));
                writeFile(v);

            }
        });

        final Button displayButton = (Button) v.findViewById(R.id.display);
        displayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                readFile(v);
            }
        });
        return v;
    }

    public boolean fileExists(View v, String fileName) {
        File file = v.getContext().getFileStreamPath(fileName);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public void writeFile(View v) {
        final int elapsedSeconds = (int) (SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000;
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String finalString = "Time: " + String.valueOf(elapsedSeconds) + " Date: " + date + " Steps: " + PedometerFragment.getSteps();
        try {
            FileInputStream fileInputStream = v.getContext().openFileInput("Work_Data.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines + "\n");
            }

            try {
                FileOutputStream fileOutputStream = v.getContext().openFileOutput("Work_Data.txt", MODE_PRIVATE);
                fileOutputStream.write(stringBuffer.toString().getBytes());
                fileOutputStream.write(finalString.getBytes());
                fileOutputStream.close();

                Toast.makeText(v.getContext(), "Text Saved", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readFile(View v) {
        try {
            FileInputStream fileInputStream = v.getContext().openFileInput("Work_Data.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuffer = new StringBuilder();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines + "\n");
            }

            displayText.setText(stringBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}


