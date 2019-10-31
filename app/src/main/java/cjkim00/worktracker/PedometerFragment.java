package cjkim00.worktracker;


import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Toast;

import java.util.Objects;



/**
 * A simple {@link Fragment} subclass.
 */
public class PedometerFragment extends Fragment implements SensorEventListener {

    int totalSteps = 0;
    TextView steps;
    SensorManager sensorManager;
    public PedometerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_pedometer, container, false);

        steps = (TextView) v.findViewById(R.id.step_counter);
        steps.setText(String.valueOf(totalSteps));
        sensorManager = (SensorManager) Objects.requireNonNull(getActivity()).getSystemService(Context.SENSOR_SERVICE);
        return v;
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
        totalSteps++;
        steps.setText(String.valueOf(totalSteps));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
