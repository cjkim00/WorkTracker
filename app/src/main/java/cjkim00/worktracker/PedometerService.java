package cjkim00.worktracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import java.util.Objects;

public class PedometerService extends Service implements SensorEventListener {
    static int totalSteps;
    SensorManager sensorManager;
    public PedometerService() {
    }

        private Looper serviceLooper;
        private ServiceHandler serviceHandler;

    @Override
    public void onSensorChanged(SensorEvent event) {
        totalSteps++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Handler that receives messages from the thread
        private final class ServiceHandler extends Handler {
            public ServiceHandler(Looper looper) {
                super(looper);
            }
            @Override
            public void handleMessage(Message msg) {

            }
        }

        @Override
        public void onCreate() {
            // Start up the thread running the service. Note that we create a
            // separate thread because the service normally runs in the process's
            // main thread, which we don't want to block. We also make it
            // background priority so CPU-intensive work doesn't disrupt our UI.
            HandlerThread thread = new HandlerThread("ServiceStartArguments",
                    10);
            thread.start();

            // Get the HandlerThread's Looper and use it for our Handler
            serviceLooper = thread.getLooper();
            serviceHandler = new ServiceHandler(serviceLooper);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

            totalSteps = -1;
            sensorManager = (SensorManager)  Objects.requireNonNull(getSystemService(Context.SENSOR_SERVICE));
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if(countSensor != null) {
                sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            } else {

            }
            // For each start request, send a message to start a job and deliver the
            // start ID so we know which request we're stopping when we finish the job
            Message msg = serviceHandler.obtainMessage();
            msg.arg1 = startId;
            serviceHandler.sendMessage(msg);

            // If we get killed, after returning from here, restart
            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // We don't provide binding, so return null
            return null;
        }

        @Override
        public void onDestroy() {

        }

        public static int getSteps() {
        return totalSteps;

        }

}
