package com.name.accelerometr;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

public class WarningActivity extends AppCompatActivity {
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 1;
    CountDownTimer userResponseTimer;
    CountDownTimer fallTimeTimer;
    TextView timer;
    String longitudeValue;
    String latitudeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);
        View button = findViewById(R.id.quitToMainActivity);
        longitudeValue = getIntent().getStringExtra("longitude");
        latitudeValue = getIntent().getStringExtra("latitude");
        button.setOnClickListener(v -> {
            userResponseTimer.cancel();
            openMainActivity();
        });
        //  fallPopUp();
        playSound();
        userResponseTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimer((int) (millisUntilFinished / 1000));
                Log.i("gps", longitudeValue + " Lat : " + latitudeValue);
            }

            @Override
            public void onFinish() {
                if (checkPermission(Manifest.permission.SEND_SMS)) {
                    sendSMS();
                    fallPopUp();
                }
            }

        }.start();
        Log.i("Wykryto upadek", "os Y");

    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void updateTimer(int secondsLeft) {
        timer = findViewById(R.id.leftTime);
        String currentTime = String.format("%02d:%02d", secondsLeft / 60, secondsLeft % 60);
        timer.setText(currentTime);
        Log.i("Current Value", Integer.toString(secondsLeft));

    }

    private void sendSMS() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String phoneNumber = sharedPreferences.getString("phone_number", "");
        String userName = sharedPreferences.getString("user_name", "");
        String textMessage = "Obecna lokalizacja to: " + "https://www.google.com/maps/search/?api=1&query=" + latitudeValue + "," + longitudeValue
                + " Powiadom odpowiednie sluzby lub idz pod wybrany adres!";

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, "Użytkownik " + userName + " wzywa pomocy!", null, null);
        smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null);
        Toast.makeText(this, "Wiadomość została wysłana!", Toast.LENGTH_SHORT).show();

    }

    private void playSound() {
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
        mediaPlayer.start();
    }

    public void fallPopUp() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Upadek!")
                .setMessage("Wykryto upadek, sms został wysłany!")
                .setNeutralButton("OK", (dialogInterface, i) -> openMainActivity()).show();
    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }
}