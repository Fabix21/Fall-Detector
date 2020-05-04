package com.name.accelerometr;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.telephony.SmsManager;
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
import androidx.preference.PreferenceManager;

import java.text.DecimalFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    LocationManager locationManager;
    LocationListener locationListener;
    TextView x, y, z;
    TextView longitude, latitude;
    String valueX, valueY, valueZ;
    double longitudeValue;
    double latitudeValue;

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }


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
            if (checkPermission(Manifest.permission.SEND_SMS)) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String phoneNumber = sharedPreferences.getString("phone_number", "");
                String userName = sharedPreferences.getString("user_name", "");

                String textMessage = "Obecna lokalizacja to: " + "https://www.latlong.net/c/?lat=" + latitudeValue + "&long=" + longitudeValue
                        + " Powiadom odpowiednie sluzby lub idz pod wybrany adres!";
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, "Użytkownik " + userName + " wzywa pomocy!", null, null);
                smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null);
                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
            }
            fallPopUp();
            playSound();
            Log.i("Wykryto upadek", "os Y");

        }
    }

    private void playSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
        mediaPlayer.start();
    }

    public void fallPopUp() {
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


