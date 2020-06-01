package com.name.accelerometr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    LocationManager locationManager;
    LocationListener locationListener;
    SensorManager sensorManager;
    TextView x, y, z;
    TextView longitude, latitude;
    String valueX, valueY, valueZ;
    double longitudeValue;
    double latitudeValue;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        x = findViewById(R.id.textView2);
        y = findViewById(R.id.textView3);
        z = findViewById(R.id.textView4);

        longitude = findViewById(R.id.textView);
        latitude = findViewById(R.id.textView5);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());

                DecimalFormat decimalFormat = new DecimalFormat();
                decimalFormat.setMaximumFractionDigits(10);

                longitudeValue = Double.parseDouble(decimalFormat.format(location.getLongitude()));
                latitudeValue = Double.parseDouble(decimalFormat.format(location.getLatitude()));

                longitude.setText("Szerokość geograficzna : " + decimalFormat.format(longitudeValue));
                latitude.setText("Długość geograficzna : " + decimalFormat.format(latitudeValue));

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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

        requestPermissions();

    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        } else {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

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

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void openWarningActivity() {
        Intent intent = new Intent(MainActivity.this, WarningActivity.class);
        intent.putExtra("longitude", String.valueOf(longitudeValue));
        intent.putExtra("latitude", String.valueOf(latitudeValue));
        startActivity(intent);
    }

    private void checkFall(float yVal) {
        if (yVal > 2) {
            openWarningActivity();
            sensorManager.unregisterListener(this);
        }
    }
}


