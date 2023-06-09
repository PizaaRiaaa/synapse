package com.example.synapsewear;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.wear.ambient.AmbientModeSupport;

import com.example.synapsewear.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends FragmentActivity implements
        AmbientModeSupport.AmbientCallbackProvider, SensorEventListener {

    private TextView mTextView;
    private ActivityMainBinding binding;

    private static final int N_SAMPLES =100;
    private static int prevIdx = -1;

    private static List<Float> ax;
    private static List<Float> ay;
    private static List<Float> az;

    private static List<Float> lx;
    private static List<Float> ly;
    private static List<Float> lz;

    private static List<Float> gx;
    private static List<Float> gy;
    private static List<Float> gz;

    private static List<Float> ma;
    private static List<Float> ml;
    private static List<Float> mg;
    private static List<Float> mm;

    private static List<Float> mx;
    private static List<Float> my;
    private static List<Float> mz;

    private static List<Float> xr;
    private static List<Float> yr;
    private static List<Float> zr;
    private static List<Float> sr;

    private EditText userID;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLinearAcceleration;
    private Sensor mMagnetometer;
    private Sensor heartRate;
    private Sensor stepCount;
    private Sensor stepDetector;
    private Sensor pressure;
    private Sensor mRot;

    private TextView Heart;
    private TextView Status;
    private TextView stepCounts;
    private TextView jumpingTextView;;
    private TextView fallingTextView;
    private TextView standingTextView;
    private TextView walkingTextView;

    private TableRow fallingTableRow;
    private TableRow jumpingTableRow;
    private TableRow standingTableRow;
    private TableRow walkingTableRow;

    private ImageView ivPosition;

    private TextToSpeech textToSpeech;
    private float[] results;
    private TFClassifier classifier;
    private String[] labels = {"jumping", "standing","walking","falling"};

    private static final String TAG = "MainActivity";
    private SensorManager sensorManager;
    private boolean permission_to_record = false;
    String InputID;
    String DATA = "";
    String newline = "";
    String modified_DATA = "";
    String dateCurrent;
    String dateCurrentTemp = "";
    String statusPosition = "";
    private FileWriter writer;

    ProgressBar progressBar;

    File gpxfile;
    int Sit = 0;
    int Stand = 0;
    int Walk = 0;
    int Jump = 0;
    int Fall = 0;
    int step_count = 0;
    String Stat ="";
    String heartValue ="";
    Context context = this;
    double maValue; double mgValue; double mlValue;
    int CounterForSave = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ax = new ArrayList<>(); ay = new ArrayList<>(); az = new ArrayList<>();
        lx = new ArrayList<>(); ly = new ArrayList<>(); lz = new ArrayList<>();
        gx = new ArrayList<>(); gy = new ArrayList<>(); gz = new ArrayList<>();
        ma = new ArrayList<>(); ml = new ArrayList<>(); mg = new ArrayList<>();
        mx = new ArrayList<>(); my = new ArrayList<>(); mz = new ArrayList<>();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


//        AmbientModeSupport.attach(this);
        //standingTextView = (TextView) findViewById(R.id.standing_prob);
  //      walkingTextView = (TextView) findViewById(R.id.walking_prob);
  //      jumpingTextView = (TextView) findViewById(R.id.jumping_prob);
  //      fallingTextView = (TextView) findViewById(R.id.falling_prob);

  //      standingTableRow = (TableRow) findViewById(R.id.standing_row);
  //      walkingTableRow = (TableRow) findViewById(R.id.walking_row);
  //      jumpingTableRow = (TableRow) findViewById(R.id.jumping_row);
  //      fallingTableRow = (TableRow) findViewById(R.id.falling_row);
        String[] items1 = new String[]{"ID : 1", "ID : 2", "ID : 3", "ID : 4"};

      //  ivPosition = findViewById(R.id.ivPosition);

       // Status = (TextView) findViewById(R.id.status);
        Heart = (TextView) findViewById(R.id.heart);
        //stepCounts = (TextView) findViewById(R.id.steps);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        heartRate = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        stepCount = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        stepDetector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        pressure = mSensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);


        mSensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, stepCount, SensorManager.SENSOR_DELAY_FASTEST);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer , SensorManager.SENSOR_DELAY_FASTEST);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){ //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

  //      mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
  //      mSensorManager.registerListener(this, mLinearAcceleration , SensorManager.SENSOR_DELAY_NORMAL);

        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope , SensorManager.SENSOR_DELAY_FASTEST);


  //      mRot = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
 //       mSensorManager.registerListener(this, mRot , SensorManager.SENSOR_DELAY_NORMAL);

        classifier = new TFClassifier(getApplicationContext());

      //  textToSpeech = new TextToSpeech(this, this);
      //  textToSpeech.setLanguage(Locale.US);


        startMeasure();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_STEM_1:
                Toast.makeText(this, "Stop key pressed", Toast.LENGTH_SHORT).show();
                finish();
                System.exit(0);
                return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    protected void onResume() {
        super.onResume();
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
 //     getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
  //    getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
 //     getSensorManager().registerListener(this, getSensorManager().getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onDestroy() {
      //  if (textToSpeech != null) {
      //      textToSpeech.stop();
      //      textToSpeech.shutdown();
      //  }
        super.onDestroy();
    }

    private void stopMeasure() {
        mSensorManager.unregisterListener(this,heartRate);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss");
        dateCurrent = sdf.format(date);
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            ax.add(event.values[0]);
            ay.add(event.values[1]);
            az.add(event.values[2]);
//            Status.setText(String.valueOf(event.values[0]));
            maValue = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));

        }
        if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx.add(event.values[0]);
            gy.add(event.values[1]);
            gz.add(event.values[2]);

            mgValue = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));

        }
//        if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
//            lx.add(event.values[0]);
//            ly.add(event.values[1]);
//            lz.add(event.values[2]);
//        }
//        else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
//            xr.add(event.values[0]);
//            yr.add(event.values[1]);
//            zr.add(event.values[2]);
//            sr.add(event.values[3]);
//        }

//        else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            mx.add(event.values[0]);
//            my.add(event.values[1]);
//            mz.add(event.values[2]);
//
//        }

        if (sensor.getType() == Sensor.TYPE_HEART_RATE){
            float mHeartRateFloat = event.values[0];
            int mHeartRate = Math.round(mHeartRateFloat);
            Heart.setText(Integer.toString(mHeartRate));
            heartValue = Integer.toString(mHeartRate);

            String message = heartValue;
            String datapath = "/myapp/synapse/heartrate";
            new SendMessage(datapath, message).start();

            // HIGH HEART RATE
           // if(mHeartRate > 120 && statusPosition.equals("standing")){
           //     startActivity(new Intent(this, HighBP.class));
           //     String hhr = String.valueOf(mHeartRate);
           //     String datapath2 = "/myapp/synapse/hhr";
           //     new SendMessage(datapath2, hhr).start();
           // }

            // LOW HEART RATE
           // if(mHeartRate < 40 && mHeartRate > 0){
           //     startActivity(new Intent(this, LowBP.class));
           //     String lhr = String.valueOf(mHeartRate);
           //     String datapath3 = "/myapp/synapse/lhr";
           //     new SendMessage(datapath3, lhr).start();
           // }
        }

       // if(sensor.getType() == Sensor.TYPE_STEP_COUNTER){
       //     if(event.values.length > 0){
       //         String steps = " " + (int)event.values[0];
       //         stepCounts.setText(steps);
       //     }
       // }

        activityPrediction();


       // if (!dateCurrentTemp.equals(dateCurrent)){
       //     dateCurrentTemp = dateCurrent;
       //     CounterForSave = 0;
       // }
       // if (CounterForSave<60 & permission_to_record) {
       //     DATA = InputID+","+dateCurrent + "," + Stand + "," + Walk + "," +  Jump + "," +  Fall + "," + Stat +"," + heartValue +"\n";
       //     modified_DATA = newline + DATA;
       //     newline = modified_DATA;
       //     CounterForSave = CounterForSave + 4;
       // }
    }
    private void startMeasure() {
        boolean sensorRegistered = mSensorManager.registerListener(this, heartRate, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void activityPrediction() {

        List<Float> data = new ArrayList<>();

        if (
                ax.size() >= N_SAMPLES &&
                        ay.size() >= N_SAMPLES && az.size() >= N_SAMPLES &&
                        gx.size() >= N_SAMPLES && gy.size() >= N_SAMPLES && gz.size() >= N_SAMPLES
//                && ma.size() >=N_SAMPLES && mg.size() >=N_SAMPLES
//               && lx.size() >= N_SAMPLES && ly.size() >= N_SAMPLES && lz.size() >= N_SAMPLES
//                && xr.size() >= N_SAMPLES && yr.size() >= N_SAMPLES && zr.size() >= N_SAMPLES&& sr.size() >= N_SAMPLES
        ) {
//            double maValue; double mgValue; double mlValue;

//            for( int i = 0; i < N_SAMPLES ; i++ ) {
//                maValue = Math.sqrt(Math.pow(ax.get(i), 2) + Math.pow(ay.get(i), 2) + Math.pow(az.get(i), 2));
//                mlValue = Math.sqrt(Math.pow(lx.get(i), 2) + Math.pow(ly.get(i), 2) + Math.pow(lz.get(i), 2));
//                mgValue = Math.sqrt(Math.pow(gx.get(i), 2) + Math.pow(gy.get(i), 2) + Math.pow(gz.get(i), 2));

//                ma.add((float)maValue);
//                ml.add((float)mlValue);
//                mg.add((float)mgValue);
//            }

            data.addAll(ax.subList(0, N_SAMPLES));
            data.addAll(ay.subList(0, N_SAMPLES));
            data.addAll(az.subList(0, N_SAMPLES));
//
            data.addAll(gx.subList(0, N_SAMPLES));
            data.addAll(gy.subList(0, N_SAMPLES));
            data.addAll(gz.subList(0, N_SAMPLES));

//            data.addAll(lx.subList(0, N_SAMPLES));
//            data.addAll(ly.subList(0, N_SAMPLES));
//            data.addAll(lz.subList(0, N_SAMPLES));

//            data.addAll(xr.subList(0, N_SAMPLES));
//            data.addAll(yr.subList(0, N_SAMPLES));
//            data.addAll(zr.subList(0, N_SAMPLES));
//            data.addAll(sr.subList(0, N_SAMPLES));

//            data.addAll(ma.subList(0, N_SAMPLES));
//            data.addAll(ml.subList(0, N_SAMPLES));
//            data.addAll(mg.subList(0, N_SAMPLES));
//            System.out.println(data);
            results = classifier.predictProbabilities(toFloatArray(data));

            float max = -1;
            int idx = -1;
            for (int i = 0; i < results.length; i++) {
                if (results[i] > max) {
                    idx = i;
                    max = results[i];
                }
            }

           // setProbabilities();
           // setRowsColor(idx);

            ax.clear();
            ay.clear(); az.clear();
            gx.clear();
            gy.clear(); gz.clear();
//            lx.clear(); ly.clear(); lz.clear();
//            xr.clear(); yr.clear(); zr.clear();
//            sr.clear();

//            ma.clear();
//            mg.clear();
        }
    }

   // private void setProbabilities() {
// //       sittingTextView.setText(Float.toString(round(results[0], 2)));
   //     standingTextView.setText(Float.toString(round(results[0], 2)));
   //     walkingTextView.setText(Float.toString(round(results[1], 2)));
   //     jumpingTextView.setText(Float.toString(round(results[2], 2)));
   //     fallingTextView.setText(Float.toString(round(0, 2)));
   // }

  //  private void setRowsColor(int idx) {
  //      fallingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
  //      standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
  //      walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));
  //      jumpingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorTransparent, null));

  //      if (idx == 0)
  //          standingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorGray, null));
  //      else if (idx == 1)
  //          walkingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorGray, null));
  //      else if (idx == 2)
  //          jumpingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorGray, null));
  //      else if (idx == 3)
  //          fallingTableRow.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorGray, null));
  //  }

    private float[] toFloatArray(List<Float> list) {
        int i = 0;
        float[] array = new float[list.size()];

        for (Float f : list) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }

    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    private SensorManager getSensorManager() {
        return (SensorManager) getSystemService(SENSOR_SERVICE);
    }

  //  @Override
  //  public void onInit(int status) {
  //      Timer timer = new Timer();
  //      timer.scheduleAtFixedRate(new TimerTask() {
  //          @Override
  //          public void run() {
  //              if (results == null || results.length == 0) {
  //                  return;
  //              }
  //              float max = -1;
  //              int idx = -1;
  //              for (int i = 0; i < results.length; i++) {
  //                  if (results[i] > max) {
  //                      idx = i;
  //                      max = results[i];
  //                  }
  //              }

  //             // if (maValue>=12.0 && mgValue>=8){

  //             //  //   try {
  //             //  //       Stat = labels[3];
  //             //  //       Stand = 0;
  //             //  //       Walk = 0;
  //             //  //       Jump = 0;
  //             //  //       Fall  = 1;
  //             //  //       Status.setText(labels[3]);
  //             //  //       textToSpeech.speak(labels[3], TextToSpeech.QUEUE_ADD, null,
  //             //  //               Integer.toString(new Random().nextInt()));
  //             //  //       TimeUnit.SECONDS.sleep(4);

  //             //  //       Stat = "";
  //             //  //   } catch (InterruptedException e) {
  //             //  //       e.printStackTrace();
  //             //  //   }
////             //       try {
////             //           Thread.sleep(2000);
////             //       } catch (InterruptedException e) {
////             //           e.printStackTrace();
////             //       }

  //             //     prevIdx = idx;
  //             // }
  //              if(max > 0.90 && idx != prevIdx && Stat!=labels[3]) {
  //                 // textToSpeech.speak(labels[idx], TextToSpeech.QUEUE_ADD, null,
  //                  //        Integer.toString(new Random().nextInt()));
  //                  Status.setText(labels[idx]);

  //                  // status position na binabato sa mobile ni senior
  //                  statusPosition = labels[idx];
  //                  String datapath = "/myapp/synapse/status";
  //                  new SendMessage(datapath, statusPosition).start();

  //                  stepCount(statusPosition);

////                    if(idx==0){
////                        Stand = 0;
////                        Walk = 0;
////                        Jump = 0;
////                        Fall = 1;
////                        Stat = labels[idx];
////                    }
  //                  if (idx ==0){
  //                      Stand = 0;
  //                      Walk = 0;
  //                      Jump = 1;
  //                      Fall  = 0;
  //                      Stat = labels[idx];
  //                  }
  //                  else if (idx ==1){
////                        Sit = 0;
  //                      Stand = 1;
  //                      Walk = 0;
  //                      Jump = 0;
  //                      Fall  = 0;
  //                      Stat = labels[idx];
  //                  }
  //                  else if (idx ==2){
////                        Sit = 0;
  //                      Stand = 0;
  //                      Walk = 1;
  //                      Jump = 0;
  //                      Fall  = 0;
  //                      Stat = labels[idx];
  //                  }

  //                  prevIdx = idx;
  //              }
  //          }
  //      }, 400, 1300);
////    }, 750, 2000);
  //  }

   // public void stepCount(String status){
   //     Handler handler = new Handler(Looper.getMainLooper());
   //     handler.postDelayed(new Runnable() {
   //         @Override
   //         public void run() {
   //             if(status.equals("walking")){
   //                 step_count++;
   //                 stepCounts.setText(String.valueOf(step_count));
   //                 String step = stepCounts.getText().toString();

   //                 // step count na binabato sa mobile ni senior
   //                 String datapath = "/myapp/synapse/stepcounts";
   //                 new SendMessage(datapath, String.valueOf(step)).start();
   //             }

   //         }
   //     }, 1000);

   // }

    class SendMessage extends Thread {
        String path;
        String message;

//Constructor for sending information to the Data Layer//

        SendMessage(String p, String m) {
            path = p;
            message = m;
        }

        public void run() {

//Retrieve the connected devices//

            Task<List<Node>> nodeListTask =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {

//Block on a task and get the result synchronously//

                List<Node> nodes = Tasks.await(nodeListTask);
                for (Node node : nodes) {

//Send the message///

                    Task<Integer> sendMessageTask =
                            Wearable.getMessageClient(MainActivity.this).sendMessage(node.getId(), path, message.getBytes());

                    try {

                        Integer result = Tasks.await(sendMessageTask);

//Handle the errors//

                    } catch (ExecutionException exception) {

//TO DO//

                    } catch (InterruptedException exception) {

//TO DO//

                    }

                }

            } catch (ExecutionException exception) {

//TO DO//

            } catch (InterruptedException exception) {

//TO DO//

            }
        }
    }


    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public AmbientModeSupport.AmbientCallback getAmbientCallback() {
        return new MyAmbientCallback();
    }
    /** Customizes appearance for Ambient mode. (We don't do anything minus default.) */
    private class MyAmbientCallback extends AmbientModeSupport.AmbientCallback {
        /** Prepares the UI for ambient mode. */
        @Override
        public void onEnterAmbient(Bundle ambientDetails) {
            super.onEnterAmbient(ambientDetails);
        }

        /**
         * Updates the display in ambient mode on the standard interval. Since we're using a custom
         * refresh cycle, this method does NOT update the data in the display. Rather, this method
         * simply updates the positioning of the data in the screen to avoid burn-in, if the display
         * requires it.
         */
        @Override
        public void onUpdateAmbient() {
            super.onUpdateAmbient();
        }

        /** Restores the UI to active (non-ambient) mode. */
        @Override
        public void onExitAmbient() {
            super.onExitAmbient();
        }
    }
}