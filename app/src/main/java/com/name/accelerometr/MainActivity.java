package com.name.accelerometr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends Activity implements SensorEventListener {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView x, y, z;
    TextView longitude, latitude;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
    String valueX, valueY, valueZ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        x = findViewById(R.id.textView2);
        y = findViewById(R.id.textView3);
        z = findViewById(R.id.textView4);

        longitude = findViewById(R.id.textView);
        latitude = findViewById(R.id.textView5);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());
                longitude.setText("Szerokość geograficzna : " + location.getLongitude());
                latitude.setText("Długość geograficzna : " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){

            float xVal = event.values[0];
            float yVal = event.values[1];
            float zVal = event.values[2];

            valueX = "Os X  : " + xVal;
            valueY = "Os Y  : " + yVal;
            valueZ = "Os Z  : " + zVal;

            x.setText(valueX);
            y.setText(valueY);
            z.setText(valueZ);
            checkFall(yVal);
        }
    }

    private void checkFall(float yVal) {
        if (yVal > 8) {
            fallPopUp();
            Log.i("Wykryto upadek", "os Y");
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


