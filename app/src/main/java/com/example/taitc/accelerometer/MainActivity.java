package com.example.taitc.accelerometer;

        import android.app.Activity;
        import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.Vibrator;
        import android.widget.TextView;
        import java.io.BufferedWriter;
        import java.io.File;
        import java.io.FileWriter;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.Date;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;

public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float mag = 0;

    private float vibrateThreshold = 0;

    float [] gravity={0,0,0};

    FileOutputStream output;


    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, magA;

    public Vibrator v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        try{
            /*String data01 = "This is OutputStream Data01!";
            String data02 = "\n";
            String data03 = "Hello! This is Data02!!";
            String data04 = "\n";*/
            //建立FileOutputStream物件，路徑為SD卡中的output.txt
            output = new FileOutputStream("/sdcard/output.txt");
            /*output.write(data01.getBytes());  //write()寫入字串，並將字串以byte形式儲存。
            output.write(data02.getBytes());   //利用getBytes()將字串內容換為Byte
            output.write(data03.getBytes());
            output.write(data04.getBytes());
            output.close();*/
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        /*maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);*/

        magA = (TextView) findViewById(R.id.magA);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        try {
            output.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        //displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        /*deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);*/

        final float alpha = 0.1f;

        gravity[0] = alpha * gravity[0] + (1-alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1-alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1-alpha) * event.values[2];

        deltaX = event.values[0] - gravity[0];
        deltaY = event.values[1] - gravity[1];
        deltaZ = event.values[2] - gravity[2];

        mag = (float) Math.sqrt( deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ );

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String format = s.format(new Date());
        String newline = "\n";
        String whitespace = " ";

        try{

            output.write(format.getBytes());
            output.write(whitespace.getBytes());
            output.write(Float.toString(mag).getBytes());
            output.write(whitespace.getBytes());
            output.write(Float.toString(deltaX).getBytes());
            output.write(whitespace.getBytes());
            output.write(Float.toString(deltaY).getBytes());
            output.write(whitespace.getBytes());
            output.write(Float.toString(deltaZ).getBytes());
            output.write(newline.getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }



    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
        magA.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
        magA.setText(Float.toString(mag));
    }


}
