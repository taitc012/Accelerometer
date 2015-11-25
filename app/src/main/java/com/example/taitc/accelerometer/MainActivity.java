package com.example.taitc.accelerometer;

        import android.app.Activity;
        import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Vibrator;
        import android.widget.TextView;

        import com.google.android.gms.appindexing.Action;
        import com.google.android.gms.appindexing.AppIndex;
        import com.google.android.gms.common.api.GoogleApiClient;

        import java.io.FileOutputStream;
        import java.util.Date;
        import java.text.SimpleDateFormat;


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

    float[] gravity = {0, 0, 0};

    static final float timeConstant = 0.297f;
    private float alpha = 0.0f;
    private float timestamp = System.nanoTime();
    private float timestampOld = System.nanoTime();
    private int count = 0;

    FileOutputStream output;


    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, magA;

    public Vibrator v;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
        }

        try {
            //建立FileOutputStream物件，路徑為SD卡中的output.txt
            output = new FileOutputStream("/sdcard/output.txt", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        timestamp = System.nanoTime();
        timestampOld = System.nanoTime();
        count = 0;


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);

        try {
            output = new FileOutputStream("/sdcard/output.txt", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        timestamp = System.nanoTime();
        timestampOld = System.nanoTime();
        count = 0;
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        try {
            output.close();
        } catch (Exception e) {
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
        //displayCleanValues();
        // display the current x,y,z accelerometer values
        //displayCurrentValues();
        // display the max x,y,z accelerometer values
        //displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        /*deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);*/

        //float alpha = 0.02f;

        float[] input = new float[]{0, 0, 0};

        System.arraycopy(event.values, 0, input, 0, event.values.length);

        timestamp = System.nanoTime();

        // Find the sample period (between updates).
        // Convert from nanoseconds to seconds
        float dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));

        count++;

        // Calculate alpha
        alpha = timeConstant / (timeConstant + dt);

        gravity[0] = alpha * gravity[0] + (1.0f - alpha) * input[0];
        gravity[1] = alpha * gravity[1] + (1.0f - alpha) * input[1];
        gravity[2] = alpha * gravity[2] + (1.0f - alpha) * input[2];

        deltaX = input[0] - gravity[0];
        deltaY = input[1] - gravity[1];
        deltaZ = input[2] - gravity[2];

        mag = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        //display value
        displayCleanValues();
        displayCurrentValues();

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String format = s.format(new Date());
        String newline = "\n";
        String whitespace = " ";

        try {

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
        } catch (Exception e) {
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
        /*currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
        magA.setText(Float.toString(mag));*/
        currentX.setText(String.format("%.4f", deltaX));
        currentY.setText(String.format("%.4f", deltaY));
        currentZ.setText(String.format("%.4f", deltaZ));
        magA.setText(String.format("%.4f", mag));
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.taitc.accelerometer/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.taitc.accelerometer/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
