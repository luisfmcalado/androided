package eu.luisfmcalado.androided;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main extends Activity implements SensorEventListener {

    private final int TIMEOUT = 1000;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private EditText serveredtx;
    private EditText portedtx;
    private TextView logtext;
    private String host = "192.168.1.5";
    private int port = 9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        logtext = (TextView) findViewById(R.id.logtext);

        serveredtx = (EditText) findViewById(R.id.server);
        serveredtx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                host = serveredtx.getText().toString();
            }
        });
        portedtx = (EditText) findViewById(R.id.port);
        portedtx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                port = Integer.valueOf(portedtx.getText().toString());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop() {
        mSensorManager.unregisterListener(this, mSensor);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] rotationMatrix = new float[16];
            float[] mOrientValues = new float[3];

            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, mOrientValues);
            for (int i = 0; i < 3; i++)
                mOrientValues[i] = (float)
                        Math.toDegrees(mOrientValues[i]) + 180.0f;

            float axisX = mOrientValues[0];
            float axisY = mOrientValues[1];
            float axisZ = mOrientValues[2];
            changedData(axisX, axisY, axisZ);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void changedData(final float x, final float y, final float z) {

        final Handler handler = new Handler();
        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Socket s = new Socket();
                    s.connect(new InetSocketAddress(host, port), TIMEOUT);
                    s.setSoTimeout(TIMEOUT);

                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);

                    final String data = String.valueOf(x) + ";" + String.valueOf(y) + ";" + String.valueOf(z);
                    out.println(data);

                    String ak;
                    if((ak = in.readLine()) == null || !ak.equals("1")){
                        throw new IOException("ak not received");
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            logtext.setText("X: " + String.valueOf(x) + "\n");
                            logtext.append("Y: " + String.valueOf(y) + "\n");
                            logtext.append("Z: " + String.valueOf(z));
                        }
                    });

                    in.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            logtext.setText("Server offline.");
                        }
                    });
                }

            }
        });
        tr.start();
    }
}
