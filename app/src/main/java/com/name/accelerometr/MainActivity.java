package com.name.accelerometr;


import android.app.AlertDialog;

import android.content.DialogInterface;
import android.os.Bundle;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.app.Activity;
import android.text.Html;
import android.util.Log;

import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;

    TextView x;
    TextView y;
    TextView z;

    String valueX, valueY, valueZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = (TextView) findViewById (R.id.textView2);
        y = (TextView) findViewById (R.id.textView3);
        z = (TextView) findViewById (R.id.textView4);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

            float xVal = event.values[0];
            float yVal = event.values[1];
            float zVal = event.values[2];

          //  float totalAcceleration = (float) Math.sqrt((xVal*xVal)+(yVal*yVal)+(zVal*zVal));
            valueX = "Os X  : " + xVal;
            valueY = "Os Y  : " + yVal;
            valueZ = "Os Z  : " + zVal;

            x.setText(valueX);
            y.setText(valueY);
            z.setText(valueZ);

            if(yVal>8) {
                fallPopUp();
                Log.i("wykryto Upadek", "os Y");
                }
            }
        }

    public void fallPopUp()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)

                .setTitle("Upadek!")
                .setMessage("Wykryto upadek, w ciągu 10 sekund zostanią powiadomione odpowienie słuzby! Jeśli to pomyłka to masz lipę")
                .setNeutralButton("TO POMYLKA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        Toast.makeText(getApplicationContext(),"Wygląda na to, że jednak żyjesz, GJ!",Toast.LENGTH_LONG).show();
                    }
                }).show();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    
}


